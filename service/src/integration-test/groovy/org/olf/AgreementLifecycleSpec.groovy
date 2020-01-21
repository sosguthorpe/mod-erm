package org.olf

import static groovyx.net.http.ContentTypes.*
import static groovyx.net.http.HttpBuilder.configure
import static org.springframework.http.HttpStatus.*

import org.olf.kb.PackageContentItem
import org.olf.kb.Pkg
import org.olf.kb.PlatformTitleInstance

import com.k_int.okapi.OkapiHeaders
import com.k_int.okapi.OkapiTenantResolver
import geb.spock.GebSpec
import grails.gorm.multitenancy.Tenants
import grails.testing.mixin.integration.Integration
import groovy.json.JsonSlurper
import groovyx.net.http.ChainedHttpConfig
import groovyx.net.http.FromServer
import groovyx.net.http.HttpBuilder
import groovyx.net.http.HttpVerb
import java.time.LocalDate
import spock.lang.Stepwise
import spock.lang.Unroll

@Integration
@Stepwise
class AgreementLifecycleSpec extends BaseSpec {

  def importService
  
  void "Load Packages" (test_package_file) {

    when: 'File loaded'

      def jsonSlurper = new JsonSlurper()
      def package_data = jsonSlurper.parse(new File(test_package_file))
      int result = 0
      final String tenantid = currentTenant.toLowerCase()
      Tenants.withId(OkapiTenantResolver.getTenantSchemaName( tenantid )) {
        result = importService.importPackageUsingInternalSchema( package_data )
      }

    then: 'Package imported'
      result > 0

    where:
      test_package_file | _
      'src/integration-test/resources/packages/apa_1062.json' | _
      'src/integration-test/resources/packages/bentham_science_bentham_science_eduserv_complete_collection_2015_2017_1386.json' | _

  }
  
  void "List Current Agreements"() {

    when:"We ask the system to list known Agreements"
      List resp = doGet("/erm/sas")

    then: "The system responds with a list of 0"
      resp.size() == 0
  }

  void "Check that we don't currently have any subscribed content" () {

    when:"We ask the subscribed content controller to list the titles we can access"
    
      List resp = doGet("/erm/titles/entitled")

    then: "The system responds with an empty list"
      resp.size() == 0
  }
  
  @Unroll
  void "Create an Agreement named #agreement_name with status #status" (agreement_name, status, packageName) {
    final LocalDate today = LocalDate.now()
    final LocalDate tomorrow = today.plusDays(1)
    
    def pkgSize = 0    
    when: "Query for Agreement with name #agreement_name"
    
      List resp = doGet("/erm/sas", [
        filters:[
          "name=i=${agreement_name}" // Case insensitive match
        ]
      ])
      
    then: "No agreement found"
      resp.size() == 0
      
    when: "Looked up package with name - #packageName"
      resp = doGet("/erm/resource", [
        filters:[
          "class==${Pkg.class.name}",
          "name=i=${packageName}" // Case insensitive match
        ]
      ])
      
    then: "Package found"
      resp.size() == 1
      resp[0].id != null
      
    when: "Looked up package item count"
      def packageId = resp[0].id
      
      Map respMap = doGet("/erm/resource", [
        perPage: 1, // Just fetch one record with the stats included. We only want the full count.
        stats: true,
        filters: [
          "class==${PackageContentItem.class.name}",
          "pkg==${packageId}"
        ]
      ])
      
    then: "Response is good and we have a count" 
      (pkgSize = respMap.totalRecords) > 0
      
    when: "Post to create new agreement named #agreement_name with our package"
      respMap = doPost("/erm/sas", {
        'name' agreement_name
        'agreementStatus' status // This can be the value or id but not the label
        'periods' ([{
          'startDate' today.toString()
          'endDate' tomorrow.toString()
        }])
        'items' ([
          { 'resource' packageId }
        ])
      })
    
    then: "Response is good and we have a new ID"
      respMap.id != null
      
    when: "Query for Agreement with name #agreement_name"
    
      def agreementId = respMap.id
    
      resp = doGet("/erm/sas", [
        filters:[
          "name=i=${agreement_name}" // Case insensitive match
        ]
      ])
      
    then: "Agreement found and ID matches returned one from before"
      resp.size() == 1
      resp[0].id == agreementId
      
    when:"We ask the titles controller to list the titles we can access"
      respMap = doGet("/erm/titles/entitled", [ stats: true ])
  
    then: "The list of content is equal to the number of package titles"
      respMap.totalRecords == pkgSize
      
    where:
      agreement_name        | status    | packageName
      'My first agreement'  | 'Active'   | "American Psychological Association:Master"
  }
  
  @Unroll
  void "Add #resourceType for title #titleName to the Agreement named #agreement_name" (agreement_name, resourceType, titleName, filterKey) {
    
    def entitledResourceCount = 0
    when:"We ask the titles controller to list the titles we can access"
      Map respMap = doGet("/erm/titles/entitled", [
        stats: true
      ])
  
    then: "The list of content is returned"
      // content responds with a JSON object containing a count and a list called subscribedTitles
      (entitledResourceCount = respMap.totalRecords) >= 0
    
    when: "Fetch #resourceType for title #titleName"
      def resourceId = null
      List resp = doGet("/erm/resource", [
        filters:[
          "class==${resourceType}", // Case insensitive match
          "${filterKey}=i=${titleName}" // Case insensitive match
        ]
      ])
      
    then: "Single Resource found"
      resp.size() == 1
      (resourceId = resp[0].id ) != null
      
    when: "Query for Agreement with name #agreement_name"
      def agreementItemCount = 0
      def currentEntitlements = []
      resp = doGet("/erm/sas", [
        filters:[
          "name=i=${agreement_name}" // Case insensitive match
        ]
      ])
      
    then: "Single Agreement found"
      resp.size() == 1
      (agreementItemCount = resp[0].items.size()) >= 0
            
    when: "Resource added to Agreement"
      
      def data = [
        'items' : resp[0].items.collect({ ['id': it.id] }) + [[resource: resourceId]]
      ] 
      
      respMap = doPut("/erm/sas/${resp[0].id}", data)
          
    then: "Response is good and item count increased by 1"
      respMap.items.size() == (agreementItemCount + 1)
      
    when:"We ask the titles controller to list the titles we can access"
      respMap = doGet("/erm/titles/entitled", [
        stats: true
      ])
  
    then: "The list of content has increased by 1"
      // content responds with a JSON object containing a count and a list called subscribedTitles
      respMap.totalRecords == (entitledResourceCount + 1)
      
    where:
      agreement_name        | resourceType                        | titleName                             | filterKey
      'My first agreement'  | PackageContentItem.class.name       | "Pharmaceutical Nanotechnology"       | "pti.titleInstance.name"
      'My first agreement'  | PlatformTitleInstance.class.name    | "Recent Patents on Corrosion Science" | "titleInstance.name"
  }
}

