package org.olf.dataimport.erm

import org.olf.dataimport.internal.PackageSchema.IdentifierSchema

import grails.compiler.GrailsCompileStatic
import grails.validation.Validateable

@GrailsCompileStatic
class Identifier implements IdentifierSchema, Validateable {
  
  String value
  String namespace
  
  String getNamespace() {
    namespace?.trim()?.toLowerCase()
  }
  
  String getValue() {
    value?.trim()
  }
  
  static constraints = {
    value      nullable: false, blank: false
    namespace  nullable: false, blank: false
  }
  
  String toString() {
    "${namespace}: ${value}"
  }
}