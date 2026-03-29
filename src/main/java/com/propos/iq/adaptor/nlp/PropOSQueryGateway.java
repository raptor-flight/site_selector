package com.propos.iq.adaptor.nlp;


import com.propos.iq.ai.PropOSAIService;
import com.propos.iq.ai.PropOSSummarisationService;
import com.propos.iq.core.cache.PropOSConversationStore;
import com.propos.iq.domain.model.wrapper.PropOSResponse;
import com.propos.iq.domain.model.wrapper.PropOSScores;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Path("/propos")
public class PropOSQueryGateway {

    private static final String SESSION_COOKIE = "propos_session";
    private static final int SESSION_MAX_AGE = 60 * 60 * 4; // 4 hours

    private static final Pattern SCORES_PATTERN = Pattern.compile(
            "SCORES:\\s*investment=([\\d.]+),grade=([A-FU]),risk=([\\d.]+)," +
                    "riskRating=(\\w+(?:\\s+\\w+)?),opportunity=(\\w+),price=([\\d.]+)",
            Pattern.CASE_INSENSITIVE
    );
    private final Logger logger;
    private final PropOSAIService aiService;
    private final PropOSSummarisationService summarisationService;
    private final PropOSConversationStore conversationStore;

    @Inject
    public PropOSQueryGateway(final Logger logger,
                              final PropOSAIService aiService,
                              final PropOSSummarisationService summarisationService,
                              final PropOSConversationStore conversationStore) {
        super();
        this.logger = logger;
        this.aiService = aiService;
        this.summarisationService = summarisationService;
        this.conversationStore = conversationStore;
    }

    @GET
    @Path("/query")
    @Produces(MediaType.APPLICATION_JSON)
    public Response query(
            @QueryParam("q") final String userQuery,
            @QueryParam("session") final String sessionParam) {

        if (userQuery == null || userQuery.isBlank()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new PropOSResponse(userQuery, "", "Query parameter 'q' is required.", null))
                    .build();
        }

        // Resolve session ID — from query param, or generate new one
        final String sessionId = (sessionParam != null && !sessionParam.isBlank())
                ? sessionParam
                : UUID.randomUUID().toString();

        logger.infof("PropOS query [session=%s]: %s", sessionId, userQuery);

        try {
            // Step 1: Prepend conversation context from previous turns
            final String context = conversationStore.buildContext(sessionId, userQuery);
            final String enrichedQuery = context.isEmpty()
                    ? userQuery
                    : context + userQuery;

            // Step 2: Call AI — tools fire normally, no LangChain4j memory, no turn ordering issues
            final String rawResponse = aiService.chat(enrichedQuery);

            if (rawResponse == null || rawResponse.isBlank()) {
                logger.warnf("PropOS null/blank response [session=%s]", sessionId);
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                        .entity(new PropOSResponse(userQuery, "No response from intelligence layer.", "ERROR", null))
                        .build();
            }

            // Step 3: Extract scores and clean narrative
            final PropOSScores scores = extractScores(rawResponse);
            final String cleanResponse = rawResponse
                    .replaceAll("(?m)^SCORES:.*$", "")
                    .stripTrailing();

            // Step 4: Summarise in background for next turn — does not block response
            final String capturedQuery = userQuery;
            final String capturedResponse = cleanResponse;
            Thread.ofVirtual().start(() -> {
                try {
                    final String summary = summarisationService.summarise(
                            "User asked: " + capturedQuery + "\n\nResponse:\n" + capturedResponse
                    );
                    conversationStore.addTurn(sessionId, capturedQuery, summary);
                    logger.debugf("Summary stored for session [%s]", sessionId);
                } catch (Exception e) {
                    logger.warnf("Summarisation failed for session [%s]: %s", sessionId, e.getMessage());
                    conversationStore.addTurn(sessionId, capturedQuery, "Summary unavailable.");
                }
            });

            return Response.ok(new PropOSResponse(userQuery, cleanResponse, "OK", scores))
                    .cookie(new NewCookie.Builder(SESSION_COOKIE)
                            .value(sessionId)
                            .maxAge(SESSION_MAX_AGE)
                            .path("/")
                            .build())
                    .header("X-Session-Id", sessionId)
                    .build();

        } catch (Exception e) {
            logger.errorf("PropOS query error [session=%s]: %s", sessionId, e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new PropOSResponse(userQuery, e.getMessage(), "ERROR", null))
                    .build();
        }
    }

    @DELETE
    @Path("/session")
    @Produces(MediaType.APPLICATION_JSON)
    public Response clearSession(@QueryParam("session") final String sessionId) {
        if (sessionId != null && !sessionId.isBlank()) {
            conversationStore.clearSession(sessionId);
        }
        return Response.ok("{\"status\":\"cleared\"}").build();
    }

    private PropOSScores extractScores(final String response) {
        if (response == null) return null;
        final Matcher m = SCORES_PATTERN.matcher(response);
        if (!m.find()) {
            logger.warn("No SCORES line found in AI response");
            return null;
        }
        try {
            return new PropOSScores(
                    new BigDecimal(m.group(1)),
                    m.group(2).equalsIgnoreCase("UNKNOWN") ? null : m.group(2),
                    new BigDecimal(m.group(3)),
                    m.group(4),
                    m.group(5),
                    new BigDecimal(m.group(6))
            );
        } catch (Exception e) {
            logger.warnf("Failed to parse SCORES line: %s", e.getMessage());
            return null;
        }
    }
}