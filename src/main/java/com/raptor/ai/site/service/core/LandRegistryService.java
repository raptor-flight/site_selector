package com.raptor.ai.site.service.core;

import com.raptor.ai.site.core.CSVParser;
import com.raptor.ai.site.core.MetricAlgo;
import com.raptor.ai.site.domain.model.common.Address;
import com.raptor.ai.site.domain.model.common.MetricStats;
import com.raptor.ai.site.domain.model.ppd.PropertyPricePaidRecord;
import com.raptor.ai.site.service.LandRegistry;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import smile.data.DataFrame;
import smile.data.vector.DoubleVector;
import smile.data.vector.IntVector;
import smile.data.vector.StringVector;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;





@ApplicationScoped
public class LandRegistryService implements LandRegistry {

    /** --- members --- */
    @Inject
    Logger logger;
    @Inject
    MetricAlgo metricAlgo;

    public LandRegistryService() {
        super();
    }

    public DataFrame getPPDetails() {
        return this.buildPPDToDataFrame(this.getRecords());
    }

    @Override
    public Double _getAveragePriceByPostCode(final String postCode) {
        return this.getRecords().stream()
                .filter(ppd -> {
                    String pc = ppd.getAddress() != null ? ppd.getAddress().postCode() : null;
                    return pc != null && pc.trim().toUpperCase().startsWith(postCode.toUpperCase());
                })
                .collect(Collectors.averagingDouble(p -> Double.parseDouble(p.getPrice())));
    }

