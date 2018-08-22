package org.olf.kb

import grails.gorm.MultiTenant


/**
 * Recording the availability of a titleInstance on a platform
 */
public class PlatformTitleInstance implements MultiTenant<PlatformTitleInstance> {

  String id
  TitleInstance titleInstance
  Platform platform
  String url

  static mapping = {
                   id column:'pti_id', generator: 'uuid', length:36
              version column:'pti_version'
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
