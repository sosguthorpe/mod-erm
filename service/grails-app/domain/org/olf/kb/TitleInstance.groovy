package org.olf.kb

import grails.gorm.MultiTenant
import javax.persistence.Transient
import org.olf.erm.Entitlement
import org.olf.general.RefdataValue

/**
 * mod-erm representation of a BIBFRAME instance
 */
public class TitleInstance extends ElectronicResource implements MultiTenant<TitleInstance> {
  
  // Title IN ORIGINAL LANGUAGE OF PUBLICATION
  String title

  // Journal/Book/...
  RefdataValue resourceType

  // Print/Electronic
  RefdataValue medium

  // For grouping sibling title instances together - EG Print and Electronic editions of the same thing
  Work work

  static mapping = {
                   id column:'ti_id'
                title column:'ti_title'
                 work column:'ti_work_fk'
               medium column:'ti_medium_fk'
         resourceType column:'ti_resource_type_fk'
  }

  static constraints = {
    resourceType (nullable:true, blank:false)
           title (nullable:false, blank:false)
          medium (nullable:true, blank:false)
            work (nullable:true, blank:false)
  }

  static hasMany = [
    identifiers: IdentifierOccurrence
  ]

  static mappedBy = [
    identifiers: 'title'
  ]
}
