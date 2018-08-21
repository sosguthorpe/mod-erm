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

  // MUST Be in format yyyy-mm-dd
  String startDate
  String endDate
  String startVolume
  String startIssue
  String endVolume
  String endIssue

  static mapping = {
                   id column:'cs_id', generator: 'uuid', length:36
              version column:'cs_version'
                  pci column:'cs_pci_fk'
                  pti column:'cs_pti_fk'
                   ti column:'cs_ti_fk'
            startDate column:'cs_start_date'
              endDate column:'cs_end_date'
          startVolume column:'cs_start_volume'
           startIssue column:'cs_start_issue'
            endVolume column:'cs_end_volume'
             endIssue column:'cs_end_issue'
  }

  static constraints = {
    pci(nullable:true, blank:false);
    pti(nullable:true, blank:false);
    ti(nullable:true, blank:false);
    startDate(nullable:true, blank:true);
    endDate(nullable:true, blank:true);
    startVolume(nullable:true, blank:true);
    startIssue(nullable:true, blank:true);
    endVolume(nullable:true, blank:true);
    endIssue(nullable:true, blank:true);
  }


  public String toString() {
    "v${startVolume?:'*'}/i${startIssue?:'*'}/${startDate} - v${endVolume?:'*'}/i${endIssue?:'*'}/${endDate?:'*'}".toString()
  }
}
