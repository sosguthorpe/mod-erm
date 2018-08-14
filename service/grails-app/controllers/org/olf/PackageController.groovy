package org.olf

import org.olf.kb.Pkg

import com.k_int.okapi.OkapiTenantAwareController

import grails.gorm.multitenancy.CurrentTenant
import groovy.util.logging.Slf4j

@Slf4j
@CurrentTenant
class PackageController extends OkapiTenantAwareController<Pkg>  {

  PackageController() {
    super(Pkg)
  }
}

