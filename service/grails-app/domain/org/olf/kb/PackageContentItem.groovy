package org.olf.kb

import grails.gorm.MultiTenant


/**
 * mod-erm representation of a package
 */
public class PackageContentItem implements MultiTenant<PackageContentItem> {

  String id
  Package pkg
  PlatformTitleInstance pti

  // The date range on which this content item is live within the package
  Date accessStart
  Date accessEnd

  // A field primarily to deposit KBART::CoverageNote type data
  String note

  // A field primarily to deposit KBART::CoverageDepth type data
  String depth

  static mapping = {
                   id column:'pci_id', generator: 'uuid', length:36
              version column:'pci_version'
                  pkg column:'pci_pkg_fk'
                  pti column:'pci_pti_fk'
          accessStart column:'pci_access_start'
            accessEnd column:'pci_access_end'
                 note column:'pci_note'
                depth column:'pci_depth'
  }

  static constraints = {
            pkg(nullable:false, blank:false)
            pti(nullable:false, blank:false)
    accessStart(nullable:true, blank:false)
      accessEnd(nullable:true, blank:false)
           note(nullable:true, blank:false)
          depth(nullable:true, blank:false)
  }

}
