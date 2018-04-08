package org.olf

import grails.gorm.multitenancy.CurrentTenant
import com.k_int.okapi.OkapiTenantAwareController
import org.olf.kb.Package

@CurrentTenant
class PackageController extends OkapiTenantAwareController<Package>  {

  PackageController() {
    super(Package)
  }
}

