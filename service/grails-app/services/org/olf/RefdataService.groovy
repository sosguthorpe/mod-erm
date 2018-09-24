package org.olf

import grails.events.annotation.Subscriber
import org.olf.general.refdata.GrailsDomainRefdataHelpers

public class RefdataService {
    
  @Subscriber('okapi:tenant_created')
  public void onTenantCreation(tid) {
    log.debug("RefdataService::onTenantCreation(${tid})")
    // Skip this until we can work out whats going wrong...
    GrailsDomainRefdataHelpers.setDefaultsForTenant(tid)
  }
}
