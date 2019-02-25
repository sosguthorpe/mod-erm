package org.olf.general

import grails.gorm.MultiTenant
import com.k_int.web.toolkit.refdata.RefdataValue
import com.k_int.web.toolkit.refdata.Defaults

class DocumentAttachment implements MultiTenant<DocumentAttachment> {

  String id
  String name
  String location
  String note
  Date dateCreated
  Date lastUpdated

  @Defaults(['License', 'Misc', 'Consortium Negotiation Document'])
  RefdataValue atType

  static mapping = {
             id column: 'da_id', generator: 'uuid', length:36
        version column: 'da_version'
           name column: 'da_name'
       location column: 'da_location'
           note column: 'da_note', type:'text'
         atType column: 'da_type_rdv_fk'
    dateCreated column: 'da_date_created'
    lastUpdated column: 'da_last_updated'
  }

  static constraints = {
           name(nullable:true, blank:false)
       location(nullable:true, blank:false)
           note(nullable:true, blank:false)
         atType(nullable:true, blank:false)
    dateCreated(nullable:true, blank:false)
    lastUpdated(nullable:true, blank:false)

  }

}
