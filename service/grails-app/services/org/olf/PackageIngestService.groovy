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
public class PackageIngestService {

  /**
   * Load the paackage data (Given in the agreed canonical json package format) into the KB
   */
  public Long upsertPackage(Map package_data) {
    def result = null;
    log.debug("PackageIngestService::upsertPackage(...)");

    return result;
  }
}
