package com.raptor.ai.site.service;

import com.raptor.ai.site.domain.model.analytics.PriceDistribution;

public interface PriceAnalytics {
    PriceDistribution distribution(final String outwardPostCode, final int fromYear, final int toYear);
    double averagePrice ( final String outwardPostCode, final int fromYear, final int toYear);
    double medianPrice (final String outwardPostCode, final int fromYear, final int toYear);
}
