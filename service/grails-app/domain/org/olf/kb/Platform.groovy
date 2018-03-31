package org.olf.kb

import grails.gorm.MultiTenant


/**
 * mod-erm representation of a platform - a venue for publishing an electronic resource
 */
public class Platform implements MultiTenant<Platform> {

  String id
  String name

  static mapping = {
                   id column:'pt_id', generator: 'uuid', length:36
              version column:'pt_version'
                 name column:'pt_name'
  }

  static constraints = {
          name(nullable:false, blank:false)
  }


}
