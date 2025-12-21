package com.raptor.ai.site.adaptor;


import com.raptor.ai.site.domain.delegate.LandRegistryDelegate;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

@Path("ppd_data")
public class SiteSelectorGateway {

    /*** --- members --- */
    @Inject
    LandRegistryDelegate landRegistry;
    @Inject
    Logger logger;

    @GET
    @Path("details")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPPDRecords() {
        String response = landRegistry.retrieveDataSchema();
        return Response.status(Response.Status.OK)
                .entity(response)
                .build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("average/{postcode}")
    public Double getAveragePriceByPostCode(@PathParam("postcode") final String postCode) {
        double averagePrice = landRegistry.retrieveAverageValueByPostCode(postCode);
        logger.infof("average price for the post-code %s is %s" , postCode , averagePrice);
        return averagePrice;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("average/primary/{postcode}")
    public Double getAveragePriceByPrimaryPostCode(@PathParam("postcode") final String postCode) {
        double averagePrice = landRegistry.retrieveAverageValueByPrimaryPostCode(postCode);
        logger.infof("average price for the post-code %s is %s" , postCode , averagePrice);
        return averagePrice;
    }
}
