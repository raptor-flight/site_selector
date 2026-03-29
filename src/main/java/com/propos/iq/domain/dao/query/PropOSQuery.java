package com.propos.iq.domain.dao.query;

public enum PropOSQuery {
    PPD_HISTORY_BY_FULL_POSTCODE("""
        SELECT
            lp.lsoa_code,
            lp.transaction_count,
            lp.median_price,
            lp.avg_price,
            lp.min_price,
            lp.max_price,
            lp.detached_count,
            lp.semi_count,
            lp.terraced_count,
            lp.flat_count,
            lp.new_build_count,
            lp.median_price_5yr,
            lp.median_price_1yr,
            lp.transactions_1yr,
            lp.price_growth_5yr_pct
        FROM feat.lsoa_ppd lp
        JOIN ref.postcode_to_lsoa p
            ON p.lsoa21cd = lp.lsoa_code
        WHERE p.postcode_norm = ?
        LIMIT 1
        """),

    PPD_HISTORY_BY_DISTRICT("""
        SELECT
            SUM(lp.transaction_count)                                           AS transaction_count,
            ROUND(AVG(lp.median_price))                                         AS median_price,
            ROUND(AVG(lp.avg_price))                                            AS avg_price,
            MIN(lp.min_price)                                                   AS min_price,
            MAX(lp.max_price)                                                   AS max_price,
            SUM(lp.detached_count)                                              AS detached_count,
            SUM(lp.semi_count)                                                  AS semi_count,
            SUM(lp.terraced_count)                                              AS terraced_count,
            SUM(lp.flat_count)                                                  AS flat_count,
            SUM(lp.new_build_count)                                             AS new_build_count,
            ROUND(AVG(lp.median_price_5yr))                                     AS median_price_5yr,
            ROUND(AVG(lp.median_price_1yr))                                     AS median_price_1yr,
            SUM(lp.transactions_1yr)                                            AS transactions_1yr,
            ROUND(AVG(lp.price_growth_5yr_pct))                                 AS price_growth_5yr_pct,
            COUNT(DISTINCT lp.lsoa_code)                                        AS lsoa_count
        FROM feat.lsoa_ppd lp
        WHERE lp.lsoa_code IN (
            SELECT DISTINCT p.lsoa21cd
            FROM ref.postcode_to_lsoa p
            WHERE p.postcode_norm LIKE ?
        )
        """),

