package com.propos.iq.domain.model.features;

/**
 * Corporate and overseas ownership profile for an LSOA or aggregated district.
 * Sourced from feat.lsoa_ownership (HMLR CCOD + OCOD, March 2026).
 *
 * Note: total_corporate_titles (CCOD) and overseas_titles (OCOD) are from
 * separate HMLR datasets and are additive, not hierarchical.
 */
public record OwnershipProfile(

        // Identity — null when aggregated at district level
        String  lsoa_code,

        // District aggregation count — null for single LSOA/postcode lookups
        Integer lsoa_count,

        // UK corporate ownership (CCOD — UK-registered companies)
        Integer total_corporate_titles,
        Integer freehold_count,
        Integer leasehold_count,
        Integer unique_companies,

        // Overseas ownership (OCOD — overseas-incorporated companies)
        Integer overseas_titles,
        String  top_overseas_country,
        Integer overseas_countries
) {}

