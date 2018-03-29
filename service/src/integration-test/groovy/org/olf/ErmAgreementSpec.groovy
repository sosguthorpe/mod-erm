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
        def resp = restBuilder().get("$baseUrl/sas") {
          header 'X-Okapi-Tenant', 'TestTenantD'
          authHeaders.rehydrate(delegate, owner, thisObject)()
        }

      then: "The system responds with a list of tenants";
        resp.status == OK.value()
        resp.json.size() == 0
  }

  void "Set up new agreements"(tenant, agreement_name) {

      expect:
        
        Map agreement_to_add =  [ 'name' : agreement_name ];

        def resp = restBuilder().post("$baseUrl/sas") {
          header 'X-Okapi-Tenant', tenant
          authHeaders.rehydrate(delegate, owner, thisObject)()
          contentType 'application/json'
          accept 'application/json'
          json agreement_to_add
        }

       resp.status == CREATED.value()


      // Use a GEB Data Table to load each record
      where:
        tenant | agreement_name
        'TestTenantD' | 'My first agreement'
        'TestTenantD' | 'My second agreement'
        'TestTenantD' | 'My third agreement'

  }

  void "Check Correct Current Agreements"() {

      logger.debug("List known external KBs");

      when:"We ask the system to list known KBs"
        def resp = restBuilder().get("$baseUrl/sas") {
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

