package org.olf

import grails.gorm.multitenancy.Tenants
import org.olf.kb.RemoteKB
import grails.events.annotation.Subscriber
import grails.gorm.multitenancy.WithoutTenant
import grails.gorm.transactions.Transactional
import org.olf.kb.CoverageStatement

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
    log.debug("Extend coverage statements on ${title}(${property}) with ${coverage_statements}");

    coverage_statements.each { cs ->
      // Do we already have any coverage statements for this item that overlap in any way with the coverage supplied?
      def existing_coverage = CoverageStatement.executeQuery("select cs from CoverageStatement as cs where cs.${property} = :t".toString(),[t:title]);

      if ( existing_coverage.size() == 0 ) {
        // no existing coverage -- create it
        log.debug("No existing coverage - create new record");
        def new_cs = new CoverageStatement();
        // This is clever groovy script to assign a property based on that prop name being in a variable,
        // property will be one of ti, pci or pti and this will set the appropriate one.
        new_cs."${property}" = title
        new_cs.startDate = cs.startDate
        new_cs.endDate = cs.endDate
        new_cs.startVolume = cs.startVolume
        new_cs.startIssue = cs.startIssue
        new_cs.endVolume = cs.endVolume
        new_cs.endIssue = cs.endIssue
        new_cs.save(flush:true, failOnError:true);
      }
      else {
        log.warn("Located existing coverage, determin extend or create additional");
      }
    }
  }
}
