package com.raptor.ai.site.service.core;

import com.raptor.ai.site.domain.model.analytics.PriceDistribution;
import com.raptor.ai.site.service.PriceAnalytics;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

@ApplicationScoped
public class PpdPriceAnalyticsService implements PriceAnalytics {


    @Inject
    Logger logger;

    public PpdPriceAnalyticsService() {
        super();
    }

    /**
     * @param outwardPostCode
     * @param fromYear
     * @param toYear
     * @return
     */
    @Override
    public PriceDistribution distribution(String outwardPostCode, int fromYear, int toYear) {
        return null;
    }

    /**
     * @param outwardPostCode
     * @param fromYear
     * @param toYear
     * @return
     */
    @Override
    public double averagePrice(String outwardPostCode, int fromYear, int toYear) {
        return 0;
    }

    /**
     * @param outwardPostCode
     * @param fromYear
     * @param toYear
     * @return
     */
    @Override
    public double medianPrice(String outwardPostCode, int fromYear, int toYear) {
        return 0;
    }
}
