package org.olf.erm

import java.time.LocalDate

import com.k_int.web.toolkit.refdata.RefdataValue

import grails.compiler.GrailsCompileStatic
import grails.gorm.MultiTenant

/**
 * Subscription agreement - object holding details about an SA connecting a resource list (Composed Of packages and platform-titles).
 */
@GrailsCompileStatic
public class SAEventHistory implements MultiTenant<SAEventHistory> {

  String id
  RefdataValue eventType
  LocalDate eventDate
  String summary
  String notes
  String eventData
  RefdataValue eventOutcome

  static hasMany = [
  ]

  static mappedBy = [
  ]

  static belongsTo = [
    owner: SubscriptionAgreement
  ]

  static mapping = {
     table 'sa_event_history'
                      id column:'eh_id', generator: 'uuid2', length:36
                 version column:'eh_version'
                   owner column:'eh_owner'
               eventType column:'eh_event_type'
            eventOutcome column:'eh_event_outcome'
                 summary column:'eh_summary'
                   notes column:'eh_notes'
               eventData column:'eh_event_data'
               eventDate column:'eh_event_date'
  }

  static constraints = {
              id(nullable:false, blank:false)
           owner(nullable:false, blank:false)
         summary(nullable:false, blank:false)
       eventType(nullable:false, blank:false)
    eventOutcome(nullable:false, blank:false)
           notes(nullable:true, blank:false)
       eventData(nullable:true, blank:false)
       eventDate(nullable:false, blank:false)
  }


}
