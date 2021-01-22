package org.olf

import java.time.LocalDate
import org.olf.erm.Period;

import groovy.transform.CompileStatic

@CompileStatic
class PeriodService {

  public static LocalDate calculateStartDate (Set<Period> periods) {
    LocalDate earliest = null
    for (def p : periods) {
      if (earliest == null || p.startDate < earliest) earliest = p.startDate
    }
    earliest
  }

  public static LocalDate calculateEndDate (Set<Period> periods) {
     LocalDate latest = null
    // Use for loop to allow us to break out if we find open ended period
    for (def p : periods) {
      if(p.endDate == null) {
        latest = null
        break
      } else if (latest == null || p.endDate > latest) {
        latest = p.endDate
      }
    }
    latest
  }
}