package org.olf.general.jobs

import grails.gorm.MultiTenant

class PackageImportJob extends PersistentJob implements MultiTenant<PackageImportJob> {
  
  final Closure work = {
    log.info "Running Package Import Job"
    importService.importFromFile(this.fileUpload)
  }
  
  static constraints = {
    fileUpload nullable: false
  }
}
