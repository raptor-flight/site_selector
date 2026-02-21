package com.raptor.ai.site.domain.model.view;

import com.raptor.ai.site.domain.model.common.Confidence;

public record PPDistributionView(String postCode, int fromYear, int toYear, int sampleSize, double mean,
                                 double median, double stdDev, double minValue, double maxValue, double percentile25, double percentile75,
                                 double iqr, double cv, double iqrToMedian, Confidence confidence) {
}
