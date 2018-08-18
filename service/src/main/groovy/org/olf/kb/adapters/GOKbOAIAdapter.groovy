package org.olf.kb.adapters;

import org.olf.kb.KBCacheUpdater;
import org.olf.kb.RemoteKB;
import org.olf.kb.KBCache;
import groovy.json.JsonSlurper;

import static groovyx.net.http.ContentType.URLENC
import static groovyx.net.http.ContentType.XML
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
import static groovyx.net.http.Method.GET
import static groovyx.net.http.Method.POST
import groovyx.net.http.*
import org.apache.http.entity.mime.*
import org.apache.http.entity.mime.content.*
import org.apache.http.*
import org.apache.http.protocol.*
import java.text.SimpleDateFormat
import java.nio.charset.Charset
import static groovy.json.JsonOutput.*


import java.text.*


public class GOKbOAIAdapter implements KBCacheUpdater {


  public Object freshen(String source_name,
                        String base_url,
                        String current_cursor,
                        KBCache cache) {

    println("GOKbOAIAdapter::freshen - fetching from URI: ${base_url}");
    def jpf_api = new HTTPBuilder(base_url)

    def query_params = [
        'verb': 'ListRecords',
        'metadataPrefix': 'gokb'
    ]

    def cursor = null;
    def found_records = true;

    if ( current_cursor  != null ) {
      cursor = current_cursor
      query_params.from=cursor;
    }
    else {
      cursor = '';
    }

    while ( found_records ) {
 
      jpf_api.request(Method.GET) { req ->
        // uri.path=''
        // requestContentType = ContentType.JSON
        headers.Accept = 'application/xml'
        uri.query=query_params

        response.success = { resp, xml ->
          // println "Success! ${resp.status} ${xml}"
          Map page_result = processPage(cursor, xml, source_name, cache)
          println("processPage returned, processed ${page_result.count} packages");
          cache.updateCursor(source_name,page_result.new_cursor);

          if ( page_result.count > 0 ) {
            // If we processed records, and we have a resumption token, carry on.
            if ( page_result.resumptionToken ) {
              query_params.resumptionToken = page_result.resumptionToken
            }
            else {
              // Reached the end of the data
              found_records = false;
            }
          }
          else {
            found_records = false;
          }
        }

        response.failure = { resp ->
          println "Request failed with status ${resp.status}"
          found_records = false;
        }
      }
    }

  }


  private Map processPage(String cursor, Object oai_page, String source_name, KBCache cache) {
    def result = [:]
    result.new_cursor = cursor;
    result.count = 0;

    oai_page.ListRecords.record.each { record ->
      result.count++;
      def record_identifier = record?.header?.identifier?.text();
      def package_name = record?.metadata?.gokb?.package?.name?.text()
      def datestamp = record?.header?.datestamp?.text()

      System.out.println(result.count)
      System.out.println(record_identifier)
      System.out.println(package_name);

      // processPackage(pkg.packageContentAsJson, source_name, cache);

      if ( datestamp > result.new_cursor ) {
        System.out.println("New cursor value - ${datestamp} > ${result.new_cursor} ");
        result.new_cursor = datestamp;
      }
    }

    result.resumptionToken = ListRecords?.resumptionToken?.text()
    return result;
  }

  private void processPackage(String url, String source_name, KBCache cache) {
    println("processPackage(${url},${source_name}) -- fetching");
    try {
    def jpf_api = new HTTPBuilder(url)
    jpf_api.request(Method.GET) { req ->
      headers.Accept = 'application/json'
      response.success = { resp, json ->
        cache.onPackageChange(source_name, json);
      }
      response.failure = { resp ->
        println "Request failed with status ${resp.status}"
      }
    }
    }
    catch ( Exception e ) {
      println("Unexpected error processing package ${url}");
      e.printStackTrace();
      throw e;
    }
  }

  /**
   * convert the gokb package metadataPrefix into our canonical ERM json structure as seen at
   * https://github.com/folio-org/mod-erm/blob/master/service/src/integration-test/resources/packages/apa_1062.json
   */
  private Map gokbToERM(Object xml_gokb_record) {

    def package_name = xml_gokb_record?.metadata?.gokb?.package?.name?.text()
    def package_shortcode = xml_gokb_record?.metadata?.gokb?.package?.shortcode?.text()
    def nominal_provider = xml_gokb_record?.metadata?.gokb?.package?.nominalProvider?.name?.text()


    def result = [
      header:[
        availability:[
          type: 'general'
        ],
        packageProvider:[
          name:nominal_provider
        ],
        packageSource:'',
        packageName: package_name,
        packageSlug: package_shortcode
      ],
      packageContents: []
    ]

    xml_gokb_record?.metadata?.gokb?.TIPPs?.TIPP.each { tipp_entry ->
      packageContents.add([
        "title": "Journal of Experimental Psychology: Learning, Memory, and Cognition",
        "instanceMedium": "electronic",
        "instanceMedia": "journal",
        "instanceIdentifiers": [
          [
          "namespace": "eissn",
          "value": "1939-1285"
          ]
        ],
        "siblingInstanceIdentifiers": [
          [
            "namespace": "issn", "value": "0278-7393"
          ]
        ],
        "coverage": [
          "startVolume": "8",
          "startIssue": "1",
          "startDate": "1982-01-01",
          "endVolume": null,
          "endIssue": null,
          "endDate": null
        ],
        "embargo": null,
        "coverageDepth": "fulltext",
        "coverageNote": "previously: Journal of Experimental Psychology: Human Learning and Memory (0096-1515) 1975-1981; Journal of Experimental Psychology (0022-1015) 1916-1974",
        "platformUrl": "http://content.apa.org/journals/xlm",
        "platformName": "APA PsycNet",
        "_platformId": 565
      ])
    }
    
    return result;
  }

}
