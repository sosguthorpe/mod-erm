package org.olf

import static grails.web.http.HttpHeaders.*
import static org.springframework.http.HttpStatus.*

import org.olf.erm.SubscriptionAgreement
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.k_int.okapi.OkapiHeaders
import com.k_int.okapi.OkapiTenantResolver

import geb.spock.*
import grails.gorm.multitenancy.Tenants
import grails.plugins.rest.client.RestBuilder
import grails.testing.mixin.integration.Integration
import grails.transaction.*
import spock.lang.*

@Integration
@Stepwise
class ErmAgreementSpec extends GebSpec {

  @Shared
  private Map test_info = [:]

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

  // Set up a new tenant called RSTestTenantD
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
      'TestTenantD' | 'TestTenantD'
  }

  void "List Current Agreements"() {

      logger.debug("List known external KBs");

      when:"We ask the system to list known KBs"
        def resp = restBuilder().get("$baseUrl/erm/sas") {
          header 'X-Okapi-Tenant', 'TestTenantD'
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
        Serializable agreement_type_id = null
        Tenants.withId(OkapiTenantResolver.getTenantSchemaName(tenant.toLowerCase())) {
          
          // All refdata values have a few helper methods on the class.
          agreement_type_id = SubscriptionAgreement.lookupOrCreateAgreementType(type).id;
        }

        Map agreement_to_add =  [ 
          'name' : agreement_name,
          'agreementType':[
            'id': agreement_type_id
          ]
        ]

        def resp = restBuilder().post("$baseUrl/erm/sas") {
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
        'TestTenantD' | 'My first agreement'  | 'Draft'
        'TestTenantD' | 'My second agreement' | 'Trial'
        'TestTenantD' | 'My third agreement'  | 'Current'

  }

  void "Check Correct Current Agreements"() {

      logger.debug("List known external KBs");

      when:"We ask the system to list known KBs"
        def resp = restBuilder().get("$baseUrl/erm/sas") {
          header 'X-Okapi-Tenant', 'TestTenantD'
          authHeaders.rehydrate(delegate, owner, thisObject)()
        }

        logger.debug("result ${resp.json}");
        resp.json.each { r ->
          logger.debug("List KBs result [requets for TestTenantD]: ${r}");
        }

      then: "The system responds with a list of tenants";
        resp.status == OK.value()
        resp.json.size() == 3
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
      'TestTenantD' | 'note'
  }

   RestBuilder restBuilder() {
        new RestBuilder()
    }

}

