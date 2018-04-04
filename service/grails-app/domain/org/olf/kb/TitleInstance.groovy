package org.olf.kb

import grails.gorm.MultiTenant


/**
 * mod-erm representation of a BIBFRAME instance
 */
public class TitleInstance implements MultiTenant<TitleInstance> {

  String id
  // Title IN ORIGINAL LANGUAGE OF PUBLICATION
  String title

  static mapping = {
                   id column:'ti_id', generator: 'uuid', length:36
              version column:'ti_version'
                title column:'ti_title'
  }

  static constraints = {
          title(nullable:false, blank:false)
  }


}
