package org.olf;

import org.olf.erm.RemoteLicenseLink

import com.k_int.okapi.OkapiTenantAwareController

import grails.gorm.multitenancy.CurrentTenant
import groovy.util.logging.Slf4j

@Slf4j
@CurrentTenant
class RemoteLicenseLinkController extends OkapiTenantAwareController<RemoteLicenseLink>  {

  RemoteLicenseLinkController() {
    super(RemoteLicenseLink, true) // The true makes this read only. No POST/PUT/DELETE
  }
}
