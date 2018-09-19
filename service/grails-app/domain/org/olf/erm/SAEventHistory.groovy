package org.olf.erm

import grails.gorm.MultiTenant
import org.olf.general.RefdataValue
import org.olf.general.Org

/**
 * Subscription agreement - object holding details about an SA connecting a resource list (Composed Of packages and platform-titles).
 */
public class SAEventHistory implements MultiTenant<SAEventHistory> {

  String id
  RefdataValue eventType
  Date eventDate
  String summary
  String eventData

  static hasMany = [
  ]

  static mappedBy = [
  ]

  static belongsTo = [
    owner: SubscriptionAgreement
  ]

  static mapping = {
     table 'sa_event_history'
                      id column:'eh_id', generator: 'uuid', length:36
                 version column:'eh_version'
                   owner column:'eh_owner'
               eventType column:'eh_event_type'
                 summary column:'eh_summary'
               eventData column:'eh_event_data'
               eventDate column:'eh_event_date'
  }

  static constraints = {
           id(nullable:false, blank:false)
        owner(nullable:false, blank:false)
      summary(nullable:false, blank:false)
    eventType(nullable:false, blank:false)
    eventData(nullable:true, blank:false)
    eventDate(nullable:false, blank:false)
  }


}
