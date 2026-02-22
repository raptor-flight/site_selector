package com.raptor.ai.site.engine;

import com.raptor.ai.site.core.MetricAlgo;
import com.raptor.ai.site.domain.dao.PpdDataProvider;
import com.raptor.ai.site.domain.model.analytics.PriceDistribution;
import com.raptor.ai.site.domain.model.common.Confidence;
import com.raptor.ai.site.domain.model.ppd.PropertyPricePaidRecord;
import com.raptor.ai.site.engine.api.PriceAnalyticsEngine;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import smile.math.MathEx;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@ApplicationScoped
public class PPDAnalyticsEngine implements PriceAnalyticsEngine  {
    private final Logger logger;
    private final PpdDataProvider ppdDataProvider;
    private final MetricAlgo metricAlgo;

    @Inject
    public PPDAnalyticsEngine(final MetricAlgo metricAlgo , final PpdDataProvider ppdDataProvider , final Logger logger) {
        super();
        this.logger = logger;
        this.ppdDataProvider = ppdDataProvider;
        this.metricAlgo = metricAlgo;
    }


    /**
     * @param outwardPostCode
     * @param fromYear
     * @param toYear
     * @return
     */
    @Override
    public PriceDistribution distribution(final String outwardPostCode, final int fromYear, final int toYear) {
        /*** 1. get PPD records --- */
        final List<PropertyPricePaidRecord> propertyPricePaidRecords = this.ppdDataProvider.find(outwardPostCode, fromYear, toYear);

        /*** validate records */
        if ( propertyPricePaidRecords == null || propertyPricePaidRecords.isEmpty() ) {
            logger.warn("no PPD records found.");
            return new PriceDistribution(outwardPostCode, fromYear, toYear ,
                    0, 0.0,0.0,0.0,0.0,0.0,0.0,0.0, null);
        }

        /*** 2. retrieve prices from the PPD records, ready for SMILE. --- */

        /*** From a list of the property records, extract the prices, clean them, convert them to numbers,
         *   and put them into a double[], so SMILE can use them. ---*/
        final double[] prices = propertyPricePaidRecords.stream().map(record -> record.getPrice())
                .filter(record -> record != null && !record.isBlank())
                .map(record -> record.replace("\"", "").trim())
                .mapToDouble(record -> Double.parseDouble(record))
                .toArray();


        if ( prices.length == 0) {
            return new PriceDistribution(outwardPostCode ,
                    fromYear, toYear, 0,0,
                    0,0,0,0,0,
                    0, null);
        }

        Arrays.sort(prices);

        int sample = prices.length;
        double mean = MathEx.mean(prices);
        double stdev = ( sample < 2 ) ? 0.0 : MathEx.sd(prices);
        /*** min price - use 1st element in the array as it is assorted in ASC order ***/
        double min = prices[0];
        double max = prices[prices.length - 1 ];
        double median = MathEx.median(prices);
        //percentile calculation
        double p25 = metricAlgo.getPercentile(prices, 0.25);
        double p75 = metricAlgo.getPercentile(prices, 0.75);


        final PriceDistribution distribution = new PriceDistribution(outwardPostCode, fromYear , toYear,
                sample, mean, median, stdev, min, max, p25, p75, Confidence.fromSampleSize(sample));

        logger.infof("price distribution - %s", distribution);

        return distribution;
    }

}
