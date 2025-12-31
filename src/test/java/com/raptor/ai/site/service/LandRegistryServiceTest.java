package com.raptor.ai.site.service;

import com.raptor.ai.site.core.nlp.PropertyQueryParser;
import com.raptor.ai.site.domain.intents.PropertyQuery;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class LandRegistryServiceTest {

    /*** --- members --- */
    @Inject
    LandRegistry landRegistry;
    @Inject
    Logger logger;

    @Test
    public void testRetrievalPPDFromCSV() {
        Assertions.assertNotNull(landRegistry.getPPDetails(), "no PPD data found." );
        landRegistry.getPPDetails().stream().forEach(propertyPricePaidRecord -> logger.info(propertyPricePaidRecord));
        return;
    }

    @Test
    public void testPropertyParserNLPBirminghamAveragePostCodeIntents() {
        final PropertyQueryParser parser = new PropertyQueryParser();
        final String query = "What's the average price in B38 over the last 5 years?";
        final PropertyQuery pQuery = parser.parse(query);
        logger.info(pQuery);
        return;
    }

    @Test
    public void testPropertyParserNLPBirminghamAverageCompareMultiplePostCodeIntents() {
        final PropertyQueryParser parser = new PropertyQueryParser();
        final String query = "Compare average prices in B38 and B29 over the last 5 years?";
        final PropertyQuery pQuery = parser.parse(query);
        logger.info(pQuery);
        return;
    }

}
