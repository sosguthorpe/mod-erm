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
public class CoverageExtenderService {

  /**
   * Given a list of coverage statements, check that we already record the extents of the coverage
   * for the given title.
   * Shared functionality between title objects like
   * org.olf.kb.TitleInstance; org.olf.kb.PlatformTitleInstance; org.olf.kb.PackageContentItem; which
   * allows a coverage block to grow and expand as we see more coverage statements.
   * handles split coverage and other edge cases.
   * @param title The title (org.olf.kb.TitleInstance, org.olf.kb.PlatformTitleInstance, org.olf.kb.PackageContentItem, etc) we are talking about
   * @param coverage_statements The array of coverage statements [ [ startDate:YYYY-MM-DD, startVolume:...], [ stateDate:....
   * @param property the reciprocal property on CoverageStatement we will be setting - one of pci pti ti depending upon what we are specifying
   */
  public void extend(title, coverage_statements, property) {
    log.debug("Extend coverage statements on ${title} with ${coverage_statements}");
  }
}
