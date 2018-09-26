package org.olf

import grails.events.Event
import grails.events.annotation.Subscriber
import org.olf.general.refdata.GrailsDomainRefdataHelpers

public class RefdataService {
    
  @Subscriber('okapi:tenant_schema_created')
  public void onTenantSchemaCreated(String new_schema_name) {
    
    log.debug("RefdataService::onTenantSchemaCreated(${new_schema_name})")
    // Skip this until we can work out whats going wrong...
    GrailsDomainRefdataHelpers.setDefaultsForTenant(new_schema_name)
  }
}
