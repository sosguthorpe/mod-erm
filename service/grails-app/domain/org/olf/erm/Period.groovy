package org.olf.erm

import java.time.LocalDate

import com.k_int.web.toolkit.refdata.CategoryId
import com.k_int.web.toolkit.refdata.Defaults
import com.k_int.web.toolkit.refdata.RefdataCategory
import com.k_int.web.toolkit.refdata.RefdataValue

import grails.compiler.GrailsCompileStatic
import grails.gorm.MultiTenant
import groovy.util.logging.Slf4j

@Slf4j
@GrailsCompileStatic
class Period implements MultiTenant<Period>  {
  
  String id
  LocalDate startDate
  LocalDate endDate
  LocalDate cancellationDeadline
  String note

  static transients = ['periodStatus']
  private String periodStatus

  String getPeriodStatus() {
    String currentPeriodId = owner.findCurrentPeriod()
    String previousPeriodId = owner.findPreviousPeriod()
    String nextPeriodId = owner.findNextPeriod()

    switch (id) {
      case currentPeriodId:
        periodStatus = "current"
        break;
      case previousPeriodId:
        periodStatus = "previous"
        break;
      case nextPeriodId:
        periodStatus = "next"
        break;
      default:
        break;
    }
    periodStatus
  }

  static belongsTo = [
    owner: SubscriptionAgreement
  ]
  
  static mapping = {
                      id column:'per_id', generator: 'uuid2', length:36
               startDate column:'per_start_date'
                 endDate column:'per_end_date'
    cancellationDeadline column:'per_cancellation_deadline'
                    note column:'per_note', type: 'text'
                   owner column:'per_owner'
  }
   
  static constraints = {
                    owner(nullable:false)
                     note(nullable:true, blank:false)
     cancellationDeadline(nullable:true)
                startDate(nullable:false, validator: Period.PERIOD_START_VALIDATOR)
                  endDate(nullable:true)
  }
  
  public static final Closure PERIOD_START_VALIDATOR = { LocalDate startDate, Period period ->
    
    // Check start date is before end date.
    if (period.endDate &&
        startDate &&
        ( startDate > period.endDate) ) {
    
      return [ 'start_after_end_date', 'startDate', period.class.name, period.startDate, period.endDate]
    }
  }
  
  public static final Closure PERIOD_COLLECTION_VALIDATOR = { Collection<Period> periods ->
    
    // Validate period statement dates. We check all points in one iteration for efficiency.
    if (periods) {

      boolean seenOpenEnded = false

      for (int i=0; i<periods.size(); i++) {
        final Period period = periods[i]

        seenOpenEnded = (period.endDate == null)

        // Check overlap with subsequent entries. We don't need to compare with ourself.
        for (int j=(i+1); j<periods.size(); j++) {
          final Period compareTo = periods[j]

          if (i==0) {
            // Check multiple open ended on first pass of this loop.
            if (seenOpenEnded && compareTo.endDate == null) {
              // Only one open ended agreement
              return [ 'period.multiple.open' ]
            }
            seenOpenEnded = (period.endDate == null)
          }

          final boolean overlapping =

              // Start-dates or end-dates can not be equal.
              period.startDate == compareTo.startDate || period.startDate == compareTo.endDate ||
              period.endDate == compareTo.endDate || period.endDate == compareTo.startDate ||

              // statement starts within compareTo range
              (period.startDate > compareTo.startDate &&
              (compareTo.endDate == null || period.startDate < compareTo.endDate)) ||

              // compareTo starts within statement range
              (compareTo.startDate > period.startDate &&
              (period.endDate == null || compareTo.startDate < period.endDate))

          if (overlapping) {
            log.debug ("failed period.overlap");
            return [ 'period.overlap', period, compareTo ]
          }
        }
      }
    }
    return true
  }
}
