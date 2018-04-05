package org.olf.general
import javax.persistence.Transient

import grails.gorm.MultiTenant

class RefdataCategory implements MultiTenant<RefdataCategory> {

  String id
  String desc

  static mapping = {
         id column: 'rdc_id', generator: 'uuid', length:36
    version column: 'rdc_version'
       desc column: 'rdc_description'
  }

  static constraints = {
  }

  static def lookupOrCreate(category_name, value, label=null, icon=null, sortKey=null) {
    def cat = RefdataCategory.findByDesc(category_name);
    if ( !cat ) {
      cat = new RefdataCategory(desc:category_name).save(flush:true, failOnError:true);
    }

    def result = RefdataValue.findByOwnerAndValue(cat, value)

    if ( !result ) {
      new RefdataValue(owner:cat, value:value, label:(label?:value),icon:icon, sortKey:sortKey).save(flush:true);
      result = RefdataValue.findByOwnerAndValue(cat, value);
    }

    result
  }

}
