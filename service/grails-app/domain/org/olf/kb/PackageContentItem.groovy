package org.olf.kb

import java.time.LocalDate

import grails.gorm.MultiTenant


/**
 * mod-erm representation of a package
 */
public class PackageContentItem extends ErmResource implements MultiTenant<PackageContentItem> {

  Pkg pkg
  PlatformTitleInstance pti
  
  String getName() {
    "${pti.name} in Package ${pkg.name}"
  }

  // Track this package content item - when did we first detect it (added) when did we last
  // see it, and when did we determine it has been removed?
  Long addedTimestamp
  Long removedTimestamp
  Long lastSeenTimestamp

  // The date range on which this content item is live within the package
  LocalDate accessStart
  LocalDate accessEnd

  // A field primarily to deposit KBART::CoverageNote type data
  String note

  // A field primarily to deposit KBART::CoverageDepth type data
  String depth

  static mapping = {
                  pkg column:'pci_pkg_fk'
                  pti column:'pci_pti_fk'
          accessStart column:'pci_access_start'
            accessEnd column:'pci_access_end'
                 note column:'pci_note'
                depth column:'pci_depth'
       addedTimestamp column:'pci_added_ts'
     removedTimestamp column:'pci_removed_ts'
    lastSeenTimestamp column:'pci_last_seen_ts'
  }

  static constraints = {
                  pkg(nullable:false, blank:false)
                  pti(nullable:false, blank:false)
          accessStart(nullable:true, blank:false)
            accessEnd(nullable:true, blank:false)
                 note(nullable:true, blank:false)
                depth(nullable:true, blank:false)
       addedTimestamp(nullable:true, blank:false)
     removedTimestamp(nullable:true, blank:false)
    lastSeenTimestamp(nullable:true, blank:false)
  }

  /**
   * Gather together all coverage information into a single summary statement that can be used in search results.
   * see: https://www.editeur.org/files/ONIX%20for%20Serials%20-%20Coverage/20120326_ONIX_Coverage_overview_v1_0.pdf
   */
  public String generateCoverageSummary() {
    return coverageStatements.join('; ');
  }
}
