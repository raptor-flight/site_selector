package com.raptor.ai.site.engine.api;

import com.raptor.ai.site.domain.model.analytics.PriceDistribution;

public interface PriceAnalyticsEngine {

    PriceDistribution distribution(final String outwardPostCode, final int fromYear, final int toYear);
}
