package com.raptor.ai.site.core.nlp;


import com.raptor.ai.site.domain.intents.PropertyQuery;
import com.raptor.ai.site.domain.intents.PropertyQueryType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.time.Year;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ApplicationScoped
public class PropertyQueryParser {
    @Inject
    Logger logger;

    private final Pattern BHAM_POST_CODE_PATTERN = Pattern.compile("\\b(B\\d{1,2})\\b" , Pattern.CASE_INSENSITIVE);
    private final Pattern LAST_YEARS_PATTERN = Pattern.compile("last\\d+(\\d+)\\s+years?" , Pattern.CASE_INSENSITIVE);

    public PropertyQueryParser() {
        super();
    }

    public PropertyQuery parse(final String query) {
        final PropertyQuery propertyQuery = new PropertyQuery();
        /** --- lowercase for consistency --- */
        final String text = query.toLowerCase();

        /*** --- (i) detect intent --- */
        if (text.contains("average") || text.contains("mean")) {
            propertyQuery.setType(PropertyQueryType.AVERAGE_PRICE);
        }

        /*** --- (ii) detect post-code (B38, B15, etc.) --- */
        final Matcher pcMatcher = BHAM_POST_CODE_PATTERN.matcher(query);
        if ( pcMatcher.find()) {
            propertyQuery.setPostCode(pcMatcher.group(1).toUpperCase());
        }

        /*** --- (iii) Detect "last N years" --- */
        final Matcher yearsMatcher = LAST_YEARS_PATTERN.matcher(text);
        if ( yearsMatcher.find() ) {
            final int years = Integer.parseInt(yearsMatcher.group(1));
            final int currentYear = Year.now().getValue();
            propertyQuery.setFromYear(years);
            propertyQuery.setToYear(currentYear);
        }

        return propertyQuery;
    }
}
