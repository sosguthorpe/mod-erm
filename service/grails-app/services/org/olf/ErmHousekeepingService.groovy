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

  def coverageService

  public void triggerHousekeeping() {
    // An administrative process - attempt to coalesce any rogue coverage statements
    coverageService.coalesceCoverageStatements();
  }
}
