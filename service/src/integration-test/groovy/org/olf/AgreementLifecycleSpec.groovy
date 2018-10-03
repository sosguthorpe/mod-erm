package org.olf

import static grails.web.http.HttpHeaders.*
import static org.springframework.http.HttpStatus.*

import org.olf.erm.SubscriptionAgreement
import org.olf.kb.PackageContentItem
import org.olf.kb.Pkg
import org.olf.kb.PlatformTitleInstance
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.k_int.okapi.OkapiHeaders
import com.k_int.okapi.OkapiTenantResolver

import geb.spock.*
import grails.gorm.multitenancy.Tenants
import grails.plugins.rest.client.RestBuilder
import grails.testing.mixin.integration.Integration
import grails.transaction.*
import groovy.json.JsonSlurper
import spock.lang.*


/**
 * unlike the other integration tests, this test is about simulating the end to end process of creating and managing agreements.
 *
 * The aim of the test is
 *   Load some packages so we have some titles to work with - make sure we load enough that we have some that will be visible
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
  private int totalContentItems = 0

  private static final String TENANT='TestTenantH'
  private static final String PACKAGE_QUERY = 'select p.id from Pkg as p where p.name = :name'
  private static final String PACKAGE_CONTENT_COUNT_QUERY = 'select count(*) from PackageContentItem as pci where pci.pkg.id = :pkgId'

  // This is a bit of a shortcut - In this test we have loaded packages where titles only appear in one place. That
  // means we can be very general when looking up items. If we add more test data, then we would need to add more
  // where clauses to actually idetify specific packages and platforms.
  private static final String PACKAGE_CONTENT_ITEM_QUERY = '''select pci.id
from PackageContentItem as pci
where pci.pti.titleInstance.name = :title
'''

  private static final String OFF_PACKAGE_TITLE_QUERY = '''select pti.id
from PlatformTitleInstance as pti
where pti.titleInstance.name = :title
and pti.platform.name = :platform
'''

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
      // Switching context, just want to make sure that the schema had time to finish initialising.
      Thread.sleep(1000);

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
      tenantid  | test_package_file
      TENANT    | 'src/integration-test/resources/packages/apa_1062.json'
      TENANT    | 'src/integration-test/resources/packages/bentham_science_bentham_science_eduserv_complete_collection_2015_2017_1386.json'

  }

  void "List Current Agreements"() {

      when:"We ask the system to list known KBs"
        def resp = restBuilder().get("$baseUrl/erm/sas") {
          header 'X-Okapi-Tenant', TENANT
          authHeaders.rehydrate(delegate, owner, thisObject)()
        }

      then: "The system responds with a list of tenants";
        resp.status == OK.value()
        resp.json.size() == 0
  }

  void "Check that we don't currently have any subscribed content"() {

      when:"We ask the subscribed content controller to list the titles we can access"
        def resp = restBuilder().get("$baseUrl/erm/content") {
          header 'X-Okapi-Tenant', TENANT
          authHeaders.rehydrate(delegate, owner, thisObject)()
        }

      then: "The system responds with a list of content";
        resp.status == OK.value()
        // content responds with a JSON object containing a count and a list called subscribedTitles
        resp.json.total == 0
        resp.json.results.size() == 0
  }

  void "Set up new agreements"(tenant, agreement_name, type) {

      when:"We add a new agreement"

        // This is a bit of a shortcut - the web interface will populate a control for this, but here we just want the value.
        // So we access the DB with the tenant Id and get back the ID of the status we need.
        Serializable agreement_type_id = null
        Tenants.withId(OkapiTenantResolver.getTenantSchemaName(tenant.toLowerCase())) {
          
          // All refdata values have a few helper methods on the class.
          agreement_type_id = SubscriptionAgreement.lookupOrCreateAgreementType(type).id
        }

        
        Map agreement_to_add =  [ 
          'name' : agreement_name,
          'agreementType':[
            'id': agreement_type_id
          ]
        ];

        def resp = restBuilder().post("$baseUrl/erm/sas") {
          header 'X-Okapi-Tenant', tenant
          authHeaders.rehydrate(delegate, owner, thisObject)()
          contentType 'application/json'
          accept 'application/json'
          json agreement_to_add
        }

      then:"The agreement is created OK"
       resp.status == CREATED.value()


      // Use a GEB Data Table to load each record
      where:
        tenant | agreement_name       | type
        TENANT | 'My first agreement' | 'Draft'

  }

  void "add a package to the agreement"(tenant,agreement_name) {
    when:"We add a package to our new agreement"
      // Find the ID of our new agreement.
      def agreement_id = null
      def pkg_id = null
      def single_package_item_id = null
      def off_package_title_id = null
      def pkg_content_count

      // This is cheating a little - normally the UI would run queries to populate controls that let the user select this info,
      // here we're just grabbing the relevant IDs from the database.
      Tenants.withId(tenant.toLowerCase()+'_olf_erm') {

        agreement_id = SubscriptionAgreement.executeQuery('select sa.id from SubscriptionAgreement as sa where sa.name = :name',[name:agreement_name]).get(0)

        pkg_id = Pkg.executeQuery(PACKAGE_QUERY,[name:'American Psychological Association:Master']).get(0)
        
        pkg_content_count = (PackageContentItem.executeQuery(PACKAGE_CONTENT_COUNT_QUERY, [pkgId: pkg_id])?.getAt(0) ?: 0)

        single_package_item_id = PackageContentItem.executeQuery(PACKAGE_CONTENT_ITEM_QUERY,[title:'Anti Inflammatory & Anti allergy Agents in Medicinal Chemistry']).get(0)

        logger.debug("Find platform title instance records for current medicinal chemistry on platform bentham science")

        off_package_title_id = PlatformTitleInstance.executeQuery(OFF_PACKAGE_TITLE_QUERY,
          [title:'Current Medicinal Chemistry - Cardiovascular & Hematological Agents',
          platform:'Bentham Science']).get(0)
      }
      
      
      // Increment the total content counter for comparison later.
      // Total for the package plus the 2 singles
      totalContentItems += (pkg_content_count + 2)

      logger.debug("Agreement ID is ${agreement_id} package to add is ${pkg_id} which contains ${pkg_content_count} titles.")
      agreement_id != null

    then: "The package is Added to the agreement"

      Map content_to_add = [
        content:[
          [ 'type':'package', 'id': pkg_id],
          [ 'type':'packageItem', 'id': single_package_item_id],
          [ 'type':'platformTitle', 'id': off_package_title_id]
        ]
      ]

      String target_url = "$baseUrl/erm/sas/${agreement_id}/addToAgreement".toString();
      logger.debug("The target URL will be ${target_url}");

      def resp = restBuilder().post(target_url) {
          header 'X-Okapi-Tenant', tenant
          authHeaders.rehydrate(delegate, owner, thisObject)()
          contentType 'application/json'
          accept 'application/json'
          json content_to_add
      }
      
      resp.status == OK.value()

    where:
      tenant | agreement_name
      TENANT | 'My first agreement'
  }

  void "Check that we see the new titles as subscribed content"() {

      when:"We ask the subscribed content controller to list the titles we can access"
        def resp = restBuilder().get("$baseUrl/erm/content") {
          header 'X-Okapi-Tenant', TENANT
          authHeaders.rehydrate(delegate, owner, thisObject)()
        }

      then: "The system responds with a list of content";
        resp.status == OK.value()
        // content responds with a JSON object containing a count and a list called subscribedTitles
        resp.json.total == totalContentItems
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

