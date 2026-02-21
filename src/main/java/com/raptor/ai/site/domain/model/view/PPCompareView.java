package com.raptor.ai.site.domain.model.view;

import com.raptor.ai.site.domain.model.common.Meta;

import java.util.List;

public record PPCompareView (int fromYear,
                             int toYear,
                             String sortBy, //"median" default

                             String direction,
                             List<PPDistributionView> areas, Meta metaData){
}
