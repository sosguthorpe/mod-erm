package org.olf

import com.k_int.okapi.OkapiTenantAdminService
import org.olf.general.jobs.JobRunnerService

class BootStrap {

  def grailsApplication
  OkapiTenantAdminService okapiTenantAdminService
  JobRunnerService jobRunnerService

  def init = { servletContext ->
    log.debug("mod-erm::init() ${grailsApplication.config.dataSource}")
//    okapiTenantAdminService.freshenAllTenantSchemas()
    
    jobRunnerService.populateJobQueue()
  }
  def destroy = {
  }
}
