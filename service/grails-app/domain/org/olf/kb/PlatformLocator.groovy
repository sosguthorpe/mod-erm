package org.olf.kb

import grails.gorm.MultiTenant


/**
 * mod-erm representation of a BIBFRAME Work
 */
public class PlatformLocator implements MultiTenant<PlatformLocator> {

  String id
  String domainNameRegex

  static mapping = {
                   id column:'pl_id', generator: 'uuid', length:36
              version column:'pl_version'
      domainNameRegex column:'pl_domain_name_regex'
  }

  static constraints = {
          domainNameRegex(nullable:false, blank:false)
  }


}
