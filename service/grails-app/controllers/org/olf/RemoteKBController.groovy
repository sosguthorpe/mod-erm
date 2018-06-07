package org.olf

import org.olf.kb.RemoteKB

import com.k_int.okapi.OkapiTenantAwareController

import grails.gorm.multitenancy.CurrentTenant
import groovy.util.logging.Slf4j

@Slf4j
@CurrentTenant
class RemoteKBController extends OkapiTenantAwareController<RemoteKB>  {

  RemoteKBController() {
    super(RemoteKB)
  }
}

