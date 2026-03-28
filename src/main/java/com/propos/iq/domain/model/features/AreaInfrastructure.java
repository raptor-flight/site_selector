package com.propos.iq.domain.model.features;

import java.math.BigDecimal;

public record AreaInfrastructure(
        // Bus
        Integer busStopCount,
        BigDecimal nearestBusStopM,
        BigDecimal busStopDensityPerKm2,

        // Rail
        Integer railStationCount,
        BigDecimal nearestRailM,
        Integer metroStopCount,

        // EV
        Integer evChargerCount,
        BigDecimal evChargersPer100k,

        // Healthcare
        BigDecimal nearestGpSurgeryM,
        Integer gpSurgeryCount,
        BigDecimal gpSurgeriesPer1000,
        BigDecimal gpAccessScore,

        // Schools
        Integer schoolCount,
        Integer primarySchools,
        Integer secondarySchools,
        Integer post16Schools,
        BigDecimal pctOutstanding,
        BigDecimal pctGood,
        BigDecimal pctRequiresImprovement,
        BigDecimal pctInadequate,
        BigDecimal avgAtt8Score,
        BigDecimal avgProgress8Score,
        BigDecimal avgPctRwmExpected,
        BigDecimal avgPctFsm,
        BigDecimal avgOverallAbsence,
        BigDecimal avgPersistentAbsence,
        String ofstedTrend,

        // Connectivity
        String connectivityCategory,
        BigDecimal superfastPct,
        BigDecimal fullFibrePct,
        BigDecimal ultrafastPct,
        BigDecimal belowUsoPct,
        BigDecimal mobile4g5gPremAny,
        BigDecimal mobile4g5gPremAll,
        BigDecimal mobile4g5gGeoAny
) {}