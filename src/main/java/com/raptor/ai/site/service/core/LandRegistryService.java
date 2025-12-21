package com.raptor.ai.site.service.core;

import com.raptor.ai.site.core.CSVParser;
import com.raptor.ai.site.domain.model.common.Address;
import com.raptor.ai.site.domain.model.ppd.PropertyPricePaidRecord;
import com.raptor.ai.site.service.LandRegistry;
import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;
import smile.data.DataFrame;
import smile.data.vector.DoubleVector;
import smile.data.vector.IntVector;
import smile.data.vector.StringVector;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;





@ApplicationScoped
public class LandRegistryService implements LandRegistry {

    /** --- members --- */
    private final Logger logger = Logger.getLogger(LandRegistryService.class);

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
        final List<String> results = csvParser.getResults("data/hmlr/ppd_data.csv");
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
                        propertyPricePaidRecord.setDateOfTransfer(LocalDate.parse(rows[2], DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                        propertyPricePaidRecord.setPropertyType(rows[4]);
                        return propertyPricePaidRecord;

                    }).collect(Collectors.toList());
        }

        return _ppdStream;
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
