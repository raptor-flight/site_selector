package com.propos.iq.domain.model.common;

public record Address(String postCode, String primaryHouseNumber, String secondaryHouseName,
                      String street, String locality, String city, String district, String county) {}
