package org.olf

import org.olf.erm.Entitlement

import com.k_int.okapi.OkapiTenantAwareController

import grails.gorm.multitenancy.CurrentTenant
import groovy.util.logging.Slf4j


/**
 * Access to Entitlement resources
 */
@Slf4j
@CurrentTenant
class EntitlementController extends OkapiTenantAwareController<EntitlementController>  {

  EntitlementController() {
    super(Entitlement)
  }
  
  def external() {
    Entitlement ent = new Entitlement ()
    ent.properties = params
    
    // Force external type.
    ent.type = 'external'
    
    // Ensure we have uppercase reference.
    ent.authority = ent.authority?.toUpperCase()
    respond ent
  }
}

