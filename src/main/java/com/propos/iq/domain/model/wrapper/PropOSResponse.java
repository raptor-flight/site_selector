package com.propos.iq.domain.model.wrapper;

public record PropOSResponse(
        String query,
        String response,
        String status,
        PropOSScores scores
) {
    // Convenience constructor for error responses with no scores
    public PropOSResponse(String query, String response, String status) {
        this(query, response, status, null);
    }
}
