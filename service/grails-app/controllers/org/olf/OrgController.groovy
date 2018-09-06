package org.olf

import org.olf.general.Org

import com.k_int.okapi.OkapiTenantAwareController

import grails.gorm.multitenancy.CurrentTenant
import groovy.util.logging.Slf4j

@Slf4j
@CurrentTenant
class OrgController extends OkapiTenantAwareController<Org>  {

  DependentServiceProxyService dependentServiceProxyService
  
  OrgController() {
    super(Org)
  }
  
  public find(String id) {
    respond dependentServiceProxyService.coordinateOrg(id)
  }
}

