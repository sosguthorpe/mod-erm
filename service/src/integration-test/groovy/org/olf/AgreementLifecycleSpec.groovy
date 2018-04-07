package org.olf

import grails.testing.mixin.integration.Integration
import grails.transaction.*
import static grails.web.http.HttpHeaders.*
import static org.springframework.http.HttpStatus.*
import spock.lang.*
import geb.spock.*
import grails.plugins.rest.client.RestBuilder
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.k_int.okapi.OkapiHeaders
import spock.lang.Shared
import grails.gorm.multitenancy.Tenants
import org.olf.general.RefdataCategory
import groovy.json.JsonSlurper


/**
 * unlike the other integration tests, this test is about simulating the end to end process of creating and managing agreements.
 *
 * The aim of the test is
 *   Load some packages so we have some titles to work with - make sure we load enough that we have some that will be visble
 *   and some that won't
 * 
 *   Create an agreement 
 *   Add to that agreement a package,
 *                           some package titles,
 *                           some non-package titles
 *   Check that "My subscribed content" lists the appropriate titles
 */
@Integration
@Stepwise
class AgreementLifecycleSpec extends GebSpec {

  def packageIngestService

  @Shared
  private Map test_info = [:]

  private static String TENANT='TestTenantH'

  final Closure authHeaders = {
    header OkapiHeaders.TOKEN, 'dummy'
    header OkapiHeaders.USER_ID, 'dummy'
    header OkapiHeaders.PERMISSIONS, '[ "erm.admin", "erm.user", "erm.own.read", "erm.any.read"]'
  }

  final static Logger logger = LoggerFactory.getLogger(ErmAgreementSpec.class);

  def setup() {
  }

  def cleanup() {
  }

  void "Set up test tenants "(tenantid, name) {
    when:"We post a new tenant request to the OKAPI controller"

      logger.debug("Post new tenant request for ${tenantid} to ${baseUrl}_/tenant");

      // Lets call delete on this tenant before we call create - this will clean up any prior test runs.
      // We don't care if this fails
      def delete_resp = restBuilder().delete("$baseUrl/_/tenant") {
        header 'X-Okapi-Tenant', tenantid
        authHeaders.rehydrate(delegate, owner, thisObject)()
      }

      def resp = restBuilder().post("${baseUrl}_/tenant") {
        header 'X-Okapi-Tenant', tenantid
        authHeaders.rehydrate(delegate, owner, thisObject)()
      }

    then:"The response is correct"
      resp.status == OK.value()
      // resp.headers[CONTENT_TYPE] == ['application/json;charset=UTF-8']
      // resp.json.message == 'Welcome to Grails!'

    // Important NOTE:: hibernateDatastore does not currently provide mirror method to addTenantForSchema. Hence when we delete
    // a tenant in the TenantTest we remove the schema, but are unable to remove it from the pool. Re-Adding a tenant added in a
    // previous test will find the old tenant name in the cache, but find the actual schema gone. Until a method is provided in 
    // hibernateDatastore, we will just use a new tenantId in each separate test.
    where:
      tenantid | name
      TENANT | TENANT
  }

  // This is actually ripped off from ErmPackageSpec, but we don't do the extensive checking that test does, we just
  // want to get the data in here.
  void "Load Packages"(tenantid, test_package_file) {

    when:
      def jsonSlurper = new JsonSlurper()
      def package_data = jsonSlurper.parse(new File(test_package_file))
      def result = null;

      // HeadsUP:: THIS IS A HACK
      // When tenantid comes through the http request it is normalised (lowecased, suffix added), we do that manually here as we are
      // directly exercising the service. It may be better to test this service via a web endpoint, however it's
      // not clear at the moment what form that endpoint will take, so exercising the service directly for now
      Tenants.withId(tenantid.toLowerCase()+'_olf_erm') {
        result = packageIngestService.upsertPackage(package_data);
      }

    then:
      result != null

    where:
      tenantid | test_package_file
      TENANT | 'src/integration-test/resources/packages/apa_1062.json'
      TENANT | 'src/integration-test/resources/packages/bentham_science_bentham_science_eduserv_complete_collection_2015_2017_1386.json'

  }

  void "List Current Agreements"() {

      logger.debug("List known external KBs");

      when:"We ask the system to list known KBs"
        def resp = restBuilder().get("$baseUrl/sas") {
          header 'X-Okapi-Tenant', TENANT
          authHeaders.rehydrate(delegate, owner, thisObject)()
        }

      then: "The system responds with a list of tenants";
        resp.status == OK.value()
        resp.json.size() == 0
  }

  void "Set up new agreements"(tenant, agreement_name, type) {

      when:

        // This is a bit of a shortcut - the web interface will populate a control for this, but here we just want the value.
        // So we access the DB with the tenant Id and get back the ID of the status we need.
        def agreement_type_id = null;
        Tenants.withId(tenant.toLowerCase()+'_olf_erm') {
          agreement_type_id = RefdataCategory.lookupOrCreate('AgreementType',type).id;
        }

        
        Map agreement_to_add =  [ 
                                  'name' : agreement_name,
                                  'agreementType':[
                                    'id': agreement_type_id
                                  ]
                                ];

        def resp = restBuilder().post("$baseUrl/sas") {
          header 'X-Okapi-Tenant', tenant
          authHeaders.rehydrate(delegate, owner, thisObject)()
          contentType 'application/json'
          accept 'application/json'
          json agreement_to_add
        }

      then:
       resp.status == CREATED.value()


      // Use a GEB Data Table to load each record
      where:
        tenant | agreement_name | type
        TENANT | 'My first agreement' | 'DRAFT'

  }

  void "Delete the tenants"(tenant_id, note) {

    expect:"post delete request to the OKAPI controller for "+tenant_id+" results in OK and deleted tennant"
      def resp = restBuilder().delete("$baseUrl/_/tenant") {
        header 'X-Okapi-Tenant', tenant_id
        authHeaders.rehydrate(delegate, owner, thisObject)()
      }

      logger.debug("completed DELETE request on ${tenant_id}");
      resp.status == OK.value()

    where:
      tenant_id | note
      TENANT | 'note'
  }

   RestBuilder restBuilder() {
        new RestBuilder()
    }

}

