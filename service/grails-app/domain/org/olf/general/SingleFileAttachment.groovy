package org.olf.general

import grails.gorm.MultiTenant
import grails.gorm.multitenancy.Tenants

class SingleFileAttachment implements MultiTenant<SingleFileAttachment> {
  
  // Add transient peroperty for flagging file removal. Transients are ignored by the persistence
  // layer.
  
  String id
  FileUpload fileUpload
  static hasOne = [fileUpload: FileUpload]
  static mappedBy = [fileUpload: 'owner']
  
  static mapping = {
    tablePerHierarchy false
    id generator: 'uuid2', length:36
    fileUpload cascade: 'all'
  }
  
  static constraints = {
    fileUpload nullable: true
  }
}
