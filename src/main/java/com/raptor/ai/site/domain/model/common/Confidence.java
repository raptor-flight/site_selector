package com.raptor.ai.site.domain.model.common;

public enum Confidence {
    HIGH("High confidence: Statistics are based on a large number of transactions and are likely to represent the true market behaviour."),
    MEDIUM("Medium confidence: Statistics are based on a moderate number of transactions and are generally reliable but may still be influenced by property mix."),
    LOW("Low confidence: Statistics are based on a small number of transactions and may be influenced by outliers."),
    VERY_LOW("Very low confidence: Very limited transaction data. Statistics should be treated as indicative only.");


    public static Confidence fromSampleSize(final int sampleSize) {
        Confidence confidence = null;
        if ( sampleSize >= 50 ) {
            confidence = HIGH;
        } else if (sampleSize >= 20 && sampleSize < 50) {
            confidence = MEDIUM;
        } else if ( sampleSize >= 5 && sampleSize < 20 ){
            confidence = LOW;
        }

        return confidence;
    }

    public boolean isReliable() {
        return this == HIGH || this == MEDIUM;
    }

    private final String description;

    Confidence(final String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }
}
