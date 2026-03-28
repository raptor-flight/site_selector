package com.propos.iq.domain.model.features;

import java.math.BigDecimal;

public record AreaEconomy(
        Integer totalBusinesses,
        BigDecimal businessesPer1000Residents,
        Integer constructionBusinesses,
        Integer retailBusinesses,
        Integer propertyBusinesses,
        Integer healthBusinesses,
        Integer professionalBusinesses,
        Integer accommodationFoodBusinesses,
        Integer financialBusinesses,
        BigDecimal jobDensity,
        Integer claimantCount,
        BigDecimal claimantRate,
        String economyType
) {}