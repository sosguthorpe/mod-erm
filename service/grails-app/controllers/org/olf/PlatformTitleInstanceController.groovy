package org.olf

import org.olf.kb.PlatformTitleInstance

import com.k_int.okapi.OkapiTenantAwareController

import grails.gorm.multitenancy.CurrentTenant
import groovy.util.logging.Slf4j

@Slf4j
@CurrentTenant
class PlatformTitleInstanceController extends OkapiTenantAwareController<PlatformTitleInstance>  {

  PlatformTitleInstanceController() {
    super(PlatformTitleInstance)
  }
}

