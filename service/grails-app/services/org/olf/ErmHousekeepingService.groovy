package org.olf

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
