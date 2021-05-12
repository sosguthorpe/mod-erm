package org.olf.general

import com.k_int.web.toolkit.async.WithPromises

import com.k_int.okapi.OkapiTenantAwareController

import grails.async.Promise
import grails.converters.JSON
import grails.gorm.multitenancy.CurrentTenant
import grails.gorm.multitenancy.Tenants
import groovy.util.logging.Slf4j


@Slf4j
@CurrentTenant
class StringTemplateController extends OkapiTenantAwareController<StringTemplate> {
  StringTemplatingService stringTemplatingService

  StringTemplateController()  {
    super(StringTemplate)
  }

  def refreshTemplatedUrls() {
    def result = [:]
    String tenantId = Tenants.currentId()
    Promise p = WithPromises.task {
      stringTemplatingService.refreshUrls(tenantId)
    }
    p.onError{ Throwable e ->
      log.error "Couldn't refresh templated urls", e
    }
    
    render result as JSON
  }

  def getStringTemplatesForId(String id) {
    def result = stringTemplatingService.findStringTemplatesForId(id)
    render result as JSON
  }
}