    public List<PropertyPricePaidRecord> getRecords() {
        final CSVParser csvParser = new CSVParser();
        final List<String> results = csvParser.getResults("data/hmlr/ppd_data_all.csv");
        List<PropertyPricePaidRecord> _ppdStream = null;
        if ( results != null && !results.isEmpty()) {
            _ppdStream = results.stream()
                    .map(record -> record.split(","))
                    .map((String[] rows) -> {
                        final PropertyPricePaidRecord propertyPricePaidRecord = new PropertyPricePaidRecord();
                        propertyPricePaidRecord.setPrice(rows[1]);
                        propertyPricePaidRecord.setAddress(new Address(rows[3], rows[8], rows[7], rows[9],
                                rows[10], rows[11], rows[12], rows[13]));
                        propertyPricePaidRecord.setTenure(this.getTenure(rows[6]));
                        propertyPricePaidRecord.setDateOfTransfer(LocalDate.parse(this.clean(rows[2]), DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                        propertyPricePaidRecord.setPropertyType(rows[4]);

                        return propertyPricePaidRecord;

                    }).collect(Collectors.toList());
        }

        return _ppdStream;
    }

    private String clean(String value) {
        return value == null ? null : value.replace("\"", "").trim();
    }

    public Double getAveragePriceByPostCode(final String postCode) {
        final Stream<PropertyPricePaidRecord> ppdStream = this.getRecords().stream();
        Map<String, Double> collect = ppdStream.filter(ppd -> ppd.getAddress() != null
                        && ppd.getAddress().postCode() != null
                        && ppd.getAddress().postCode().equalsIgnoreCase(postCode))
                .collect(Collectors.groupingBy(
                        p -> p.getAddress().postCode().trim().toLowerCase(),
                        Collectors.averagingDouble(p -> Double.parseDouble(p.getPrice()))));

        return collect.get(postCode.toLowerCase());
    }

    public Double getAveragePriceByPrimaryPostCode(final String postCode) {
        final Stream<PropertyPricePaidRecord> ppdStream = this.getRecords().stream();
        double averagePrimaryPostCodePrice = ppdStream.filter(ppd -> ppd.getAddress() != null
                        && ppd.getAddress().postCode() != null
                        && ppd.getAddress().postCode().trim().toUpperCase().startsWith(postCode.toUpperCase()))
                .mapToDouble(ppd -> Double.parseDouble(ppd.getPrice())).average().orElse(0.0);

        logger.infof("average price of primary post code %s " , averagePrimaryPostCodePrice);

        return averagePrimaryPostCodePrice;
    }

    private final ConcurrentMap<String , List<PropertyPricePaidRecord>> ppd_cache = new ConcurrentHashMap<>(1);


    public IntSummaryStatistics retrieveAveragePriceCriteria( final String postCode, final Integer fromYear, final Integer toYear) {
        if ( ppd_cache.get("PPD_CACHE") == null) {
            ppd_cache.put("PPD_CACHE" ,  this.getRecords());
        }

        Stream<PropertyPricePaidRecord> ppdStream = ppd_cache.get("PPD_CACHE" ).stream();
        Stream<PropertyPricePaidRecord> propertyPricePaidRecordFilteredStream = ppdStream.filter(ppd -> {
            final String postCodeStream = ppd.getAddress().postCode().replaceAll("\\s+", "").replaceAll("\"", "").toUpperCase();
            final int yearOfTransferStream = ppd.getDateOfTransfer().getYear();

            if (postCodeStream.startsWith(postCode)
                   && (yearOfTransferStream >= fromYear && yearOfTransferStream <= toYear)) {
                logger.infof("year of transfer %d ", yearOfTransferStream);
                return true;
            }

            return false;
        });

        IntSummaryStatistics statistics = propertyPricePaidRecordFilteredStream.mapToInt(ppd -> Integer.parseInt(ppd.getPrice().replaceAll("\"", ""))).summaryStatistics();

        logger.infof("statistics %s " , statistics.toString());
        return statistics;
    }

    public MetricStats retrieveMedianPrice(final String postCode, final Integer fromYear, final Integer toYear) {
        if (ppd_cache.get("PPD_CACHE") == null) {
            ppd_cache.put("PPD_CACHE", this.getRecords());
        }

        Stream<PropertyPricePaidRecord> ppdStream = ppd_cache.get("PPD_CACHE").stream();

        var result = ppdStream
                .filter(ppd -> {
                    String postCodeStream = ppd.getAddress().postCode().replaceAll("\\s+", "").replaceAll("\"", "").toUpperCase();
                    int yearOfTransferStream = ppd.getDateOfTransfer().getYear();
                    if (postCodeStream.startsWith(postCode)
                            && yearOfTransferStream >= fromYear
                            && yearOfTransferStream <= toYear) {
                        logger.infof("year of transfer %d ", yearOfTransferStream);
                        return true;
                    }
                    return false;
                })
                .collect(Collectors.teeing(
                        Collectors.toList(), // collect filtered records
                        Collectors.summarizingInt(ppd -> Integer.parseInt(ppd.getPrice().replaceAll("\"", ""))), // stats
                        (list, stats) -> Map.entry(list, stats) // merge into a single object
                ));

        List<PropertyPricePaidRecord> list = result.getKey();
        IntSummaryStatistics statistics = result.getValue();

        return new MetricStats(statistics, metricAlgo.getMedianValue(list));
    }


    private String extractPrimaryPostCode(String postCode) {
        if (postCode == null) return null;

        String normalized = postCode.replaceAll("\\s+", "").toUpperCase();

        // Extract outward code (B38, B1, B15, etc.)
        return normalized.replaceAll("([A-Z]{1,2}\\d{1,2}).*", "$1");
    }




    private DataFrame buildPPDToDataFrame(final List<PropertyPricePaidRecord> ppdRecords ) {
        /*** --- price sold --- */
        final double []prices = ppdRecords.stream().mapToDouble(value -> Double.parseDouble(value.getPrice())).toArray();
        /*** --- getYear, derived from deed_date --- */
        final int[] yearSold = ppdRecords.stream().mapToInt((ppr) ->  ppr.getDateOfTransfer().getYear()).toArray();
        /*** --- postCodes --- */
        final String[] postCodes = ppdRecords.stream().map((ppr) -> ppr.getAddress().postCode()).toArray(String[]::new);
        /*** --- property types --- */
        final String[] propertyTypes = ppdRecords.stream().map((ppr) -> ppr.getPropertyType()).toArray(String[]::new);
        /*** --- tenures --- */
        final String[] tenureTypes = ppdRecords.stream().map(ppr -> ppr.getTenure()).toArray(String[]::new);

        /*** --- build the DataFrame for SMILE --- */
        final DataFrame dataFrame = DataFrame.of(DoubleVector.of("price" , prices),
                IntVector.of("year" , yearSold),
                StringVector.of("postcode" , postCodes),
                StringVector.of("propertyType" , propertyTypes),
                StringVector.of("tenure" , tenureTypes));


        return dataFrame;
    }

    private String getTenure(final String record) {
        if ( record.isEmpty()) {
            final String errorMessage = "tenure type not found.";
            logger.log(Logger.Level.WARN , errorMessage);
            throw new RuntimeException(errorMessage);
        }

        return  (record.contentEquals(new StringBuilder("F")) ||
                record.contentEquals(new StringBuilder("L"))) ? record : "invalid type";
    }

}
