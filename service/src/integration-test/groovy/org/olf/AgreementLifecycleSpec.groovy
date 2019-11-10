package org.olf

import static groovyx.net.http.ContentTypes.*
import static groovyx.net.http.HttpBuilder.configure
import static org.springframework.http.HttpStatus.*

import com.k_int.okapi.OkapiHeaders
import geb.spock.GebSpec
import grails.gorm.multitenancy.Tenants
import grails.plugins.rest.client.RestBuilder
import grails.testing.mixin.integration.Integration
import groovy.json.JsonSlurper
import groovy.util.logging.Slf4j
import groovyx.net.http.ChainedHttpConfig
import groovyx.net.http.FromServer
import groovyx.net.http.HttpBuilder
import groovyx.net.http.HttpVerb
import org.olf.kb.PackageContentItem
import org.olf.kb.Pkg
import org.olf.kb.PlatformTitleInstance
import spock.lang.IgnoreRest
import spock.lang.Shared
import spock.lang.Stepwise
import spock.lang.Unroll


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
 * TODO: Refactor this to use HttpSpec or BaseSpec
 */
@Slf4j
@Integration
@Stepwise
class AgreementLifecycleSpec extends GebSpec {

  def packageIngestService

  private static final String TENANT='TestTenantH'
  

  def setup() {
  }

  def cleanup() {
  }
  
  void "Set up test tenants" () {
    when:"We post a new tenant request to the OKAPI controller"

      // Lets call delete on this tenant before we call create - this will clean up any prior test runs.
      // We don't care if this fails
      def delete_resp = doDelete("/_/tenant")
      
      def resp = doPost("/_/tenant")

    then:"The response is OK"
      resp.fromServer.statusCode == OK.value()
      // resp.headers[CONTENT_TYPE] == ['application/json;charset=UTF-8']
      // resp.json.message == 'Welcome to Grails!'

    // Important NOTE:: hibernateDatastore does not currently provide mirror method to addTenantForSchema. Hence when we delete
    // a tenant in the TenantTest we remove the schema, but are unable to remove it from the pool. Re-Adding a tenant added in a
    // previous test will find the old tenant name in the cache, but find the actual schema gone. Until a method is provided in 
    // hibernateDatastore, we will just use a new tenantId in each separate test.
  }

  // This is actually ripped off from ErmPackageSpec, but we don't do the extensive checking that test does, we just
  // want to get the data in here.
  void "Load Packages"(test_package_file) {

    when:
      // Switching context, just want to make sure that the schema had time to finish initialising.
      Thread.sleep(1000)

      def jsonSlurper = new JsonSlurper()
      def package_data = jsonSlurper.parse(new File(test_package_file))
      def result = null;

      // HeadsUP:: THIS IS A HACK
      // When tenantid comes through the http request it is normalised (lowecased, suffix added), we do that manually here as we are
      // directly exercising the service. It may be better to test this service via a web endpoint, however it's
      // not clear at the moment what form that endpoint will take, so exercising the service directly for now
      Tenants.withId(TENANT.toLowerCase()+'_olf_erm') {
        result = packageIngestService.upsertPackage(package_data)
      }

    then:
      result != null

    where:
      test_package_file | _
      'src/integration-test/resources/packages/apa_1062.json' | _
      'src/integration-test/resources/packages/bentham_science_bentham_science_eduserv_complete_collection_2015_2017_1386.json' | _

  }
  
  void "List Current Agreements"() {

    when:"We ask the system to list known KBs"
      def resp = doGet("/erm/sas")

    then: "The system responds with a list of tenants"
      resp.fromServer.statusCode == OK.value()
      resp.content.size() == 0
  }

  void "Check that we don't currently have any subscribed content"() {

      when:"We ask the subscribed content controller to list the titles we can access"
      
        def params = [
          stats: true
        ]
      
        def resp = doGet("/erm/titles/entitled", params)

      then: "The system responds with a list of content"
        resp.fromServer.statusCode == OK.value()
        // content responds with a JSON object containing a count and a list called subscribedTitles
        resp.content.totalRecords == 0
        resp.content.results.size() == 0
  }
  
