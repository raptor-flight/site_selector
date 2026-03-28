package com.propos.iq.domain.model.features;

import java.math.BigDecimal;

public record AreaMarket(
        BigDecimal medianPrice,
        BigDecimal predictedPrice,
        String valueSignal,
        BigDecimal valueGapPct,
        BigDecimal priceCv,
        BigDecimal medianPrice24m,
        Integer transactionCount,
        Integer transactionCount24m,
        String priceTrend,
        BigDecimal newBuildPct,
        BigDecimal freeholdPct,
        BigDecimal medianPriceDetached,
        BigDecimal medianPriceSemi,
        BigDecimal medianPriceTerraced,
        BigDecimal medianPriceFlat
) {}