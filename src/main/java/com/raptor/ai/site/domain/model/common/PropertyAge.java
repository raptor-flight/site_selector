package com.raptor.ai.site.domain.model.common;

public enum PropertyAge {

    OLD("N"),
    NEW("Y");

    String propertyAge;

    PropertyAge(String age) {
        propertyAge = age;
    }

    public String getPropertyAge() {
        return propertyAge;
    }
}