  @Unroll
  void "Create an Agreement named #agreement_name of type #type" (agreement_name, type, packageName) {
    
    def pkgSize = 0
    when:"We ask the titles controller to list the titles we can access"
      def resp = doGet("/erm/titles/entitled", [
        stats: true
      ])
  
    then: "The system responds with a list of zero content"
      resp.fromServer.statusCode == OK.value()
      // content responds with a JSON object containing a count and a list called subscribedTitles
      resp.content.totalRecords == 0
    
    when: "Query for Agreement with name #agreement_name"
    
      resp = doGet("/erm/sas", [
        filters:[
          "name=i=${agreement_name}" // Case insensitive match
        ]
      ])
      
    then: "No agreement found"
      resp.fromServer.statusCode == OK.value()
      resp.content.size() == 0
      
      
    when: "Looked up package with name - #packageName"
      resp = doGet("/erm/resource", [
        filters:[
          "class==${Pkg.class.name}",
          "name=i=${packageName}" // Case insensitive match
        ]
      ])
      
    then: "Package found"
      resp.fromServer.statusCode == OK.value()
      resp.content.size() == 1
      resp.content[0].id != null
      
    when: "Looked up package item count"
      def packageId = resp.content[0].id
      
      resp = doGet("/erm/resource", [
        stats: true,
        perPage: 1, // Just fetch one record with the stats included. We only want the full count.
        filters:[
          "class==${PackageContentItem.class.name}",
          "pkg==${packageId}"
        ]
      ])
      
    then: "Response is good and we have a count"
      resp.fromServer.statusCode == OK.value()
      (pkgSize = resp.content.totalRecords) > 0
      
    when: "Post to create new agreement named #agreement_name with our package"
    
      Map data = [
        'name' : agreement_name,
        'agreementType': type, // This can be the value or id but not the label
        'items' : [
          [resource: packageId]
        ]
      ]
      resp = doPost("/erm/sas/", data)
    
    then: "Response is good and we have a new ID"
      resp.fromServer.statusCode == CREATED.value()
      resp.content.id != null
      
    when: "Query for Agreement with name #agreement_name"
    
      def agreementId = resp.content.id
    
      resp = doGet("/erm/sas", [
        filters:[
          "name=i=${agreement_name}" // Case insensitive match
        ]
      ])
      
    then: "Agreement found and ID matches returned one from before"
      resp.fromServer.statusCode == OK.value()
      resp.content.size() == 1
      resp.content[0].id != null
      resp.content[0].id == agreementId
      
    when:"We ask the titles controller to list the titles we can access"
      resp = doGet("/erm/titles/entitled", [
        stats: true
      ])
  
    then: "The list of content is equal to the number of package titles"
      resp.fromServer.statusCode == OK.value()
      // content responds with a JSON object containing a count and a list called subscribedTitles
      resp.content.totalRecords == pkgSize
      
    where:
      agreement_name        | type    | packageName
      'My first agreement'  | 'draft' | "American Psychological Association:Master"
  }
  
