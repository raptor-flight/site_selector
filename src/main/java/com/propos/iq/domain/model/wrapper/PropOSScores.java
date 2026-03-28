package com.propos.iq.domain.model.wrapper;

import java.math.BigDecimal;

public record PropOSScores(
        BigDecimal investmentScore,
        String investmentGrade,
        BigDecimal riskScore,
        String riskRating,
        String opportunityGrade,
        BigDecimal medianPrice
) {}