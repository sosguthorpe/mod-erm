package org.olf.erm

import grails.gorm.MultiTenant
import org.olf.general.RefdataValue
import org.olf.general.Org

/**
 * Subscription agreement - object holding details about an SA connecting a resource list (Composed Of packages and platform-titles).
 */
public class SubscriptionAgreement implements MultiTenant<SubscriptionAgreement> {

  String id
  String name
  String localReference
  String vendorReference
  Date startDate
  Date endDate
  Date renewalDate
  Date nextReviewDate

  // Subscription agreement type - DRAFT, TRIAL, CURRENT
  RefdataValue agreementType

  Boolean enabled

  Org vendor

  static hasMany = [
    items:Entitlement
  ]

  static mappedBy = [
    items:'owner'
  ]

  static mapping = {
                   id column:'sa_id', generator: 'uuid', length:36
              version column:'sa_version'
                 name column:'sa_name'
       localReference column:'sa_local_reference'
      vendorReference column:'sa_vendor_reference'
            startDate column:'sa_start_date'
              endDate column:'sa_end_date'
          renewalDate column:'sa_renewal_date'
       nextReviewDate column:'sa_next_review_date'
        agreementType column:'sa_agreement_type'
              enabled column:'sa_enabled'
               vendor column:'sa_vendor_fk'
  }

  static constraints = {
               name(nullable:false, blank:false)
     localReference(nullable:true, blank:false)
    vendorReference(nullable:true, blank:false)
          startDate(nullable:true, blank:false)
            endDate(nullable:true, blank:false)
        renewalDate(nullable:true, blank:false)
     nextReviewDate(nullable:true, blank:false)
      agreementType(nullable:true, blank:false)
            enabled(nullable:true, blank:false)
             vendor(nullable:true, blank:false)
  }


}
