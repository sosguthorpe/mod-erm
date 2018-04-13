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
  // enabled setting
  Boolean enabled 
  

  static mapping = {
                   id column: 'ali_id', generator: 'uuid', length:36
              version column: 'ali_version'
                owner column: 'ali_owner_fk'
                  pkg column: 'ali_pkg_fk'
                  pci column: 'ali_pci_fk'
                  pti column: 'ali_pti_fk'
              enabled column: 'ali_enabled'
  }


  static constraints = {
      owner(nullable:false, blank:false)
        pkg(nullable:true, blank:false)
        pci(nullable:true, blank:false)
        pti(nullable:true, blank:false)
    enabled(nullable:true, blank:false)
  }


}
