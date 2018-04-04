package org.olf.general
import javax.persistence.Transient

import grails.gorm.MultiTenant

class RefdataCategory implements MultiTenant<RefdataCategory> {

  String desc

  static mapping = {
         id column:'rdc_id'
    version column:'rdc_version'
       desc column:'rdc_description'
  }

  static constraints = {
  }

}
