package org.olf

import grails.gorm.multitenancy.Tenants
import org.olf.kb.RemoteKB
import grails.events.annotation.Subscriber
import grails.gorm.multitenancy.WithoutTenant
import grails.gorm.transactions.Transactional

/**
 * This service works at the module level, it's often called without a tenant context.
 */
@Transactional
public class ErmHousekeepingService {

  /**
   * This is called by the eventing mechanism - There is no web request context
   * there may be something funky to do with events and multi tenant setup.
   */
  @Subscriber('okapi:schema_update')
  public void onSchemaUpdate(tn, tid) {
    log.debug("ErmHousekeepingService::onSchemaUpdate(${tn},${tid})")
    // Skip this until we can work out whats going wrong...
    setupData(tn, tid);
  }

  private void setupData(tenantName, tenantId) {

    // leaving this here as an example - it works, but we don't actually want to do this in practice!
    Tenants.withId(tenantId) {

      // A special record for packages which are really defined locally - this is an exceptional situation
      RemoteKB local_kb = RemoteKB.findByName('LOCAL') ?: new RemoteKB( name:'LOCAL',
                                                                        rectype: new Long(1),
                                                                        active:Boolean.TRUE).save(flush:true, failOnError:true);
    
    }
  }
}
