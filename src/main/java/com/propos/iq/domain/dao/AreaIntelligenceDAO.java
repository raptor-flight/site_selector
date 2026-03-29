package com.propos.iq.domain.dao;


import com.propos.iq.domain.dao.query.PropOSQuery;
import com.propos.iq.domain.model.features.*;
import io.agroal.api.AgroalDataSource;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class AreaIntelligenceDAO {

    private final Logger logger;
    private final AgroalDataSource dataSource;

    @Inject
    public AreaIntelligenceDAO(final Logger logger,
                               final AgroalDataSource dataSource) {
        super();
        this.logger = logger;
        this.dataSource = dataSource;
    }

    public AreaProfile getAreaProfile(final String postcode) {
        final String normalisedPostcode = postcode.toUpperCase()
                .replaceAll("\\s+", "");
        logger.infof("getAreaProfile: %s", normalisedPostcode);

        try (final Connection conn = dataSource.getConnection();
             final PreparedStatement ps = conn.prepareStatement(
                     PropOSQuery.AREA_PROFILE_BY_POSTCODE.query())) {

            ps.setString(1, normalisedPostcode);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapAreaProfile(rs);
                }
            }
        } catch (SQLException e) {
            logger.errorf("getAreaProfile error [%s]: %s",
                    normalisedPostcode, e.getMessage());
        }

        return null;
    }

    public List<AreaProfile> findOpportunities(final String location,
                                               final String criteria) {
        logger.infof("findOpportunities: %s / %s", location, criteria);
        final List<AreaProfile> results = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     PropOSQuery.OPPORTUNITIES_BY_LOCATION.query())) {

            ps.setString(1, "%" + location + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(mapAreaProfile(rs));
                }
            }
        } catch (SQLException e) {
            logger.errorf("findOpportunities error [%s]: %s",
                    location, e.getMessage());
        }
        return results;
    }

    public List<AreaProfile> compareAreas(final List<String> postcodes) {
        logger.infof("compareAreas: %s", postcodes);
        final List<AreaProfile> results = new ArrayList<>();
        for (String postcode : postcodes) {
            final AreaProfile profile = getAreaProfile(postcode);
            if (profile != null) results.add(profile);
        }
        return results;
    }

    public List<AreaProfile> getTopOpportunities(final String region,
                                                 final int limit) {
        logger.infof("getTopOpportunities: %s / %d", region, limit);
        final List<AreaProfile> results = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     PropOSQuery.TOP_OPPORTUNITIES.query())) {

            ps.setString(1, region);
            ps.setString(2, "%" + region + "%");
            ps.setInt(3, limit > 0 ? limit : 10);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(mapAreaProfile(rs));
                }
            }
        } catch (SQLException e) {
            logger.errorf("getTopOpportunities error [%s]: %s",
                    region, e.getMessage());
        }
        return results;
    }

    public List<AreaProfile> getFloodRiskAreas(final String location,
                                               final String riskLevel) {
        logger.infof("getFloodRiskAreas: %s / %s", location, riskLevel);
        final List<AreaProfile> results = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     PropOSQuery.FLOOD_RISK_BY_LOCATION.query())) {

            ps.setString(1, riskLevel.toUpperCase());
            ps.setString(2, "%" + location + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(mapAreaProfile(rs));
                }
            }
        } catch (SQLException e) {
            logger.errorf("getFloodRiskAreas error [%s]: %s",
                    location, e.getMessage());
        }
        return results;
    }

    public List<AreaProfile> getPoorEPCAreas(final String location,
                                             final double maxPctABC) {
        logger.infof("getPoorEPCAreas: %s / %f", location, maxPctABC);
        final List<AreaProfile> results = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     PropOSQuery.POOR_EPC_BY_LOCATION.query())) {

            ps.setDouble(1, maxPctABC);
            ps.setString(2, "%" + location + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(mapAreaProfile(rs));
                }
            }
        } catch (SQLException e) {
            logger.errorf("getPoorEPCAreas error [%s]: %s",
                    location, e.getMessage());
        }
        return results;
    }

    public List<AreaProfile> findOpportunitiesByDistrict(final String district) {
        logger.infof("findOpportunitiesByDistrict: %s", district);
        final List<AreaProfile> results = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     PropOSQuery.OPPORTUNITIES_BY_POSTCODE_DISTRICT.query())) {

            ps.setString(1, district.toUpperCase().replaceAll("\\s+", "") + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(mapAreaProfile(rs));
                }
            }
        } catch (SQLException e) {
            logger.errorf("findOpportunitiesByDistrict error [%s]: %s",
                    district, e.getMessage());
        }
        return results;
    }

    public List<AreaProfile> getCrimeTrendAreas(final String location,
                                                final String trend) {
        logger.infof("getCrimeTrendAreas: %s / %s", location, trend);
        final List<AreaProfile> results = new ArrayList<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     PropOSQuery.CRIME_TREND_BY_LOCATION.query())) {

            ps.setString(1, trend.toUpperCase());
            ps.setString(2, "%" + location + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    results.add(mapAreaProfile(rs));
                }
            }
        } catch (SQLException e) {
            logger.errorf("getCrimeTrendAreas error [%s]: %s",
                    location, e.getMessage());
        }
        return results;
    }

    public String getMarketSummary(final String location) {
        logger.infof("getMarketSummary: %s", location);

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     PropOSQuery.MARKET_SUMMARY_BY_LOCATION.query())) {

            ps.setString(1, "%" + location + "%");

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return String.format(
                            "Market summary for %s: %d neighbourhoods analysed. " +
                                    "Average investment score: %.1f. " +
                                    "Average median price: £%,.0f. " +
                                    "Price growth (1yr): %.1f%%. " +
                                    "PRIME areas: %d. HIGH risk areas: %d.",
                            location,
                            rs.getInt("lsoa_count"),
                            rs.getDouble("avg_investment_score"),
                            rs.getDouble("avg_median_price"),
                            rs.getDouble("avg_price_growth_1yr"),
                            rs.getInt("prime_count"),
                            rs.getInt("high_risk_count")
                    );
                }
            }
        } catch (SQLException e) {
            logger.errorf("getMarketSummary error [%s]: %s",
                    location, e.getMessage());
        }
        return "No market data found for " + location;
    }

    public AreaAmenityNames getAmenityNames(final String postcodeOrDistrict) {
        final String normalised = postcodeOrDistrict.toUpperCase().replaceAll("\\s+", "");
        logger.infof("getAmenityNames: %s", normalised);

        // Detect whether this is a full postcode (contains inward code) or district only
        // Full postcode pattern: letters+digits+digits+letters+letters (e.g. HU11TN, B388DR)
        // District pattern: letters+digits only (e.g. HU1, B38)
        final boolean isFullPostcode = normalised.matches("[A-Z]{1,2}\\d{1,2}\\d[A-Z]{2}");

        if (isFullPostcode) {
            return getAmenityNamesByPostcode(normalised);
        } else {
            return getAmenityNamesByDistrict(normalised);
        }
    }

    private AreaAmenityNames getAmenityNamesByPostcode(final String normalisedPostcode) {
        // Use spatial nearest amenities query — works for any postcode, not just LSOA-bound
        final java.util.Map<String, java.util.List<AreaAmenityNames.AmenityEntry>> named =
                new java.util.LinkedHashMap<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     PropOSQuery.NEAREST_AMENITIES_BY_POSTCODE.query())) {

            ps.setString(1, normalisedPostcode);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    final String type = rs.getString("poi_type");
                    if (type == null) continue;
                    final String name = rs.getString("primary_name");
                    final String address = rs.getString("address");
                    final String postcode = rs.getString("postcode");
                    final int distanceM = rs.getInt("distance_m");
                    // Keep top 5 per category by distance
                    final java.util.List<AreaAmenityNames.AmenityEntry> entries =
                            named.computeIfAbsent(type, k -> new java.util.ArrayList<>());
                    if (entries.size() < 5) {
                        entries.add(new AreaAmenityNames.AmenityEntry(name, address, postcode, distanceM));
                    }
                }
            }
        } catch (SQLException e) {
            logger.errorf("getAmenityNamesByPostcode error [%s]: %s",
                    normalisedPostcode, e.getMessage());
        }

        // Derive label from postcode
        return new AreaAmenityNames(normalisedPostcode, normalisedPostcode + " area", named);
    }

    private AreaAmenityNames getAmenityNamesByDistrict(final String district) {
        return fetchAmenityNames(
                PropOSQuery.AMENITY_NAMES_BY_DISTRICT.query(),
                district + "%",
                district,
                district + " district"
        );
    }

    private AreaAmenityNames fetchAmenityNames(final String sql,
                                               final String queryParam,
                                               final String lsoa21cd,
                                               final String label) {
        final java.util.Map<String, java.util.List<AreaAmenityNames.AmenityEntry>> named =
                new java.util.LinkedHashMap<>();

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, queryParam);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    final String type = rs.getString("poi_type");
                    if (type == null) continue;
                    final String name = rs.getString("primary_name");
                    final String address = rs.getString("address");
                    final String postcode = rs.getString("postcode");
                    // District queries have no distance column
                    final int distanceM = rs.getMetaData().getColumnCount() >= 5
                            ? rs.getInt("distance_m") : 0;
                    named.computeIfAbsent(type, k -> new java.util.ArrayList<>())
                            .add(new AreaAmenityNames.AmenityEntry(name, address, postcode, distanceM));
                }
            }
        } catch (SQLException e) {
            logger.errorf("fetchAmenityNames error [%s]: %s", queryParam, e.getMessage());
        }

        return new AreaAmenityNames(lsoa21cd, label, named);
    }

    private AreaProfile mapAreaProfile(final ResultSet rs) throws SQLException {
        return new AreaProfile(
                new AreaLocation(
                        rs.getString("lsoa21cd"),
                        rs.getString("label"),
                        rs.getString("postcode")
                ),
                new AreaScores(
                        rs.getBigDecimal("investment_score"),
                        rs.getString("investment_grade"),
                        rs.getBigDecimal("risk_score"),
                        rs.getString("risk_rating"),
                        rs.getBigDecimal("opportunity_score"),
                        rs.getString("opportunity_grade")
                ),
                new AreaRisks(
                        // Flood — rivers and sea
                        rs.getString("combined_flood_category"),
                        rs.getBigDecimal("flood_risk_score"),
                        rs.getBigDecimal("rofrs_high_pct"),
                        rs.getBigDecimal("rofrs_medium_pct"),
                        rs.getBigDecimal("rofrs_low_pct"),
                        rs.getString("rofrs_category"),
                        // Flood — surface water
                        rs.getBigDecimal("rofsw_high_pct"),
                        rs.getBigDecimal("rofsw_medium_pct"),
                        rs.getBigDecimal("rofsw_low_pct"),
                        rs.getString("rofsw_category"),
                        // Crime
                        rs.getBigDecimal("total_rate_per_1000"),
                        rs.getString("trend"),
                        rs.getBigDecimal("trend_pct_change"),
                        rs.getBigDecimal("violence_rate"),
                        rs.getBigDecimal("burglary_rate"),
                        rs.getBigDecimal("asb_rate"),
                        rs.getBigDecimal("vehicle_crime_rate"),
                        rs.getBigDecimal("drugs_rate"),
                        // Planning
                        rs.getBoolean("in_green_belt"),
                        rs.getString("green_belt_name"),
                        rs.getBoolean("ancient_woodland_any"),
                        rs.getBigDecimal("ancient_woodland_pct"),
                        rs.getInt("listed_building_count"),
                        rs.getInt("grade_i_count"),
                        rs.getInt("grade_ii_star_count"),
                        rs.getInt("grade_ii_count"),
                        rs.getBigDecimal("planning_constraint_score"),
                        // Road safety
                        rs.getInt("total_collisions"),
                        rs.getInt("fatal_collisions"),
                        rs.getInt("serious_collisions"),
                        rs.getBigDecimal("collision_rate_per_1000"),
                        rs.getBigDecimal("pct_fatal_or_serious"),
                        rs.getBigDecimal("road_safety_score")
                ),
                new AreaMarket(
                        rs.getBigDecimal("median_price"),
                        rs.getBigDecimal("predicted_median_price"),
                        rs.getString("value_signal"),
                        rs.getBigDecimal("value_gap_pct"),
                        rs.getBigDecimal("price_cv"),
                        rs.getBigDecimal("median_price_24m"),
                        rs.getInt("transaction_count_12m"),
                        rs.getInt("transaction_count_24m"),
                        rs.getString("price_trend"),
                        rs.getBigDecimal("new_build_pct"),
                        rs.getBigDecimal("freehold_pct"),
                        rs.getBigDecimal("median_price_detached"),
                        rs.getBigDecimal("median_price_semi"),
                        rs.getBigDecimal("median_price_terraced"),
                        rs.getBigDecimal("median_price_flat")
                ),
                new AreaEnvironment(
                        rs.getBigDecimal("pct_abc"),
                        rs.getBigDecimal("pct_d"),
                        rs.getBigDecimal("pct_fg"),
                        rs.getString("energy_efficiency_category"),
                        rs.getBigDecimal("avg_efficiency"),
                        rs.getBigDecimal("pct_mains_gas"),
                        rs.getBigDecimal("avg_floor_area_m2"),
                        rs.getBigDecimal("pct_pre_1919"),
                        rs.getBigDecimal("pct_1919_to_1944"),
                        rs.getBigDecimal("pct_1945_to_1964"),
                        rs.getBigDecimal("pct_1965_to_1982"),
                        rs.getBigDecimal("pct_1983_to_1995"),
                        rs.getBigDecimal("pct_post_1995"),
                        rs.getBigDecimal("total_greenspace_pct"),
                        rs.getBigDecimal("park_garden_pct"),
                        rs.getBigDecimal("playing_field_pct"),
                        rs.getInt("play_space_count"),
                        rs.getInt("allotment_count"),
                        rs.getBigDecimal("greenspace_score")
                ),
                new AreaPeople(
                        rs.getInt("total_population"),
                        rs.getBigDecimal("working_age_pct"),
                        rs.getBigDecimal("pct_under_16"),
                        rs.getBigDecimal("pct_over_65"),
                        rs.getBigDecimal("owner_occupied_pct"),
                        rs.getBigDecimal("pct_social_rented"),
                        rs.getBigDecimal("pct_private_rented"),
                        rs.getBigDecimal("pct_detached"),
                        rs.getBigDecimal("pct_flat"),
                        rs.getBigDecimal("pct_white_british"),
                        rs.getBigDecimal("pct_non_white"),
                        rs.getBigDecimal("pct_asian"),
                        rs.getBigDecimal("pct_black"),
                        rs.getBigDecimal("pct_mixed"),
                        rs.getBigDecimal("simpsons_diversity_index"),
                        rs.getBigDecimal("pct_employed"),
                        rs.getBigDecimal("pct_unemployed"),
                        rs.getBigDecimal("pct_inactive"),
                        rs.getInt("imd_decile"),
                        rs.getBigDecimal("claimant_rate")
                ),
                new AreaInfrastructure(
                        // Bus
                        rs.getInt("bus_stop_count"),
                        rs.getBigDecimal("nearest_bus_stop_m"),
                        rs.getBigDecimal("bus_stop_density_per_km2"),
                        // Rail
                        rs.getInt("rail_station_count"),
                        rs.getBigDecimal("nearest_rail_station_m"),
                        rs.getInt("metro_stop_count"),
                        // EV
                        rs.getInt("ev_charger_count"),
                        rs.getBigDecimal("ev_chargers_per_100k"),
                        // Healthcare
                        rs.getBigDecimal("nearest_gp_surgery_m"),
                        rs.getInt("gp_surgery_count"),
                        rs.getBigDecimal("gp_surgeries_per_1000"),
                        rs.getBigDecimal("gp_access_score"),
                        // Schools
                        rs.getInt("school_count"),
                        rs.getInt("primary_schools"),
                        rs.getInt("secondary_schools"),
                        rs.getInt("post16_schools"),
                        rs.getBigDecimal("pct_outstanding"),
                        rs.getBigDecimal("pct_good"),
                        rs.getBigDecimal("pct_requires_improvement"),
                        rs.getBigDecimal("pct_inadequate"),
                        rs.getBigDecimal("avg_att8_score"),
                        rs.getBigDecimal("avg_progress8_score"),
                        rs.getBigDecimal("avg_pct_rwm_expected"),
                        rs.getBigDecimal("avg_pct_fsm"),
                        rs.getBigDecimal("avg_overall_absence"),
                        rs.getBigDecimal("avg_persistent_absence"),
                        rs.getString("ofsted_trend"),
                        // Connectivity
                        rs.getString("connectivity_category"),
                        rs.getBigDecimal("superfast_pct"),
                        rs.getBigDecimal("full_fibre_pct"),
                        rs.getBigDecimal("ultrafast_pct"),
                        rs.getBigDecimal("below_uso_pct"),
                        rs.getBigDecimal("mobile_4g5g_prem_any"),
                        rs.getBigDecimal("mobile_4g5g_prem_all"),
                        rs.getBigDecimal("mobile_4g5g_geo_any")
                ),
                new AreaEconomy(
                        rs.getInt("total_businesses"),
                        rs.getBigDecimal("businesses_per_1000_residents"),
                        rs.getInt("construction_businesses"),
                        rs.getInt("retail_businesses"),
                        rs.getInt("property_businesses"),
                        rs.getInt("health_businesses"),
                        rs.getInt("professional_businesses"),
                        rs.getInt("accommodation_food_businesses"),
                        rs.getInt("financial_businesses"),
                        rs.getBigDecimal("job_density"),
                        rs.getInt("claimant_count"),
                        rs.getBigDecimal("claimant_rate"),
                        rs.getString("economy_type")
                ),
                new AreaDeprivation(
                        rs.getBigDecimal("imd_score"),
                        rs.getInt("imd_rank"),
                        rs.getInt("imd_decile"),
                        rs.getBigDecimal("income_score"),
                        rs.getBigDecimal("employment_score"),
                        rs.getBigDecimal("education_score"),
                        rs.getBigDecimal("health_score"),
                        rs.getBigDecimal("crime_score"),
                        rs.getBigDecimal("housing_score"),
                        rs.getBigDecimal("environment_score"),
                        rs.getInt("dependent_children"),
                        rs.getInt("older_population")
                ),
                new AreaAmenities(
                        rs.getInt("poi_gp_count"),
                        rs.getInt("poi_pharmacy_count"),
                        rs.getInt("poi_hospital_count"),
                        rs.getInt("poi_dentist_count"),
                        rs.getInt("poi_urgent_care_count"),
                        rs.getInt("poi_supermarket_count"),
                        rs.getInt("poi_convenience_store_count"),
                        rs.getInt("poi_petrol_station_count"),
                        rs.getInt("poi_fast_food_count"),
                        rs.getInt("poi_bank_count"),
                        rs.getInt("poi_atm_count"),
                        rs.getInt("poi_gym_count"),
                        rs.getInt("poi_pub_count"),
                        rs.getInt("poi_park_count"),
                        rs.getInt("poi_library_count"),
                        rs.getInt("poi_community_centre_count"),
                        rs.getInt("poi_school_count")
                ),
                new AreaFlags(
                        rs.getBoolean("regeneration_candidate"),
                        rs.getBoolean("family_market"),
                        rs.getBoolean("undervalued"),
                        rs.getBoolean("digital_exclusion")
                )
        );
    }


    // ─────────────────────────────────────────────────────────────────────────────
