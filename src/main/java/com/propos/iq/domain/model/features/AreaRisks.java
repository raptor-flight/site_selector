package com.propos.iq.domain.model.features;

import java.math.BigDecimal;

public record AreaRisks(
        String floodCategory,
        BigDecimal floodScore,
        BigDecimal crimeRatePer1000,
        String crimeTrend,
        Boolean inGreenBelt,
        Boolean ancientWoodland
) {}
