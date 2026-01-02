package com.raptor.ai.site.domain.delegate;

import com.raptor.ai.site.core.nlp.PropertyQueryParser;
import com.raptor.ai.site.domain.intents.IntentMetrics;
import com.raptor.ai.site.domain.intents.PropertyIntentQueryType;
import com.raptor.ai.site.domain.intents.PropertyQuery;
import com.raptor.ai.site.domain.model.PropertyMetaData;
import com.raptor.ai.site.domain.model.PropertyResultQuery;
import com.raptor.ai.site.domain.model.common.MetricStats;
import com.raptor.ai.site.service.LandRegistry;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import smile.data.DataFrame;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Delegate responsible for orchestrating Land Registry data retrieval,
 * postcode normalisation, NLP intent handling, and property‑related
 * analytical queries.
 *
 * <p>This class acts as a domain‑level façade between NLP parsing,
 * Land Registry data services, and result aggregation logic.</p>
 *
 * <p><strong>Java Version:</strong> 21 LTS<br>
 * <strong>Quarkus Version:</strong> 3.20.0 LTS</p>
 *
 * @author Kuldip Bajwa
 */
@ApplicationScoped
public class LandRegistryDelegate {

    @Inject
    LandRegistry landRegistryService;

    @Inject
    PropertyQueryParser propertyQueryParser;

    @Inject
    Logger logger;

    /**
     * Default constructor.
     */
    public LandRegistryDelegate() {
        super();
    }

    /**
     * Retrieves the schema of the underlying Land Registry dataset.
     * Logs summary, structure, and a preview slice of the data.
     *
     * @return a textual representation of the dataset structure
     */
    public String retrieveDataSchema() {
        DataFrame dataFrame = landRegistryService.getPPDetails();
        logger.infof("summary -> %s", dataFrame.summary());
        logger.info(dataFrame.structure());
        logger.info(dataFrame.slice(0, 1000));

        return dataFrame.structure().toString();
    }

    /**
     * Retrieves the average property value for a full postcode.
     * The result is rounded to 2 decimal places using CEILING mode.
     *
     * @param postCode the full postcode (e.g., "B38 9TS")
     * @return the rounded average price as a Double
     */
    public Double retrieveAverageValueByPostCode(final String postCode) {
        var bd = BigDecimal.valueOf(landRegistryService.getAveragePriceByPostCode(postCode));
        bd = bd.setScale(2, RoundingMode.CEILING);
        return bd.doubleValue();
    }

    /**
     * Retrieves the average property value for a primary (outward) postcode.
     * The outward code is extracted and the result rounded to 2 decimal places.
     *
     * @param postCode the raw postcode input
     * @return the rounded average price as a Double
     */
    public Double retrieveAverageValueByPrimaryPostCode(final String postCode) {
        final String[] splitPostCode = postCode.split("//s+");
        var bd = BigDecimal.valueOf(landRegistryService.getAveragePriceByPrimaryPostCode(postCode));
        bd = bd.setScale(2, RoundingMode.CEILING);
        return bd.doubleValue();
    }

    /**
     * Handles a natural‑language property query by:
     * <ol>
     *     <li>Parsing the query into structured intent</li>
     *     <li>Executing the appropriate Land Registry lookup</li>
     *     <li>Returning a structured result</li>
     * </ol>
     *
     * @param userQuery the freehand user query
     * @return a populated {@link PropertyResultQuery}
     */
    public PropertyResultQuery handleNLPQuery(final String userQuery) {
        final PropertyQuery parsedQuery = propertyQueryParser.parse(userQuery);
        PropertyResultQuery response = null;

        switch (parsedQuery.getType()) {
            case PropertyIntentQueryType.AVERAGE_PRICE -> {
                final IntSummaryStatistics statistics =
                        landRegistryService.retrieveAveragePriceCriteria(
                                parsedQuery.getPostCodes().get(0),
                                parsedQuery.getFromYear(),
                                parsedQuery.getToYear()
                        );

                PropertyMetaData propertyMetaData = new PropertyMetaData(
                        parsedQuery.getPostCodes().get(0),
                        parsedQuery.getFromYear(),
                        parsedQuery.getToYear(),
                        statistics.getCount(),
                        (int) statistics.getAverage()
                );

                List<PropertyMetaData> metaData = new ArrayList<>(1);
                metaData.add(propertyMetaData);
                response = new PropertyResultQuery(userQuery, metaData);
            }
            default -> {
                logger.warnf("intent type %s not mapped.", parsedQuery.getType());
                response = new PropertyResultQuery("no intent found.", new ArrayList<>(1));
            }
        }

        return response;
    }

