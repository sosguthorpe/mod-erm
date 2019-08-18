package org.olf.general.jobs

import grails.gorm.MultiTenant
import groovy.json.JsonSlurper
import org.olf.general.FileUpload

class PackageImportJob extends PersistentJob implements MultiTenant<PackageImportJob> {
  
  private final JsonSlurper js = new JsonSlurper()
  
  final Closure getWork() {
    
    Closure theWork = { final String eventId, final String tenantId ->
    
      log.info "Running Package Import Job"
      
      // We should ensure the job is read into the current session. This closure will probably execute
      // in a future session and we need to reread the event in.
      final PersistentJob job = PersistentJob.read(eventId)
      
      if (job.fileUpload) {
        importService.importFromFile(js.parse(job.fileUpload.fileContentBytes))
      } else {
        log.error "No file attached to the Job."
      }
    }.curry( this.id )
    
    theWork
  }
  
  static constraints = {
      fileUpload (nullable:false)
  }
}
