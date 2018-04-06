package org.olf

import grails.gorm.multitenancy.Tenants
import org.olf.kb.RemoteKB
import grails.events.annotation.Subscriber
import grails.gorm.multitenancy.WithoutTenant
import grails.gorm.transactions.Transactional
import org.olf.kb.Package;
import org.olf.kb.Platform;
import org.olf.kb.TitleInstance;

/**
 * This service works at the module level, it's often called without a tenant context.
 */
@Transactional
public class PackageIngestService {

  def titleInstanceResolverService

  /**
   * Load the paackage data (Given in the agreed canonical json package format) into the KB.
   * This function must be passed VALID package data. At this point, all package contents are
   * assumed to be valid. Any invalid rows should be filtered out at this point.
   * @return id of package upserted
   */
  public String upsertPackage(String tenantId, Map package_data) {
    
    def result = null;
    log.debug("PackageIngestService::upsertPackage(${tenantId},...)");

    Tenants.withId(tenantId) {
      result = internalUpsertPackage(package_data);
    }
  }

  /**
   * Load the paackage data (Given in the agreed canonical json package format) into the KB.
   * This function must be passed VALID package data. At this point, all package contents are
   * assumed to be valid. Any invalid rows should be filtered out at this point.
   * This is to keep the implementation of this function clean, all error checking shoud be
   * performed prior to this step. This function is soley concerned with absorbing a valid
   * package into the KB.
   * @return id of package upserted
   */
  private String internalUpsertPackage(Map package_data) {

    def result = '';

    log.debug("Package header: ${package_data.header}");

    // header.packageSlug contains the package maintainers authoritative identifier for this package.
    def pkg = Package.findBySourceAndReference(package_data.header.packageSource, package_data.header.packageSlug)

    if ( pkg == null ) {
      pkg = new Package(
                             name: package_data.header.packageName,
                           source: package_data.header.packageSource,
                        reference: package_data.header.packageSlug).save(flush:true, failOnError:true);
    }

    package_data.packageContents.each { pc ->
      log.debug("Try to resolve ${pc}");
      if ( pc.instanceIdentifiers?.size() > 0 ) {
        TitleInstance title = titleInstanceResolverService.resolve(pc);

        if ( pc.platformUrl ) {
          log.debug("platform ${pc.platformUrl}");
          // lets try and work out the platform for the item
          try {
            Platform platform = Platform.resolve(pc.platformUrl);
            log.debug("Platform: ${platform}");
          }
          catch ( Exception e ) {
            log.error("problem",e);
          }
        }

        println("Resolved title: ${pc.title} as ${title}");
      }
      else {
        log.error("Skipping ${pc} - No identifiers.. This will change in an upcoming commit where we do normalised title matching");
      }

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
    }

    return result;
  }
}
