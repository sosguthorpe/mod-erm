package org.olf.kb

import grails.gorm.MultiTenant


/**
 *
 */
public class IdentifierNamespace implements MultiTenant<IdentifierNamespace> {

  String id
  String value

  static mapping = {
                   id column:'idns_id', generator: 'uuid', length:36
              version column:'idns_version'
                value column:'idns_value'
  }

  static constraints = {
          value(nullable:false, blank:false)
  }


}
