package org.olf.kb

import grails.gorm.MultiTenant


/**
 *
 */
public class Identifier implements MultiTenant<Identifier> {

  String id
  String value
  IdentifierNamespace ns
  
  static hasMany = [
    occurrences: IdentifierOccurrence
  ]

  static mappedBy = [
    occurrences: 'identifier'
  ]

  static mapping = {
                   id column:'id_id', generator: 'uuid', length:36
              version column:'id_version'
                value column:'id_value'
                   ns column:'id_ns_fk'
  }

  static constraints = {
          value(nullable:false, blank:false)
             ns(nullable:false, blank:false)
  }


}
