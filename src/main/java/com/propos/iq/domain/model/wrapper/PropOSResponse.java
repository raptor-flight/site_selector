package com.propos.iq.domain.model.wrapper;

public record PropOSResponse(
        String query,
        String response,
        String status
) {}