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
  
  static hasMany = [
    directEntitlements: Entitlement
  ]

  static mappedBy = [
    directEntitlements: 'resource'
  ]
  static mapping = {
    tablePerHierarchy false
                   id generator: 'uuid', length:36
  }
   
}
