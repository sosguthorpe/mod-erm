package org.olf;

import org.olf.erm.UsageDataProvider

import com.k_int.okapi.OkapiTenantAwareController

import grails.gorm.multitenancy.CurrentTenant
import groovy.util.logging.Slf4j

@Slf4j
@CurrentTenant
class UsageDataProviderController extends OkapiTenantAwareController<UsageDataProvider>  {

  UsageDataProviderController() {
    super(UsageDataProvider, true) // The true makes this read only. No POST/PUT/DELETE
  }
}
