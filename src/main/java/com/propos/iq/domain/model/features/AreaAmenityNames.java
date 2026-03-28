package com.propos.iq.domain.model.features;

import java.util.List;
import java.util.Map;

public record AreaAmenityNames(
        String lsoa21cd,
        String label,
        Map<String, List<AmenityEntry>> namedAmenities
) {
    public record AmenityEntry(String name, String address, String postcode, Integer distanceM) {}
}