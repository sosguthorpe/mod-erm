package org.olf.kb

import java.time.LocalDate
import org.olf.erm.Entitlement

abstract class AbstractCoverageStatement {

  LocalDate startDate
  LocalDate endDate
  
  String startVolume
  String startIssue
  String endVolume
  String endIssue
  
  static constraints = {
    startDate(nullable:true)
    endDate(nullable:true, blank:false)
    startVolume(nullable:true, blank:false)
    startIssue(nullable:true, blank:false)
    endVolume(nullable:true, blank:false)
    endIssue(nullable:true, blank:false)
  }


  public String toString() {
    "v${startVolume?:'*'}/i${startIssue?:'*'}/${startDate} - v${endVolume?:'*'}/i${endIssue?:'*'}/${endDate?:'*'}".toString()
  }
}
