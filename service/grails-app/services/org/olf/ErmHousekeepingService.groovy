package org.olf

import grails.gorm.transactions.Transactional

/**
 * This service works at the module level, it's often called without a tenant context.
 */
@Transactional
public class ErmHousekeepingService {

  def coverageService
  def entitlementLogService
  def subscriptionAgreementCleanupService

  public void triggerHousekeeping() {
    // An administrative process - attempt to coalesce any rogue coverage statements
    coverageService.coalesceCoverageStatements();
    entitlementLogService.triggerUpdate();

    // A process to ensure the correct start/end date is stored per agreement
    subscriptionAgreementCleanupService.triggerDateCleanup();
  }
}
