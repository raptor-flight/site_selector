package com.propos.iq.adaptor.nlp;


import com.propos.iq.ai.PropOSAIService;
import com.propos.iq.domain.model.wrapper.PropOSResponse;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestResponse;

@Path("/propos")
public class PropOSQueryGateway {

    private final Logger logger;
    private final PropOSAIService aiService;

    @Inject
    public PropOSQueryGateway(final Logger logger,
                              final PropOSAIService aiService) {
        super();
        this.logger = logger;
        this.aiService = aiService;
    }

    @GET
    @Path("/query")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResponse<PropOSResponse> query(@QueryParam("q") final String userQuery) {

        if (userQuery == null || userQuery.isBlank()) {
            return RestResponse.status(
                    RestResponse.Status.BAD_REQUEST,
                    new PropOSResponse(userQuery, "","Query parameter 'q' is required."));

        }

        logger.infof("PropOS query: %s", userQuery);

        try {
            final String response = aiService.chat(userQuery);
            return RestResponse.ok(new PropOSResponse(userQuery, response, "OK"));
        } catch (Exception e) {
            logger.errorf("PropOS query error: %s", e.getMessage());
            return RestResponse.status(
                    RestResponse.Status.INTERNAL_SERVER_ERROR,
                    new PropOSResponse(userQuery, e.getMessage(), "ERROR"));
        }
    }
}
