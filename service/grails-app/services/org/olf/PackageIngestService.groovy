package org.olf

import grails.gorm.multitenancy.Tenants
import org.olf.kb.RemoteKB
import grails.events.annotation.Subscriber
import grails.gorm.multitenancy.WithoutTenant
import grails.gorm.transactions.Transactional
import org.olf.kb.Package;

/**
 * This service works at the module level, it's often called without a tenant context.
 */
@Transactional
public class PackageIngestService {

  /**
   * Load the paackage data (Given in the agreed canonical json package format) into the KB
   * @return id of package upserted
   */
  public String upsertPackage(String tenantId, Map package_data) {
    
    def result = null;
    log.debug("PackageIngestService::upsertPackage(${tenantId},...)");

    Tenants.withId(tenantId) {
      result = internalUpsertPackage(tenant,package_data);
    }
  }

  private String internalUpsertPackage(String tenant, Map package_data) {

    def result = '';

    log.debug("Package header: ${package_data.header}");

    // header.packageSlug contains the package maintainers authoritative identifier for this package.
    def pkg = Package.findBySourceAndReference(package_data.header.packageSource, package_data.header.packageSlug)

    if ( pkg == null ) {
      pkg = new Package(
                           source: package_data.header.packageSource,
                        reference: package_data.header.packageSlug).save(flush:true, failOnError:true);
    }

    package_data.packageContents.each { pc ->
      // {
      //   "title": "Nordic Psychology",
      //   "instanceMedium": "electronic",
      //   "instanceMedia": "journal",
      //   "instanceIdentifiers": {
      //   "namespace": "jusp",
      //   "value": "12342"
      //   },
      //   "siblingInstanceIdentifiers": {
      //   "namespace": "issn",
      //   "value": "1901-2276"
      //   },
      //   "coverage": {
      //   "startVolume": "58",
      //   "startIssue": "1",
      //   "startDate": "2006-03-31T23:00:00Z",
      //   "endVolume": "63",
      //   "endIssue": "4",
      //   "endDate": "2011-12-31T00:00:00Z"
      //   },
      //   "embargo": null,
      //   "coverageDepth": "fulltext",
      //   "coverageNote": null
      //   }
      log.debug("title: ${pc.title}");
    }

    return result;
  }
}
