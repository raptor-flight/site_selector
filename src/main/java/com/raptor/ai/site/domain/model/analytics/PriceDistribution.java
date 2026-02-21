package com.raptor.ai.site.domain.model.analytics;

import com.raptor.ai.site.domain.model.common.Confidence;

public record PriceDistribution(String outwardPostCode, int fromYear, int toYear, int  sampleSize, double mean, double median, double stdDev,
                                double min, double max, double percentile25, double percentile75, Confidence confidence) {}
