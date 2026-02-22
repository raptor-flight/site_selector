package com.raptor.ai.site.adaptor.system;


import com.raptor.ai.site.domain.assembler.PriceDistributionViewAssembler;
import com.raptor.ai.site.domain.model.common.RiskCriteria;
import com.raptor.ai.site.domain.model.common.SortBy;
import com.raptor.ai.site.domain.model.common.SortDirection;
import com.raptor.ai.site.domain.model.exception.APIError;
import com.raptor.ai.site.domain.model.view.PPDistributionView;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestResponse;


@Path("/price")
public class APIPriceGateway {


    private final Logger logger;
    private final PriceDistributionViewAssembler assembler;

    @Inject
    public APIPriceGateway(final PriceDistributionViewAssembler assembler, final Logger logger) {
        super();
        this.assembler = assembler;
        this.logger = logger;
    }

    @GET
    @Path("/distribution/{outward-postcode}")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResponse<PPDistributionView> distribution(@PathParam("outward-postcode") final String outwardPostCode,
                                                         @QueryParam("fromYear") final Integer fromYear,
                                                         @QueryParam("toYear") final Integer toYear) {

        RestResponse response;
        /***--- validation ---*/
        if ( outwardPostCode == null || outwardPostCode.isBlank()) {
            final String warningMessage = "no primary post-code found.";
            logger.warn(warningMessage);
            response = RestResponse.<Object>status(RestResponse.Status.BAD_REQUEST, warningMessage);
        } else
            if ( fromYear == null || toYear == null || fromYear <= 0 || toYear <= 0 || fromYear > toYear) {
            final String warningMessage = "invalid range criteria found.";
            logger.warn(warningMessage);
            response = RestResponse.<Object>status(RestResponse.Status.BAD_REQUEST, warningMessage);
        } else {
            response = RestResponse.ok(assembler.build(outwardPostCode, fromYear, toYear));
        }

        return response;
    }
//GET /price/compare?outward=B38,B15,B16&fromYear=2000&toYear=2025
    @GET
    @Path("/compare")
    @Produces(MediaType.APPLICATION_JSON)
    public RestResponse compare(@QueryParam("outward") final String outwards,
                                @QueryParam("fromYear") final Integer fromYear,
                                @QueryParam("toYear") final Integer toYear,
                                @DefaultValue("median") @QueryParam("sortBy") final String sortBy,
                                @DefaultValue("asc") @QueryParam("direction") final String direction,
                                @DefaultValue("0") @QueryParam("minSamples") final Integer minSample,
                                @DefaultValue("none") @QueryParam("risk") final String risk) {

        /*** --- validation --- */
        if ( outwards == null || outwards.isBlank() ) {
            return RestResponse.status(RestResponse.Status.BAD_REQUEST, new APIError("Missing outward postcodes." , "MISSING_OUTWARDS"));
        }
        if (fromYear == null || toYear == null || fromYear <= 0 || toYear <= 0 || fromYear > toYear) {
            return RestResponse.status(RestResponse.Status.BAD_REQUEST, new APIError("Invalid year range." , "INVALID_YEAR_RANGE"));
        }

        final SortBy sortMode = SortBy.from(sortBy);
        logger.infof("sort mode %s." , sortMode);

        SortDirection sortDirection = SortDirection.from(direction);
        logger.infof("sort direction %s." , sortDirection);

        RiskCriteria riskType = RiskCriteria.from(risk);

        return RestResponse.ok(assembler.compare(fromYear , toYear, outwards, sortMode, sortDirection, minSample, riskType));
    }

}
