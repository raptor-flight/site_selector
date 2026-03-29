package com.propos.iq.ai;


import com.propos.iq.domain.dao.AreaIntelligenceDAO;
import com.propos.iq.domain.model.features.AreaAmenityNames;
import com.propos.iq.domain.model.features.AreaProfile;
import com.propos.iq.domain.model.features.OwnershipProfile;
import com.propos.iq.domain.model.features.PpdHistory;
import dev.langchain4j.agent.tool.Tool;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.List;

@ApplicationScoped
public class PropOSTools {

    private final Logger logger;
    private final AreaIntelligenceDAO areaIntelligenceDAO;

    @Inject
    public PropOSTools(final Logger logger, final AreaIntelligenceDAO areaIntelligenceDAO) {
        super();
        this.logger = logger;
        this.areaIntelligenceDAO = areaIntelligenceDAO;
    }

    @Tool("""
            TIER 1 TOOL — use ONLY when the user supplies a FULL postcode (district + inward code).
            Examples of full postcodes: B38 8DR, SW1A 2AA, M1 1AE, LS1 4AP.
            A full postcode always contains a space and an inward code after the space.
            DO NOT use this for district-only postcodes (B38, M1, SW1) or city names.
            Returns the complete intelligence profile for that postcode: investment score,
            risk score, opportunity grade, AVM median price, flood risk (NaFRA2), crime rate
            and trend, EPC energy rating distribution, transport (bus stop count and nearest
            distance, rail stations), schools (Ofsted, KS2/KS4/KS5), healthcare (GP surgeries),
            green space, demographics, connectivity (Ofcom) and planning constraints.
            Parameter: postcode — the full postcode exactly as provided by the user.
            """)
    public AreaProfile getAreaProfile(String postcode) {
        logger.infof("Tool called: getAreaProfile(%s)", postcode);
        return this.areaIntelligenceDAO.getAreaProfile(postcode);
    }

    @Tool("""
            Get the named businesses and amenities within a postcode area or district.
            Use when the user asks about specific shops, restaurants, banks, GP surgeries,
            gyms, pubs or other named places near a postcode or district.
            Examples: "what supermarkets are near HU1 1TN?", "list the banks in B38",
            "what GP surgeries are in this area?", "show me the pubs near SW1A 2AA",
            "what amenities are in the B38 area?".
            Accepts EITHER a full postcode (e.g. "HU1 1TN", "B38 8DR") OR a district
            postcode (e.g. "B38", "HU1", "M1") — both work correctly.
            Returns named POIs grouped by category with address: supermarkets, GP surgeries,
            pharmacies, hospitals, dentists, fast food, petrol stations, banks, gyms,
            pubs, libraries, community centres.
            Parameter: postcode — full postcode or district code as provided by the user.
            """)
    public AreaAmenityNames getAreaAmenityNames(String postcode) {
        logger.infof("Tool called: getAreaAmenityNames(%s)", postcode);
        return this.areaIntelligenceDAO.getAmenityNames(postcode);
    }

    @Tool("""
            TIER 3 TOOL — use ONLY when the user supplies a city, town or region name
            with NO postcode component at all.
            Examples: "Birmingham", "Manchester", "Yorkshire", "South East".
            DO NOT use this if the input contains any postcode district (e.g. B38, M1, SW1).
            Searches for areas within that city or region matching the specified investment
            criteria. Returns the top 5 results with FULL details including: investment score,
            opportunity grade, AVM price, flood risk, crime, EPC, transport (bus stops and
            rail stations), schools, healthcare, green space, demographics, connectivity
            and planning constraints. Never omit any data dimension.
            Parameters:
              location — city, town or region name only (e.g. "Birmingham", "Yorkshire").
              criteria — investment criteria or intent (e.g. "high yield", "low flood risk",
                         "PRIME opportunity grade"). Pass an empty string if no criteria given.
            """)
    public List<AreaProfile> findOpportunities(String location, String criteria) {
        logger.infof("Tool called: findOpportunities(%s, %s)", location, criteria);
        return this.areaIntelligenceDAO.findOpportunities(location, criteria);
    }

    @Tool("""
            Compare intelligence profiles for two or more areas side by side.
            Use when the user asks to compare postcodes or neighbourhoods.
            Accepts a list of full postcodes (e.g. ["B38 8DR", "M60 1NW"]).
            Returns profiles for all postcodes ranked by investment score.
            Parameters: postcodes — list of full postcodes to compare.
            """)
    public List<AreaProfile> compareAreas(List<String> postcodes) {
        logger.infof("Tool called: compareAreas(%s)", postcodes);
        return this.areaIntelligenceDAO.compareAreas(postcodes);
    }

    @Tool("""
            Find the top investment opportunity areas in England or a specific region.
            Use for broad questions: "best areas to invest in", "PRIME opportunities in Yorkshire",
            "where should I buy investment property".
            Parameters:
              region — optional region name (e.g. "Yorkshire"). Pass empty string for England-wide.
              limit  — number of results to return (default 10).
            """)
    public List<AreaProfile> getTopOpportunities(String region, int limit) {
        logger.infof("Tool called: getTopOpportunities(%s, %d)", region, limit);
        return this.areaIntelligenceDAO.getTopOpportunities(region, limit);
    }