// Add to AreaIntelligenceDAO.java — replace the stub at the bottom
// ─────────────────────────────────────────────────────────────────────────────

    public PpdHistory getPpdHistory(final String location) {
        final String normalised = location.toUpperCase().replaceAll("\\s+", "");
        logger.infof("getPpdHistory: %s", normalised);

        // ── Input type detection ──────────────────────────────────────────────
        // LSOA code:       starts with E01, E02 etc — 9 chars alphanumeric
        // Full postcode:   e.g. B388DR, SW1A2AA — ends with digit+2 letters
        // District:        everything else — e.g. B38, SW1, M1
        final boolean isLsoa     = normalised.matches("E\\d{8}");
        final boolean isFullPost = normalised.matches("[A-Z]{1,2}\\d{1,2}[A-Z]?\\d[A-Z]{2}");

        if (isLsoa) {
            return getPpdHistoryByLsoa(normalised);
        } else if (isFullPost) {
            return getPpdHistoryByFullPostcode(normalised);
        } else {
            return getPpdHistoryByDistrict(normalised);
        }
    }

    private PpdHistory getPpdHistoryByFullPostcode(final String normalisedPostcode) {
        logger.infof("getPpdHistoryByFullPostcode: %s", normalisedPostcode);

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     PropOSQuery.PPD_HISTORY_BY_FULL_POSTCODE.query())) {

            ps.setString(1, normalisedPostcode);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapPpdHistory(rs, false);
                }
            }
        } catch (SQLException e) {
            logger.errorf("getPpdHistoryByFullPostcode error [%s]: %s",
                    normalisedPostcode, e.getMessage());
        }
        return null;
    }

    private PpdHistory getPpdHistoryByDistrict(final String district) {
        logger.infof("getPpdHistoryByDistrict: %s", district);

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     PropOSQuery.PPD_HISTORY_BY_DISTRICT.query())) {

            // District match: B38 → LIKE 'B38%'
            ps.setString(1, district + "%");

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapPpdHistory(rs, true);
                }
            }
        } catch (SQLException e) {
            logger.errorf("getPpdHistoryByDistrict error [%s]: %s",
                    district, e.getMessage());
        }
        return null;
    }

    private PpdHistory getPpdHistoryByLsoa(final String lsoaCode) {
        logger.infof("getPpdHistoryByLsoa: %s", lsoaCode);

        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     PropOSQuery.PPD_HISTORY_BY_LSOA.query())) {

            ps.setString(1, lsoaCode);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapPpdHistory(rs, false);
                }
            }
        } catch (SQLException e) {
            logger.errorf("getPpdHistoryByLsoa error [%s]: %s",
                    lsoaCode, e.getMessage());
        }
        return null;
    }

    private PpdHistory mapPpdHistory(final ResultSet rs,
                                     final boolean isDistrict) throws SQLException {
        // lsoa_code and lsoa_count are only present depending on query type
        final String  lsoaCode    = isDistrict ? null        : rs.getString("lsoa_code");
        final Integer lsoaCount   = isDistrict ? rs.getInt("lsoa_count") : null;

        return new PpdHistory(
                lsoaCode,
                lsoaCount,
                rs.getLong("transaction_count"),
                rs.getLong("median_price"),
                rs.getLong("avg_price"),
                rs.getLong("min_price"),
                rs.getLong("max_price"),
                rs.getLong("detached_count"),
                rs.getLong("semi_count"),
                rs.getLong("terraced_count"),
                rs.getLong("flat_count"),
                rs.getLong("new_build_count"),
                rs.getLong("median_price_5yr"),
                rs.getLong("median_price_1yr"),
                rs.getLong("transactions_1yr"),
                rs.getLong("price_growth_5yr_pct")
        );
    }


    public OwnershipProfile getOwnershipProfile(final String location) {
        final String normalised = location.toUpperCase().replaceAll("\\s+", "");
        logger.infof("getOwnershipProfile: %s", normalised);

        final boolean isLsoa     = normalised.matches("E\\d{8}");
        final boolean isFullPost = normalised.matches("[A-Z]{1,2}\\d{1,2}[A-Z]?\\d[A-Z]{2}");

        if (isLsoa) {
            return getOwnershipByLsoa(normalised);
        } else if (isFullPost) {
            return getOwnershipByFullPostcode(normalised);
        } else {
            return getOwnershipByDistrict(normalised);
        }
    }

    private OwnershipProfile getOwnershipByFullPostcode(final String normalisedPostcode) {
        logger.infof("getOwnershipByFullPostcode: %s", normalisedPostcode);
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     PropOSQuery.OWNERSHIP_BY_POSTCODE.query())) {
            ps.setString(1, normalisedPostcode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapOwnership(rs, false);
            }
        } catch (SQLException e) {
            logger.errorf("getOwnershipByFullPostcode error [%s]: %s",
                    normalisedPostcode, e.getMessage());
        }
        return null;
    }

    private OwnershipProfile getOwnershipByDistrict(final String district) {
        logger.infof("getOwnershipByDistrict: %s", district);
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     PropOSQuery.OWNERSHIP_BY_DISTRICT.query())) {
            ps.setString(1, district + "%");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapOwnership(rs, true);
            }
        } catch (SQLException e) {
            logger.errorf("getOwnershipByDistrict error [%s]: %s",
                    district, e.getMessage());
        }
        return null;
    }

    private OwnershipProfile getOwnershipByLsoa(final String lsoaCode) {
        logger.infof("getOwnershipByLsoa: %s", lsoaCode);
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(
                     PropOSQuery.OWNERSHIP_BY_LSOA.query())) {
            ps.setString(1, lsoaCode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapOwnership(rs, false);
            }
        } catch (SQLException e) {
            logger.errorf("getOwnershipByLsoa error [%s]: %s",
                    lsoaCode, e.getMessage());
        }
        return null;
    }

    private OwnershipProfile mapOwnership(final ResultSet rs,
                                          final boolean isDistrict) throws SQLException {
        final String  lsoaCode  = isDistrict ? null : rs.getString("lsoa_code");
        final Integer lsoaCount = isDistrict ? rs.getInt("lsoa_count") : null;

        return new OwnershipProfile(
                lsoaCode,
                lsoaCount,
                rs.getInt("total_corporate_titles"),
                rs.getInt("freehold_count"),
                rs.getInt("leasehold_count"),
                rs.getInt("unique_companies"),
                rs.getInt("overseas_titles"),
                rs.getString("top_overseas_country"),
                rs.getInt("overseas_countries")
        );
    }

}