package com.propos.iq.domain.model.common;

import java.util.IntSummaryStatistics;

public record MetricStats(IntSummaryStatistics statistics , int value) {
}
