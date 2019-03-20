package org.olf.erm

import org.olf.kb.AbstractCoverageStatement

import grails.gorm.MultiTenant


/**
 * Represents the local coverage override
 */
public class HoldingsCoverage extends AbstractCoverageStatement implements MultiTenant<HoldingsCoverage> {
  
  String id
  Entitlement entitlement

  static mapping = {
             id column:'co_id', generator: 'uuid', length:36
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
    importFrom AbstractCoverageStatement
    entitlement(nullable:false)
  }
}