    /**
     * Handles a structured property query, supporting:
     * <ul>
     *     <li>Area comparison</li>
     *     <li>Average price retrieval</li>
     * </ul>
     *
     * @param userQuery the original user query text
     * @return a populated {@link PropertyResultQuery}
     */
    public PropertyResultQuery handle(String userQuery) {
        final PropertyQuery parsedQuery = propertyQueryParser.parse(userQuery);
        final int postCodeCount = parsedQuery.getPostCodes().size();

        PropertyResultQuery response = null;

        switch (parsedQuery.getType()) {
            case PropertyIntentQueryType.COMPARE_AREAS -> {
                response = this.handleCompare(userQuery, parsedQuery);
            }
            case PropertyIntentQueryType.AVERAGE_PRICE -> {
                final List<PropertyMetaData> metaData = new ArrayList<>(2);
                for (final String postCode : this.deDuplicateOutwardPostCodes(parsedQuery)) {
                    final IntSummaryStatistics statistics =
                            landRegistryService.retrieveAveragePriceCriteria(
                                    postCode,
                                    parsedQuery.getFromYear(),
                                    parsedQuery.getToYear()
                            );

                    final PropertyMetaData propertyMetaData = new PropertyMetaData(
                            postCode,
                            parsedQuery.getFromYear(),
                            parsedQuery.getToYear(),
                            statistics.getCount(),
                            (int) statistics.getAverage()
                    );

                    metaData.add(propertyMetaData);
                }

                if (postCodeCount > 1 && metaData.size() == 1) {
                    userQuery = "Only one unique area detected (" +
                            metaData.getFirst().postCode() +
                            "). Showing average price instead.";
                }

                response = new PropertyResultQuery(userQuery, metaData);
            }
        }

        return response;
    }

    /**
     * Normalises and deduplicates outward postcodes from a query.
     * Ensures consistent comparison and aggregation.
     *
     * @param userQuery the parsed property query
     * @return a list of unique outward postcodes
     */
    private List<String> deDuplicateOutwardPostCodes(final PropertyQuery userQuery) {
        List<String> normalisedPostCodeList = null;

        if (userQuery.getPostCodes().size() == 1) {
            userQuery.getPostCodes().set(0, extractPrimaryPostCode(userQuery.getPostCodes().get(0)));
            normalisedPostCodeList = userQuery.getPostCodes();
        } else {
            final Set<String> distinctPostCodes = new LinkedHashSet<>(1);
            for (final String postCode : userQuery.getPostCodes()) {
                final String outwardPostCode = extractPrimaryPostCode(postCode).toUpperCase();
                logger.infof("outward postcode %s ", outwardPostCode);
                distinctPostCodes.add(outwardPostCode);
            }
            normalisedPostCodeList = new ArrayList<>(distinctPostCodes);
        }

        return normalisedPostCodeList;
    }

    /**
     * Extracts the outward (primary) postcode from a full postcode.
     * Examples:
     * <ul>
     *     <li>B38 9TS → B38</li>
     *     <li>B1 1AA → B1</li>
     *     <li>B15 2TT → B15</li>
     * </ul>
     *
     * @param postCode the raw postcode input
     * @return the outward postcode, or null if input is null
     */
    private String extractPrimaryPostCode(final String postCode) {
        final String normalized = postCode.replaceAll("\\s+", "").toUpperCase();
        logger.infof("normalised postcode %s." , normalized);

        return normalized.replaceAll("([A-Z]{1,2}\\d{1,2}).*", "$1");
    }

    /**
     * Handles comparison‑based property queries such as comparing
     * average prices across multiple areas.
     *
     * @param userQuery the original user query text
     * @param query     the parsed structured query
     * @return a populated {@link PropertyResultQuery}
     */
    private PropertyResultQuery handleCompare(String userQuery, final PropertyQuery query) {
        logger.infof("query %s", query.toString());

        final int postCodeCount = query.getPostCodes().size();
        PropertyResultQuery response = null;

        switch (query.getMetric()) {
            case IntentMetrics.MEDIAN_PRICE -> {
                logger.infof("metric %s",IntentMetrics.MEDIAN_PRICE);
                final List<PropertyMetaData> meta = new ArrayList<>(1);
                for (final String postCode : this.deDuplicateOutwardPostCodes(query)) {
                    if (postCode != null && !postCode.isEmpty()) {
                        MetricStats metricStats = landRegistryService.retrieveMedianPrice( postCode, query.getFromYear(), query.getToYear());
                        final PropertyMetaData propertyMetaData = new PropertyMetaData(
                                postCode,
                                query.getFromYear(),
                                query.getToYear(),
                                metricStats.statistics().getCount(),
                                metricStats.value());
                        meta.add(propertyMetaData);
                    }
                }

                if (postCodeCount > 1 && meta.size() == 1) {
                    userQuery = "Only one unique area detected (" +
                            meta.getFirst().postCode() +
                            "). Showing average price instead.";
                }

                response = new PropertyResultQuery(userQuery, meta);
            }
            case IntentMetrics.AVERAGE_PRICE -> {
                final List<PropertyMetaData> meta = new ArrayList<>(1);
                for (final String postCode : this.deDuplicateOutwardPostCodes(query)) {
                    if (postCode != null && !postCode.isEmpty()) {
                        IntSummaryStatistics statistics =
                                landRegistryService.retrieveAveragePriceCriteria(
                                        postCode,
                                        query.getFromYear(),
                                        query.getToYear()
                                );

                        final PropertyMetaData propertyMetaData = new PropertyMetaData(
                                postCode,
                                query.getFromYear(),
                                query.getToYear(),
                                statistics.getCount(),
                                (int) statistics.getAverage()
                        );

                        meta.add(propertyMetaData);
                    }
                }

                if (postCodeCount > 1 && meta.size() == 1) {
                    userQuery = "Only one unique area detected (" +
                            meta.getFirst().postCode() +
                            "). Showing average price instead.";
                }

                response = new PropertyResultQuery(userQuery, meta);
            }
            default -> logger.infof("metric %s not defined.", query.getMetric());
        }

        return response;
    }
}