    PPD_HISTORY_BY_LSOA("""
        SELECT
            lp.lsoa_code,
            lp.transaction_count,
            lp.median_price,
            lp.avg_price,
            lp.min_price,
            lp.max_price,
            lp.detached_count,
            lp.semi_count,
            lp.terraced_count,
            lp.flat_count,
            lp.new_build_count,
            lp.median_price_5yr,
            lp.median_price_1yr,
            lp.transactions_1yr,
            lp.price_growth_5yr_pct
        FROM feat.lsoa_ppd lp
        WHERE lp.lsoa_code = ?
        LIMIT 1
        """),
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
            -- Market
            at2.median_price_12m                    AS median_price,
            at2.price_cv_12m                        AS price_cv,
            at2.transaction_count_12m,
            at2.transaction_count_24m,
            at2.median_price_24m,
            at2.new_build_pct,
            at2.freehold_pct,
            at2.median_price_detached,
            at2.median_price_semi,
            at2.median_price_terraced,
            at2.median_price_flat,
            CASE
                WHEN at2.median_price_24m IS NULL OR at2.median_price_12m IS NULL THEN 'UNKNOWN'
                WHEN at2.median_price_12m > at2.median_price_24m * 1.03 THEN 'RISING'
                WHEN at2.median_price_12m < at2.median_price_24m * 0.97 THEN 'FALLING'
                ELSE 'STABLE'
            END                                     AS price_trend,
            avm.predicted_median_price,
            avm.value_signal,
            avm.value_gap_pct,
            -- Flood
            af.combined_flood_category,
            af.flood_risk_score,
            af.rofrs_high_pct,
            af.rofrs_medium_pct,
            af.rofrs_low_pct,
            af.rofrs_category,
            af.rofsw_high_pct,
            af.rofsw_medium_pct,
            af.rofsw_low_pct,
            af.rofsw_category,
            -- Crime
            ac.crime_rate_per_1000                  AS total_rate_per_1000,
            ac.crime_trend                          AS trend,
            ac.crime_percentile                     AS percentile_rank,
            ac.trend_pct_change,
            ac.violence_rate,
            ac.burglary_rate,
            ac.asb_rate,
            ac.vehicle_crime_rate,
            ac.drugs_rate,
            -- Energy
            ae.pct_abc,
            ae.pct_fg,
            ROUND(100.0 - COALESCE(ae.pct_abc, 0) - COALESCE(ae.pct_fg, 0), 2)  AS pct_d,
            ae.avg_efficiency,
            ae.energy_efficiency_category,
            ae.pct_mains_gas,
            ae.avg_floor_area_m2,
            ae.pct_pre_1919,
            ae.pct_1919_to_1944,
            ae.pct_1945_to_1964,
            ae.pct_1965_to_1982,
            ae.pct_1983_to_1995,
            ae.pct_post_1995,
            -- Transport
            atr.bus_stop_count,
            atr.nearest_bus_stop_m,
            atr.bus_stop_density_per_km2,
            atr.rail_station_count,
            atr.nearest_rail_station_m,
            atr.metro_stop_count,
            atr.ev_charger_count,
            atr.ev_chargers_per_100k,
            atr.total_collisions,
            atr.fatal_collisions,
            atr.serious_collisions,
            atr.collision_rate_per_1000,
            atr.pct_fatal_or_serious,
            atr.road_safety_score,
            -- Schools
            asc2.total_schools                      AS school_count,
            asc2.primary_schools,
            asc2.secondary_schools,
            asc2.post16_schools,
            asc2.pct_outstanding,
            asc2.pct_good,
            asc2.pct_requires_improvement,
            asc2.pct_inadequate,
            asc2.avg_att8_score,
            asc2.avg_progress8_score,
            asc2.avg_pct_rwm_expected,
            asc2.avg_pct_fsm,
            asc2.avg_overall_absence,
            asc2.avg_persistent_absence,
            asc2.ofsted_trend,
            -- Health
            ah.gp_surgery_count,
            ah.nearest_gp_surgery_m,
            ah.gp_surgeries_per_1000,
            ah.gp_access_score,
            -- Planning
            apl.in_green_belt,
            apl.green_belt_pct,
            apl.green_belt_name,
            apl.listed_building_count,
            apl.grade_i_count,
            apl.grade_ii_star_count,
            apl.grade_ii_count,
            apl.ancient_woodland_pct,
            apl.ancient_woodland_any,
            apl.planning_constraint_score,
            -- Greenspace
            ag.total_greenspace_pct,
            ag.park_garden_pct,
            ag.playing_field_pct,
            ag.play_space_count,
            ag.allotment_count,
            ag.greenspace_score,
            -- Demographics
            ad.total_population,
            ad.pct_working_age                      AS working_age_pct,
            ad.pct_under_16,
            ad.pct_over_65,
            ad.pct_owned                            AS owner_occupied_pct,
            ad.pct_social_rented,
            ad.pct_private_rented,
            ad.pct_detached,
            ad.pct_flat,
            ad.pct_white_british,
            ad.pct_non_white,
            ad.asian_total                          AS pct_asian,
            ad.black_total                          AS pct_black,
            ad.mixed_total                          AS pct_mixed,
            ad.simpsons_diversity_index,
            ad.pct_employed,
            ad.pct_unemployed,
            ad.pct_inactive,
            -- Deprivation
            dep.imd_score,
            dep.imd_rank,
            dep.imd_decile,
            dep.income_score,
            dep.employment_score,
            dep.education_score,
            dep.health_score,
            dep.crime_score,
            dep.housing_score,
            dep.environment_score,
            dep.dependent_children,
            dep.older_population,
            -- Economy
            eco.total_businesses,
            eco.businesses_per_1000_residents,
            eco.construction_businesses,
            eco.retail_businesses,
            eco.property_businesses,
            eco.health_businesses,
            eco.professional_businesses,
            eco.accommodation_food_businesses,
            eco.financial_businesses,
            eco.job_density,
            eco.claimant_count,
            eco.claimant_rate,
            eco.economy_type,
            -- Connectivity
            conn.connectivity_category,
            conn.avg_pct_sfbb                       AS superfast_pct,
            conn.avg_pct_gigabit                    AS full_fibre_pct,
            conn.avg_pct_ufbb                       AS ultrafast_pct,
            conn.avg_pct_below_uso                  AS below_uso_pct,
            conn.mobile_4g5g_prem_any,
            conn.mobile_4g5g_prem_all,
            conn.mobile_4g5g_geo_any,
            -- POI Amenities
            poi.gp_count                            AS poi_gp_count,
            poi.pharmacy_count                      AS poi_pharmacy_count,
            poi.hospital_count                      AS poi_hospital_count,
            poi.dentist_count                       AS poi_dentist_count,
            poi.urgent_care_count                   AS poi_urgent_care_count,
            poi.supermarket_count                   AS poi_supermarket_count,
            poi.convenience_store_count             AS poi_convenience_store_count,
            poi.petrol_station_count                AS poi_petrol_station_count,
            poi.fast_food_count                     AS poi_fast_food_count,
            poi.bank_count                          AS poi_bank_count,
            poi.atm_count                           AS poi_atm_count,
            poi.gym_count                           AS poi_gym_count,
            poi.pub_count                           AS poi_pub_count,
            poi.park_count                          AS poi_park_count,
            poi.library_count                       AS poi_library_count,
            poi.community_centre_count              AS poi_community_centre_count,
            poi.school_count                        AS poi_school_count,
            -- Flags
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
        LEFT JOIN feat.area_poi poi
            ON poi.geo_area_id = ga.geo_area_id
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
            -- Market
            at2.median_price_12m                    AS median_price,
            at2.price_cv_12m                        AS price_cv,
            at2.transaction_count_12m,
            at2.transaction_count_24m,
            at2.median_price_24m,
            at2.new_build_pct,
            at2.freehold_pct,
            at2.median_price_detached,
            at2.median_price_semi,
            at2.median_price_terraced,
            at2.median_price_flat,
            CASE
                WHEN at2.median_price_24m IS NULL OR at2.median_price_12m IS NULL THEN 'UNKNOWN'
                WHEN at2.median_price_12m > at2.median_price_24m * 1.03 THEN 'RISING'
                WHEN at2.median_price_12m < at2.median_price_24m * 0.97 THEN 'FALLING'
                ELSE 'STABLE'
            END                                     AS price_trend,
            avm.predicted_median_price,
            avm.value_signal,
            avm.value_gap_pct,
            -- Flood
            af.combined_flood_category,
            af.flood_risk_score,
            af.rofrs_high_pct,
            af.rofrs_medium_pct,
            af.rofrs_low_pct,
            af.rofrs_category,
            af.rofsw_high_pct,
            af.rofsw_medium_pct,
            af.rofsw_low_pct,
            af.rofsw_category,
            -- Crime
            ac.crime_rate_per_1000                  AS total_rate_per_1000,
            ac.crime_trend                          AS trend,
            ac.crime_percentile                     AS percentile_rank,
            ac.trend_pct_change,
            ac.violence_rate,
            ac.burglary_rate,
            ac.asb_rate,
            ac.vehicle_crime_rate,
            ac.drugs_rate,
            -- Energy
            ae.pct_abc,
            ae.pct_fg,
            ROUND(100.0 - COALESCE(ae.pct_abc, 0) - COALESCE(ae.pct_fg, 0), 2)  AS pct_d,
            ae.avg_efficiency,
            ae.energy_efficiency_category,
            ae.pct_mains_gas,
            ae.avg_floor_area_m2,
            ae.pct_pre_1919,
            ae.pct_1919_to_1944,
            ae.pct_1945_to_1964,
            ae.pct_1965_to_1982,
            ae.pct_1983_to_1995,
            ae.pct_post_1995,
            -- Transport
            atr.bus_stop_count,
            atr.nearest_bus_stop_m,
            atr.bus_stop_density_per_km2,
            atr.rail_station_count,
            atr.nearest_rail_station_m,
            atr.metro_stop_count,
            atr.ev_charger_count,
            atr.ev_chargers_per_100k,
            atr.total_collisions,
            atr.fatal_collisions,
            atr.serious_collisions,
            atr.collision_rate_per_1000,
            atr.pct_fatal_or_serious,
            atr.road_safety_score,
            -- Schools
            asc2.total_schools                      AS school_count,
            asc2.primary_schools,
            asc2.secondary_schools,
            asc2.post16_schools,
            asc2.pct_outstanding,
            asc2.pct_good,
            asc2.pct_requires_improvement,
            asc2.pct_inadequate,
            asc2.avg_att8_score,
            asc2.avg_progress8_score,
            asc2.avg_pct_rwm_expected,
            asc2.avg_pct_fsm,
            asc2.avg_overall_absence,
            asc2.avg_persistent_absence,
            asc2.ofsted_trend,
            -- Health
            ah.gp_surgery_count,
            ah.nearest_gp_surgery_m,
            ah.gp_surgeries_per_1000,
            ah.gp_access_score,
            -- Planning
            apl.in_green_belt,
            apl.green_belt_pct,
            apl.green_belt_name,
            apl.listed_building_count,
            apl.grade_i_count,
            apl.grade_ii_star_count,
            apl.grade_ii_count,
            apl.ancient_woodland_pct,
            apl.ancient_woodland_any,
            apl.planning_constraint_score,
            -- Greenspace
            ag.total_greenspace_pct,
            ag.park_garden_pct,
            ag.playing_field_pct,
            ag.play_space_count,
            ag.allotment_count,
            ag.greenspace_score,
            -- Demographics
            ad.total_population,
            ad.pct_working_age                      AS working_age_pct,
            ad.pct_under_16,
            ad.pct_over_65,
            ad.pct_owned                            AS owner_occupied_pct,
            ad.pct_social_rented,
            ad.pct_private_rented,
            ad.pct_detached,
            ad.pct_flat,
            ad.pct_white_british,
            ad.pct_non_white,
            ad.asian_total                          AS pct_asian,
            ad.black_total                          AS pct_black,
            ad.mixed_total                          AS pct_mixed,
            ad.simpsons_diversity_index,
            ad.pct_employed,
            ad.pct_unemployed,
            ad.pct_inactive,
            -- Deprivation
            dep.imd_score,
            dep.imd_rank,
            dep.imd_decile,
            dep.income_score,
            dep.employment_score,
            dep.education_score,
            dep.health_score,
            dep.crime_score,
            dep.housing_score,
            dep.environment_score,
            dep.dependent_children,
            dep.older_population,
            -- Economy
            eco.total_businesses,
            eco.businesses_per_1000_residents,
            eco.construction_businesses,
            eco.retail_businesses,
            eco.property_businesses,
            eco.health_businesses,
            eco.professional_businesses,
            eco.accommodation_food_businesses,
            eco.financial_businesses,
            eco.job_density,
            eco.claimant_count,
            eco.claimant_rate,
            eco.economy_type,
            -- Connectivity
            conn.connectivity_category,
            conn.avg_pct_sfbb                       AS superfast_pct,
            conn.avg_pct_gigabit                    AS full_fibre_pct,
            conn.avg_pct_ufbb                       AS ultrafast_pct,
            conn.avg_pct_below_uso                  AS below_uso_pct,
            conn.mobile_4g5g_prem_any,
            conn.mobile_4g5g_prem_all,
            conn.mobile_4g5g_geo_any,
            -- POI Amenities
            poi.gp_count                            AS poi_gp_count,
            poi.pharmacy_count                      AS poi_pharmacy_count,
            poi.hospital_count                      AS poi_hospital_count,
            poi.dentist_count                       AS poi_dentist_count,
            poi.urgent_care_count                   AS poi_urgent_care_count,
            poi.supermarket_count                   AS poi_supermarket_count,
            poi.convenience_store_count             AS poi_convenience_store_count,
            poi.petrol_station_count                AS poi_petrol_station_count,
            poi.fast_food_count                     AS poi_fast_food_count,
            poi.bank_count                          AS poi_bank_count,
            poi.atm_count                           AS poi_atm_count,
            poi.gym_count                           AS poi_gym_count,
            poi.pub_count                           AS poi_pub_count,
            poi.park_count                          AS poi_park_count,
            poi.library_count                       AS poi_library_count,
            poi.community_centre_count              AS poi_community_centre_count,
            poi.school_count                        AS poi_school_count,
            -- Flags
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
        LEFT JOIN feat.area_poi poi
            ON poi.geo_area_id = ga.geo_area_id
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
            -- Market
            at2.median_price_12m                    AS median_price,
            at2.price_cv_12m                        AS price_cv,
            at2.transaction_count_12m,
            at2.transaction_count_24m,
            at2.median_price_24m,
            at2.new_build_pct,
            at2.freehold_pct,
            at2.median_price_detached,
            at2.median_price_semi,
            at2.median_price_terraced,
            at2.median_price_flat,
            CASE
                WHEN at2.median_price_24m IS NULL OR at2.median_price_12m IS NULL THEN 'UNKNOWN'
                WHEN at2.median_price_12m > at2.median_price_24m * 1.03 THEN 'RISING'
                WHEN at2.median_price_12m < at2.median_price_24m * 0.97 THEN 'FALLING'
                ELSE 'STABLE'
            END                                     AS price_trend,
            avm.predicted_median_price,
            avm.value_signal,
            avm.value_gap_pct,
            -- Flood
            af.combined_flood_category,
            af.flood_risk_score,
            af.rofrs_high_pct,
            af.rofrs_medium_pct,
            af.rofrs_low_pct,
            af.rofrs_category,
            af.rofsw_high_pct,
            af.rofsw_medium_pct,
            af.rofsw_low_pct,
            af.rofsw_category,
            -- Crime
            ac.crime_rate_per_1000                  AS total_rate_per_1000,
            ac.crime_trend                          AS trend,
            ac.crime_percentile                     AS percentile_rank,
            ac.trend_pct_change,
            ac.violence_rate,
            ac.burglary_rate,
            ac.asb_rate,
            ac.vehicle_crime_rate,
            ac.drugs_rate,
            -- Energy
            ae.pct_abc,
            ae.pct_fg,
            ROUND(100.0 - COALESCE(ae.pct_abc, 0) - COALESCE(ae.pct_fg, 0), 2)  AS pct_d,
            ae.avg_efficiency,
            ae.energy_efficiency_category,
            ae.pct_mains_gas,
            ae.avg_floor_area_m2,
            ae.pct_pre_1919,
            ae.pct_1919_to_1944,
            ae.pct_1945_to_1964,
            ae.pct_1965_to_1982,
            ae.pct_1983_to_1995,
            ae.pct_post_1995,
            -- Transport
            atr.bus_stop_count,
            atr.nearest_bus_stop_m,
            atr.bus_stop_density_per_km2,
            atr.rail_station_count,
            atr.nearest_rail_station_m,
            atr.metro_stop_count,
            atr.ev_charger_count,
            atr.ev_chargers_per_100k,
            atr.total_collisions,
            atr.fatal_collisions,
            atr.serious_collisions,
            atr.collision_rate_per_1000,
            atr.pct_fatal_or_serious,
            atr.road_safety_score,
            -- Schools
            asc2.total_schools                      AS school_count,
            asc2.primary_schools,
            asc2.secondary_schools,
            asc2.post16_schools,
            asc2.pct_outstanding,
            asc2.pct_good,
            asc2.pct_requires_improvement,
            asc2.pct_inadequate,
            asc2.avg_att8_score,
            asc2.avg_progress8_score,
            asc2.avg_pct_rwm_expected,
            asc2.avg_pct_fsm,
            asc2.avg_overall_absence,
            asc2.avg_persistent_absence,
            asc2.ofsted_trend,
            -- Health
            ah.gp_surgery_count,
            ah.nearest_gp_surgery_m,
            ah.gp_surgeries_per_1000,
            ah.gp_access_score,
            -- Planning
            apl.in_green_belt,
            apl.green_belt_pct,
            apl.green_belt_name,
            apl.listed_building_count,
            apl.grade_i_count,
            apl.grade_ii_star_count,
            apl.grade_ii_count,
            apl.ancient_woodland_pct,
            apl.ancient_woodland_any,
            apl.planning_constraint_score,
            -- Greenspace
            ag.total_greenspace_pct,
            ag.park_garden_pct,
            ag.playing_field_pct,
            ag.play_space_count,
            ag.allotment_count,
            ag.greenspace_score,
            -- Demographics
            ad.total_population,
            ad.pct_working_age                      AS working_age_pct,
            ad.pct_under_16,
            ad.pct_over_65,
            ad.pct_owned                            AS owner_occupied_pct,
            ad.pct_social_rented,
            ad.pct_private_rented,
            ad.pct_detached,
            ad.pct_flat,
            ad.pct_white_british,
            ad.pct_non_white,
            ad.asian_total                          AS pct_asian,
            ad.black_total                          AS pct_black,
            ad.mixed_total                          AS pct_mixed,
            ad.simpsons_diversity_index,
            ad.pct_employed,
            ad.pct_unemployed,
            ad.pct_inactive,
            -- Deprivation
            dep.imd_score,
            dep.imd_rank,
            dep.imd_decile,
            dep.income_score,
            dep.employment_score,
            dep.education_score,
            dep.health_score,
            dep.crime_score,
            dep.housing_score,
            dep.environment_score,
            dep.dependent_children,
            dep.older_population,
            -- Economy
            eco.total_businesses,
            eco.businesses_per_1000_residents,
            eco.construction_businesses,
            eco.retail_businesses,
            eco.property_businesses,
            eco.health_businesses,
            eco.professional_businesses,
            eco.accommodation_food_businesses,
            eco.financial_businesses,
            eco.job_density,
            eco.claimant_count,
            eco.claimant_rate,
            eco.economy_type,
            -- Connectivity
            conn.connectivity_category,
            conn.avg_pct_sfbb                       AS superfast_pct,
            conn.avg_pct_gigabit                    AS full_fibre_pct,
            conn.avg_pct_ufbb                       AS ultrafast_pct,
            conn.avg_pct_below_uso                  AS below_uso_pct,
            conn.mobile_4g5g_prem_any,
            conn.mobile_4g5g_prem_all,
            conn.mobile_4g5g_geo_any,
            -- POI Amenities
            poi.gp_count                            AS poi_gp_count,
            poi.pharmacy_count                      AS poi_pharmacy_count,
            poi.hospital_count                      AS poi_hospital_count,
            poi.dentist_count                       AS poi_dentist_count,
            poi.urgent_care_count                   AS poi_urgent_care_count,
            poi.supermarket_count                   AS poi_supermarket_count,
            poi.convenience_store_count             AS poi_convenience_store_count,
            poi.petrol_station_count                AS poi_petrol_station_count,
            poi.fast_food_count                     AS poi_fast_food_count,
            poi.bank_count                          AS poi_bank_count,
            poi.atm_count                           AS poi_atm_count,
            poi.gym_count                           AS poi_gym_count,
            poi.pub_count                           AS poi_pub_count,
            poi.park_count                          AS poi_park_count,
            poi.library_count                       AS poi_library_count,
            poi.community_centre_count              AS poi_community_centre_count,
            poi.school_count                        AS poi_school_count,
            -- Flags
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
        LEFT JOIN feat.area_poi poi
            ON poi.geo_area_id = ga.geo_area_id
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
            -- Market
            at2.median_price_12m                    AS median_price,
            at2.price_cv_12m                        AS price_cv,
            at2.transaction_count_12m,
            at2.transaction_count_24m,
            at2.median_price_24m,
            at2.new_build_pct,
            at2.freehold_pct,
            at2.median_price_detached,
            at2.median_price_semi,
            at2.median_price_terraced,
            at2.median_price_flat,
            CASE
                WHEN at2.median_price_24m IS NULL OR at2.median_price_12m IS NULL THEN 'UNKNOWN'
                WHEN at2.median_price_12m > at2.median_price_24m * 1.03 THEN 'RISING'
                WHEN at2.median_price_12m < at2.median_price_24m * 0.97 THEN 'FALLING'
                ELSE 'STABLE'
            END                                     AS price_trend,
            avm.predicted_median_price,
            avm.value_signal,
            avm.value_gap_pct,
            -- Flood
            af.combined_flood_category,
            af.flood_risk_score,
            af.rofrs_high_pct,
            af.rofrs_medium_pct,
            af.rofrs_low_pct,
            af.rofrs_category,
            af.rofsw_high_pct,
            af.rofsw_medium_pct,
            af.rofsw_low_pct,
            af.rofsw_category,
            -- Crime
            ac.crime_rate_per_1000                  AS total_rate_per_1000,
            ac.crime_trend                          AS trend,
            ac.crime_percentile                     AS percentile_rank,
            ac.trend_pct_change,
            ac.violence_rate,
            ac.burglary_rate,
            ac.asb_rate,
            ac.vehicle_crime_rate,
            ac.drugs_rate,
            -- Energy
            ae.pct_abc,
            ae.pct_fg,
            ROUND(100.0 - COALESCE(ae.pct_abc, 0) - COALESCE(ae.pct_fg, 0), 2)  AS pct_d,
            ae.avg_efficiency,
            ae.energy_efficiency_category,
            ae.pct_mains_gas,
            ae.avg_floor_area_m2,
            ae.pct_pre_1919,
            ae.pct_1919_to_1944,
            ae.pct_1945_to_1964,
            ae.pct_1965_to_1982,
            ae.pct_1983_to_1995,
            ae.pct_post_1995,
            -- Transport
            atr.bus_stop_count,
            atr.nearest_bus_stop_m,
            atr.bus_stop_density_per_km2,
            atr.rail_station_count,
            atr.nearest_rail_station_m,
            atr.metro_stop_count,
            atr.ev_charger_count,
            atr.ev_chargers_per_100k,
            atr.total_collisions,
            atr.fatal_collisions,
            atr.serious_collisions,
            atr.collision_rate_per_1000,
            atr.pct_fatal_or_serious,
            atr.road_safety_score,
            -- Schools
            asc2.total_schools                      AS school_count,
            asc2.primary_schools,
            asc2.secondary_schools,
            asc2.post16_schools,
            asc2.pct_outstanding,
            asc2.pct_good,
            asc2.pct_requires_improvement,
            asc2.pct_inadequate,
            asc2.avg_att8_score,
            asc2.avg_progress8_score,
            asc2.avg_pct_rwm_expected,
            asc2.avg_pct_fsm,
            asc2.avg_overall_absence,
            asc2.avg_persistent_absence,
            asc2.ofsted_trend,
            -- Health
            ah.gp_surgery_count,
            ah.nearest_gp_surgery_m,
            ah.gp_surgeries_per_1000,
            ah.gp_access_score,
            -- Planning
            apl.in_green_belt,
            apl.green_belt_pct,
            apl.green_belt_name,
            apl.listed_building_count,
            apl.grade_i_count,
            apl.grade_ii_star_count,
            apl.grade_ii_count,
            apl.ancient_woodland_pct,
            apl.ancient_woodland_any,
            apl.planning_constraint_score,
            -- Greenspace
            ag.total_greenspace_pct,
            ag.park_garden_pct,
            ag.playing_field_pct,
            ag.play_space_count,
            ag.allotment_count,
            ag.greenspace_score,
            -- Demographics
            ad.total_population,
            ad.pct_working_age                      AS working_age_pct,
            ad.pct_under_16,
            ad.pct_over_65,
            ad.pct_owned                            AS owner_occupied_pct,
            ad.pct_social_rented,
            ad.pct_private_rented,
            ad.pct_detached,
            ad.pct_flat,
            ad.pct_white_british,
            ad.pct_non_white,
            ad.asian_total                          AS pct_asian,
            ad.black_total                          AS pct_black,
            ad.mixed_total                          AS pct_mixed,
            ad.simpsons_diversity_index,
            ad.pct_employed,
            ad.pct_unemployed,
            ad.pct_inactive,
            -- Deprivation
            dep.imd_score,
            dep.imd_rank,
            dep.imd_decile,
            dep.income_score,
            dep.employment_score,
            dep.education_score,
            dep.health_score,
            dep.crime_score,
            dep.housing_score,
            dep.environment_score,
            dep.dependent_children,
            dep.older_population,
            -- Economy
            eco.total_businesses,
            eco.businesses_per_1000_residents,
            eco.construction_businesses,
            eco.retail_businesses,
            eco.property_businesses,
            eco.health_businesses,
            eco.professional_businesses,
            eco.accommodation_food_businesses,
            eco.financial_businesses,
            eco.job_density,
            eco.claimant_count,
            eco.claimant_rate,
            eco.economy_type,
            -- Connectivity
            conn.connectivity_category,
            conn.avg_pct_sfbb                       AS superfast_pct,
            conn.avg_pct_gigabit                    AS full_fibre_pct,
            conn.avg_pct_ufbb                       AS ultrafast_pct,
            conn.avg_pct_below_uso                  AS below_uso_pct,
            conn.mobile_4g5g_prem_any,
            conn.mobile_4g5g_prem_all,
            conn.mobile_4g5g_geo_any,
            -- POI Amenities
            poi.gp_count                            AS poi_gp_count,
            poi.pharmacy_count                      AS poi_pharmacy_count,
            poi.hospital_count                      AS poi_hospital_count,
            poi.dentist_count                       AS poi_dentist_count,
            poi.urgent_care_count                   AS poi_urgent_care_count,
            poi.supermarket_count                   AS poi_supermarket_count,
            poi.convenience_store_count             AS poi_convenience_store_count,
            poi.petrol_station_count                AS poi_petrol_station_count,
            poi.fast_food_count                     AS poi_fast_food_count,
            poi.bank_count                          AS poi_bank_count,
            poi.atm_count                           AS poi_atm_count,
            poi.gym_count                           AS poi_gym_count,
            poi.pub_count                           AS poi_pub_count,
            poi.park_count                          AS poi_park_count,
            poi.library_count                       AS poi_library_count,
            poi.community_centre_count              AS poi_community_centre_count,
            poi.school_count                        AS poi_school_count,
            -- Flags
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
        LEFT JOIN feat.area_poi poi
            ON poi.geo_area_id = ga.geo_area_id
        WHERE af.combined_flood_category = ?
        AND LOWER(ll.ladnm) LIKE LOWER(?)
        ORDER BY af.flood_risk_score DESC
        LIMIT 20
        """),

    OPPORTUNITIES_BY_POSTCODE_DISTRICT("""
    SELECT DISTINCT ON (ga.geo_area_id)

