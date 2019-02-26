package org.olf

import com.k_int.okapi.OkapiTenantAdminService

class BootStrap {

  def grailsApplication
  OkapiTenantAdminService okapiTenantAdminService

  def init = { servletContext ->
    log.debug("mod-erm::init() ${grailsApplication.config.dataSource}")
    okapiTenantAdminService.freshenAllTenantSchemas()
  }
  def destroy = {
  }
}
