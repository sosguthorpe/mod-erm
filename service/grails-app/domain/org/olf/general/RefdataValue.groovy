package org.olf.general

import grails.gorm.MultiTenant

class RefdataValue implements MultiTenant<RefdataValue> {

  String value
  String label
  String visualStyle

  static belongsTo = [
    owner:RefdataCategory
  ]
  
  static hasMany = [
  ]

  static mapping = {
             id column:'rdv_id'
        version column:'rdv_version'
          owner column:'rdv_owner', index:'rdv_entry_idx'
          value column:'rdv_value', index:'rdv_entry_idx'
          label column:'rdv_label'
    visualStyle column:'rdv_visualStyle'
  }

  static constraints = {
    visualStyle(nullable:true)
          label(nullable:true)
  }

}