  @Unroll
  void "Add #resourceType for title #titleName to the Agreement named #agreement_name" (agreement_name, resourceType, titleName, filterKey) {
    
    def entitledResourceCount = 0
    when:"We ask the titles controller to list the titles we can access"
      def resp = doGet("/erm/titles/entitled", [
        stats: true
      ])
  
    then: "The list of content is returned"
      resp.fromServer.statusCode == OK.value()
      // content responds with a JSON object containing a count and a list called subscribedTitles
      (entitledResourceCount = resp.content.totalRecords) >= 0
    
    when: "Fetch #resourceType for title #titleName"
      def resourceId = null
      resp = doGet("/erm/resource", [
        filters:[
          "class==${resourceType}", // Case insensitive match
          "${filterKey}=i=${titleName}" // Case insensitive match
        ]
      ])
      
    then: "Single Resource found"
      resp.fromServer.statusCode == OK.value()
      resp.content.size() == 1
      (resourceId = resp.content[0].id ) != null
      
    when: "Query for Agreement with name #agreement_name"
      def agreementItemCount = 0
      def currentEntitlements = []
      resp = doGet("/erm/sas", [
        filters:[
          "name=i=${agreement_name}" // Case insensitive match
        ]
      ])
      
    then: "Single Agreement found"
      resp.fromServer.statusCode == OK.value()
      resp.content.size() == 1
      (agreementItemCount = resp.content[0].items.size()) >= 0
            
    when: "Resource added to Agreement"
      
      def data = [
        'items' : resp.content[0].items.collect({ ['id': it.id] }) + [[resource: resourceId]]
      ] 
      
      resp = doPut("/erm/sas/${resp.content[0].id}", data)
          
    then: "Response is good and item count increased by 1"
      resp.fromServer.statusCode == OK.value()
      resp.content.items.size() == (agreementItemCount + 1)
      
    when:"We ask the titles controller to list the titles we can access"
      resp = doGet("/erm/titles/entitled", [
        stats: true
      ])
  
    then: "The list of content has increased by 1"
      resp.fromServer.statusCode == OK.value()
      // content responds with a JSON object containing a count and a list called subscribedTitles
      resp.content.totalRecords == (entitledResourceCount + 1)
      
    where:
      agreement_name        | resourceType                        | titleName                             | filterKey
      'My first agreement'  | PackageContentItem.class.name       | "Pharmaceutical Nanotechnology"       | "pti.titleInstance.name"
      'My first agreement'  | PlatformTitleInstance.class.name    | "Recent Patents on Corrosion Science" | "titleInstance.name"
  }

  void "Delete the tenant" () {

    given: "delete request to the OKAPI controller"
      def resp = doDelete("/_/tenant")
      
    expect:"OK response and deleted tennant"
      resp.fromServer.statusCode == OK.value()
  }

  
  /**
   * HTTP methods for test added here.
   */
  private HttpBuilder client = null
  private FromServer lastResponse
  private HttpBuilder getClient() {
    
    if (!client) {
      final String urlBase = "${baseUrl}"
      client = configure {
      
        // Default root as specified in config.
        request.uri = urlBase
        // Intercept all verbs.
        execution.interceptor(HttpVerb.values()) { ChainedHttpConfig cfg, fx ->
          cfg.request.headers = [
            (OkapiHeaders.TOKEN): 'dummy',
            (OkapiHeaders.USER_ID): 'dummy',
            (OkapiHeaders.PERMISSIONS): '[ "erm.admin", "erm.user", "erm.own.read", "erm.any.read"]',
            (OkapiHeaders.TENANT): TENANT
          ]
          
          // Request JSON.
          cfg.chainedRequest.contentType = JSON[0]
                    
          // Apply the original action.
          fx.apply(cfg)
        }
        
        response.success { FromServer fs, Object body ->
          lastResponse = fs
          body
        }
        
        response.failure { FromServer fs, Object body ->
          lastResponse = fs
          body
        }
      
      }
    }
    
    client
  }
  
  private cleanUri (String uri) {
    uri?.startsWith('//') ? uri.substring(1) : uri
  }
    
  private def doGet (final String uri, final Map params = null) {    
    [ 'content' : getClient().get({
        request.uri = cleanUri(uri)
        request.uri.query = params
      }),
      'fromServer' : lastResponse
    ]
  }
  
  private def doPost (final String uri, final def jsonData = null, final Map params = null){
    [ 'content' : getClient().post({
        request.uri = cleanUri(uri)
        request.uri.query = params
        request.body = jsonData
      }),
      'fromServer' : lastResponse
    ]
  }
  
  private def doPut (final String uri, final def jsonData = null, final Map params = null) {
    [ 'content' : getClient().put({
        request.uri = cleanUri(uri)
        request.uri.query = params
        request.body = jsonData
      }),
      'fromServer' : lastResponse
    ]
  }
  
  private def doDelete (final String uri, final Map params = null) {
    [ 'content' : getClient().delete({
        request.uri = cleanUri(uri)
        request.uri.query = params
      }),
      'fromServer' : lastResponse
    ]
  }
}

