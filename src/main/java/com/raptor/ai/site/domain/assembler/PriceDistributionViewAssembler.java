package com.raptor.ai.site.domain.assembler;


import com.raptor.ai.site.core.PostCodeParser;
import com.raptor.ai.site.domain.model.analytics.PriceDistribution;
import com.raptor.ai.site.domain.model.common.Meta;
import com.raptor.ai.site.domain.model.common.RiskCriteria;
import com.raptor.ai.site.domain.model.common.SortBy;
import com.raptor.ai.site.domain.model.common.SortDirection;
import com.raptor.ai.site.domain.model.view.PPCompareView;
import com.raptor.ai.site.domain.model.view.PPDistributionView;
import com.raptor.ai.site.engine.api.PriceAnalyticsEngine;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.*;

@ApplicationScoped
public class PriceDistributionViewAssembler {

    @Inject
    PriceAnalyticsEngine priceAnalyticsEngine;

    @Inject
    PostCodeParser postCodeParser;

    @Inject
    Logger logger;

    public PriceDistributionViewAssembler() {
        super();
    }

    public PPDistributionView build(final String outwardPostCode, int fromYear, int toYear) {
        final PriceDistribution distribution = priceAnalyticsEngine.distribution(
                postCodeParser.extractPrimaryPostCode(outwardPostCode.toUpperCase(Locale.UK)),
                fromYear,
                toYear
        );

        double iqr = distribution.percentile75() - distribution.percentile25();
        double cv = (distribution.mean() == 0.0) ? 0.0 : distribution.stdDev() / distribution.mean();
        double iqrToMedian = (distribution.median() == 0.0) ? 0.0 : iqr / distribution.median();

        return new PPDistributionView(
                distribution.outwardPostCode(), fromYear, toYear,
                distribution.sampleSize(), distribution.mean(), distribution.median(),
                distribution.stdDev(), distribution.min(), distribution.max(),
                distribution.percentile25(), distribution.percentile75(),
                iqr, cv, iqrToMedian,
                distribution.confidence()
        );
    }

    public PPCompareView compare(final Integer fromYear,
                                 final Integer toYear,
                                 final String postCodes,
                                 final SortBy sortBy,
                                 final SortDirection sortDirection,
                                 final int minSample) {

        // (i) requested
        final List<String> requested = this.retrieveRequestedPostCodes(postCodes);

        // (ii) resolved
        final List<String> resolved = this.retrievedResolvedPostCodes(requested);

        // (iii) all distributions (unfiltered)
        final List<PPDistributionView> distributions = this.allPostCodeDistributions(resolved, fromYear, toYear);

        // (iv) kept + sorted
        final List<PPDistributionView> areas = this.keptPostCodes(distributions, sortBy, sortDirection, minSample);

        // (v) filtered out (based on ALL distributions)
        final List<String> filteredOut = this.filteredOutPostCodes(distributions, minSample);

        final Meta metaData = new Meta(
                minSample,
                requested.size(),
                areas.size(),
                requested,
                resolved,
                filteredOut
        );

        logger.infof(
                "requested=%s size=%d | resolved=%s size=%d | filteredOut=%s size=%d | areas=%d",
                requested, requested.size(),
                resolved, resolved.size(),
                filteredOut, filteredOut.size(),
                areas.size()
        );

        return new PPCompareView(
                fromYear,
                toYear,
                sortBy.name().toLowerCase(java.util.Locale.UK),
                sortDirection == null ? "asc" : sortDirection.name().toLowerCase(java.util.Locale.UK),
                areas,
                metaData
        );
    }


    private List<String> retrieveRequestedPostCodes(final String postCodes) {
        return Arrays.stream(postCodes.split(","))
                .map(s -> s.toUpperCase(Locale.UK))
                .map(String::trim)
                .toList();
    }

    private List<String> retrievedResolvedPostCodes(final List<String> requestedPostCodes ) {
        return requestedPostCodes.stream()
                .map(s -> postCodeParser.extractPrimaryPostCode(s.toUpperCase(java.util.Locale.UK)))
                .filter(Objects::nonNull)
                .distinct()
                .toList();
    }

    private List<PPDistributionView> allPostCodeDistributions(final List<String> resolvedPostCodes ,
                                                              final int fromYear, final int toYear) {
        return resolvedPostCodes.stream()
                .map(outwards -> this.build(outwards , fromYear , toYear))
                .filter(Objects::nonNull)
                .toList();
    }

    private List<PPDistributionView> keptPostCodes(final List<PPDistributionView> distributions, final SortBy sortBy ,
                                                   final SortDirection sortDirection, final int minSample) {
        return distributions.stream()
                .filter(ppd -> ppd.sampleSize() >= minSample)
                .sorted(metricComparator(sortBy , sortDirection))
                .toList();
    }

    private List<String> filteredOutPostCodes ( final List<PPDistributionView> distributions , final int minSample) {
        return distributions.stream()
                .filter(ppd -> ppd.sampleSize() < minSample)
                .map(PPDistributionView::postCode)
                .toList();
    }



    private Comparator<PPDistributionView> metricComparator(final SortBy sortBy,
                                                            final SortDirection direction) {
        final SortDirection dir = (direction == null) ? SortDirection.ASC : direction;

        final Comparator<PPDistributionView> primary = switch (sortBy) {
            case MEAN -> Comparator.comparingDouble(PPDistributionView::mean);
            case MEDIAN -> Comparator.comparingDouble(PPDistributionView::median);
        };

        final Comparator<PPDistributionView> comparator = primary.thenComparing(PPDistributionView::postCode);

        return dir == SortDirection.DESC ? comparator.reversed() : comparator;
    }

    private Comparator<PPDistributionView> riskComparator( final RiskCriteria riskMode ) {
        Comparator<PPDistributionView> riskComparator;

        switch (riskMode) {
            case VOLATILITY -> riskComparator = Comparator.comparingDouble(PPDistributionView::stdDev);
            case CV -> riskComparator = Comparator.comparingDouble(v -> v.mean() == 0.0 ? Double.POSITIVE_INFINITY : v.stdDev() / v.mean());
            case IQR -> riskComparator = Comparator.comparingDouble(v -> v.percentile75() - v.percentile25());
            case IQR_TO_MEDIAN -> riskComparator = Comparator.comparingDouble(PPDistributionView::iqrToMedian);
            default -> riskComparator = Comparator.comparingDouble(PPDistributionView::median);
        }

        return riskComparator;
    }


}

