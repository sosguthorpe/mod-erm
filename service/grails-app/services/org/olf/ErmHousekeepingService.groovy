package org.olf

import static grails.gorm.multitenancy.Tenants.*

import org.olf.kb.RemoteKB

import grails.events.annotation.*
import grails.gorm.multitenancy.WithoutTenant


/**
 * This service works at the module level, it's often called without a tenant context.
 */
@WithoutTenant
class ErmHousekeepingService {

  @Subscriber('okapi:schema_update')
  void onSchemaUpdate(tenantName, tenantId) {
    log.debug("ErmHousekeepingService::onSchemaUpdate(${tenantName},${tenantId})")
    // Skip this until we can work out whats going wrong...
    withId(tenantId) {

      // A special record for packages which are really defined locally - this is an exceptional situation
      RemoteKB local_kb = RemoteKB.findByName('LOCAL') ?: new RemoteKB( name:'LOCAL',
                                                                        type:'LOCAL',
                                                                        rectype: new Long(1),
                                                                        active:Boolean.TRUE).save(flush:true, failOnError:true)
  
      // A default record for remote GOKb - Switched off by default, will need to add other properties shortly
      RemoteKB gokb = RemoteKB.findByName('GOKb') ?: new RemoteKB( name:'GOKb',
                                                                   type:'kb-oai',
                                                                   rectype: new Long(1),
                                                                   active:Boolean.FALSE).save(flush:true, failOnError:true)
    }

  }
}
