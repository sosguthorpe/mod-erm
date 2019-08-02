package org.olf.dataimport.folio

import grails.validation.Validateable

class FolioErmPTI implements Validateable {
  
  String platform
  String platformUrl
  String url
  FolioErmTI titleInstance
  
  static constraints = {
    platform      nullable:true, blank:false
    platformUrl   blank:false, validator: { String platformUrl, FolioErmPTI instance ->
      if (!platformUrl && !instance.platform) {
        // If platform is blank then this can't be.
        return ['null.message']
      }
    }
    url           nullable:true, blank:false
    titleInstance nullable:false
  }
}