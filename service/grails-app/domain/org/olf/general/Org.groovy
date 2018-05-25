package org.olf.general
import javax.persistence.Transient

import grails.gorm.MultiTenant
import org.olf.general.annotations.OkapiDistributedDomain

@OkapiDistributedDomain(config='Org')
class Org implements MultiTenant<Org> {

  String id
  String name

  // IF this resource is controlled by another service,
  // what is the URI of the primary resource
  String sourceURI


  static mapping = {
           id column: 'org_id', generator: 'uuid', length:36
      version column: 'org_version'
         name column: 'org_name'
    sourceURI column: 'org_source_uri'
  }

  static constraints = {
  }

}
