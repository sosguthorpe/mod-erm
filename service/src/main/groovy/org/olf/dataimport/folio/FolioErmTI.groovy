package org.olf.dataimport.folio

import com.k_int.web.toolkit.refdata.RefdataValue

import grails.validation.Validateable

class FolioErmTI implements Validateable {
  String name
  Set<FolioErmIdentifier> identifiers
  RefdataValue type
  RefdataValue subType
  
  static hasMany = [
    identifiers: FolioErmIdentifier
  ]
  
  static constraints = {
    name      nullable: true, blank: false
    type      nullable: true
    subType   nullable: true
  }
  
}