package org.olf.dataimport.erm

import grails.validation.Validateable
import org.olf.dataimport.internal.PackageSchema.PackageProviderSchema

class PackageProvider implements PackageProviderSchema, Validateable {
  
  String name
  String reference
  
  static constraints = {
    name        nullable: true, blank: false
    reference   nullable: true, blank: false
  }
}