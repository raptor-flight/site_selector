package com.raptor.ai.site.domain.dao;

import com.raptor.ai.site.core.CSVParser;
import com.raptor.ai.site.core.cache.GenericCacheEngine;
import com.raptor.ai.site.domain.model.common.Address;
import com.raptor.ai.site.domain.model.ppd.PropertyPricePaidRecord;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class CSVPpdDataProvider implements PpdDataProvider {

    @Inject
    Logger logger;

    private GenericCacheEngine cacheEngine = GenericCacheEngine.getInstance();

    @PostConstruct
    private void init() {
        cacheEngine.addEntry("PPD_DATA" , this.streamAll());
    }

    public CSVPpdDataProvider() {
        super();
    }

    /**
     * @return
     */
    private List<PropertyPricePaidRecord> streamAll() {
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


    private String getTenure(final String record) {
        if ( record.isEmpty()) {
            final String errorMessage = "tenure type not found.";
            logger.log(Logger.Level.WARN , errorMessage);
            throw new RuntimeException(errorMessage);
        }

        return  (record.contentEquals(new StringBuilder("F")) ||
                record.contentEquals(new StringBuilder("L"))) ? record : "invalid type";
    }

    private String clean(String value) {
        return value == null ? null : value.replace("\"", "").trim();
    }

    /**
     * @param outwardPostCode
     * @param fromYear
     * @return
     */
    @Override
    public List<PropertyPricePaidRecord> find(String outwardPostCode, final int fromYear, final int toYear ) {
        if ( outwardPostCode == null || outwardPostCode.isEmpty()) {
            final String errorMessage = "post-code not found";
            logger.error(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }

        List<PropertyPricePaidRecord> ppdRecords = (List<PropertyPricePaidRecord>)cacheEngine.get("PPD_DATA");
        List<PropertyPricePaidRecord> ppdFilteredRecords = ppdRecords.stream().filter(ppd -> {
            final String postCodeStream = ppd.getAddress().postCode().replaceAll("\\s+", "").replaceAll("\"", "").toUpperCase();
            final int yearOfTransferStream = ppd.getDateOfTransfer().getYear();

            if (postCodeStream.startsWith(outwardPostCode)
                    && (yearOfTransferStream >= fromYear && yearOfTransferStream <= toYear)) {
                logger.infof("year of transfer %d ", yearOfTransferStream);
                return true;
            }

            return false;
        }).collect(Collectors.toList());

        return ppdFilteredRecords;
    }
}



