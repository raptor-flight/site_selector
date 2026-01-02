package com.raptor.ai.site.domain.model.common;

import java.util.IntSummaryStatistics;

public record MetricStats(IntSummaryStatistics statistics , int value) {
}
