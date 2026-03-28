package com.propos.iq.domain.model.features;

public record AreaAmenities(
        // Healthcare
        Integer gpCount,
        Integer pharmacyCount,
        Integer hospitalCount,
        Integer dentistCount,
        Integer urgentCareCount,

        // Grocery
        Integer supermarketCount,
        Integer convenienceStoreCount,

        // Petrol
        Integer petrolStationCount,

        // Fast food
        Integer fastFoodCount,

        // Finance
        Integer bankCount,
        Integer atmCount,

        // Leisure
        Integer gymCount,
        Integer pubCount,
        Integer parkCount,
        Integer libraryCount,
        Integer communityCentreCount,

        // Schools
        Integer poiSchoolCount
) {}