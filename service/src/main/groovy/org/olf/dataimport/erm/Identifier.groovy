package org.olf.dataimport.erm

import grails.validation.Validateable
import org.olf.dataimport.internal.PackageSchema.IdentifierSchema

class Identifier implements IdentifierSchema, Validateable {
  
  String value
  String namespace
  
  static constraints = {
    value      nullable: true, blank: false
    namespace  nullable: true, blank: false
  }
}