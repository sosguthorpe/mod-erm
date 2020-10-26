package org.olf.general

import net.sf.json.JSONObject

import org.olf.general.StringTemplate
import org.olf.general.StringTemplatingService

import com.k_int.okapi.OkapiTenantAwareController

import grails.converters.JSON

import grails.gorm.multitenancy.Tenants
import grails.gorm.multitenancy.CurrentTenant
import groovy.util.logging.Slf4j


@Slf4j
@CurrentTenant
class StringTemplateController extends OkapiTenantAwareController<StringTemplate> {
  StringTemplatingService stringTemplatingService

  StringTemplateController()  {
    super(StringTemplate)
  }

  def refreshTemplatedUrls() {
    String tenantId = Tenants.currentId()
    stringTemplatingService.generateTemplatedUrlsForErmResources(tenantId)

    def result = [:]
    render result as JSON
  }
  /* def templateStringsForId(String id) {

    // Grab the body to use as the binding for the templates
    JSONObject binding = request.JSON

    def templatesForId = stringTemplatingService.findStringTemplatesForId(id)
    def result = stringTemplatingService.performStringTemplates(templatesForId, binding)

    render result as JSON
  } */

  def getStringTemplatesForId(String id) {
    def result = stringTemplatingService.findStringTemplatesForId(id)
    render result as JSON
  }
}
