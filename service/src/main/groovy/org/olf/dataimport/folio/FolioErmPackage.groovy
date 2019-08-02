package org.olf.dataimport.folio

import grails.validation.Validateable

class FolioErmPackageRecord implements Validateable {
  
  String source
  String reference
  String name
  FolioErmPackageProvider packageProvider
  Set<FolioErmContentItem> contentItems
  
  static hasMany = [
    contentItems: FolioErmContentItem
  ]
  
  static constraints = {
    source    nullable: false, blank: false
    reference nullable: false, blank: false
    name      nullable: false, blank: false
    packageProvider nullable: true, blank: false
    contentItems minSize: 1
  }
}
