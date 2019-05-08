package org.olf.general
import org.olf.general.annotations.OkapiDistributedDomain

import grails.gorm.MultiTenant

@OkapiDistributedDomain(config='Org')
class Org implements MultiTenant<Org> {

  String id
  String name
  String orgsUuid

  static mapping = {
            id column: 'org_id', generator: 'uuid2', length:36
       version column: 'org_version'
          name column: 'org_name'
      orgsUuid column: 'org_orgs_uuid'
  }

  static constraints = {
           name(nullable:true, blank:false)
       orgsUuid(nullable:true, blank:false)
  }

  /**
   * In many places we will look up an org and in the context of that lookup we may know something
   * extra about the org we are looking up. This method checks a known list of properties and if not set
   * will absorb any extra context into the record. This is used to accumulate external identifers and
   * other contextual information.
   */
  public void enrich(Map<String, Object> props) {
    boolean changed = false;
    props.keySet().each { k -> 
      if ( ( props[k] != null ) && ( [ 'reference'].contains(k) ) ) {
        if ( this[k] == null ) {
          this[k] = props[k]
        }
      }
    }

    if ( changed ) {
      this.save(flush:true, failOnError);
    }
  }
}
