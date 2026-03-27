package com.propos.iq.domain.dao.query;

public enum PropOSQuery {

    AREA_PROFILE_BY_POSTCODE("""
        SELECT
            ga.external_code                        AS lsoa21cd,
            ga.label,
            p.postcode_norm                         AS postcode,
            ais.investment_score,
            ais.investment_grade,
            ars.risk_score,
            ars.risk_rating,
            ao.opportunity_score,
            ao.opportunity_grade,
            at2.median_price_12m                    AS median_price,
            at2.price_cv_12m                        AS price_cv,
            at2.transaction_count_12m,
            at2.median_price_24m,
            at2.new_build_pct,
            avm.predicted_median_price,
            avm.value_signal,
            avm.value_gap_pct,
            af.combined_flood_category,
            af.flood_risk_score,
            af.rofrs_high_pct,
            af.rofsw_high_pct,
            ac.crime_rate_per_1000                  AS total_rate_per_1000,
            ac.crime_trend                          AS trend,
            ac.crime_percentile                     AS percentile_rank,
            ae.pct_abc,
            ae.pct_fg,
            ae.avg_efficiency,
            ae.energy_efficiency_category,
            atr.bus_stop_count,
            atr.nearest_rail_station_m,
            atr.transport_connectivity_score,
            asc2.total_schools                      AS school_count,
            asc2.pct_outstanding                    AS avg_ofsted_score,
            ah.gp_surgery_count,
            ah.nearest_gp_surgery_m,
            apl.in_green_belt,
            apl.green_belt_pct,
            apl.listed_building_count,
            apl.ancient_woodland_any,
            ag.total_greenspace_pct,
            ag.greenspace_score,
            ad.total_population,
            ad.pct_working_age                      AS working_age_pct,
            ad.pct_owned                            AS owner_occupied_pct,
            dep.imd_decile,
            eco.claimant_rate,
            eco.job_density,
            eco.claimant_count,
            conn.connectivity_category,
            conn.avg_pct_sfbb                       AS superfast_pct,
            ao.is_regeneration_play                 AS regeneration_candidate,
            ao.is_family_market                     AS family_market,
            CASE WHEN avm.value_signal = 'UNDERVALUED'
                 THEN true ELSE false END           AS undervalued,
            ars.digital_exclusion_flag              AS digital_exclusion
        FROM ref.postcode_to_lsoa p
        JOIN core.geo_area ga
            ON ga.geo_area_id = p.geo_area_id
        LEFT JOIN ana.area_investment_score ais
            ON ais.geo_area_id = ga.geo_area_id
        LEFT JOIN ana.area_risk_score ars
            ON ars.geo_area_id = ga.geo_area_id
        LEFT JOIN ana.area_opportunity ao
            ON ao.geo_area_id = ga.geo_area_id
        LEFT JOIN ana.area_avm avm
            ON avm.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_transactions at2
            ON at2.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_flood af
            ON af.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_crime ac
            ON ac.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_energy ae
            ON ae.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_transport atr
            ON atr.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_schools asc2
            ON asc2.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_health ah
            ON ah.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_planning apl
            ON apl.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_greenspace ag
            ON ag.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_demographics ad
            ON ad.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_deprivation dep
            ON dep.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_economy eco
            ON eco.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_connectivity conn
            ON conn.geo_area_id = ga.geo_area_id
        WHERE p.postcode_norm = ?
        LIMIT 1
        """),

