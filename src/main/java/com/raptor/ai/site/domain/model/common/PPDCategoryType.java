package com.raptor.ai.site.domain.model.common;

public enum PPDCategoryType {

    /** --- Indicates the type of Price Paid transaction. --- */
    STANDARD_PRICE_PAID("A"),
    ADDITIONAL_PRICE_PAID("B");

    String categoryType;
    PPDCategoryType(String categoryType) {
        categoryType = categoryType;
    }

    public String getCategoryType() {
        return categoryType;
    }
}
