package com.propos.iq.domain.model.features;

import java.math.BigDecimal;

public record AreaEnvironment(
        BigDecimal pctABC,
        BigDecimal pctD,
        BigDecimal pctFG,
        String energyCategory,
        BigDecimal avgEfficiency,
        BigDecimal pctMainsGas,
        BigDecimal avgFloorAreaM2,
        BigDecimal pctPre1919,
        BigDecimal pct1919to1944,
        BigDecimal pct1945to1964,
        BigDecimal pct1965to1982,
        BigDecimal pct1983to1995,
        BigDecimal pctPost1995,
        BigDecimal greenspacePct,
        BigDecimal parkGardenPct,
        BigDecimal playingFieldPct,
        Integer playSpaceCount,
        Integer allotmentCount,
        BigDecimal greenspaceScore
) {}