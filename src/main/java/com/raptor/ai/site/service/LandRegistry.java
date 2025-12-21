package com.raptor.ai.site.service;

import smile.data.DataFrame;

public interface LandRegistry {
    DataFrame getPPDetails();
    Double _getAveragePriceByPostCode(final String postCode);

    Double getAveragePriceByPostCode(final String postCode);

    Double getAveragePriceByPrimaryPostCode(final String primaryPostCode);
}
