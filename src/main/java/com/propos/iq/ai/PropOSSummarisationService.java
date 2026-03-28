package com.propos.iq.ai;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;
import jakarta.enterprise.context.ApplicationScoped;

@RegisterAiService
@ApplicationScoped
public interface PropOSSummarisationService {

    @SystemMessage("""
            You are a precise data summariser for PropOS IQ, a UK property intelligence platform.
            Your job is to extract and preserve key facts from property intelligence responses.
            Output plain text only. Be concise but factually complete.
            Always preserve: area names, postcodes, scores, grades, prices, risk levels,
            opportunity grades, flood risk, crime rates, EPC ratings, transport distances.
            Maximum 250 words per summary.
            """)
    String summarise(@UserMessage String responseToSummarise);
}