    @Tool("""
            Get areas with high flood risk in a specific region or city.
            Use for questions about flood risk, flood zones or NaFRA2 risk exposure.
            Parameters:
              location  — city or region name (e.g. "Hull", "Somerset").
              riskLevel — MEDIUM, HIGH or VERY_HIGH.
            """)
    public List<AreaProfile> getFloodRiskAreas(String location, String riskLevel) {
        logger.infof("Tool called: getFloodRiskAreas(%s, %s)", location, riskLevel);
        return this.areaIntelligenceDAO.getFloodRiskAreas(location, riskLevel);
    }

    @Tool("""
            Get areas with poor EPC energy ratings in a region.
            Useful for identifying retrofit opportunities, fuel poverty hotspots
            or green mortgage risk areas.
            Parameters:
              location  — city or region name.
              maxPctABC — maximum percentage of A/B/C-rated properties (e.g. 40.0 means
                          areas where fewer than 40% of homes are rated A, B or C).
            """)
    public List<AreaProfile> getPoorEPCAreas(String location, double maxPctABC) {
        logger.infof("Tool called: getPoorEPCAreas(%s, %f)", location, maxPctABC);
        return this.areaIntelligenceDAO.getPoorEPCAreas(location, maxPctABC);
    }

    @Tool("""
            Get areas with worsening crime trends in a region.
            Use for lender risk assessment, insurance pricing or development due diligence.
            Parameters:
              location — city or region name.
              trend    — WORSENING, STABLE or IMPROVING.
            """)
    public List<AreaProfile> getCrimeTrendAreas(String location, String trend) {
        logger.infof("Tool called: getCrimeTrendAreas(%s, %s)", location, trend);
        return this.areaIntelligenceDAO.getCrimeTrendAreas(location, trend);
    }

    @Tool("""
            Get a summary of market conditions for a city or region.
            Returns average investment score, risk distribution, opportunity grade breakdown,
            median prices and market trends.
            Parameters: location — city, region or local authority name.
            """)
    public String getMarketSummary(String location) {
        logger.infof("Tool called: getMarketSummary(%s)", location);
        return this.areaIntelligenceDAO.getMarketSummary(location);
    }

    @Tool("""
            TIER 2 TOOL — use ONLY when the user supplies a DISTRICT postcode.
            A district postcode is letters + digits with NO space and NO inward code.
            Examples of district postcodes: B38, M1, E1, SW1, LS1, W1A, EC1A.
            DO NOT use this for full postcodes (B38 8DR) or city names (Birmingham).
            If the user provides a district postcode alongside a city name (e.g. "B38 Birmingham"
            or "M1 Manchester"), extract only the district code and ignore the city name.
            Finds all neighbourhoods within that postcode district and returns investment
            opportunities ranked by score, with FULL details including all data dimensions.
            Parameters: district — the district code only (e.g. "B38", "M1", "SW1").
            """)
    public List<AreaProfile> findOpportunitiesByDistrict(String district) {
        logger.infof("Tool called: findOpportunitiesByDistrict(%s)", district);
        return this.areaIntelligenceDAO.findOpportunitiesByDistrict(district);
    }

    @Tool("""
            Get the full 30-year property price history for an area.
            Use when the user asks about historical prices, long-term price trends,
            price growth over time, or transaction volumes.
            Examples: "what have prices done in B38 8DR over the last 5 years?",
            "show me the price history for SW1A", "how much have prices grown in E1?",
            "what is the median house price history for E01004736?",
            "how many properties sold in LS1 last year?".
            Accepts THREE input formats — auto-detected:
              1. Full postcode  (e.g. "B38 8DR", "SW1A 2AA")  — returns single LSOA history
              2. District postcode (e.g. "B38", "SW1", "M1")  — returns aggregated district history
              3. LSOA code (e.g. "E01004736", "E01000206")    — returns direct LSOA history
            Returns: all-time median price, 5-year median, 1-year median, total transaction count,
            transactions in last 12 months (liquidity), 5-year price growth %, property type
            breakdown (detached/semi/terraced/flat counts), new build count.
            Parameter: location — full postcode, district postcode, or LSOA code.
            """)
    public PpdHistory getPpdHistory(String location) {
        logger.infof("Tool called: getPpdHistory(%s)", location);
        return this.areaIntelligenceDAO.getPpdHistory(location);
    }

    @Tool("""
            Get corporate and overseas ownership data for an area.
            Use when the user asks about who owns properties, corporate landlords,
            overseas investors, foreign ownership, company ownership, or institutional buyers.
            Examples: "who owns properties in W1?", "how much overseas ownership is there in SW1A 2AA?",
            "show me corporate ownership in E1", "which countries own property in Kensington?",
            "how many properties in B38 are owned by companies?".
            Accepts THREE input formats — auto-detected:
              1. Full postcode  (e.g. "B38 8DR", "SW1A 2AA")  — returns single LSOA profile
              2. District postcode (e.g. "B38", "SW1", "M1")  — returns aggregated district profile
              3. LSOA code (e.g. "E01004736")                 — returns direct LSOA profile
            Returns: total UK corporate titles (CCOD), freehold/leasehold split,
            unique company count, overseas titles (OCOD), top overseas country,
            number of distinct overseas countries.
            Note: corporate titles and overseas titles come from separate HMLR datasets
            and are additive — a property may appear in both counts.
            Parameter: location — full postcode, district postcode, or LSOA code.
            """)
    public OwnershipProfile getOwnershipProfile(String location) {
        logger.infof("Tool called: getOwnershipProfile(%s)", location);
        return this.areaIntelligenceDAO.getOwnershipProfile(location);
    }

}