        ga.external_code                        AS lsoa21cd,
        ga.label,
        NULL                                    AS postcode,
        ais.investment_score,
        ais.investment_grade,
        ars.risk_score,
        ars.risk_rating,
        ao.opportunity_score,
        ao.opportunity_grade,
        -- Market
        at2.median_price_12m                    AS median_price,
        at2.price_cv_12m                        AS price_cv,
        at2.transaction_count_12m,
        at2.transaction_count_24m,
        at2.median_price_24m,
        at2.new_build_pct,
        at2.freehold_pct,
        at2.median_price_detached,
        at2.median_price_semi,
        at2.median_price_terraced,
        at2.median_price_flat,
        CASE
            WHEN at2.median_price_24m IS NULL OR at2.median_price_12m IS NULL THEN 'UNKNOWN'
            WHEN at2.median_price_12m > at2.median_price_24m * 1.03 THEN 'RISING'
            WHEN at2.median_price_12m < at2.median_price_24m * 0.97 THEN 'FALLING'
            ELSE 'STABLE'
        END                                     AS price_trend,
        avm.predicted_median_price,
        avm.value_signal,
        avm.value_gap_pct,
        -- Flood
        af.combined_flood_category,
        af.flood_risk_score,
        af.rofrs_high_pct,
        af.rofrs_medium_pct,
        af.rofrs_low_pct,
        af.rofrs_category,
        af.rofsw_high_pct,
        af.rofsw_medium_pct,
        af.rofsw_low_pct,
        af.rofsw_category,
        -- Crime
        ac.crime_rate_per_1000                  AS total_rate_per_1000,
        ac.crime_trend                          AS trend,
        ac.crime_percentile                     AS percentile_rank,
        ac.trend_pct_change,
        ac.violence_rate,
        ac.burglary_rate,
        ac.asb_rate,
        ac.vehicle_crime_rate,
        ac.drugs_rate,
        -- Energy
        ae.pct_abc,
        ae.pct_fg,
        ROUND(100.0 - COALESCE(ae.pct_abc, 0) - COALESCE(ae.pct_fg, 0), 2)  AS pct_d,
        ae.avg_efficiency,
        ae.energy_efficiency_category,
        ae.pct_mains_gas,
        ae.avg_floor_area_m2,
        ae.pct_pre_1919,
        ae.pct_1919_to_1944,
        ae.pct_1945_to_1964,
        ae.pct_1965_to_1982,
        ae.pct_1983_to_1995,
        ae.pct_post_1995,
        -- Transport
        atr.bus_stop_count,
        atr.nearest_bus_stop_m,
        atr.bus_stop_density_per_km2,
        atr.rail_station_count,
        atr.nearest_rail_station_m,
        atr.metro_stop_count,
        atr.ev_charger_count,
        atr.ev_chargers_per_100k,
        atr.total_collisions,
        atr.fatal_collisions,
        atr.serious_collisions,
        atr.collision_rate_per_1000,
        atr.pct_fatal_or_serious,
        atr.road_safety_score,
        -- Schools
        asc2.total_schools                      AS school_count,
        asc2.primary_schools,
        asc2.secondary_schools,
        asc2.post16_schools,
        asc2.pct_outstanding,
        asc2.pct_good,
        asc2.pct_requires_improvement,
        asc2.pct_inadequate,
        asc2.avg_att8_score,
        asc2.avg_progress8_score,
        asc2.avg_pct_rwm_expected,
        asc2.avg_pct_fsm,
        asc2.avg_overall_absence,
        asc2.avg_persistent_absence,
        asc2.ofsted_trend,
        -- Health
        ah.gp_surgery_count,
        ah.nearest_gp_surgery_m,
        ah.gp_surgeries_per_1000,
        ah.gp_access_score,
        -- Planning
        apl.in_green_belt,
        apl.green_belt_pct,
        apl.green_belt_name,
        apl.listed_building_count,
        apl.grade_i_count,
        apl.grade_ii_star_count,
        apl.grade_ii_count,
        apl.ancient_woodland_pct,
        apl.ancient_woodland_any,
        apl.planning_constraint_score,
        -- Greenspace
        ag.total_greenspace_pct,
        ag.park_garden_pct,
        ag.playing_field_pct,
        ag.play_space_count,
        ag.allotment_count,
        ag.greenspace_score,
        -- Demographics
        ad.total_population,
        ad.pct_working_age                      AS working_age_pct,
        ad.pct_under_16,
        ad.pct_over_65,
        ad.pct_owned                            AS owner_occupied_pct,
        ad.pct_social_rented,
        ad.pct_private_rented,
        ad.pct_detached,
        ad.pct_flat,
        ad.pct_white_british,
        ad.pct_non_white,
        ad.asian_total                          AS pct_asian,
        ad.black_total                          AS pct_black,
        ad.mixed_total                          AS pct_mixed,
        ad.simpsons_diversity_index,
        ad.pct_employed,
        ad.pct_unemployed,
        ad.pct_inactive,
        -- Deprivation
        dep.imd_score,
        dep.imd_rank,
        dep.imd_decile,
        dep.income_score,
        dep.employment_score,
        dep.education_score,
        dep.health_score,
        dep.crime_score,
        dep.housing_score,
        dep.environment_score,
        dep.dependent_children,
        dep.older_population,
        -- Economy
        eco.total_businesses,
        eco.businesses_per_1000_residents,
        eco.construction_businesses,
        eco.retail_businesses,
        eco.property_businesses,
        eco.health_businesses,
        eco.professional_businesses,
        eco.accommodation_food_businesses,
        eco.financial_businesses,
        eco.job_density,
        eco.claimant_count,
        eco.claimant_rate,
        eco.economy_type,
        -- Connectivity
        conn.connectivity_category,
        conn.avg_pct_sfbb                       AS superfast_pct,
        conn.avg_pct_gigabit                    AS full_fibre_pct,
        conn.avg_pct_ufbb                       AS ultrafast_pct,
        conn.avg_pct_below_uso                  AS below_uso_pct,
        conn.mobile_4g5g_prem_any,
        conn.mobile_4g5g_prem_all,
        conn.mobile_4g5g_geo_any,
        -- POI Amenities
        poi.gp_count                            AS poi_gp_count,
        poi.pharmacy_count                      AS poi_pharmacy_count,
        poi.hospital_count                      AS poi_hospital_count,
        poi.dentist_count                       AS poi_dentist_count,
        poi.urgent_care_count                   AS poi_urgent_care_count,
        poi.supermarket_count                   AS poi_supermarket_count,
        poi.convenience_store_count             AS poi_convenience_store_count,
        poi.petrol_station_count                AS poi_petrol_station_count,
        poi.fast_food_count                     AS poi_fast_food_count,
        poi.bank_count                          AS poi_bank_count,
        poi.atm_count                           AS poi_atm_count,
        poi.gym_count                           AS poi_gym_count,
        poi.pub_count                           AS poi_pub_count,
        poi.park_count                          AS poi_park_count,
        poi.library_count                       AS poi_library_count,
        poi.community_centre_count              AS poi_community_centre_count,
        poi.school_count                        AS poi_school_count,
        -- Flags
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
    LEFT JOIN feat.area_poi poi
        ON poi.geo_area_id = ga.geo_area_id
    WHERE p.postcode_norm LIKE ?
    AND ao.opportunity_score IS NOT NULL
    ORDER BY ga.geo_area_id, ao.opportunity_score DESC
    LIMIT 5
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
            -- Market
            at2.median_price_12m                    AS median_price,
            at2.price_cv_12m                        AS price_cv,
            at2.transaction_count_12m,
            at2.transaction_count_24m,
            at2.median_price_24m,
            at2.new_build_pct,
            at2.freehold_pct,
            at2.median_price_detached,
            at2.median_price_semi,
            at2.median_price_terraced,
            at2.median_price_flat,
            CASE
                WHEN at2.median_price_24m IS NULL OR at2.median_price_12m IS NULL THEN 'UNKNOWN'
                WHEN at2.median_price_12m > at2.median_price_24m * 1.03 THEN 'RISING'
                WHEN at2.median_price_12m < at2.median_price_24m * 0.97 THEN 'FALLING'
                ELSE 'STABLE'
            END                                     AS price_trend,
            avm.predicted_median_price,
            avm.value_signal,
            avm.value_gap_pct,
            -- Flood
            af.combined_flood_category,
            af.flood_risk_score,
            af.rofrs_high_pct,
            af.rofrs_medium_pct,
            af.rofrs_low_pct,
            af.rofrs_category,
            af.rofsw_high_pct,
            af.rofsw_medium_pct,
            af.rofsw_low_pct,
            af.rofsw_category,
            -- Crime
            ac.crime_rate_per_1000                  AS total_rate_per_1000,
            ac.crime_trend                          AS trend,
            ac.crime_percentile                     AS percentile_rank,
            ac.trend_pct_change,
            ac.violence_rate,
            ac.burglary_rate,
            ac.asb_rate,
            ac.vehicle_crime_rate,
            ac.drugs_rate,
            -- Energy
            ae.pct_abc,
            ae.pct_fg,
            ROUND(100.0 - COALESCE(ae.pct_abc, 0) - COALESCE(ae.pct_fg, 0), 2)  AS pct_d,
            ae.avg_efficiency,
            ae.energy_efficiency_category,
            ae.pct_mains_gas,
            ae.avg_floor_area_m2,
            ae.pct_pre_1919,
            ae.pct_1919_to_1944,
            ae.pct_1945_to_1964,
            ae.pct_1965_to_1982,
            ae.pct_1983_to_1995,
            ae.pct_post_1995,
            -- Transport
            atr.bus_stop_count,
            atr.nearest_bus_stop_m,
            atr.bus_stop_density_per_km2,
            atr.rail_station_count,
            atr.nearest_rail_station_m,
            atr.metro_stop_count,
            atr.ev_charger_count,
            atr.ev_chargers_per_100k,
            atr.total_collisions,
            atr.fatal_collisions,
            atr.serious_collisions,
            atr.collision_rate_per_1000,
            atr.pct_fatal_or_serious,
            atr.road_safety_score,
            -- Schools
            asc2.total_schools                      AS school_count,
            asc2.primary_schools,
            asc2.secondary_schools,
            asc2.post16_schools,
            asc2.pct_outstanding,
            asc2.pct_good,
            asc2.pct_requires_improvement,
            asc2.pct_inadequate,
            asc2.avg_att8_score,
            asc2.avg_progress8_score,
            asc2.avg_pct_rwm_expected,
            asc2.avg_pct_fsm,
            asc2.avg_overall_absence,
            asc2.avg_persistent_absence,
            asc2.ofsted_trend,
            -- Health
            ah.gp_surgery_count,
            ah.nearest_gp_surgery_m,
            ah.gp_surgeries_per_1000,
            ah.gp_access_score,
            -- Planning
            apl.in_green_belt,
            apl.green_belt_pct,
            apl.green_belt_name,
            apl.listed_building_count,
            apl.grade_i_count,
            apl.grade_ii_star_count,
            apl.grade_ii_count,
            apl.ancient_woodland_pct,
            apl.ancient_woodland_any,
            apl.planning_constraint_score,
            -- Greenspace
            ag.total_greenspace_pct,
            ag.park_garden_pct,
            ag.playing_field_pct,
            ag.play_space_count,
            ag.allotment_count,
            ag.greenspace_score,
            -- Demographics
            ad.total_population,
            ad.pct_working_age                      AS working_age_pct,
            ad.pct_under_16,
            ad.pct_over_65,
            ad.pct_owned                            AS owner_occupied_pct,
            ad.pct_social_rented,
            ad.pct_private_rented,
            ad.pct_detached,
            ad.pct_flat,
            ad.pct_white_british,
            ad.pct_non_white,
            ad.asian_total                          AS pct_asian,
            ad.black_total                          AS pct_black,
            ad.mixed_total                          AS pct_mixed,
            ad.simpsons_diversity_index,
            ad.pct_employed,
            ad.pct_unemployed,
            ad.pct_inactive,
            -- Deprivation
            dep.imd_score,
            dep.imd_rank,
            dep.imd_decile,
            dep.income_score,
            dep.employment_score,
            dep.education_score,
            dep.health_score,
            dep.crime_score,
            dep.housing_score,
            dep.environment_score,
            dep.dependent_children,
            dep.older_population,
            -- Economy
            eco.total_businesses,
            eco.businesses_per_1000_residents,
            eco.construction_businesses,
            eco.retail_businesses,
            eco.property_businesses,
            eco.health_businesses,
            eco.professional_businesses,
            eco.accommodation_food_businesses,
            eco.financial_businesses,
            eco.job_density,
            eco.claimant_count,
            eco.claimant_rate,
            eco.economy_type,
            -- Connectivity
            conn.connectivity_category,
            conn.avg_pct_sfbb                       AS superfast_pct,
            conn.avg_pct_gigabit                    AS full_fibre_pct,
            conn.avg_pct_ufbb                       AS ultrafast_pct,
            conn.avg_pct_below_uso                  AS below_uso_pct,
            conn.mobile_4g5g_prem_any,
            conn.mobile_4g5g_prem_all,
            conn.mobile_4g5g_geo_any,
            -- POI Amenities
            poi.gp_count                            AS poi_gp_count,
            poi.pharmacy_count                      AS poi_pharmacy_count,
            poi.hospital_count                      AS poi_hospital_count,
            poi.dentist_count                       AS poi_dentist_count,
            poi.urgent_care_count                   AS poi_urgent_care_count,
            poi.supermarket_count                   AS poi_supermarket_count,
            poi.convenience_store_count             AS poi_convenience_store_count,
            poi.petrol_station_count                AS poi_petrol_station_count,
            poi.fast_food_count                     AS poi_fast_food_count,
            poi.bank_count                          AS poi_bank_count,
            poi.atm_count                           AS poi_atm_count,
            poi.gym_count                           AS poi_gym_count,
            poi.pub_count                           AS poi_pub_count,
            poi.park_count                          AS poi_park_count,
            poi.library_count                       AS poi_library_count,
            poi.community_centre_count              AS poi_community_centre_count,
            poi.school_count                        AS poi_school_count,
            -- Flags
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
        LEFT JOIN feat.area_poi poi
            ON poi.geo_area_id = ga.geo_area_id
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
            -- Market
            at2.median_price_12m                    AS median_price,
            at2.price_cv_12m                        AS price_cv,
            at2.transaction_count_12m,
            at2.transaction_count_24m,
            at2.median_price_24m,
            at2.new_build_pct,
            at2.freehold_pct,
            at2.median_price_detached,
            at2.median_price_semi,
            at2.median_price_terraced,
            at2.median_price_flat,
            CASE
                WHEN at2.median_price_24m IS NULL OR at2.median_price_12m IS NULL THEN 'UNKNOWN'
                WHEN at2.median_price_12m > at2.median_price_24m * 1.03 THEN 'RISING'
                WHEN at2.median_price_12m < at2.median_price_24m * 0.97 THEN 'FALLING'
                ELSE 'STABLE'
            END                                     AS price_trend,
            avm.predicted_median_price,
            avm.value_signal,
            avm.value_gap_pct,
            -- Flood
            af.combined_flood_category,
            af.flood_risk_score,
            af.rofrs_high_pct,
            af.rofrs_medium_pct,
            af.rofrs_low_pct,
            af.rofrs_category,
            af.rofsw_high_pct,
            af.rofsw_medium_pct,
            af.rofsw_low_pct,
            af.rofsw_category,
            -- Crime
            ac.crime_rate_per_1000                  AS total_rate_per_1000,
            ac.crime_trend                          AS trend,
            ac.crime_percentile                     AS percentile_rank,
            ac.trend_pct_change,
            ac.violence_rate,
            ac.burglary_rate,
            ac.asb_rate,
            ac.vehicle_crime_rate,
            ac.drugs_rate,
            -- Energy
            ae.pct_abc,
            ae.pct_fg,
            ROUND(100.0 - COALESCE(ae.pct_abc, 0) - COALESCE(ae.pct_fg, 0), 2)  AS pct_d,
            ae.avg_efficiency,
            ae.energy_efficiency_category,
            ae.pct_mains_gas,
            ae.avg_floor_area_m2,
            ae.pct_pre_1919,
            ae.pct_1919_to_1944,
            ae.pct_1945_to_1964,
            ae.pct_1965_to_1982,
            ae.pct_1983_to_1995,
            ae.pct_post_1995,
            -- Transport
            atr.bus_stop_count,
            atr.nearest_bus_stop_m,
            atr.bus_stop_density_per_km2,
            atr.rail_station_count,
            atr.nearest_rail_station_m,
            atr.metro_stop_count,
            atr.ev_charger_count,
            atr.ev_chargers_per_100k,
            atr.total_collisions,
            atr.fatal_collisions,
            atr.serious_collisions,
            atr.collision_rate_per_1000,
            atr.pct_fatal_or_serious,
            atr.road_safety_score,
            -- Schools
            asc2.total_schools                      AS school_count,
            asc2.primary_schools,
            asc2.secondary_schools,
            asc2.post16_schools,
            asc2.pct_outstanding,
            asc2.pct_good,
            asc2.pct_requires_improvement,
            asc2.pct_inadequate,
            asc2.avg_att8_score,
            asc2.avg_progress8_score,
            asc2.avg_pct_rwm_expected,
            asc2.avg_pct_fsm,
            asc2.avg_overall_absence,
            asc2.avg_persistent_absence,
            asc2.ofsted_trend,
            -- Health
            ah.gp_surgery_count,
            ah.nearest_gp_surgery_m,
            ah.gp_surgeries_per_1000,
            ah.gp_access_score,
            -- Planning
            apl.in_green_belt,
            apl.green_belt_pct,
            apl.green_belt_name,
            apl.listed_building_count,
            apl.grade_i_count,
            apl.grade_ii_star_count,
            apl.grade_ii_count,
            apl.ancient_woodland_pct,
            apl.ancient_woodland_any,
            apl.planning_constraint_score,
            -- Greenspace
            ag.total_greenspace_pct,
            ag.park_garden_pct,
            ag.playing_field_pct,
            ag.play_space_count,
            ag.allotment_count,
            ag.greenspace_score,
            -- Demographics
            ad.total_population,
            ad.pct_working_age                      AS working_age_pct,
            ad.pct_under_16,
            ad.pct_over_65,
            ad.pct_owned                            AS owner_occupied_pct,
            ad.pct_social_rented,
            ad.pct_private_rented,
            ad.pct_detached,
            ad.pct_flat,
            ad.pct_white_british,
            ad.pct_non_white,
            ad.asian_total                          AS pct_asian,
            ad.black_total                          AS pct_black,
            ad.mixed_total                          AS pct_mixed,
            ad.simpsons_diversity_index,
            ad.pct_employed,
            ad.pct_unemployed,
            ad.pct_inactive,
            -- Deprivation
            dep.imd_score,
            dep.imd_rank,
            dep.imd_decile,
            dep.income_score,
            dep.employment_score,
            dep.education_score,
            dep.health_score,
            dep.crime_score,
            dep.housing_score,
            dep.environment_score,
            dep.dependent_children,
            dep.older_population,
            -- Economy
            eco.total_businesses,
            eco.businesses_per_1000_residents,
            eco.construction_businesses,
            eco.retail_businesses,
            eco.property_businesses,
            eco.health_businesses,
            eco.professional_businesses,
            eco.accommodation_food_businesses,
            eco.financial_businesses,
            eco.job_density,
            eco.claimant_count,
            eco.claimant_rate,
            eco.economy_type,
            -- Connectivity
            conn.connectivity_category,
            conn.avg_pct_sfbb                       AS superfast_pct,
            conn.avg_pct_gigabit                    AS full_fibre_pct,
            conn.avg_pct_ufbb                       AS ultrafast_pct,
            conn.avg_pct_below_uso                  AS below_uso_pct,
            conn.mobile_4g5g_prem_any,
            conn.mobile_4g5g_prem_all,
            conn.mobile_4g5g_geo_any,
            -- POI Amenities
            poi.gp_count                            AS poi_gp_count,
            poi.pharmacy_count                      AS poi_pharmacy_count,
            poi.hospital_count                      AS poi_hospital_count,
            poi.dentist_count                       AS poi_dentist_count,
            poi.urgent_care_count                   AS poi_urgent_care_count,
            poi.supermarket_count                   AS poi_supermarket_count,
            poi.convenience_store_count             AS poi_convenience_store_count,
            poi.petrol_station_count                AS poi_petrol_station_count,
            poi.fast_food_count                     AS poi_fast_food_count,
            poi.bank_count                          AS poi_bank_count,
            poi.atm_count                           AS poi_atm_count,
            poi.gym_count                           AS poi_gym_count,
            poi.pub_count                           AS poi_pub_count,
            poi.park_count                          AS poi_park_count,
            poi.library_count                       AS poi_library_count,
            poi.community_centre_count              AS poi_community_centre_count,
            poi.school_count                        AS poi_school_count,
            -- Flags
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
        LEFT JOIN feat.area_poi poi
            ON poi.geo_area_id = ga.geo_area_id
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
        """),

    AMENITY_NAMES_BY_LSOA("""
        WITH raw AS (
            SELECT
                CASE
                    WHEN main_category IN ('supermarket','grocery_store','superstore')   THEN 'SUPERMARKET'
                    WHEN main_category IN ('doctor','medical_center','family_practice',
                                           'community_health_clinic','walk_in_clinic')   THEN 'GP_SURGERY'
                    WHEN main_category = 'pharmacy'                                      THEN 'PHARMACY'
                    WHEN main_category = 'hospital'                                      THEN 'HOSPITAL'
                    WHEN main_category IN ('dentist','general_dentistry')                THEN 'DENTIST'
                    WHEN main_category = 'urgent_care_clinic'                            THEN 'URGENT_CARE'
                    WHEN main_category = 'fast_food_restaurant'                          THEN 'FAST_FOOD'
                    WHEN main_category = 'gas_station'                                   THEN 'PETROL'
                    WHEN main_category IN ('bank_credit_union','banks','credit_union')   THEN 'BANK'
                    WHEN main_category = 'gym'                                           THEN 'GYM'
                    WHEN main_category = 'pub'                                           THEN 'PUB'
                    WHEN main_category = 'library'                                       THEN 'LIBRARY'
                    WHEN main_category = 'community_center'                              THEN 'COMMUNITY_CENTRE'
                END                                                     AS poi_type,
                TRIM(LOWER(
                    REGEXP_REPLACE(
                        REGEXP_REPLACE(primary_name,
                            '\\\\s+(bank|building society|local|express|limited|ltd|plc)\\\\s*$',
                            '', 'gi'),
                        '[^a-z0-9\\\\s]', '', 'g')
                ))                                                      AS name_key,
                primary_name,
                address,
                postcode
            FROM staging.poi_raw
            WHERE lsoa21cd = ?
            AND primary_name IS NOT NULL
            AND primary_name NOT LIKE '%,%'
            AND LENGTH(TRIM(primary_name)) > 2
            AND LOWER(primary_name) NOT LIKE '%register%'
            AND LOWER(primary_name) NOT LIKE '%gaming%'
            AND LOWER(primary_name) NOT LIKE '%tabletop%'
            AND LOWER(primary_name) NOT LIKE '%antigen%'
            AND LOWER(primary_name) NOT LIKE '%travel%'
            AND LOWER(primary_name) NOT LIKE '%arts quarter%'
            AND LOWER(primary_name) NOT LIKE '%pudding%'
            AND LOWER(primary_name) NOT LIKE '%bar & grill%'
            AND LOWER(primary_name) NOT LIKE '%bar and grill%'
            AND LOWER(primary_name) NOT LIKE '%hull libraries%'
        ),
        deduplicated AS (
            SELECT DISTINCT ON (poi_type, name_key)
                poi_type, primary_name, address, postcode, name_key
            FROM raw
            WHERE poi_type IS NOT NULL
            ORDER BY poi_type, name_key, postcode NULLS LAST
        ),
        ranked AS (
            SELECT poi_type, primary_name, address, postcode,
                ROW_NUMBER() OVER (PARTITION BY poi_type ORDER BY primary_name) AS rn
            FROM deduplicated
        )
        SELECT poi_type, primary_name, address, postcode
        FROM ranked
        WHERE rn <= 10
        ORDER BY poi_type, primary_name
        """),

    AMENITY_NAMES_BY_DISTRICT("""
        WITH raw AS (
            SELECT
                CASE
                    WHEN main_category IN ('supermarket','grocery_store','superstore')   THEN 'SUPERMARKET'
                    WHEN main_category IN ('doctor','medical_center','family_practice',
                                           'community_health_clinic','walk_in_clinic')   THEN 'GP_SURGERY'
                    WHEN main_category = 'pharmacy'                                      THEN 'PHARMACY'
                    WHEN main_category = 'hospital'                                      THEN 'HOSPITAL'
                    WHEN main_category IN ('dentist','general_dentistry')                THEN 'DENTIST'
                    WHEN main_category = 'urgent_care_clinic'                            THEN 'URGENT_CARE'
                    WHEN main_category = 'fast_food_restaurant'                          THEN 'FAST_FOOD'
                    WHEN main_category = 'gas_station'                                   THEN 'PETROL'
                    WHEN main_category IN ('bank_credit_union','banks','credit_union')   THEN 'BANK'
                    WHEN main_category = 'gym'                                           THEN 'GYM'
                    WHEN main_category = 'pub'                                           THEN 'PUB'
                    WHEN main_category = 'library'                                       THEN 'LIBRARY'
                    WHEN main_category = 'community_center'                              THEN 'COMMUNITY_CENTRE'
                END                                                     AS poi_type,
                TRIM(LOWER(
                    REGEXP_REPLACE(
                        REGEXP_REPLACE(primary_name,
                            '\\\\s+(bank|building society|local|express|limited|ltd|plc)\\\\s*$',
                            '', 'gi'),
                        '[^a-z0-9\\\\s]', '', 'g')
                ))                                                      AS name_key,
                primary_name,
                address,
                postcode
            FROM staging.poi_raw
            WHERE lsoa21cd IN (
                SELECT ga.external_code
                FROM ref.postcode_to_lsoa p
                JOIN core.geo_area ga ON ga.geo_area_id = p.geo_area_id
                WHERE p.postcode_norm LIKE ?
            )
            AND primary_name IS NOT NULL
            AND primary_name NOT LIKE '%,%'
            AND LENGTH(TRIM(primary_name)) > 2
            AND LOWER(primary_name) NOT LIKE '%register%'
            AND LOWER(primary_name) NOT LIKE '%gaming%'
            AND LOWER(primary_name) NOT LIKE '%tabletop%'
            AND LOWER(primary_name) NOT LIKE '%antigen%'
            AND LOWER(primary_name) NOT LIKE '%travel%'
            AND LOWER(primary_name) NOT LIKE '%arts quarter%'
            AND LOWER(primary_name) NOT LIKE '%pudding%'
            AND LOWER(primary_name) NOT LIKE '%bar & grill%'
            AND LOWER(primary_name) NOT LIKE '%bar and grill%'
        ),
        deduplicated AS (
            SELECT DISTINCT ON (poi_type, name_key)
                poi_type, primary_name, address, postcode, name_key
            FROM raw
            WHERE poi_type IS NOT NULL
            ORDER BY poi_type, name_key, postcode NULLS LAST
        ),
        ranked AS (
            SELECT poi_type, primary_name, address, postcode,
                ROW_NUMBER() OVER (PARTITION BY poi_type ORDER BY primary_name) AS rn
            FROM deduplicated
        )
        SELECT poi_type, primary_name, address, postcode
        FROM ranked
        WHERE rn <= 15
        ORDER BY poi_type, primary_name
        """),

    NEAREST_AMENITIES_BY_POSTCODE("""
        SELECT
            CASE
                WHEN main_category IN ('supermarket','grocery_store','superstore')   THEN 'SUPERMARKET'
                WHEN main_category IN ('doctor','medical_center','family_practice',
                                       'community_health_clinic','walk_in_clinic')   THEN 'GP_SURGERY'
                WHEN main_category = 'pharmacy'                                      THEN 'PHARMACY'
                WHEN main_category = 'hospital'                                      THEN 'HOSPITAL'
                WHEN main_category IN ('dentist','general_dentistry')                THEN 'DENTIST'
                WHEN main_category = 'urgent_care_clinic'                            THEN 'URGENT_CARE'
                WHEN main_category = 'fast_food_restaurant'                          THEN 'FAST_FOOD'
                WHEN main_category = 'gas_station'                                   THEN 'PETROL'
                WHEN main_category IN ('bank_credit_union','banks','credit_union')   THEN 'BANK'
                WHEN main_category = 'gym'                                           THEN 'GYM'
                WHEN main_category = 'pub'                                           THEN 'PUB'
                WHEN main_category = 'library'                                       THEN 'LIBRARY'
                WHEN main_category = 'community_center'                              THEN 'COMMUNITY_CENTRE'
            END                                                     AS poi_type,
            p.primary_name,
            p.address,
            p.postcode,
            ROUND(ST_Distance(
                ST_Transform(centroid.geom, 4326)::geography,
                ST_SetSRID(ST_MakePoint(p.long, p.lat), 4326)::geography
            )::numeric, 0)                                          AS distance_m
        FROM staging.poi_raw p
        CROSS JOIN (
            SELECT ST_Centroid(ga.geom) AS geom
            FROM core.geo_area ga
            JOIN ref.postcode_to_lsoa ptl ON ptl.geo_area_id = ga.geo_area_id
            WHERE ptl.postcode_norm = ?
            LIMIT 1
        ) centroid
        WHERE p.lat IS NOT NULL
        AND p.long IS NOT NULL
        AND p.primary_name IS NOT NULL
        AND p.primary_name NOT LIKE '%,%'
        AND LENGTH(TRIM(p.primary_name)) > 2
        AND LOWER(p.primary_name) NOT LIKE '%tesco bank%'
        AND LOWER(p.primary_name) NOT LIKE '%register%'
        AND LOWER(p.primary_name) NOT LIKE '%gaming%'
        AND LOWER(p.primary_name) NOT LIKE '%antigen%'
        AND LOWER(p.primary_name) NOT LIKE '%travel%'
        AND LOWER(p.primary_name) NOT LIKE '%arts quarter%'
        AND LOWER(p.primary_name) NOT LIKE '%pudding%'
        AND LOWER(p.primary_name) NOT LIKE '%bar & grill%'
        AND main_category IN (
            'supermarket','grocery_store','superstore',
            'doctor','medical_center','family_practice',
            'community_health_clinic','walk_in_clinic',
            'pharmacy','hospital','dentist','general_dentistry',
            'urgent_care_clinic','fast_food_restaurant','gas_station',
            'bank_credit_union','banks','credit_union',
            'gym','pub','library','community_center'
        )
        AND ST_Distance(
            ST_Transform(centroid.geom, 4326)::geography,
            ST_SetSRID(ST_MakePoint(p.long, p.lat), 4326)::geography
        ) <= 2000
        ORDER BY poi_type, distance_m
        LIMIT 100
        """),
    OWNERSHIP_BY_POSTCODE("""
        SELECT
            lo.lsoa_code,
            lo.total_corporate_titles,
            lo.freehold_count,
            lo.leasehold_count,
            lo.unique_companies,
            lo.overseas_titles,
            lo.top_overseas_country,
            lo.overseas_countries
        FROM feat.lsoa_ownership lo
        JOIN ref.postcode_to_lsoa p ON p.lsoa21cd = lo.lsoa_code
        WHERE p.postcode_norm = ?
        LIMIT 1
        """),

    OWNERSHIP_BY_DISTRICT("""
        SELECT
            SUM(lo.total_corporate_titles)          AS total_corporate_titles,
            SUM(lo.freehold_count)                  AS freehold_count,
            SUM(lo.leasehold_count)                 AS leasehold_count,
            SUM(lo.unique_companies)                AS unique_companies,
            SUM(lo.overseas_titles)                 AS overseas_titles,
            SUM(lo.overseas_countries)              AS overseas_countries,
            MODE() WITHIN GROUP (
                ORDER BY lo.top_overseas_country
            )                                       AS top_overseas_country,
            COUNT(DISTINCT lo.lsoa_code)            AS lsoa_count
        FROM feat.lsoa_ownership lo
        WHERE lo.lsoa_code IN (
            SELECT DISTINCT p.lsoa21cd
            FROM ref.postcode_to_lsoa p
            WHERE p.postcode_norm LIKE ?
        )
        """),

    OWNERSHIP_BY_LSOA("""
        SELECT
            lo.lsoa_code,
            lo.total_corporate_titles,
            lo.freehold_count,
            lo.leasehold_count,
            lo.unique_companies,
            lo.overseas_titles,
            lo.top_overseas_country,
            lo.overseas_countries
        FROM feat.lsoa_ownership lo
        WHERE lo.lsoa_code = ?
        LIMIT 1
        """);


    private final String query;

    PropOSQuery(final String query) {
        this.query = query;
    }

    public String query() {
        return query;
    }
}