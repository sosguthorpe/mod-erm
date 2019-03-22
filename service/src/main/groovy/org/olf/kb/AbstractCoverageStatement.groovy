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
  
  public static final Closure STATEMENT_START_VALIDATOR = { LocalDate startDate, AbstractCoverageStatement statement ->
    
    // Check start date is before end date.
    if (statement.endDate && startDate > statement.endDate) {
      return [ 'coveragestatement.start.after.end', statement.endDate]
    }
  }
  
  public static final Closure STATEMENT_COLLECTION_VALIDATOR = { Collection<AbstractCoverageStatement> coverage_statements ->
            
    // Validate coverage statements. We check all points in one iteration for efficiency.
    if (coverage_statements) {
      
      boolean seenOpenEnded = false
      
      for (int i=0; i<coverage_statements.size(); i++) {
        final AbstractCoverageStatement statement = coverage_statements[i]
        
        seenOpenEnded = (statement.endDate == null)
        
        // Check overlap with subsequent entries. We don't need to compare with ourself.
        for (int j=(i+1); j<coverage_statements.size(); j++) {
          final AbstractCoverageStatement compareTo = coverage_statements[j]
          
          if (i==0) {
            // Check multiple open ended on first pass of this loop.
            if (seenOpenEnded && compareTo.endDate == null) {
              // Only one open ended agreement
              return [ 'coveragestatement.multiple.open' ]
            }
            seenOpenEnded = (statement.endDate == null)
          }

          final boolean overlapping =

            // Start-dates or end-dates can not be equal.
            statement.startDate == compareTo.startDate || statement.startDate == compareTo.endDate ||
            statement.endDate == compareTo.endDate || statement.endDate == compareTo.startDate ||

            // statement starts within compareTo range
            (statement.startDate > compareTo.startDate &&
              (compareTo.endDate == null || statement.startDate < compareTo.endDate)) ||

            // compareTo starts within statement range
            (compareTo.startDate > statement.startDate &&
              (statement.endDate == null || compareTo.startDate < statement.endDate))

          if (overlapping) {
            return [ 'coveragestatement.overlap', statement, compareTo ]
          }
        }
      }
    }
    return true
  }
}
