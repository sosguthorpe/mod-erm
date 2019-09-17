package org.olf.kb

import java.time.LocalDate

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
    
    // When GOKb closes out or removes a tipp on a package, it appears that the startDate may be nulled out.
    // therefore, it appears in this case that a coverageStatement might be updated to have no startDate.

    // Check start date is before end date.
    if (statement.endDate && 
        startDate &&
        ( startDate > statement.endDate) ) {
      // Custom validators should return property name, class name, property value, other values
//      println("failed AbstractCoverageStatement::statment_start_validator");
      // statement.errors.rejectValue('startDate', 'start_after_end_date')
      return [ 'start_after_end_date', 'startDate', statement.class.name, statement.startDate, statement.endDate]
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
            println("failed AbstractCoverageStatement::coveragestatement.overlap");
            return [ 'coveragestatement.overlap', statement, compareTo ]
          }
        }
      }
    }
    return true
  }
}
