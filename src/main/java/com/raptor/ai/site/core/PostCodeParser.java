package com.raptor.ai.site.core;

import com.raptor.ai.site.domain.intents.PropertyQuery;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@ApplicationScoped
public class PostCodeParser {


    private final Pattern LAST_YEARS_PATTERN = Pattern.compile("last\\s+(\\d+)\\s+years?" , Pattern.CASE_INSENSITIVE);

    private final Pattern POSTCODE_PATTERN = Pattern.compile("\\b([A-Z]{1,2}\\d{1,2})(?:\\s?\\d[A-Z]{2})?\\b",
            Pattern.CASE_INSENSITIVE);


    @Inject
    Logger logger;
    public PostCodeParser() {
        super();
    }

    /**
     * Extracts the outward (primary) postcode from a full postcode.
     * Examples:
     * <ul>
     *     <li>B38 9TS → B38</li>
     *     <li>B1 1AA → B1</li>
     *     <li>B15 2TT → B15</li>
     * </ul>
     *
     * @param postCode the raw postcode input
     * @return the outward postcode, or null if input is null
     */
    public String extractPrimaryPostCode(final String postCode) {
        final String normalized = postCode.replaceAll("\\s+", "").toUpperCase();
        logger.infof("normalised postcode %s." , normalized);

        return normalized.replaceAll("([A-Z]{1,2}\\d{1,2}).*", "$1");
    }

    /**
     * Normalises and deduplicates outward postcodes from a query.
     * Ensures consistent comparison and aggregation.
     *
     * @param userQuery the parsed property query
     * @return a list of unique outward postcodes
     */
    public List<String> deDuplicateOutwardPostCodes(final PropertyQuery userQuery) {
        List<String> normalisedPostCodeList = null;

        if (userQuery.getPostCodes().size() == 1) {
            userQuery.getPostCodes().set(0, extractPrimaryPostCode(userQuery.getPostCodes().get(0)));
            normalisedPostCodeList = userQuery.getPostCodes();
        } else {
            final Set<String> distinctPostCodes = new LinkedHashSet<>(1);
            for (final String postCode : userQuery.getPostCodes()) {
                final String outwardPostCode = extractPrimaryPostCode(postCode).toUpperCase();
                logger.infof("outward postcode %s ", outwardPostCode);
                distinctPostCodes.add(outwardPostCode);
            }
            normalisedPostCodeList = new ArrayList<>(distinctPostCodes);
        }

        return normalisedPostCodeList;
    }

}
