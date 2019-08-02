package org.olf.dataimport.folio

import java.time.LocalDate
import org.olf.kb.AbstractCoverageStatement

import grails.validation.Validateable

class FolioErmCoverageStatement extends AbstractCoverageStatement implements Validateable {
  LocalDate startDate
  String startVolume
  String startIssue
  
  LocalDate endDate
  String endVolume
  String endIssue
  
  static constraints = {
    startDate(nullable:false, validator: STATEMENT_START_VALIDATOR)
    endDate(nullable:true)
    startVolume(nullable:true, blank:false)
    startIssue(nullable:true, blank:false)
    endVolume(nullable:true, blank:false)
    endIssue(nullable:true, blank:false)
  }
}