package com.raptor.ai.site.domain.intents;

import java.util.List;

public class PropertyQuery {
    private PropertyQueryType type;
    private String postCode;
    private List<String> postCodes;
    private Integer fromYear;
    private Integer toYear;
    private String propertyType;

    public PropertyQuery() {
        super();
    }

    public PropertyQueryType getType() {
        return type;
    }

    public void setType(PropertyQueryType type) {
        this.type = type;
    }

    public String getPostCode() {
        return postCode;
    }

    public void setPostCode(String postCode) {
        this.postCode = postCode;
    }

    public List<String> getPostCodes() {
        return postCodes;
    }

    public void setPostCodes(List<String> postCodes) {
        this.postCodes = postCodes;
    }

    public Integer getFromYear() {
        return fromYear;
    }

    public void setFromYear(Integer fromYear) {
        this.fromYear = fromYear;
    }

    public Integer getToYear() {
        return toYear;
    }

    public void setToYear(Integer toYear) {
        this.toYear = toYear;
    }

    public String getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(String propertyType) {
        this.propertyType = propertyType;
    }
}
