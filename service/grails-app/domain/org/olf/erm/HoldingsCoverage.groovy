package org.olf.erm

import java.time.LocalDate

import org.olf.kb.AbstractCoverageStatement

import com.k_int.web.toolkit.domain.traits.Clonable

import grails.compiler.GrailsCompileStatic
import grails.gorm.MultiTenant


/**
 * Represents the local coverage override
 */

@GrailsCompileStatic
public class HoldingsCoverage extends AbstractCoverageStatement implements MultiTenant<HoldingsCoverage>, Clonable<HoldingsCoverage> {
  
  String id

  LocalDate startDate
  LocalDate endDate
  
  String startVolume
  String startIssue
  String endVolume
  String endIssue
  
  static belongsTo = [entitlement: Entitlement]
  
  static mapping = {
             id column:'co_id', generator: 'uuid2', length:36
        version column:'co_version'
    entitlement column:'co_ent_fk'
      startDate column:'co_start_date'
        endDate column:'co_end_date'
    startVolume column:'co_start_volume'
     startIssue column:'co_start_issue'
      endVolume column:'co_end_volume'
       endIssue column:'co_end_issue'
  }
  
  static constraints = {
    entitlement(nullable:false)
    startDate(nullable:false, validator: STATEMENT_START_VALIDATOR)
    endDate(nullable:true)
    startVolume(nullable:true, blank:false)
    startIssue(nullable:true, blank:false)
    endVolume(nullable:true, blank:false)
    endIssue(nullable:true, blank:false)
  }
  
  /**
   * Need to resolve the conflict manually and add the call to the clonable method here.
   */
  @Override
  public HoldingsCoverage clone () {
    Clonable.super.clone()
  }
}
