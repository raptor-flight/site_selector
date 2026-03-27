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
                        rs.getString("combined_flood_category"),
                        rs.getBigDecimal("flood_risk_score"),
                        rs.getBigDecimal("total_rate_per_1000"),
                        rs.getString("trend"),
                        rs.getBoolean("in_green_belt"),
                        rs.getBoolean("ancient_woodland_any")
                ),
                new AreaMarket(
                        rs.getBigDecimal("median_price"),
                        rs.getBigDecimal("predicted_median_price"),
                        rs.getString("value_signal"),
                        rs.getBigDecimal("value_gap_pct"),
                        rs.getBigDecimal("price_cv"),
                        rs.getBigDecimal("median_price_24m"),
                        rs.getInt("transaction_count_12m")
                ),
                new AreaEnvironment(
                        rs.getBigDecimal("pct_abc"),
                        rs.getBigDecimal("pct_fg"),
                        rs.getString("energy_efficiency_category"),
                        rs.getBigDecimal("total_greenspace_pct"),
                        rs.getBigDecimal("greenspace_score")
                ),
                new AreaPeople(
                        rs.getInt("total_population"),
                        rs.getBigDecimal("working_age_pct"),
                        rs.getBigDecimal("owner_occupied_pct"),
                        rs.getInt("imd_decile"),
                        rs.getBigDecimal("claimant_rate")
                ),
                new AreaInfrastructure(
                        rs.getInt("bus_stop_count"),
                        rs.getBigDecimal("nearest_rail_station_m"),
                        rs.getInt("gp_surgery_count"),
                        rs.getInt("school_count"),
                        rs.getString("connectivity_category"),
                        rs.getBigDecimal("superfast_pct")
                ),
                new AreaFlags(
                        rs.getBoolean("regeneration_candidate"),
                        rs.getBoolean("family_market"),
                        rs.getBoolean("undervalued"),
                        rs.getBoolean("digital_exclusion")
                )
        );
    }
}
