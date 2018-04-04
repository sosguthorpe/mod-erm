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
import groovy.json.JsonSlurper

@Integration
@Stepwise
class ErmPackageSpec extends GebSpec {

  @Shared
  private Map test_info = [:]

  def packageIngestService
  def titleInstanceResolverService

  final Closure authHeaders = {
    header OkapiHeaders.TOKEN, 'dummy'
    header OkapiHeaders.USER_ID, 'dummy'
    header OkapiHeaders.PERMISSIONS, '[ "erm.admin", "erm.user", "erm.own.read", "erm.any.read"]'
  }

  final static Logger logger = LoggerFactory.getLogger(ErmPackageSpec.class);

  def setup() {
  }

  def cleanup() {
  }

  // Set up a new tenant called RSTestTenantE
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
      'TestTenantE' | 'TestTenantE'
  }

  void testTitleinstanceResolverService() {
    when:
      // N.B. This is a groovy MAP, not a JSON document.
      def title_instance = titleInstanceResolverService.resolve([
        'title':'Brain of the firm',
        'instanceMedium': 'print',
        'instanceMedia': 'BKM',
        'instanceIdentifiers': [ 
          [
            'namespace': 'isbn',
            'value': '0713902191'
          ],
          [
            'namespace': 'isbn',
            'value': '9780713902198'
          ] 
        ],
        'siblingInstanceIdentifiers': [ 
          [
            // 2e - print
            'namespace': 'isbn',
            'value': '047194839X'
          ] ]

      ]);
    then:
      title_instance == null;
  }

  void "Load Packages"(tenantid, test_package_file) {

    when:
      def jsonSlurper = new JsonSlurper()
      def package_data = jsonSlurper.parse(new File(test_package_file))

      // HeadsUP:: THIS IS A HACK
      // When tenantid comes through the http request it is normalised (lowecased, suffix added), we do that manually here as we are
      // directly exercising the service. It may be better to test this service via a web endpoint, however it's
      // not clear at the moment what form that endpoint will take, so exercising the service directly for now
      def result = packageIngestService.upsertPackage(tenantid.toLowerCase()+'_olf_erm', package_data);

    then:
      result != null

    where:
      tenantid | test_package_file
      'TestTenantE' | 'src/integration-test/resources/packages/apa_1062.json'
      'TestTenantE' | 'src/integration-test/resources/packages/bentham_science_bentham_science_eduserv_complete_collection_2015_2017_1386.json'

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
      'TestTenantE' | 'note'
  }

   RestBuilder restBuilder() {
        new RestBuilder()
    }

}

