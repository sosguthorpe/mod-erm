package org.olf.erm

import grails.gorm.MultiTenant
import org.olf.general.RefdataValue
import org.olf.kb.Package
import org.olf.kb.PackageContentItem
import org.olf.kb.PlatformTitleInstance

/**
 * Subscription agreement - object holding details about an SA connecting a resource list (Composed Of packages and platform-titles).
 */
public class AgreementLineItem implements MultiTenant<AgreementLineItem> {

  String id

  // This line item is for ONE of:
  Package pkg
  PackageContentItem pci
  PlatformTitleInstance pti

  // The date ranges on which this line item is active. These date ranges allow the system to determine
  // what content is "Live" in an agreement. Content can be "Live" without being switched on, and 
  // vice versa. The dates indicate that we believe the agreement is in force for the items specified.
  // For Trials, these dates will indicate the dates of the trial, for live agreements the agreement item dates
  Date activeFrom
  Date activeTo

  static belongsTo = [
    owner:SubscriptionAgreement
  ]

  static hasMany = [
    coverage: HoldingsCoverage
  ]

  static mappedBy = [
    coverage: 'ali'
  ]

  // Allow users to individually switch on or off this content item. If null, should default to the agreement
  // enabled setting. The activeFrom and activeTo dates determine if a content item is "live" or not. This flag
  // determines if we wish live content to be visible to patrons or not. Content can be "Live" but not enabled,
  // although that would be unusual.
  Boolean enabled 
  

  static mapping = {
                   id column: 'ali_id', generator: 'uuid', length:36
              version column: 'ali_version'
                owner column: 'ali_owner_fk'
                  pkg column: 'ali_pkg_fk'
                  pci column: 'ali_pci_fk'
                  pti column: 'ali_pti_fk'
              enabled column: 'ali_enabled'
           activeFrom column: 'ali_active_from'
             activeTo column: 'ali_active_to'
  }


  static constraints = {
        owner(nullable:false, blank:false)
          pkg(nullable:true, blank:false)
          pci(nullable:true, blank:false)
          pti(nullable:true, blank:false)
      enabled(nullable:true, blank:false)
   activeFrom(nullable:true, blank:false)
     activeTo(nullable:true, blank:false)
  }


}
