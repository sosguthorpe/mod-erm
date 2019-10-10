package org.olf

import java.time.LocalDate

import com.k_int.okapi.OkapiHeaders
import com.k_int.web.toolkit.testing.HttpSpec

import grails.testing.mixin.integration.Integration
import groovy.util.logging.Slf4j
import groovyx.net.http.HttpException
import spock.lang.Shared
import spock.lang.Stepwise
import spock.util.concurrent.PollingConditions

@Slf4j
@Integration
@Stepwise
class AgreementRelationshipsSpec extends HttpSpec {
  
  @Shared String type_value = ''

  def setupSpec() {
    httpClientConfig = {
      client.clientCustomizer { HttpURLConnection conn ->
        conn.connectTimeout = 2000
        conn.readTimeout = 10000
      }
    }
    addDefaultHeaders(
      (OkapiHeaders.TENANT): 'http_tests',
      (OkapiHeaders.USER_ID): 'http_test_user'
    )
  }

  

  void 'Ensure test tenant' () {

    // Max time to wait is 10 seconds
    def conditions = new PollingConditions(timeout: 10)
    when: 'Create the tenant'
      def resp = doPost('/_/tenant', {
        parameters ([["key": "loadReference", "value": true]])
      })

    then: 'Response obtained'
      resp != null

    and: 'Refdata added'

      List list
      // Wait for the refdata to be loaded.
      conditions.eventually {
        (list = doGet('/erm/refdata')).size() > 0
      }
  }
  
  void 'Check relationship types present' () {
    given: 'Get types'
      List httpResult = doGet('/erm/refdata/AgreementRelationship/type')
    
    expect: 'Types returned'
      assert (httpResult ?: []).size() > 0
      assert (type_value = httpResult[0].value) != null
  }

  void 'Test agreements and relationship' () {
    final LocalDate today = LocalDate.now()
    final LocalDate tomorrow = today.plusDays(1)
    
    when: 'Post simple agreement'
      Map httpResult = doPost('/erm/sas') {
        periods ([{
          startDate today.toString()
          endDate tomorrow.toString()
        }])
        name 'Agreement 1'
      }

      final String ag1 = httpResult.id
    then: 'Agreement created and returned'
      assert ag1 != null

    when: 'Post another simple agreement with outward relationship'
      httpResult = doPost('/erm/sas') {
        name 'Agreement 2'
        periods ([{
          startDate today.plusDays(2).toString()
          endDate tomorrow.plusDays(2).toString()
        }])
        outwardRelationships ([{
          type type_value
          inward ag1
        }])
      }

      final String ag2 = httpResult.id

    then: 'Agreement created and returned with outward relationship'
      assert ag2 != null
      assert httpResult.outwardRelationships.size() == 1
      assert httpResult.outwardRelationships[0].inward?.id == ag1
      
    when: 'Check reciprocal inward relationship'
      httpResult = doGet("/erm/sas/${ag1}")
    then: 'Reciprocal inward relationship present'
      assert httpResult.inwardRelationships.size() == 1
      assert httpResult.inwardRelationships[0].outward?.id == ag2
      
    when: 'New inward relationship added to agreement 2'
      httpResult = doPut("/erm/sas/${ag2}") {
        inwardRelationships ([{
          type type_value
          outward ag1
        }])
      }
    then: 'Agreement updated and New inward relationship present'
      assert httpResult.inwardRelationships.size() == 1
      assert httpResult.inwardRelationships[0].outward?.id == ag1
      
    when: 'Check reciprocal outward relationship'
      httpResult = doGet("/erm/sas/${ag1}")
    then: 'Reciprocal outward relationship present'
      assert httpResult.outwardRelationships.size() == 1
      assert httpResult.outwardRelationships[0].inward?.id == ag2
      
    when: 'Attempt to relate agreement 1 to itself'
    
      httpResult = doPut("/erm/sas/${ag1}") {
        inwardRelationships ([{
          type type_value
          outward ag1
        }])
      }
    then: 'Failure messsage returned'
     HttpException ex = thrown()
     assert ex.statusCode == 422
     assert ex.fromServer?.message
    
    when: 'Attempt to add inward realtionship with no outward property'
      httpResult = doPut("/erm/sas/${ag1}") {
        inwardRelationships ([{
          type type_value
        }])
      }
    then: 'Failure messsage returned'
     ex = thrown()
     assert ex.statusCode == 422
     assert ex.fromServer?.message
    
    when: 'Attempt to add outward realtionship with no inward property'
      httpResult = doPut("/erm/sas/${ag1}") {
        outwardRelationships ([{
          type type_value
        }])
      }
    then: 'Failure messsage returned'
     ex = thrown()
     assert ex.statusCode == 422
     assert ex.fromServer?.message
  }


  def cleanupSpecWithSpring() {
    Map resp = doDelete('/_/tenant', null)
  }
}
