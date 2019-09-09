package org.olf.dataimport.erm

import grails.compiler.GrailsCompileStatic
import grails.validation.Validateable
import groovy.transform.ToString

@ToString(includePackage=false)
@GrailsCompileStatic
class PlatformTitileInstance implements Validateable {
  
  String platform
  String platformUrl
  String url
  TitleInstance titleInstance
  
  static constraints = {
    platform      nullable:true, blank:false
    platformUrl   blank:false, validator: { String platformUrl, PlatformTitileInstance instance ->
      if (!platformUrl && !instance.platform) {
        // If platform is blank then this can't be.
        return ['null.message']
      }
    }
    url           nullable:true, blank:false
    titleInstance nullable:false
  }
}