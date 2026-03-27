package com.propos.iq.domain.model.features;

public record AreaFlags(
        Boolean regenerationCandidate,
        Boolean familyMarket,
        Boolean undervalued,
        Boolean digitalExclusion
) {}