    OPPORTUNITIES_BY_LOCATION("""
        SELECT
            ga.external_code                        AS lsoa21cd,
            ga.label,
            NULL                                    AS postcode,
            ais.investment_score,
            ais.investment_grade,
            ars.risk_score,
            ars.risk_rating,
            ao.opportunity_score,
            ao.opportunity_grade,
            at2.median_price_12m                    AS median_price,
            at2.price_cv_12m                        AS price_cv,
            at2.transaction_count_12m,
            at2.median_price_24m,
            at2.new_build_pct,
            avm.predicted_median_price,
            avm.value_signal,
            avm.value_gap_pct,
            af.combined_flood_category,
            af.flood_risk_score,
            af.rofrs_high_pct,
            af.rofsw_high_pct,
            ac.crime_rate_per_1000                  AS total_rate_per_1000,
            ac.crime_trend                          AS trend,
            ac.crime_percentile                     AS percentile_rank,
            ae.pct_abc,
            ae.pct_fg,
            ae.avg_efficiency,
            ae.energy_efficiency_category,
            atr.bus_stop_count,
            atr.nearest_rail_station_m,
            atr.transport_connectivity_score,
            asc2.total_schools                      AS school_count,
            asc2.pct_outstanding                    AS avg_ofsted_score,
            ah.gp_surgery_count,
            ah.nearest_gp_surgery_m,
            apl.in_green_belt,
            apl.green_belt_pct,
            apl.listed_building_count,
            apl.ancient_woodland_any,
            ag.total_greenspace_pct,
            ag.greenspace_score,
            ad.total_population,
            ad.pct_working_age                      AS working_age_pct,
            ad.pct_owned                            AS owner_occupied_pct,
            dep.imd_decile,
            eco.claimant_rate,
            eco.job_density,
            eco.claimant_count,
            conn.connectivity_category,
            conn.avg_pct_sfbb                       AS superfast_pct,
            ao.is_regeneration_play                 AS regeneration_candidate,
            ao.is_family_market                     AS family_market,
            CASE WHEN avm.value_signal = 'UNDERVALUED'
                 THEN true ELSE false END           AS undervalued,
            ars.digital_exclusion_flag              AS digital_exclusion
        FROM core.geo_area ga
        JOIN ref.lsoa_to_laua ll
            ON ll.lsoa21cd = ga.external_code
        LEFT JOIN ana.area_investment_score ais
            ON ais.geo_area_id = ga.geo_area_id
        LEFT JOIN ana.area_risk_score ars
            ON ars.geo_area_id = ga.geo_area_id
        LEFT JOIN ana.area_opportunity ao
            ON ao.geo_area_id = ga.geo_area_id
        LEFT JOIN ana.area_avm avm
            ON avm.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_transactions at2
            ON at2.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_flood af
            ON af.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_crime ac
            ON ac.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_energy ae
            ON ae.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_transport atr
            ON atr.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_schools asc2
            ON asc2.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_health ah
            ON ah.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_planning apl
            ON apl.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_greenspace ag
            ON ag.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_demographics ad
            ON ad.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_deprivation dep
            ON dep.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_economy eco
            ON eco.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_connectivity conn
            ON conn.geo_area_id = ga.geo_area_id
        WHERE LOWER(ll.ladnm) LIKE LOWER(?)
        AND ao.opportunity_score IS NOT NULL
        ORDER BY ao.opportunity_score DESC
        LIMIT 20
        """),

    TOP_OPPORTUNITIES("""
        SELECT
            ga.external_code                        AS lsoa21cd,
            ga.label,
            NULL                                    AS postcode,
            ais.investment_score,
            ais.investment_grade,
            ars.risk_score,
            ars.risk_rating,
            ao.opportunity_score,
            ao.opportunity_grade,
            at2.median_price_12m                    AS median_price,
            at2.price_cv_12m                        AS price_cv,
            at2.transaction_count_12m,
            at2.median_price_24m,
            at2.new_build_pct,
            avm.predicted_median_price,
            avm.value_signal,
            avm.value_gap_pct,
            af.combined_flood_category,
            af.flood_risk_score,
            af.rofrs_high_pct,
            af.rofsw_high_pct,
            ac.crime_rate_per_1000                  AS total_rate_per_1000,
            ac.crime_trend                          AS trend,
            ac.crime_percentile                     AS percentile_rank,
            ae.pct_abc,
            ae.pct_fg,
            ae.avg_efficiency,
            ae.energy_efficiency_category,
            atr.bus_stop_count,
            atr.nearest_rail_station_m,
            atr.transport_connectivity_score,
            asc2.total_schools                      AS school_count,
            asc2.pct_outstanding                    AS avg_ofsted_score,
            ah.gp_surgery_count,
            ah.nearest_gp_surgery_m,
            apl.in_green_belt,
            apl.green_belt_pct,
            apl.listed_building_count,
            apl.ancient_woodland_any,
            ag.total_greenspace_pct,
            ag.greenspace_score,
            ad.total_population,
            ad.pct_working_age                      AS working_age_pct,
            ad.pct_owned                            AS owner_occupied_pct,
            dep.imd_decile,
            eco.claimant_rate,
            eco.job_density,
            eco.claimant_count,
            conn.connectivity_category,
            conn.avg_pct_sfbb                       AS superfast_pct,
            ao.is_regeneration_play                 AS regeneration_candidate,
            ao.is_family_market                     AS family_market,
            CASE WHEN avm.value_signal = 'UNDERVALUED'
                 THEN true ELSE false END           AS undervalued,
            ars.digital_exclusion_flag              AS digital_exclusion
        FROM core.geo_area ga
        JOIN ref.lsoa_to_laua ll
            ON ll.lsoa21cd = ga.external_code
        LEFT JOIN ana.area_investment_score ais
            ON ais.geo_area_id = ga.geo_area_id
        LEFT JOIN ana.area_risk_score ars
            ON ars.geo_area_id = ga.geo_area_id
        LEFT JOIN ana.area_opportunity ao
            ON ao.geo_area_id = ga.geo_area_id
        LEFT JOIN ana.area_avm avm
            ON avm.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_transactions at2
            ON at2.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_flood af
            ON af.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_crime ac
            ON ac.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_energy ae
            ON ae.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_transport atr
            ON atr.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_schools asc2
            ON asc2.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_health ah
            ON ah.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_planning apl
            ON apl.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_greenspace ag
            ON ag.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_demographics ad
            ON ad.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_deprivation dep
            ON dep.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_economy eco
            ON eco.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_connectivity conn
            ON conn.geo_area_id = ga.geo_area_id
        WHERE ao.opportunity_grade = 'PRIME'
        AND (? IS NULL OR LOWER(ll.ladnm) LIKE LOWER(?))
        ORDER BY ao.opportunity_score DESC
        LIMIT ?
        """),

