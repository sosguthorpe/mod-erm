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

  static <T extends RefdataValue> T lookupOrCreateValue(final String category_name, final String value, final String label=null, Class<T> clazz = RefdataValue) {
    RefdataCategory cat = RefdataCategory.findOrCreateByDesc(category_name).save(flush:true, failOnError:true)
//    if ( !cat ) {
//      cat = new RefdataCategory(desc:category_name).save(flush:true, failOnError:true)
//    }

    T result = clazz.findOrCreateByOwnerAndValue(cat, value).save(flush:true, failOnError:true)

//    if ( !result ) {
//      new RefdataValue(owner:cat, value:value, label:(label?:value)).save(flush:true)
//      result = RefdataValue.findByOwnerAndValue(cat, value)
//    }

    result
  }

}
