package org.olf.general.jobs

import grails.gorm.MultiTenant
import grails.gorm.multitenancy.Tenants

class CoverageRegenerationJob extends PersistentJob implements MultiTenant<CoverageRegenerationJob>{

  final Closure work = {
    log.info "Running Coverage Regeneration Job"
    coverageService.triggerRegenration()
  }
}
