package org.olf.kb

import grails.gorm.annotation.Entity
import java.time.LocalDate
import org.olf.erm.Entitlement

abstract class AbstractCoverageStatement {

  abstract LocalDate getStartDate()
  abstract LocalDate getEndDate()
  
  abstract String getStartVolume()
  abstract String getStartIssue()
  abstract String getEndVolume()
  abstract String getEndIssue()

  public String toString() {
    "v${startVolume?:'*'}/i${startIssue?:'*'}/${startDate} - v${endVolume?:'*'}/i${endIssue?:'*'}/${endDate?:'*'}".toString()
  }
}
