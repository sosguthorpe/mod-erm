package org.olf.dataimport.erm

import org.olf.dataimport.internal.PackageSchema.IdentifierSchema

import grails.compiler.GrailsCompileStatic
import grails.validation.Validateable

@GrailsCompileStatic
class Identifier implements IdentifierSchema, Validateable {
  
  String value
  String namespace
  
  static constraints = {
    value      nullable: true, blank: false
    namespace  nullable: true, blank: false
  }
}