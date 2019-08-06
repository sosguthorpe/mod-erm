package org.olf.dataimport.erm

import java.time.LocalDate
import org.olf.dataimport.internal.PackageSchema.CoverageStatementSchema
import org.olf.kb.AbstractCoverageStatement

import grails.validation.Validateable

class CoverageStatement extends AbstractCoverageStatement implements CoverageStatementSchema, Validateable {
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