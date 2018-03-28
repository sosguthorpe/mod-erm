package org.olf

import grails.gorm.transactions.Transactional
import grails.events.annotation.*
import org.olf.kb.RemoteKB
import grails.gorm.multitenancy.Tenants
import grails.gorm.multitenancy.CurrentTenant
import grails.gorm.multitenancy.WithoutTenant


/**
 * This service works at the module level, it's often called without a tenant context.
 */
class ErmHousekeepingService {

  @Subscriber('okapi:schema_update')
  void onSchemaUpdate(tenantId, schemaName) {
    log.debug("ErmHousekeepingService::onSchemaUpdate(${tenantId},${schemaName})");
  }

  private void setupData(enantId, schemaName) {

    // Skip this until we can work out whats going wrong...
    Tenants.withId(schemaName) {
          // A special record for packages which are really defined locally - this is an exceptional situation
          RemoteKB local_kb = RemoteKB.findByName('LOCAL') ?: new RemoteKB( name:'LOCAL',
                                                                            type:'LOCAL',
                                                                            rectype: new Long(1),
                                                                            active:Boolean.TRUE).save(flush:true, failOnError:true);
    
          // A default record for remote GOKb - Switched off by default, will need to add other properties shortly
          RemoteKB gokb = RemoteKB.findByName('GOKb') ?: new RemoteKB( name:'GOKb',
                                                                       type:'kb-oai',
                                                                       rectype: new Long(1),
                                                                       active:Boolean.FALSE).save(flush:true, failOnError:true);
    }

  }
}
