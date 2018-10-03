package org.olf.kb

import grails.gorm.MultiTenant


/**
 * Recording the availability of a titleInstance on a platform
 */
public class PlatformTitleInstance extends ErmResource implements MultiTenant<PlatformTitleInstance> {

  TitleInstance titleInstance
  Platform platform
  String url
  
  String getName() {
    "'${titleInstance.name}' on Platform '${platform.name}'"
  }
  
  static hasMany = [
    packageOccurences: PackageContentItem,
  ]

  static mappedBy = [
    packageOccurences: 'pti'
  ]

  static mapping = {
        titleInstance column:'pti_ti_fk'
             platform column:'pti_pt_fk'
                  url column:'pti_url'
  }

  static constraints = {
          titleInstance(nullable:false, blank:false)
               platform(nullable:false, blank:false)
                    url(nullable:true, blank:false)
  }

}
