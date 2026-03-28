package com.propos.iq.core.cache;

import jakarta.enterprise.context.ApplicationScoped;
import org.jboss.logging.Logger;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@ApplicationScoped
public class PropOSConversationStore {

    private static final int MAX_TURNS = 5;

    private final Logger logger;
    private final Map<String, Deque<ConversationTurn>> sessions = new ConcurrentHashMap<>();

    public PropOSConversationStore(final Logger logger) {
        this.logger = logger;
    }

    public void addTurn(final String sessionId,
                        final String userQuery,
                        final String summary) {
        final Deque<ConversationTurn> history = sessions
                .computeIfAbsent(sessionId, k -> new ArrayDeque<>());
        history.addLast(new ConversationTurn(userQuery, summary));
        // Keep only the last MAX_TURNS turns
        while (history.size() > MAX_TURNS) {
            history.removeFirst();
        }
        logger.debugf("Session [%s] now has %d turns", sessionId, history.size());
    }

    /**
     * Build a context block to prepend to the next user message.
     * Returns empty string if no history exists for this session.
     */
    public String buildContext(final String sessionId) {
        final Deque<ConversationTurn> history = sessions.get(sessionId);
        if (history == null || history.isEmpty()) {
            return "";
        }
        final StringBuilder sb = new StringBuilder();
        sb.append("CONVERSATION CONTEXT — what has been discussed in this session:\n\n");
        int turn = 1;
        for (final ConversationTurn t : history) {
            sb.append("Turn ").append(turn++).append(":\n");
            sb.append("  User asked: ").append(t.userQuery()).append("\n");
            sb.append("  Key findings: ").append(t.summary()).append("\n\n");
        }
        sb.append("---\n");
        sb.append("Now answer the following new question, referring to the above context where relevant:\n\n");
        return sb.toString();
    }

    public void clearSession(final String sessionId) {
        sessions.remove(sessionId);
        logger.debugf("Session [%s] cleared", sessionId);
    }

    public record ConversationTurn(String userQuery, String summary) {}
}