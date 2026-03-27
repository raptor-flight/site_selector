package com.propos.iq.domain.model.features;


public record AreaProfile(
        AreaLocation location,
        AreaScores scores,
        AreaRisks risks,
        AreaMarket market,
        AreaEnvironment environment,
        AreaPeople people,
        AreaInfrastructure infrastructure,
        AreaFlags flags
) {}