package org.olf.kb

import org.hibernate.search.annotations.Analyze
import org.hibernate.search.annotations.DocumentId
import org.hibernate.search.annotations.Field
import org.hibernate.search.annotations.Index
import org.hibernate.search.annotations.Store
import org.olf.erm.Entitlement
import com.k_int.web.toolkit.refdata.RefdataValue

import grails.gorm.MultiTenant
import java.time.LocalDate

/**
 * an ErmResource - Superclass
 * Represents a selectable resource - a package, a title in a package, a title on a platform, etc
 */
public class ErmResource implements MultiTenant<ErmResource> {
 
  @DocumentId
  String id
  
  @Field(index=Index.YES, analyze=Analyze.YES, store=Store.YES)
  String name
  String description
  
  RefdataValue type
  RefdataValue subType

  Date dateCreated
  Date lastUpdated
  
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
                 name column:'res_name'
          description column:'res_description', type:'text'
                 type column:'res_type_fk'
          dateCreated column:'res_date_created'
          lastUpdated column:'res_last_updated'
              subType column:'res_sub_type_fk'
             coverage cascade: 'all-delete-orphan'
  }

  static constraints = {
            name (nullable:true, blank:false)
     description (nullable:true, blank:false)
            type (nullable:true, blank:false)
         subType (nullable:true, blank:false)
     dateCreated (nullable:true, blank:false)
     lastUpdated (nullable:true, blank:false)
      coverage (validator: CoverageStatement.STATEMENT_COLLECTION_VALIDATOR, sort:'startDate')
  }
  
  String toString() {
    name
  }
   
}
