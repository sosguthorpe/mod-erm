package org.olf.dataimport.erm

import com.k_int.web.toolkit.refdata.RefdataValue

import grails.compiler.GrailsCompileStatic
import grails.validation.Validateable

@GrailsCompileStatic
class TitleInstance implements Validateable {
  String name
  Set<Identifier> identifiers
  RefdataValue type
  RefdataValue subType
  
  static hasMany = [
    identifiers: Identifier
  ]
  
  static constraints = {
    name      nullable: true, blank: false
    type      nullable: true
    subType   nullable: true
  }
  
}