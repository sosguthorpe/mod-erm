package org.olf.kb

import grails.gorm.MultiTenant

/**
 * an ElectronicResource - Superclass
 * Blank apart from the Identifier. This adds no real functionality apart
 * from being able to query easily for all e-resources. 
 */
public class ElectronicResource implements MultiTenant<ElectronicResource> {
 
  String id
  static mapping = {
      tablePerHierarchy false
         id generator: 'uuid', length:36
    version false
  }
}
