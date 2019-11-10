package org.olf

import static groovyx.net.http.ContentTypes.*
import static org.springframework.http.HttpStatus.*

import com.k_int.okapi.OkapiHeaders
import com.k_int.web.toolkit.testing.HttpSpec

import grails.testing.mixin.integration.Integration
import groovy.util.logging.Slf4j
import java.time.format.DateTimeFormatter
import spock.lang.Stepwise

@Slf4j
@Integration
@Stepwise
class DateParsingSpec extends BaseSpec {
  
  def 'Test various date(/time) string formats' () {
    
    when: 'Create SubscriptionAgreement with date #date_string'
      Map resp = doPost('/erm/sas', {
        name 'Test Agreement 1'
        cancellationDeadline '2018-01-01'
        startDate '2019-05-31T00:30:00Z'
        endDate '2019-06-01T04:55:04Z'
      })
      final sas_id = resp.id
    and: 'Refetch'
      resp = doGet("/erm/sas/${sas_id}")
    then: 'Expect dates to be unchanged'
      assert resp.cancellationDeadline == '2018-01-01'
      assert resp.startDate == '2019-05-31'
      assert resp.endDate == '2019-06-01'
  }
}

