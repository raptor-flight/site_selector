package com.propos.iq.domain.model.features;

import java.math.BigDecimal;

public record AreaPeople(
        // Population
        Integer population,
        BigDecimal workingAgePct,
        BigDecimal underSixteenPct,
        BigDecimal overSixtyFivePct,

        // Tenure
        BigDecimal ownerOccupiedPct,
        BigDecimal socialRentedPct,
        BigDecimal privateRentedPct,

        // Housing type
        BigDecimal pctDetached,
        BigDecimal pctFlat,

        // Ethnicity
        BigDecimal pctWhiteBritish,
        BigDecimal pctNonWhite,
        BigDecimal pctAsian,
        BigDecimal pctBlack,
        BigDecimal pctMixed,
        BigDecimal simpsonssDiversityIndex,

        // Employment
        BigDecimal pctEmployed,
        BigDecimal pctUnemployed,
        BigDecimal pctEconomicallyInactive,

        // Deprivation
        Integer imdDecile,
        BigDecimal claimantRate
) {}