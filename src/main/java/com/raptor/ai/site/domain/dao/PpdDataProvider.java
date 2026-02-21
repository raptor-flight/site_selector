package com.raptor.ai.site.domain.dao;

import com.raptor.ai.site.domain.model.ppd.PropertyPricePaidRecord;

import java.util.List;

public interface PpdDataProvider {

    List<PropertyPricePaidRecord> find(final String outwardPostCode, final int fromYear, final int toYear);


}