    FLOOD_RISK_BY_LOCATION("""
        SELECT
            ga.external_code                        AS lsoa21cd,
            ga.label,
            NULL                                    AS postcode,
            ais.investment_score,
            ais.investment_grade,
            ars.risk_score,
            ars.risk_rating,
            ao.opportunity_score,
            ao.opportunity_grade,
            at2.median_price_12m                    AS median_price,
            at2.price_cv_12m                        AS price_cv,
            at2.transaction_count_12m,
            at2.median_price_24m,
            at2.new_build_pct,
            avm.predicted_median_price,
            avm.value_signal,
            avm.value_gap_pct,
            af.combined_flood_category,
            af.flood_risk_score,
            af.rofrs_high_pct,
            af.rofsw_high_pct,
            ac.crime_rate_per_1000                  AS total_rate_per_1000,
            ac.crime_trend                          AS trend,
            ac.crime_percentile                     AS percentile_rank,
            ae.pct_abc,
            ae.pct_fg,
            ae.avg_efficiency,
            ae.energy_efficiency_category,
            atr.bus_stop_count,
            atr.nearest_rail_station_m,
            atr.transport_connectivity_score,
            asc2.total_schools                      AS school_count,
            asc2.pct_outstanding                    AS avg_ofsted_score,
            ah.gp_surgery_count,
            ah.nearest_gp_surgery_m,
            apl.in_green_belt,
            apl.green_belt_pct,
            apl.listed_building_count,
            apl.ancient_woodland_any,
            ag.total_greenspace_pct,
            ag.greenspace_score,
            ad.total_population,
            ad.pct_working_age                      AS working_age_pct,
            ad.pct_owned                            AS owner_occupied_pct,
            dep.imd_decile,
            eco.claimant_rate,
            eco.job_density,
            eco.claimant_count,
            conn.connectivity_category,
            conn.avg_pct_sfbb                       AS superfast_pct,
            ao.is_regeneration_play                 AS regeneration_candidate,
            ao.is_family_market                     AS family_market,
            CASE WHEN avm.value_signal = 'UNDERVALUED'
                 THEN true ELSE false END           AS undervalued,
            ars.digital_exclusion_flag              AS digital_exclusion
        FROM core.geo_area ga
        JOIN ref.lsoa_to_laua ll
            ON ll.lsoa21cd = ga.external_code
        LEFT JOIN ana.area_investment_score ais
            ON ais.geo_area_id = ga.geo_area_id
        LEFT JOIN ana.area_risk_score ars
            ON ars.geo_area_id = ga.geo_area_id
        LEFT JOIN ana.area_opportunity ao
            ON ao.geo_area_id = ga.geo_area_id
        LEFT JOIN ana.area_avm avm
            ON avm.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_transactions at2
            ON at2.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_flood af
            ON af.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_crime ac
            ON ac.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_energy ae
            ON ae.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_transport atr
            ON atr.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_schools asc2
            ON asc2.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_health ah
            ON ah.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_planning apl
            ON apl.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_greenspace ag
            ON ag.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_demographics ad
            ON ad.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_deprivation dep
            ON dep.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_economy eco
            ON eco.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_connectivity conn
            ON conn.geo_area_id = ga.geo_area_id
        WHERE af.combined_flood_category = ?
        AND LOWER(ll.ladnm) LIKE LOWER(?)
        ORDER BY af.flood_risk_score DESC
        LIMIT 20
        """),

