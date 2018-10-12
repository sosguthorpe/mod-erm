package org.olf

import grails.gorm.multitenancy.CurrentTenant
import groovy.util.logging.Slf4j
import com.k_int.okapi.OkapiTenantAwareController
import org.olf.erm.Entitlement
import org.olf.kb.ErmResource
import grails.converters.JSON


/**
 * Access to Entitlement resources
 */
@Slf4j
@CurrentTenant
class EntitlementController extends OkapiTenantAwareController<EntitlementController>  {

  EntitlementController() {
    super(Entitlement)
  }
}

