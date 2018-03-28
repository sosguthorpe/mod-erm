package org.olf

import grails.gorm.multitenancy.CurrentTenant
import com.k_int.okapi.OkapiTenantAwareController
import org.olf.kb.RemoteKB

@CurrentTenant
class RemoteKBController extends OkapiTenantAwareController<RemoteKB>  {

  RemoteKBController() {
    super(RemoteKB)
  }
}

