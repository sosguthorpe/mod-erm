package org.olf.kb.adapters;

import org.olf.kb.KBCacheUpdater;
import org.olf.kb.RemoteKB;
import org.olf.kb.KBCache;
import groovy.json.JsonSlurper;
import java.util.Map;


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


/**
 * An adapter to go betweent the GOKb OAI service, for example the one at 
 *   https://gokbt.gbv.de/gokb/oai/index/packages?verb=ListRecords&metadataPrefix=gokb
 * and our internal KBCache implementation.
 */
public class GOKbOAIAdapter implements KBCacheUpdater {


  public void freshenPackageData(String source_name,
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

 
      println("** GET https://gokbt.gbv.de/gokb/oai/index/packages ${query_params}");

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

  public void freshenHoldingsData(String cursor,
                                  String source_name,
                                  KBCache cache) {
    throw new RuntimeException("Holdings data not suported by GOKb");
  }


  private Map processPage(String cursor, Object oai_page, String source_name, KBCache cache) {

    def result = [:]
    result.new_cursor = cursor;
    result.count = 0;

    // log.debug("GOKbOAIAdapter::processPage(${cursor},...");

    oai_page.ListRecords.record.each { record ->
      result.count++;
      def record_identifier = record?.header?.identifier?.text();
      def package_name = record?.metadata?.gokb?.package?.name?.text()
      def datestamp = record?.header?.datestamp?.text()

      System.out.println(result.count)
      System.out.println(record_identifier)
      System.out.println(package_name);

      def json_package_description = gokbToERM(record);
      if ( json_package_description.header.status == 'deleted' ) {
        // ToDo: Decide what to do about deleted records
      }
      else {
        cache.onPackageChange(source_name, json_package_description);
      }

      if ( datestamp > result.new_cursor ) {
        System.out.println("New cursor value - ${datestamp} > ${result.new_cursor} ");
        result.new_cursor = datestamp;
      }
    }

    result.resumptionToken = oai_page.ListRecords?.resumptionToken?.text()
    return result;
  }

  /**
   * convert the gokb package metadataPrefix into our canonical ERM json structure as seen at
   *   https://github.com/folio-org/mod-erm/blob/master/service/src/integration-test/resources/packages/apa_1062.json
   * the GOKb records look like this
   *   https://gokbt.gbv.de/gokb/oai/index/packages?verb=ListRecords&metadataPrefix=gokb
   */
  private Map gokbToERM(Object xml_gokb_record) {

    def package_record = xml_gokb_record?.metadata?.gokb?.package

    def result = null;

    if ( ( package_record != null ) && ( package_record.name != null ) ) {

      def package_name = package_record.name?.text()
      def package_shortcode = package_record.shortcode?.text()
      def nominal_provider = package_record.nominalProvider?.name?.text()
  
  
      result = [
        header:[
          status: xml_gokb_record?.header?.status,
          availability:[
            type: 'general'
          ],
          packageProvider:[
            name:nominal_provider
          ],
          packageSource:'GOKb',
          packageName: package_name,
          packageSlug: package_shortcode
        ],
        packageContents: []
      ]
  
      package_record.TIPPs?.TIPP.each { tipp_entry ->

        def tipp_title = tipp_entry?.title?.name?.text()
        def tipp_medium = tipp_entry?.medium?.text()
        def tipp_media = 'journal'
        def tipp_instance_identifiers = [] // [ "namespace": "issn", "value": "0278-7393" ]
        def tipp_sibling_identifiers = []

        // If we're processing an electronic record then issn is a sibling identifier
        tipp_entry.title.identifiers.identifier.each { ti_id ->
          if ( ti_id.@namespace == 'issn' ) {
            tipp_sibling_identifiers.add(["namespace": "issn", "value": ti_id.@value?.toString() ]);
          }
          else {
            tipp_instance_identifiers.add(["namespace": ti_id.@namespace?.toString(), "value": ti_id.@value?.toString() ]);
          }
        }

        def tipp_coverage = [] // [ "startVolume": "8", "startIssue": "1", "startDate": "1982-01-01", "endVolume": null, "endIssue": null, "endDate": null ],
 
        tipp_coverage.add(["startVolume": tipp_entry.coverage?.@startVolume?.toString(),
                           "startIssue": tipp_entry.coverage?.@startIssue?.toString(),
                           "startDate": tipp_entry.coverage?.@startDate?.toString(),
                           "endVolume":tipp_entry.coverage?.@endVolume?.toString(),
                           "endIssue": tipp_entry.coverage?.@endIssue?.toString(),
                           "endDate": tipp_entry.coverage?.@endDate?.toString()])

        def tipp_coverage_depth = tipp_entry.coverage.@coverageDepth?.toString()
        def tipp_coverage_note = tipp_entry.coverage.@coverageNote?.toString()

        def tipp_url = tipp_entry.url?.text()
        def tipp_platform_url = tipp_entry.platform?.primaryUrl?.text()
        def tipp_platform_name = tipp_entry.platform?.name?.text()

        // log.debug("consider tipp ${tipp_title}");

        result.packageContents.add([
          "title": tipp_title,
          "instanceMedium": tipp_medium,
          "instanceMedia": tipp_media,
          "instanceIdentifiers": tipp_instance_identifiers,
          "siblingInstanceIdentifiers": tipp_sibling_identifiers,
          "coverage": tipp_coverage,
          "embargo": null,
          "coverageDepth": tipp_coverage_depth,
          "coverageNote": tipp_coverage_note,
          "platformUrl": tipp_platform_url,
          "platformName": tipp_platform_name,
          "url": tipp_url
        ])
      }
    }
    else {
      throw new RuntimeException("Problem decoding package record: ${package_record}");
    }

    return result;
  }

  public Map importPackage(Map params,
                            KBCache cache) {
    throw new RuntimeException("Not yet implemented");
    return null;
  }

  public boolean activate(Map params, KBCache cache) {
    throw new RuntimeException("Not supported by this KB provider");
    return false;
  }

  public String makePackageReference(Map params) {
    throw new RuntimeException("Not yet implemented");
    return null;
  }


}
