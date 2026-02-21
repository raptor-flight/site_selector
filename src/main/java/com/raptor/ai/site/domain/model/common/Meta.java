package com.raptor.ai.site.domain.model.common;

import java.util.List;

public record Meta(int minSamples, int totalPostCodesInRequest, int totalReturned, List<String> requestedOutwards, List<String> resolvedOutwards, List<String> filteredOut) {
}