    POOR_EPC_BY_LOCATION("""
        SELECT
            ga.external_code                        AS lsoa21cd,
            ga.label,
            NULL                                    AS postcode,
            ais.investment_score,
            ais.investment_grade,
            ars.risk_score,
            ars.risk_rating,
            ao.opportunity_score,
            ao.opportunity_grade,
            at2.median_price_12m                    AS median_price,
            at2.price_cv_12m                        AS price_cv,
            at2.transaction_count_12m,
            at2.median_price_24m,
            at2.new_build_pct,
            avm.predicted_median_price,
            avm.value_signal,
            avm.value_gap_pct,
            af.combined_flood_category,
            af.flood_risk_score,
            af.rofrs_high_pct,
            af.rofsw_high_pct,
            ac.crime_rate_per_1000                  AS total_rate_per_1000,
            ac.crime_trend                          AS trend,
            ac.crime_percentile                     AS percentile_rank,
            ae.pct_abc,
            ae.pct_fg,
            ae.avg_efficiency,
            ae.energy_efficiency_category,
            atr.bus_stop_count,
            atr.nearest_rail_station_m,
            atr.transport_connectivity_score,
            asc2.total_schools                      AS school_count,
            asc2.pct_outstanding                    AS avg_ofsted_score,
            ah.gp_surgery_count,
            ah.nearest_gp_surgery_m,
            apl.in_green_belt,
            apl.green_belt_pct,
            apl.listed_building_count,
            apl.ancient_woodland_any,
            ag.total_greenspace_pct,
            ag.greenspace_score,
            ad.total_population,
            ad.pct_working_age                      AS working_age_pct,
            ad.pct_owned                            AS owner_occupied_pct,
            dep.imd_decile,
            eco.claimant_rate,
            eco.job_density,
            eco.claimant_count,
            conn.connectivity_category,
            conn.avg_pct_sfbb                       AS superfast_pct,
            ao.is_regeneration_play                 AS regeneration_candidate,
            ao.is_family_market                     AS family_market,
            CASE WHEN avm.value_signal = 'UNDERVALUED'
                 THEN true ELSE false END           AS undervalued,
            ars.digital_exclusion_flag              AS digital_exclusion
        FROM core.geo_area ga
        JOIN ref.lsoa_to_laua ll
            ON ll.lsoa21cd = ga.external_code
        LEFT JOIN ana.area_investment_score ais
            ON ais.geo_area_id = ga.geo_area_id
        LEFT JOIN ana.area_risk_score ars
            ON ars.geo_area_id = ga.geo_area_id
        LEFT JOIN ana.area_opportunity ao
            ON ao.geo_area_id = ga.geo_area_id
        LEFT JOIN ana.area_avm avm
            ON avm.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_transactions at2
            ON at2.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_flood af
            ON af.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_crime ac
            ON ac.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_energy ae
            ON ae.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_transport atr
            ON atr.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_schools asc2
            ON asc2.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_health ah
            ON ah.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_planning apl
            ON apl.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_greenspace ag
            ON ag.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_demographics ad
            ON ad.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_deprivation dep
            ON dep.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_economy eco
            ON eco.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_connectivity conn
            ON conn.geo_area_id = ga.geo_area_id
        WHERE ae.pct_abc <= ?
        AND LOWER(ll.ladnm) LIKE LOWER(?)
        ORDER BY ae.pct_abc ASC
        LIMIT 20
        """),

