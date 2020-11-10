package org.olf.general

import net.sf.json.JSONObject

import org.olf.general.StringTemplate
import org.olf.general.StringTemplatingService

import com.k_int.okapi.OkapiTenantAwareController

import grails.converters.JSON

import grails.gorm.multitenancy.Tenants
import grails.gorm.multitenancy.CurrentTenant
import grails.async.Promise
import grails.async.Promises
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
    Promise p = Promises.task {
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
