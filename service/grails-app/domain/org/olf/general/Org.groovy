package org.olf.general
import org.olf.general.annotations.OkapiDistributedDomain

import grails.gorm.MultiTenant

@OkapiDistributedDomain(config='Org')
class Org implements MultiTenant<Org> {

  String id
  String name
  String vendorsUuid

  // IF this resource is controlled by another service,
  // what is the URI of the primary resource
  String sourceURI


  static mapping = {
            id column: 'org_id', generator: 'uuid', length:36
       version column: 'org_version'
          name column: 'org_name'
   vendorsUuid column: 'org_vendors_uuid'
     sourceURI column: 'org_source_uri'
  }

  static constraints = {
    sourceURI(nullable:true, blank:false)
    vendorsUuid(nullable:true, blank:false)
  }

}
