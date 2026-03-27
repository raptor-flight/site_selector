package com.propos.iq.domain.model.features;

import java.math.BigDecimal;

public record AreaScores(
        BigDecimal investment,
        String investmentGrade,
        BigDecimal risk,
        String riskRating,
        BigDecimal opportunity,
        String opportunityGrade
) {}