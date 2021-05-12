package org.olf.kb

import grails.gorm.MultiTenant

/**
 * Represents a resource that yields a list of titles. These resources can be compared.
 */
class ErmTitleList implements MultiTenant<ErmTitleList> { 
  
  String id
  static mapping = {
    tablePerHierarchy false
    id column: 'id', generator: 'uuid2', length:36
  }
}
