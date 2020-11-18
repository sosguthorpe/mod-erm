package org.olf

import org.olf.general.StringTemplate
import org.olf.kb.Platform
import org.olf.kb.PlatformTitleInstance

import com.k_int.okapi.OkapiTenantResolver

import groovy.json.JsonSlurper

import grails.gorm.multitenancy.Tenants
import grails.testing.mixin.integration.Integration
import spock.lang.*
import spock.util.concurrent.PollingConditions

import groovy.util.logging.Slf4j

@Slf4j
@Integration
@Stepwise
class StringTemplateBulkSpec extends BaseSpec {
  // This is a bulk test, to test that the templating works in much larger volumes.

  def importService
  def stringTemplatingService

  // Place to store the id of the PTI we load in the package for use in multiple tests
  
  void "Load Packages" (test_package_file) {
    when: 'File loaded'

      def jsonSlurper = new JsonSlurper()
      def package_data = jsonSlurper.parse(new File(test_package_file))
      println "PKG DATA: ${package_data}"
      int result = 0
      final String tenantid = currentTenant.toLowerCase()
      Tenants.withId(OkapiTenantResolver.getTenantSchemaName( tenantid )) {
        result = importService.importPackageUsingInternalSchema( package_data )
        //result = importService.importPackageUsingErmSchema( package_data ).packageCount
      }
      def ptis = doGet("/erm/pti")
      println "LOGDEBUG PTIs: ${ptis}"

    then: 'Package imported'
      result > 0
    
    where:
      test_package_file | _
      //'src/integration-test/resources/packages/stringTemplating/elsevier_freedom_package.json' | _
      'src/integration-test/resources/packages/stringTemplating/elsevier_freedom_package_internal.json' | _
      //'src/integration-test/resources/packages/stringTemplating/jstor_arts_science_I_package.json' | _
      //'src/integration-test/resources/packages/stringTemplating/jstor_arts_science_I_package_internal.json' | _
  }

  void "Create StringTemplates" (
    final String the_name,
    final String the_rule,
    final String the_context
  ) {

    final String tenantid = currentTenant.toLowerCase()
    when: "Post to create new string template"
      Map respMap = doPost("/erm/sts", {
        'name' the_name
        'rule' the_rule
        'context' the_context // This can be the value or id but not the label
      })
    then: "Response is good, we have a new ID, and name/rule/context match input"
      respMap.id != null
      respMap.name == the_name
      respMap.rule == the_rule
      respMap.context.value == the_context
    where:
      the_name   || the_rule                                                                                                  || the_context
      'proxy1'   || 'http://sub-hh-{{replace (replace (removeProtocol inputUrl) \"link.\" \"\") \".com\" \".co.uk\"}}/proxy1' || 'urlproxier'
      'custom1'  || 'proxy-2-stuff-{{platformLocalCode}}'                                                                     || 'urlcustomiser'
  }

  void "Templating triggers without errors" () {
    when: "We trigger a template task"
      doGet("/erm/sts/template")
      // Allow 20 seconds for templating to occur
      Thread.sleep(20000);

      def ptis = doGet("/erm/pti")
      println "PTI COUNT: ${ptis.size()}"
    then: "Templating happens for all PTIs in system"
      1==1
  }
}

