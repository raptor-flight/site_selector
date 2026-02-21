package com.raptor.ai.site.core;

import com.raptor.ai.site.domain.model.ppd.PropertyPricePaidRecord;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
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

    public double getPercentile(final List<PropertyPricePaidRecord> list, final double p) {
        List<PropertyPricePaidRecord> sorted = new ArrayList<>(list);
        if (list == null || list.isEmpty()) {
            throw new IllegalArgumentException("List is empty");
        }

        if (p < 0.0 || p > 1.0) {
            throw new IllegalArgumentException("Percentile must be between 0 and 1");
        }

        // Ensure sorted by price
        sorted.sort(Comparator.comparingDouble(
                r -> Double.parseDouble((r.getPrice() != null && !
                        r.getPrice().trim().equals("") ) ?
                        r.getPrice().replace("\"", "").trim() : "0.0")
        ));

        int n = sorted.size();
        double pos = p * (n - 1);
        int lower = (int) Math.floor(pos);
        int upper = (int) Math.ceil(pos);

        double lowerValue = Double.parseDouble(sorted.get(lower).getPrice().replace("\"","").trim());
        double upperValue = Double.parseDouble(sorted.get(upper).getPrice().replace("\"","").trim());

        if (lower == upper) {
            return lowerValue;
        }

        double weight = pos - lower;
        return lowerValue * (1 - weight) + upperValue * weight;
    }

    public double getPercentile(final double[] values, final double p) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("List is empty");
        }
        if (p < 0.0 || p > 1.0) {
            throw new IllegalArgumentException("Percentile must be between 0 and 1");
        }

        /*** sort price array for SMILE **/
        Arrays.sort(values);
        int n = values.length;
        double pos = p * (n - 1);
        int lower = (int) Math.floor(pos);
        int upper = (int) Math.ceil(pos);

        if (lower == upper) {
            return values[lower];
        }

        double weight = pos - lower;

        return values[lower] * (1 - weight) + values[upper] * weight;
    }




}
