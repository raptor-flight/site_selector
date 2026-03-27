package com.propos.iq.domain.model.features;

import java.math.BigDecimal;

public record AreaEnvironment(
        BigDecimal pctABC,
        BigDecimal pctFG,
        String energyCategory,
        BigDecimal greenspacePct,
        BigDecimal greenspaceScore
) {}