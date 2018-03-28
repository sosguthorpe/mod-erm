package org.olf.kb

import grails.gorm.MultiTenant
public class SubscriptionAgreement implements MultiTenant<SubscriptionAgreement> {

  String id
  String name
  String localReference
  String vendorReference

  static mapping = {
                   id column:'sa_id', generator: 'uuid', length:36
              version column:'sa_version'
                   id column:'sa_identifier'
                 name column:'sa_name'
       localReference column:'sa_local_reference'
      vendorReference column:'sa_vendor_reference'
  }

  static constraints = {
               name(nullable:false, blank:false)
     localReference(nullable:true, blank:false)
    vendorReference(nullable:true, blank:false)
  }


}
