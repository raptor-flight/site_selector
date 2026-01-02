package com.raptor.ai.site.core;

import com.raptor.ai.site.domain.model.ppd.PropertyPricePaidRecord;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.List;

@ApplicationScoped
public class MetricAlgo {

    @Inject
    Logger logger;
    public MetricAlgo() {
        super();
    }


    public<T extends PropertyPricePaidRecord> int getMedianValue(final List<T> values ) {
        /*** --- validation --- */
        if ( values == null || values.isEmpty()) {
            final String warnMessage = "no sorted collection found.";
            logger.warn(warnMessage);
            throw new IllegalArgumentException(warnMessage);
        }
        int medianValue = 0;
        int size = values.size();
        int midPoint = size / 2;

        /*** --- if odd, then middle value --- */
        if (values.size() % 2 == 1) {
            int pos = (values.size()) / 2;
            medianValue = Integer.parseInt(values.get(pos).getPrice().replaceAll("\"", ""));
        } else {
            int left = Integer.parseInt(values.get(midPoint - 1).getPrice().replaceAll("\"", ""));
            int right = Integer.parseInt(values.get(midPoint).getPrice().replaceAll("\"", ""));
            medianValue = (left + right) / 2;
        }

        return medianValue;
    }

}
