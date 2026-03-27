package com.propos.iq.engine.api;

import com.propos.iq.domain.model.analytics.PriceDistribution;

public interface PriceAnalyticsEngine {

    PriceDistribution distribution(final String outwardPostCode, final int fromYear, final int toYear);
}
