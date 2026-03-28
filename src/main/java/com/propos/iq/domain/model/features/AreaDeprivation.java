package com.propos.iq.domain.model.features;

import java.math.BigDecimal;

public record AreaDeprivation(
        BigDecimal imdScore,
        Integer imdRank,
        Integer imdDecile,
        BigDecimal incomeScore,
        BigDecimal employmentScore,
        BigDecimal educationScore,
        BigDecimal healthScore,
        BigDecimal crimeScore,
        BigDecimal housingScore,
        BigDecimal environmentScore,
        Integer dependentChildren,
        Integer olderPopulation
) {}