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

  String dateMonographPublished
  String firstAuthor
  String firstEditor
  String monographEdition
  String monographVolume
  
  String getType () {
    this.type?.toLowerCase()
  }
  String getSubType () {
    this.subType?.toLowerCase()
  }

  String getDateMonographPublished() {
    this.dateMonographPublished
  }
  String getFirstAuthor() {
    this.firstAuthor
  }
  String getFirstEditor() {
    this.firstEditor
  }
  String getMonographEdition() {
    this.monographEdition
  }
  String getMonographVolume() {
    this.monographVolume
  }
  
  static hasMany = [
    identifiers: Identifier
  ]
  
  static constraints = {
    name      nullable: true, blank: false
    type      nullable: true, blank: false
    subType   nullable: true, blank: false
    dateMonographPublished (nullable:true, blank:false)
    firstAuthor (nullable:true, blank:false)
    firstEditor (nullable:true, blank:false)
    monographEdition (nullable:true, blank:false)
    monographVolume (nullable:true, blank:false)
  }
  
}