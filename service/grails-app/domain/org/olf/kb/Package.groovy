package org.olf.kb

import grails.gorm.MultiTenant


/**
 * mod-erm representation of a package
 */
public class Package extends Subscribable implements MultiTenant<Package> {

  String id
  String name

  static mapping = {
                   id column:'pkg_id', generator: 'uuid', length:36
              version column:'pkg_version'
                 name column:'pkg_name'
  }

  static constraints = {
          name(nullable:false, blank:false)
  }


}
