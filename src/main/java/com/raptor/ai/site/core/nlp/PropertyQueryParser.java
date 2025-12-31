package com.raptor.ai.site.core.nlp;


import com.raptor.ai.site.domain.intents.IntentMetrics;
import com.raptor.ai.site.domain.intents.PropertyIntentQueryType;
import com.raptor.ai.site.domain.intents.PropertyQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.time.Year;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ApplicationScoped
public class PropertyQueryParser {
    @Inject
    Logger logger;

    private final Pattern DISTRICT_POST_CODE_PATTERN = Pattern.compile("\\b(B\\d{1,2})\\b" , Pattern.CASE_INSENSITIVE);

    private final Pattern FULL_POST_CODE_PATTERN = Pattern.compile("\\b[A-Z]{1,2}\\d[A-Z\\d]?\\s?\\d[A-Z]{2}\\b" , Pattern.CASE_INSENSITIVE);
    private final Pattern LAST_YEARS_PATTERN = Pattern.compile("last\\s+(\\d+)\\s+years?" , Pattern.CASE_INSENSITIVE);

    private final Pattern POSTCODE_PATTERN = Pattern.compile("\\b([A-Z]{1,2}\\d{1,2})(?:\\s?\\d[A-Z]{2})?\\b",
            Pattern.CASE_INSENSITIVE);


    public PropertyQueryParser() {
        super();
    }

    public PropertyQuery parse(final String query) {
        final PropertyQuery propertyQuery = new PropertyQuery();
        /** --- lowercase for consistency --- */
        final String text = query.toLowerCase();

        /*** --- detect average intent --- */
        if (text.contains("average") || text.contains("mean")) {
            propertyQuery.setMetric(IntentMetrics.AVERAGE_PRICE);
        }

        if ( text.contains("compare") ) {
            propertyQuery.setType(PropertyIntentQueryType.COMPARE_AREAS);
        } else {
            propertyQuery.setType(PropertyIntentQueryType.AVERAGE_PRICE);
        }


        /*** --- detect post-code (B38, B15, etc.) either outward (primary) or full. --- */
        final Matcher pcMatcher = POSTCODE_PATTERN.matcher(text);
        //List<String> postCodes = new ArrayList<>(1);
        final Set<String> _postCodes = new LinkedHashSet<>(1);
        while ( pcMatcher.find() ) {
            _postCodes.add(pcMatcher.group().toUpperCase());
            //postCodes.add(pcMatcher.group().toUpperCase());
        }

        if ( !_postCodes.isEmpty()) {
            propertyQuery.setPostCodes(new ArrayList<>(_postCodes));
        }

        /*
        if ( pcMatcher.find()) {
            propertyQuery.setPostCode(pcMatcher.group().toUpperCase());
        }*/

        /*** --- Detect "last N years" --- */
        final Matcher yearsMatcher = LAST_YEARS_PATTERN.matcher(text);
        if ( yearsMatcher.find() ) {
            final int years = Integer.parseInt(yearsMatcher.group(1));
            final int currentYear = Year.now().getValue();
            propertyQuery.setFromYear(currentYear - years);
            propertyQuery.setToYear(currentYear);
        }

        return propertyQuery;
    }
}
