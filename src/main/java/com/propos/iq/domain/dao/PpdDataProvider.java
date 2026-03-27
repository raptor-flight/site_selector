package com.propos.iq.domain.dao;

import com.propos.iq.domain.model.ppd.PropertyPricePaidRecord;

import java.util.List;

public interface PpdDataProvider {

    List<PropertyPricePaidRecord> find(final String outwardPostCode, final int fromYear, final int toYear);


}
