package com.raptor.ai.site.service;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class LandRegistryServiceTest {

    /*** --- members --- */
    @Inject
    LandRegistry landRegistry;

    @Test
    public void testRetrievalPPDFromCSV() {
        Assertions.assertNotNull(landRegistry.getPPDetails(), "no PPD data found." );
        landRegistry.getPPDetails().stream().forEach(propertyPricePaidRecord -> System.out.println(propertyPricePaidRecord));
    }

}
