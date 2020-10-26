package org.olf

import grails.gorm.multitenancy.CurrentTenant
import groovy.util.logging.Slf4j
import com.k_int.okapi.OkapiTenantAwareController
import org.olf.kb.Platform


/**
 * Explore package content items - the KB
 */
@Slf4j
@CurrentTenant
class PlatformController extends OkapiTenantAwareController<Platform>  {

  PlatformController() {
    super(Platform)
  }

}

