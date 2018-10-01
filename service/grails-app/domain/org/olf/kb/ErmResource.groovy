package org.olf.kb

import org.olf.erm.Entitlement

import grails.gorm.MultiTenant

/**
 * an ErmResource - Superclass
 * Blank apart from the Identifier. This adds no real functionality apart
 * from being able to query easily for all e-resources. 
 */
public class ErmResource implements MultiTenant<ErmResource> {
 
  String id
  String type
  
  def beforeValidate() {
    if (type == null) {
      type = this.class.simpleName
    } 
  }
  
  static hasMany = [
    directEntitlements: Entitlement
  ]

  static mappedBy = [
    directEntitlements: 'resource'
  ]
  static mapping = {
    tablePerHierarchy false
                   id generator: 'uuid', length:36
                 type nullable: false, blank: false
  }
   
}
