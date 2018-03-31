package org.olf.kb

import grails.gorm.MultiTenant


/**
 * A subscribable resource
 */
public class Subscribable implements MultiTenant<Subscribable> {

  String id

  static mapping = {
    tablePerHierarchy false
    id column:'s_id', generator: 'uuid', length:36
  }

  static constraints = {
          name(nullable:false, blank:false)
  }


}
