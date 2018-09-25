package org.olf.kb

import grails.gorm.MultiTenant
import javax.persistence.Transient

/**
 * an ElectronicResource - Superclass of PlatformTitleInstance and Package
 * and a kind of synonym for "Buyable thing"
 *
 * N.B. THIS CLASS MAPS TO A VIEW NOT A TABLE - IT IS HERE TO SUPPORT THE eRESOURCE wireframe. TAKE CARE!
 *
 */
public class ElectronicResource implements MultiTenant<ElectronicResource> {
 
  String id
  String type
  TitleInstance ti
  String name
  Pkg pkg

  static mapping = {
      table 'all_electronic_resources'
    version false
         id generator: 'assigned', column:'id'
       type column:'type'
         ti column:'ti_id'
       name column:'name'
       pkg column:'pkg_id'
  }

}
