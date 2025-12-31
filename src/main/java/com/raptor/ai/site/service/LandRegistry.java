package com.raptor.ai.site.service;

import smile.data.DataFrame;

import java.util.DoubleSummaryStatistics;
import java.util.IntSummaryStatistics;

public interface LandRegistry {
    DataFrame getPPDetails();
    Double _getAveragePriceByPostCode(final String postCode);

    Double getAveragePriceByPostCode(final String postCode);

    Double getAveragePriceByPrimaryPostCode(final String primaryPostCode);

    IntSummaryStatistics retrieveAveragePriceCriteria(final String postCode, final Integer fromYear, final Integer toYear);
}
