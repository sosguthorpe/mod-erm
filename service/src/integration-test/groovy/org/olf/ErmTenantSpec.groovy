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
@Rollback
class ErmTenantSpec extends GebSpec {

  @Shared
  private Map test_info = [:]

  final Closure authHeaders = {
    header OkapiHeaders.TOKEN, 'dummy'
    header OkapiHeaders.USER_ID, 'dummy'
    header OkapiHeaders.PERMISSIONS, '[ "erm.admin", "erm.user", "erm.own.read", "erm.any.read"]'
  }

  final static Logger logger = LoggerFactory.getLogger(ErmTenantSpec.class);

  def setup() {
  }

  def cleanup() {
  }

  // Set up a new tenant called RSTestTenantA
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

    where:
      tenantid | name
      'TestTenantA' | 'TestTenantA'
      'TestTenantB' | 'TestTenantB'
      'TestTenantC' | 'TestTenantC'
  }

  void "List known external KBs"() {

      logger.debug("List known external KBs");

      when:"We ask the system to list known KBs"
        def resp = restBuilder().get("$baseUrl/kbs") {
          header 'X-Okapi-Tenant', 'TestTenantA'
          authHeaders.rehydrate(delegate, owner, thisObject)()
        }

        logger.debug("result ${resp.json}");
        resp.json.each { r ->
          logger.debug("List KBs result [requets for TestTenantA]: ${r}");
        }

      then: "The system responds with a list of tenants";
        resp.status == OK.value()

        // The search should only return 1 record - the one for the American Libraries article
        // resp.json.size() == 1;
        // resp.json[0].title=='American Libraries'

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
      'TestTenantA' | 'note'
      'TestTenantB' | 'note'
      'TestTenantC' | 'note'
  }

   RestBuilder restBuilder() {
        new RestBuilder()
    }

}

