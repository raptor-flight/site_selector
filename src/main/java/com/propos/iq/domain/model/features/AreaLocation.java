package com.propos.iq.domain.model.features;

public record AreaLocation(
        String lsoa21cd,
        String label,
        String postcode
) {}