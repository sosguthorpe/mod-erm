package org.olf.dataimport.folio

import java.time.LocalDate

import grails.validation.Validateable

class FolioErmIdentifier implements Validateable {
  
  String value
  String ns
  
  static constraints = {
    value   nullable: true, blank: false
    ns      nullable: true, blank: false
  }
}