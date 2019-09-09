package org.olf.dataimport.erm

import com.k_int.web.toolkit.refdata.RefdataValue

import grails.compiler.GrailsCompileStatic
import grails.validation.Validateable
import groovy.transform.ToString

@ToString(includePackage=false)
@GrailsCompileStatic
class TitleInstance implements Validateable {
  String name
  Set<Identifier> identifiers
  String type = 'journal'
  String subType = 'electronic'
  
  String getType () {
    this.type?.toLowerCase()
  }
  String getSubType () {
    this.subType?.toLowerCase()
  }
  
  static hasMany = [
    identifiers: Identifier
  ]
  
  static constraints = {
    name      nullable: true, blank: false
    type      nullable: true, blank: false
    subType   nullable: true, blank: false
  }
  
}