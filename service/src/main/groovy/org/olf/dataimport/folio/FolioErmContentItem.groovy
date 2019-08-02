package org.olf.dataimport.folio

import java.time.LocalDate
import org.olf.kb.AbstractCoverageStatement

import grails.validation.Validateable

class FolioErmContentItem implements Validateable {
  
  String note
  String depth
  LocalDate accessStart
  LocalDate accessEnd
  
  Set<FolioErmCoverageStatement> coverage
  
  FolioErmPTI platformTitleInstance
  
  static hasMany = [
    coverage: FolioErmCoverageStatement
  ]
  
  static constraints = {
    note          nullable: true, blank: false
    depth         nullable: true, blank: false
    accessStart   nullable: true
    accessEnd     nullable: true
    
    coverage (validator: AbstractCoverageStatement.STATEMENT_COLLECTION_VALIDATOR, sort:'startDate')
    platformTitleInstance nullable: false
  }
}