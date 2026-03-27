package com.propos.iq.service;

import com.propos.iq.domain.model.analytics.PriceDistribution;

public interface PriceAnalytics {
    PriceDistribution distribution(final String outwardPostCode, final int fromYear, final int toYear);
    double averagePrice ( final String outwardPostCode, final int fromYear, final int toYear);
    double medianPrice (final String outwardPostCode, final int fromYear, final int toYear);
}
