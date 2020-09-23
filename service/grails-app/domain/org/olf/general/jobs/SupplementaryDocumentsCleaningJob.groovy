package org.olf.general.jobs

import grails.gorm.MultiTenant
import grails.gorm.multitenancy.Tenants

class SupplementaryDocumentsCleaningJob extends PersistentJob implements MultiTenant<SupplementaryDocumentsCleaningJob>{

  final Closure work = {
    log.info "Running Supplementary Documents Cleaning Job"
    documentAttachmentService.triggerCleanSuppDocs()
  }
}
