package com.propos.iq.domain.model.features;

import java.math.BigDecimal;

public record AreaRisks(
        // Flood — rivers and sea
        String floodCategory,
        BigDecimal floodScore,
        BigDecimal rofrsHighPct,
        BigDecimal rofrsMediumPct,
        BigDecimal rofrsLowPct,
        String rofrsCategory,

        // Flood — surface water
        BigDecimal rofswHighPct,
        BigDecimal rofswMediumPct,
        BigDecimal rofswLowPct,
        String rofswCategory,

        // Crime
        BigDecimal crimeRatePer1000,
        String crimeTrend,
        BigDecimal trendPctChange,
        BigDecimal violenceRate,
        BigDecimal burglaryRate,
        BigDecimal asbRate,
        BigDecimal vehicleCrimeRate,
        BigDecimal drugsRate,

        // Planning
        Boolean inGreenBelt,
        String greenBeltName,
        Boolean ancientWoodland,
        BigDecimal ancientWoodlandPct,
        Integer listedBuildingCount,
        Integer gradeICount,
        Integer gradeIIStarCount,
        Integer gradeIICount,
        BigDecimal planningConstraintScore,

        // Road safety
        Integer totalCollisions,
        Integer fatalCollisions,
        Integer seriousCollisions,
        BigDecimal collisionRatePer1000,
        BigDecimal pctFatalOrSerious,
        BigDecimal roadSafetyScore
) {}