package org.olf.kb

import grails.gorm.MultiTenant


/**
 *
 */
public class IdentifierOccurrence implements MultiTenant<IdentifierOccurrence> {

  String id
  Identifier identifier
  TitleInstance title


  static mapping = {
                   id column:'io_id', generator: 'uuid', length:36
              version column:'io_version'
           identifier column:'io_identifier_fk'
                title column:'io_ti_fk'
  }

  static constraints = {
      identifier(nullable:false, blank:false)
           title(nullable:true, blank:false)
  }


}
