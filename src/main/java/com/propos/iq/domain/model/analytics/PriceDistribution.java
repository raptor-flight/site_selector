package com.propos.iq.domain.model.analytics;

import com.propos.iq.domain.model.common.Confidence;

public record PriceDistribution(String outwardPostCode, int fromYear, int toYear, int  sampleSize, double mean, double median, double stdDev,
                                double min, double max, double percentile25, double percentile75, Confidence confidence) {}
