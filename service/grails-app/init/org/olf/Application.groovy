package org.olf

import com.k_int.web.toolkit.refdata.GrailsDomainRefdataHelpers
import org.springframework.boot.context.embedded.undertow.UndertowEmbeddedServletContainerFactory
import org.springframework.context.annotation.Bean

import com.k_int.okapi.OkapiTenantAdminService

import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration
import grails.core.GrailsClass
import groovy.util.logging.Slf4j

@Slf4j
class Application extends GrailsAutoConfiguration {
  
  static void main(String[] args) {
    GrailsApp.run(Application, args)
  }
}