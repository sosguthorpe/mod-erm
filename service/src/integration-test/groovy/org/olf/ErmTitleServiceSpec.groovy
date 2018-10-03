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
import grails.gorm.multitenancy.Tenants
import org.olf.general.RefdataValue
import org.olf.general.RefdataCategory
import org.olf.kb.TitleInstance

@Integration
@Stepwise
class ErmTitleServiceSpec extends GebSpec {

  @Shared
  private Map test_info = [:]

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
      'TestTenantF' | 'TestTenantF'
  }

  void testRefdataSetup() {
    when:  
      def testvalue1 = null;

      Tenants.withId('testtenantf_olf_erm') {
        testvalue1 = RefdataValue.lookupOrCreate('testcat','testvalue')
      }
    then:
      testvalue1 != null;
  }

  void testTitleinstanceResolverService(tenantid, name) {

    when:  

      def title_instance = null;
      def num_identifiers = 0;

      // We are exercising the service directly, normally a transactional context will
      // be supplied by the HTTPRequest, but we fake it here to talk directly to the service
      Tenants.withId(tenantid.toLowerCase()+'_olf_erm') {
        // N.B. This is a groovy MAP, not a JSON document.
        title_instance = titleInstanceResolverService.resolve([
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
        num_identifiers = title_instance.identifiers.size();
      }

    then:
      title_instance.name == 'Brain of the firm'
      title_instance.id != null
      // It would be nice to do this. but DON'T. Our session is terminated in the withId block above, so doing
      // this will cause the test to blow up as the session has gone away. Use the approach take, where we count
      // inside the block and check the count below.
      // title_instance.identifiers.size() == 2
      num_identifiers == 2

    where:
      tenantid | name
      'TestTenantF' | 'TestTenantF'
  }

  void "Second time around don't create a new title"(tenantid, note) {

    when:  
      def title_instance = null;
      def num_identifiers = 0;
      def num_titles = 0;

      // We are exercising the service directly, normally a transactional context will
      // be supplied by the HTTPRequest, but we fake it here to talk directly to the service
      Tenants.withId(tenantid.toLowerCase()+'_olf_erm') {
        // N.B. This is a groovy MAP, not a JSON document.
        title_instance = titleInstanceResolverService.resolve([
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
        num_identifiers = title_instance.identifiers.size();
        num_titles = TitleInstance.findAllByName('Brain of the firm').size();
      }

    then:
      title_instance.name == 'Brain of the firm'
      title_instance.id != null
      // It would be nice to do this. but DON'T. Our session is terminated in the withId block above, so doing
      // this will cause the test to blow up as the session has gone away. Use the approach take, where we count
      // inside the block and check the count below.
      // title_instance.identifiers.size() == 2
      num_identifiers == 2
      num_titles == 1

    where:
      tenantid | note
      'TestTenantF' | 'TestTenantF'
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
      'TestTenantF' | 'note'
  }

   RestBuilder restBuilder() {
        new RestBuilder()
    }

}

