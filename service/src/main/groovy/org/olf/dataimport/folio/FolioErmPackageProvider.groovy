package org.olf.dataimport.folio

import java.time.LocalDate

import grails.validation.Validateable

class FolioErmPackageProvider implements Validateable {
  
  String name
  String reference
  
  static constraints = {
    name        nullable: true, blank: false
    reference   nullable: true, blank: false
  }
}