package com.raptor.ai.site.adaptor.nlp;


import com.raptor.ai.site.core.JSONUtils;
import com.raptor.ai.site.domain.delegate.LandRegistryDelegate;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestResponse;

@Path("/nlp")
public class SiteQueryNLPGateway {

    @Inject
    Logger logger;

    @Inject
    LandRegistryDelegate landRegistryDelegate;

    public SiteQueryNLPGateway() {
        super();
    }


    @GET
    @Path("query")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResponse<?> query(@QueryParam("q") final String userQuery ) {
        /*** --- arg validation --- */
        if ( userQuery == null || userQuery.isEmpty()) {
            final String warnMessage = "Query parameter 'q' not found.";
            logger.warn(warnMessage);
            return RestResponse.status(RestResponse.Status.BAD_REQUEST , warnMessage);
        }

        logger.infof("query %s" , userQuery);
        return RestResponse.status(RestResponse.Status.OK ,  JSONUtils.toJSON(landRegistryDelegate.handle(userQuery)));
    }
}
