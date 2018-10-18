package org.olf

class BootStrap {

  def grailsApplication

  def init = { servletContext ->
    log.debug("mod-erm::init() ${grailsApplication.config.dataSource}")
  }
  def destroy = {
  }
}
