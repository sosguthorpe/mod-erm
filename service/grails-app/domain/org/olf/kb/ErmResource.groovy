package org.olf.kb

import org.olf.erm.Entitlement
import org.olf.general.RefdataValue

import grails.gorm.MultiTenant

/**
 * an ErmResource - Superclass
 */
public class ErmResource implements MultiTenant<ErmResource> {
 
  String id
  String name
  
  RefdataValue type
  RefdataValue subType
  
  static hasMany = [
    entitlements: Entitlement
  ]

  static mappedBy = [
    entitlements: 'resource'
  ]
  static mapping = {
    tablePerHierarchy false
                   id generator: 'uuid', length:36
                 name column:'res_name'
                 type column:'res_type_fk'
              subType column:'res_sub_type_fk'
  }

  static constraints = {
            name (nullable:true, blank:false)
            type (nullable:true, blank:false)
         subType (nullable:true, blank:false)
  }
  
  String toString() {
    name
  }
   
}
