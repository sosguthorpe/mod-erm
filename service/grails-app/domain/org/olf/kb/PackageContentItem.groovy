package org.olf.kb

import grails.gorm.MultiTenant


/**
 * mod-erm representation of a package
 */
public class PackageContentItem implements MultiTenant<PackageContentItem> {

  String id
  Package pkg
  PlatformTitleInstance pti

  static mapping = {
                   id column:'pci_id', generator: 'uuid', length:36
              version column:'pci_version'
                  pkg column:'pci_pkg_fk'
                  pti column:'pci_pti_fk'
  }

  static constraints = {
    pkg(nullable:false, blank:false)
    pti(nullable:false, blank:false)
  }

}