    CRIME_TREND_BY_LOCATION("""
        SELECT
            ga.external_code                        AS lsoa21cd,
            ga.label,
            NULL                                    AS postcode,
            ais.investment_score,
            ais.investment_grade,
            ars.risk_score,
            ars.risk_rating,
            ao.opportunity_score,
            ao.opportunity_grade,
            at2.median_price_12m                    AS median_price,
            at2.price_cv_12m                        AS price_cv,
            at2.transaction_count_12m,
            at2.median_price_24m,
            at2.new_build_pct,
            avm.predicted_median_price,
            avm.value_signal,
            avm.value_gap_pct,
            af.combined_flood_category,
            af.flood_risk_score,
            af.rofrs_high_pct,
            af.rofsw_high_pct,
            ac.crime_rate_per_1000                  AS total_rate_per_1000,
            ac.crime_trend                          AS trend,
            ac.crime_percentile                     AS percentile_rank,
            ae.pct_abc,
            ae.pct_fg,
            ae.avg_efficiency,
            ae.energy_efficiency_category,
            atr.bus_stop_count,
            atr.nearest_rail_station_m,
            atr.transport_connectivity_score,
            asc2.total_schools                      AS school_count,
            asc2.pct_outstanding                    AS avg_ofsted_score,
            ah.gp_surgery_count,
            ah.nearest_gp_surgery_m,
            apl.in_green_belt,
            apl.green_belt_pct,
            apl.listed_building_count,
            apl.ancient_woodland_any,
            ag.total_greenspace_pct,
            ag.greenspace_score,
            ad.total_population,
            ad.pct_working_age                      AS working_age_pct,
            ad.pct_owned                            AS owner_occupied_pct,
            dep.imd_decile,
            eco.claimant_rate,
            eco.job_density,
            eco.claimant_count,
            conn.connectivity_category,
            conn.avg_pct_sfbb                       AS superfast_pct,
            ao.is_regeneration_play                 AS regeneration_candidate,
            ao.is_family_market                     AS family_market,
            CASE WHEN avm.value_signal = 'UNDERVALUED'
                 THEN true ELSE false END           AS undervalued,
            ars.digital_exclusion_flag              AS digital_exclusion
        FROM core.geo_area ga
        JOIN ref.lsoa_to_laua ll
            ON ll.lsoa21cd = ga.external_code
        LEFT JOIN ana.area_investment_score ais
            ON ais.geo_area_id = ga.geo_area_id
        LEFT JOIN ana.area_risk_score ars
            ON ars.geo_area_id = ga.geo_area_id
        LEFT JOIN ana.area_opportunity ao
            ON ao.geo_area_id = ga.geo_area_id
        LEFT JOIN ana.area_avm avm
            ON avm.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_transactions at2
            ON at2.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_flood af
            ON af.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_crime ac
            ON ac.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_energy ae
            ON ae.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_transport atr
            ON atr.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_schools asc2
            ON asc2.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_health ah
            ON ah.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_planning apl
            ON apl.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_greenspace ag
            ON ag.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_demographics ad
            ON ad.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_deprivation dep
            ON dep.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_economy eco
            ON eco.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_connectivity conn
            ON conn.geo_area_id = ga.geo_area_id
        WHERE ac.crime_trend = ?
        AND LOWER(ll.ladnm) LIKE LOWER(?)
        ORDER BY ac.crime_rate_per_1000 DESC
        LIMIT 20
        """),

    MARKET_SUMMARY_BY_LOCATION("""
        SELECT
            COUNT(*)                                AS lsoa_count,
            ROUND(AVG(ais.investment_score), 2)     AS avg_investment_score,
            ROUND(AVG(at2.median_price_12m), 0)     AS avg_median_price,
            ROUND(AVG(at2.price_cv_12m), 2)         AS avg_price_growth_1yr,
            COUNT(*) FILTER (WHERE ao.opportunity_grade = 'PRIME')   AS prime_count,
            COUNT(*) FILTER (WHERE ao.opportunity_grade = 'STRONG')  AS strong_count,
            COUNT(*) FILTER (WHERE ars.risk_rating = 'HIGH'
                OR ars.risk_rating = 'VERY HIGH')                    AS high_risk_count
        FROM core.geo_area ga
        JOIN ref.lsoa_to_laua ll
            ON ll.lsoa21cd = ga.external_code
        LEFT JOIN ana.area_investment_score ais
            ON ais.geo_area_id = ga.geo_area_id
        LEFT JOIN ana.area_risk_score ars
            ON ars.geo_area_id = ga.geo_area_id
        LEFT JOIN ana.area_opportunity ao
            ON ao.geo_area_id = ga.geo_area_id
        LEFT JOIN feat.area_transactions at2
            ON at2.geo_area_id = ga.geo_area_id
        WHERE LOWER(ll.ladnm) LIKE LOWER(?)
        """);

    private final String query;

    PropOSQuery(final String query) {
        this.query = query;
    }

    public String query() {
        return query;
    }
}