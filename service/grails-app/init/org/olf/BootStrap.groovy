package org.olf

import com.k_int.okapi.OkapiTenantAdminService
import grails.gorm.multitenancy.Tenants
import org.olf.general.jobs.JobRunnerService
import org.olf.kb.TitleInstance

class BootStrap {

  def grailsApplication
  OkapiTenantAdminService okapiTenantAdminService
  JobRunnerService jobRunnerService

  def init = { servletContext ->
    log.debug("mod-erm::init() ${grailsApplication.config.dataSource}")
    log.debug "TitleInstance Type Cat : " + TitleInstance.getTypeCategory()
    log.debug "TitleInstance SubType Cat : " + TitleInstance.getSubTypeCategory()
    Tenants.withId('diku_mod_agreements') {
      List<String> typeDefaults = TitleInstance.getAllRefdataDefaults().get('type')
      typeDefaults.each { String val ->
        TitleInstance.lookupOrCreateType(val)
      }
    }
    
    jobRunnerService.populateJobQueue()
  }
  def destroy = {
  }
}
