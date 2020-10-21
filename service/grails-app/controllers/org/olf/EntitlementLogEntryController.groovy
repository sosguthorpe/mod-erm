package org.olf

import org.olf.erm.EntitlementLogEntry

import com.k_int.okapi.OkapiTenantAwareController
import grails.converters.JSON
import grails.gorm.multitenancy.CurrentTenant
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import java.time.LocalDate

@Slf4j
@CurrentTenant
class EntitlementLogEntryController extends OkapiTenantAwareController<EntitlementLogEntry> {
  
  EntitlementLogEntryController() {
    super(EntitlementLogEntry)
  }

}

