package com.raptor.ai.site.core;

import com.raptor.ai.site.domain.model.ppd.PropertyPricePaidRecord;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

@QuarkusTest
public class MetricAlgoTest {

    @Inject
    MetricAlgo metricAlgo;

    public MetricAlgoTest() {
        super();
    }

    @Test
    public void testMedianSortedOddCount() {
        final List<PropertyPricePaidRecord> sortedValues = new ArrayList<>();
        PropertyPricePaidRecord propertyPricePaidRecord1 = new PropertyPricePaidRecord();
        propertyPricePaidRecord1.setPrice("100");
        PropertyPricePaidRecord propertyPricePaidRecord2 = new PropertyPricePaidRecord();
        propertyPricePaidRecord2.setPrice("200");
        PropertyPricePaidRecord propertyPricePaidRecord3 = new PropertyPricePaidRecord();
        propertyPricePaidRecord3.setPrice("300");
        sortedValues.add(propertyPricePaidRecord1);
        sortedValues.add(propertyPricePaidRecord2);
        sortedValues.add(propertyPricePaidRecord3);


        metricAlgo.getMedianValue(sortedValues);
    }

    @Test
    public void testMedianSortedEvenCount() {
        final List<PropertyPricePaidRecord> sortedValues = new ArrayList<>();
        PropertyPricePaidRecord propertyPricePaidRecord1 = new PropertyPricePaidRecord();
        propertyPricePaidRecord1.setPrice("100");
        PropertyPricePaidRecord propertyPricePaidRecord2 = new PropertyPricePaidRecord();
        propertyPricePaidRecord2.setPrice("200");
        PropertyPricePaidRecord propertyPricePaidRecord3 = new PropertyPricePaidRecord();
        propertyPricePaidRecord3.setPrice("300");
        PropertyPricePaidRecord propertyPricePaidRecord4 = new PropertyPricePaidRecord();
        propertyPricePaidRecord3.setPrice("400");
        sortedValues.add(propertyPricePaidRecord1);
        sortedValues.add(propertyPricePaidRecord2);
        sortedValues.add(propertyPricePaidRecord3);
        sortedValues.add(propertyPricePaidRecord4);


        metricAlgo.getMedianValue(sortedValues);
    }
}
