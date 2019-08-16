package org.olf.general.jobs

import grails.gorm.MultiTenant
import groovy.json.JsonSlurper
import org.olf.general.FileUpload

class PackageImportJob extends PersistentJob implements MultiTenant<PackageImportJob> {
  
  private final JsonSlurper js = new JsonSlurper()
  
  final Closure work = {
    log.info "Running Package Import Job"
    if (this.fileUpload) {
      importService.importFromFile(js.parse(this.fileUpload.fileContentBytes))
    } else {
      log.error "No file attached to the Job."
    }
  }
  
  static constraints = {
      fileUpload (nullable:false)
  }
}
