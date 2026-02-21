package com.raptor.ai.site.domain.model.common;

public enum Confidence {
    HIGH,
    MEDIUM,
    LOW,
    VERY_LOW;

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
}
