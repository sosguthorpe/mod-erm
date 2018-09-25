package org.olf.general

import grails.gorm.MultiTenant

class RefdataValue implements MultiTenant<RefdataValue> {

  String id
  String value
  String label

  static belongsTo = [
    owner:RefdataCategory
  ]

  static mapping = {
    id column: 'rdv_id', generator: 'uuid', length:36
    version column: 'rdv_version'
    owner column: 'rdv_owner', index:'rdv_entry_idx'
    value column: 'rdv_value', index:'rdv_entry_idx'
    label column: 'rdv_label'
  }

  static constraints = {
    label(nullable: true, blank: false)
  }
  
  
  /**
   * Lookup or create a RefdataValue
   * @param category_name
   * @param value
   * @return
   */
  static <T extends RefdataValue> T lookupOrCreate(final String category_name, final String value, final String label=null, Class<T> clazz = this) {
    RefdataCategory cat = RefdataCategory.findOrCreateByDesc(category_name).save(flush:true, failOnError:true)
    T result = clazz.findOrCreateByOwnerAndValue(cat, value)
    
    if (result) {
      result.label = label ?: value
    }
    
    result.save(flush:true, failOnError:true)
    result
  }

}
