package org.olf.kb

import grails.gorm.MultiTenant


/**
 * A coverage statement - can apply to a PackageContentItem OR a TitlePlatform OR a title
 * but that should be an exclusive link
 */
public class CoverageStatement implements MultiTenant<CoverageStatement> {

  String id

  // Mutually exclusive --- ONE of pci, pti or ti
  PackageContentItem  pci
  PlatformTitleInstance pti
  TitleInstance ti

  static mapping = {
                   id column:'cs_id', generator: 'uuid', length:36
              version column:'cs_version'
                  pci column:'cs_pci_fk'
                  pti column:'cs_pti_fk'
                   ti column:'cs_ti_fk'
  }

  static constraints = {
    pct(nullable:true, blank:false);
    pti(nullable:true, blank:false);
    ti(nullable:true, blank:false);
  }


}
