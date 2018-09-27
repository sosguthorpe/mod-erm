package org.olf

import org.olf.kb.ElectronicResource

import com.k_int.okapi.OkapiTenantAwareController

import grails.gorm.multitenancy.CurrentTenant
import groovy.util.logging.Slf4j

@Slf4j
@CurrentTenant
class EresourceController extends OkapiTenantAwareController<ElectronicResource>  {

  EresourceController() {
    // True means read only. This should block post and puts to this.
    super(ElectronicResource, true)
  }
}

