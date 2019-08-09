package org.olf.dataimport.erm

import org.olf.dataimport.internal.PackageSchema.PackageProviderSchema

import grails.compiler.GrailsCompileStatic
import grails.validation.Validateable

@GrailsCompileStatic
class PackageProvider implements PackageProviderSchema, Validateable {
  
  String name
  String reference
  
  static constraints = {
    name        nullable: true, blank: false
    reference   nullable: true, blank: false
  }
}