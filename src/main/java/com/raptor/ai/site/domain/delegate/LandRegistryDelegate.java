package com.raptor.ai.site.domain.delegate;

import com.raptor.ai.site.service.LandRegistry;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.logging.Logger;
import smile.data.DataFrame;

import java.math.BigDecimal;
import java.math.RoundingMode;

@ApplicationScoped
public class LandRegistryDelegate {

    @Inject
    LandRegistry landRegistryService;
    @Inject
    Logger logger;

    public LandRegistryDelegate() {
        super();
    }

    public String retrieveDataSchema() {
        DataFrame dataFrame = landRegistryService.getPPDetails();
        logger.infof("summary -> %s" , dataFrame.summary());
        logger.info(dataFrame.structure());
        logger.info(dataFrame.slice(0, 1000));
        return dataFrame.structure().toString();
    }

    public Double retrieveAverageValueByPostCode(final String postCode) {
        var bd = new BigDecimal(landRegistryService.getAveragePriceByPostCode(postCode));
        bd = bd.setScale(2, RoundingMode.CEILING);
        return bd.doubleValue();
    }

    public Double retrieveAverageValueByPrimaryPostCode(final String postCode) {
        final String[] splitPostCode = postCode.split("//s+");
        var bd = new BigDecimal(landRegistryService.getAveragePriceByPrimaryPostCode(postCode));
        bd = bd.setScale(2, RoundingMode.CEILING);
        return bd.doubleValue();

    }
}
