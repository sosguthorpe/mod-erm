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

  public static LocalDate calculateCancellationDeadline (Set<Period> periods) {
    LocalDate latestCancellationDeadline = null
    for (def p : periods) {
      /*
       * If the period has a cancellation deadline,
       * and either the latest is null or it's later than the latest,
       * then replace it. 
       */
      if (
        p.cancellationDeadline &&
        (
          latestCancellationDeadline == null ||
          p.cancellationDeadline > latestCancellationDeadline
        )
      ) {
        latestCancellationDeadline = p.cancellationDeadline
      }
    }
    latestCancellationDeadline
  }
}