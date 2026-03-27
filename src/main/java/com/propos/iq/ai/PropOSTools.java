package com.propos.iq.ai;


import com.propos.iq.domain.dao.AreaIntelligenceDAO;
import com.propos.iq.domain.model.features.AreaProfile;
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

    @Tool("Get the complete intelligence profile for a UK postcode. " +
            "Returns investment score, risk score, opportunity grade, AVM valuation, " +
            "flood risk, crime rate, EPC rating, transport, schools, demographics and planning constraints. " +
            "Use this for any question about a specific postcode or area.")
    public AreaProfile getAreaProfile(String postcode) {
        logger.infof("Tool called: getAreaProfile(%s)", postcode);
        return this.areaIntelligenceDAO.getAreaProfile(postcode);
    }

    @Tool("Search for areas matching specific investment criteria. " +
            "Use for questions like 'find regeneration opportunities near Manchester', " +
            "'show me low risk areas in the North West', " +
            "'where are the best areas for buy-to-let in Birmingham'. " +
            "Parameters: location (city or region), criteria (regeneration/low-risk/high-yield/opportunity)")
    public List<AreaProfile> findOpportunities(String location, String criteria) {
        logger.infof("Tool called: findOpportunities(%s, %s)", location, criteria);
        return this.areaIntelligenceDAO.findOpportunities(location, criteria);
    }

    @Tool("Compare intelligence profiles for multiple postcodes side by side. " +
            "Use when user asks to compare two or more areas, postcodes or neighbourhoods. " +
            "Returns profiles for all postcodes ranked by investment score.")
    public List<AreaProfile> compareAreas(List<String> postcodes) {
        logger.infof("Tool called: compareAreas(%s)", postcodes);
        return this.areaIntelligenceDAO.compareAreas(postcodes);
    }

    @Tool("Find the top investment opportunity areas in England or a specific region. " +
            "Use for questions like 'what are the best areas to invest in', " +
            "'show me PRIME opportunity areas in Yorkshire', " +
            "'where should I buy investment property'. " +
            "Parameters: region (optional), limit (number of results, default 10)")
    public List<AreaProfile> getTopOpportunities(String region, int limit) {
        logger.infof("Tool called: getTopOpportunities(%s, %d)", region, limit);
        return this.areaIntelligenceDAO.getTopOpportunities(region, limit);
    }

    @Tool("Get areas with high flood risk in a specific region or city. " +
            "Use for questions about flood risk, flood zones, NaFRA2 risk areas. " +
            "Parameters: location (city or region), riskLevel (HIGH/VERY_HIGH/MEDIUM)")
    public List<AreaProfile> getFloodRiskAreas(String location, String riskLevel) {
        logger.infof("Tool called: getFloodRiskAreas(%s, %s)", location, riskLevel);
        return this.areaIntelligenceDAO.getFloodRiskAreas(location, riskLevel);
    }

    @Tool("Get areas with poor EPC energy ratings in a region — useful for identifying " +
            "retrofit opportunities, fuel poverty hotspots, or green mortgage risk areas. " +
            "Parameters: location (city or region), maxPctABC (maximum % of A-B-C rated properties)")
    public List<AreaProfile> getPoorEPCAreas(String location, double maxPctABC) {
        logger.infof("Tool called: getPoorEPCAreas(%s, %f)", location, maxPctABC);
        return this.areaIntelligenceDAO.getPoorEPCAreas(location, maxPctABC);
    }

    @Tool("Get areas with worsening crime trends in a region. " +
            "Use for lender risk assessment, insurance pricing or development due diligence. " +
            "Parameters: location (city or region), trend (WORSENING/STABLE/IMPROVING)")
    public List<AreaProfile> getCrimeTrendAreas(String location, String trend) {
        logger.infof("Tool called: getCrimeTrendAreas(%s, %s)", location, trend);
        return this.areaIntelligenceDAO.getCrimeTrendAreas(location, trend);
    }

    @Tool("Get a summary of market conditions for a city or region. " +
            "Returns average investment score, risk distribution, opportunity grades, " +
            "median prices and market trends. " +
            "Parameters: location (city, region or local authority name)")
    public String getMarketSummary(String location) {
        logger.infof("Tool called: getMarketSummary(%s)", location);
        return this.areaIntelligenceDAO.getMarketSummary(location);
    }
}
