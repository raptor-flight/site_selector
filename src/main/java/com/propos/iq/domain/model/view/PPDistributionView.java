package com.propos.iq.domain.model.view;

import com.propos.iq.domain.model.common.Confidence;

public record PPDistributionView(String postCode, int fromYear, int toYear, int sampleSize, double mean,
                                 double median, double stdDev, double minValue, double maxValue, double percentile25, double percentile75,
                                 double iqr, double cv, double iqrToMedian, Confidence confidence, String dataConfidenceExplanation) {
}
