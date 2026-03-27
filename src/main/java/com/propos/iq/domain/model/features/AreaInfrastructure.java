package com.propos.iq.domain.model.features;

import java.math.BigDecimal;

public record AreaInfrastructure(
        Integer busStopCount,
        BigDecimal nearestRailM,
        Integer gpSurgeryCount,
        Integer schoolCount,
        String connectivityCategory,
        BigDecimal superfastPct
) {}