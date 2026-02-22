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
                distribution.confidence(), distribution.confidence().description()
        );
    }

    public PPCompareView compare(final Integer fromYear,
                                 final Integer toYear,
                                 final String postCodes,
                                 final SortBy sortBy,
                                 final SortDirection sortDirection,
                                 final int minSample,
                                 final RiskCriteria riskCriteria) {

        final List<String> requested = retrieveRequestedPostCodes(postCodes);
        final List<String> resolved = retrievedResolvedPostCodes(requested);
        final List<PPDistributionView> distributions = allPostCodeDistributions(resolved, fromYear, toYear);

        Comparator<PPDistributionView> comparator =
                (riskCriteria != null && riskCriteria.isActive())
                        ? riskComparator(riskCriteria)
                        : metricComparator(sortBy);

        final SortDirection dir = (sortDirection == null) ? SortDirection.ASC : sortDirection;
        if (dir == SortDirection.DESC) comparator = comparator.reversed();

        // optional but recommended: stable tie-breaks
        comparator = comparator
                .thenComparing(PPDistributionView::sampleSize, Comparator.reverseOrder())
                .thenComparing(PPDistributionView::postCode);

        final List<PPDistributionView> areas = keptPostCodes(distributions, minSample, comparator);

        final List<String> filteredOut = filteredOutPostCodes(distributions, minSample);

        final Meta metaData = new Meta(
                minSample,
                requested.size(),
                areas.size(),
                requested,
                resolved,
                filteredOut
        );

        return new PPCompareView(
                fromYear,
                toYear,
                (riskCriteria != null ? ("risk:" + riskCriteria.name().toLowerCase(java.util.Locale.UK))
                        : sortBy.name().toLowerCase(java.util.Locale.UK)),
                dir.name().toLowerCase(java.util.Locale.UK),
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

    private List<PPDistributionView> keptPostCodes(final List<PPDistributionView> distributions,
                                                   final int minSample,
                                                   final Comparator<PPDistributionView> comparator) {
        return distributions.stream()
                .filter(ppd -> ppd.sampleSize() >= minSample)
                .sorted(comparator)
                .toList();
    }

    private List<String> filteredOutPostCodes ( final List<PPDistributionView> distributions , final int minSample) {
        return distributions.stream()
                .filter(ppd -> ppd.sampleSize() < minSample)
                .map(PPDistributionView::postCode)
                .toList();
    }



    private Comparator<PPDistributionView> metricComparator(final SortBy sortBy) {
        return switch (sortBy) {
            case MEAN -> Comparator.comparingDouble(PPDistributionView::mean);
            case MEDIAN -> Comparator.comparingDouble(PPDistributionView::median);
        };
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

