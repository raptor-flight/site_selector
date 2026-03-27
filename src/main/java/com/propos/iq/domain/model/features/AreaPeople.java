package com.propos.iq.domain.model.features;

import java.math.BigDecimal;

public record AreaPeople(
        Integer population,
        BigDecimal workingAgePct,
        BigDecimal ownerOccupiedPct,
        Integer imdDecile,
        BigDecimal claimantRate
) {}