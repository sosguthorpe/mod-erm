package org.olf.general

import grails.gorm.MultiTenant

class RefdataValue implements MultiTenant<RefdataValue> {

  String id
  String value
  String label
  String style

  static belongsTo = [
    owner:RefdataCategory
  ]
  
  static hasMany = [
  ]

  static mapping = {
              id column: 'rdv_id', generator: 'uuid', length:36
         version column: 'rdv_version'
           owner column: 'rdv_owner', index:'rdv_entry_idx'
           value column: 'rdv_value', index:'rdv_entry_idx'
           label column: 'rdv_label'
           style column: 'rdv_style'
  }

  static constraints = {
           style(nullable: true, blank: false)
           label(nullable: true, blank: false)
  }

}
