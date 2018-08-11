package org.olf

import grails.gorm.multitenancy.Tenants
import grails.events.annotation.Subscriber
import grails.gorm.multitenancy.WithoutTenant
import grails.gorm.transactions.Transactional
import org.olf.kb.Pkg;
import org.olf.kb.Platform;
import org.olf.kb.TitleInstance;
import org.olf.kb.PlatformTitleInstance;
import org.olf.kb.PackageContentItem;

/**
 * This service works at the module level, it's often called without a tenant context.
 */
@Transactional
public class PackageIngestService {

  def titleInstanceResolverService
  def coverageExtenderService

  /**
   * Load the paackage data (Given in the agreed canonical json package format) into the KB.
   * This function must be passed VALID package data. At this point, all package contents are
   * assumed to be valid. Any invalid rows should be filtered out at this point.
   * This is to keep the implementation of this function clean, all error checking shoud be
   * performed prior to this step. This function is soley concerned with absorbing a valid
   * package into the KB.
   * @return id of package upserted
   */
  public Map upsertPackage(Map package_data) {

    def result = [:];

    log.debug("Package header: ${package_data.header}");
    result.updateTime = System.currentTimeMillis();

    // header.packageSlug contains the package maintainers authoritative identifier for this package.
    def pkg = Pkg.findBySourceAndReference(package_data.header.packageSource, package_data.header.packageSlug)

    if ( pkg == null ) {
      pkg = new Pkg(
                             name: package_data.header.packageName,
                           source: package_data.header.packageSource,
                        reference: package_data.header.packageSlug).save(flush:true, failOnError:true);

      result.newPackageId = pkg.id
    }

    int rownum = 0;

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

            // See if we already have a title platform record for the presence of this title on this platform
            PlatformTitleInstance pti = PlatformTitleInstance.findByTitleInstanceAndPlatform(title, platform)

            if ( pti == null ) 
              pti = new PlatformTitleInstance(titleInstance:title, platform:platform).save(flush:true, failOnError:true);


            // Lookup or create a package content item record for this title on this platform in this package
            // We only check for currently live pci records, as titles can come and go from the package.
            // N.B. addedTimestamp removedTimestamp lastSeenTimestamp
            def pci_qr = PackageContentItem.executeQuery('select pci from PackageContentItem as pci where pci.pti = :pti and pci.pkg = :pkg and pci.removedTimestamp is null',[pti:pti, pkg:pkg]);
            PackageContentItem pci = pci_qr.size() == 1 ? pci_qr.get(0) : null; 

            if ( pci == null ) {
              log.debug("Create new package content item for title ${pti}");
              pci = new PackageContentItem(
                                           pti:pti, 
                                           pkg:pkg, 
                                           note:pc.coverageNote, 
                                           depth:pc.coverageDepth,
                                           accessStart:null,
                                           accessEnd:null, 
                                           addedTimestamp:result.updateTime,
                                           lastSeenTimestamp:result.updateTime).save(flush:true, failOnError:true);
            }
            else {
              // Note that we have seen the package content item now
              log.debug("update package content item last seen timestamp (Last time this platform title instance was seen in this package");
              pci.lastSeenTimestamp = result.updateTime;
              // TODO: Check for and record any CHANGES to this title in this package (coverage, embargo, etc)
            }

            // If the row has a coverage statement, check that the range of coverage we know about for this title on this platform
            // extends to include the supplied information. It is a contract with the KB that we assume this is correct info.
            // We store this generally for the title on the platform, and specifically for this title in this package on this platform.
            if ( pc.coverage ) {

              // We define coverage to be a list in the exchange format, but sometimes it comes just as a JSON map. Convert that
              // to the list of mpas that coverageExtenderService.extend expects
              List cov = pc.coverage instanceof List ? pc.coverage : [ pc.coverage ]

              coverageExtenderService.extend(pti, cov, 'pti');
              coverageExtenderService.extend(pci, cov, 'pci');
              coverageExtenderService.extend(title, cov, 'ti');
            }

            // Save needed either way
            pci.save(flush:true, failOnError:true);
          }
          catch ( Exception e ) {
            log.error("problem",e);
          }
        }
        else {
          log.error("row ${rownum} No platform URL");
        }

        println("rownum ${rownum} Resolved title: ${pc.title} as ${title}");
      }
      else {
        log.error("row ${rownum} Skipping ${pc} - No identifiers.. This will change in an upcoming commit where we do normalised title matching");
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

    // At the end - Any PCIs that are currently live (Don't have a removedTimestamp) but whos lastSeenTimestamp is < result.updateTime
    // were not found on this run, and have been removed. We *may* introduce some extra checks here - like 3 times or a time delay, but for now,
    // this is how we detect deletions in the package file.
    log.debug("end of packageUpsert. Remove any content items that have disappeared since the last upload. ${pkg.name}/${pkg.source}/${pkg.reference}/${result.updateTime}");
    PackageContentItem.executeQuery('select pci from PackageContentItem as pci where pci.pkg = :pkg and pci.lastSeenTimestamp < :updateTime',[pkg:pkg, updateTime:result.updateTime]).each { removal_candidate ->
      log.debug("Removal candidate: PIC #${removal_candidate.id} (Last seen ${removal_candidate.lastSeenTimestamp}, thisUpdate ${result.updateTime}) -- Set removed");
      removal_candidate.removedTimestamp = result.updateTime;
      removal_candidate.save(flush:true, failOnError:true);
    }


    return result;
  }
}
