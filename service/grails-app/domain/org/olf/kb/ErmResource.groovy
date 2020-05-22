package org.olf.kb

import org.olf.CoverageService
import org.olf.erm.Entitlement
import com.k_int.web.toolkit.refdata.RefdataValue
import grails.async.Promises
import grails.gorm.MultiTenant
import grails.gorm.multitenancy.Tenants
import java.time.LocalDate

/**
 * an ErmResource - Superclass
 * Represents a selectable resource - a package, a title in a package, a title on a platform, etc
 */
public class ErmResource implements MultiTenant<ErmResource> {
 
  String id
  String name
  String description
  
  RefdataValue type
  RefdataValue subType

  Date dateCreated
  Date lastUpdated

  boolean suppressFromDiscovery = false 
  
  static hasMany = [
    coverage: CoverageStatement,
    entitlements: Entitlement
  ]

  static mappedBy = [
    coverage: 'resource',
    entitlements: 'resource'
  ]
  static mapping = {
    tablePerHierarchy false
                  id generator: 'uuid2', length:36
                   name column: 'res_name'
            description column: 'res_description', type:'text'
                   type column: 'res_type_fk'
            dateCreated column: 'res_date_created'
            lastUpdated column: 'res_last_updated'
                subType column: 'res_sub_type_fk'
  suppressFromDiscovery column: 'res_suppress_discovery'
              coverage cascade: 'all-delete-orphan'
  }

  static constraints = {
                   name (nullable:true, blank:false)
            description (nullable:true, blank:false)
                   type (nullable:true, blank:false)
                subType (nullable:true, blank:false)
            dateCreated (nullable:true, blank:false)
            lastUpdated (nullable:true, blank:false)
  suppressFromDiscovery (nullable:false, blank:false)
               coverage (validator: CoverageStatement.STATEMENT_COLLECTION_VALIDATOR, sort:'startDate')
  }
  
  protected void checkCoverage() {
    final Serializable tenantId = Tenants.currentId()
    final Serializable me = this.id
    Promises.task ({ final Serializable tid, final Serializable resId ->
      Tenants.withId(tid) {
        CoverageService.changeListener(resId)
      }
    }.curry(tenantId, me))
  }
  
  def afterInsert() {
    checkCoverage()
  }
  
  def afterUpdate() {
    checkCoverage()
  }
  
  String toString() {
    name
  }
   
}
