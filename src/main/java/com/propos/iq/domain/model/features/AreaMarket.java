package com.propos.iq.domain.model.features;

import java.math.BigDecimal;

public record AreaMarket(
        BigDecimal medianPrice,
        BigDecimal predictedPrice,
        String valueSignal,
        BigDecimal valueGapPct,
        BigDecimal priceCv,
        BigDecimal medianPrice24m,
        Integer transactionCount
) {}