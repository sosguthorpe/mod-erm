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


public class EbscoKBAdapter implements KBCacheUpdater {


  public void freshenPackageData(String source_name,
                                 String base_url,
                                 String current_cursor,
                                 KBCache cache) {
    throw new RuntimeException("Not supported by this KB provider");
  }

  public void freshenHoldingsData(String cursor,
                                  String source_name,
                                  KBCache cache) {
    throw new RuntimeException("Not yet implemented");
  }

  /**
   * using a native package identifier, request a specific package from the remote source and add it to the KB Cache.
   * If the package already exists, implementors MAY update the existing package with the new information.
   */
  public void importPackage(Map params,
                            KBCache cache) {
    def erm_package = buildErmPackage(params)
    println(erm_package);
  }

  /**
   * Use the EBSCO api and paginate the list of titles into our internal canonical package format, 
   * @param params - A map containing vendorid and packageid
   * @return the canonicalpackage definition.
   */
  private Map buildErmPackage(Map params) {

    println("buildErmPackage(${params})");

    def result = null;

    if ( ( params.vendorid == null  ) ||
         ( params.packageid == null ) ) 
      throw new RuntimeException("buildErmPackage requires vendorid and packageid parameters");

    result = [
      header:[
        availability:[
          type: 'general'
        ],
        packageProvider:[
          name:''
        ],
        packageSource:'EBSCO',
        packageName: '',
        packageSlug: ''
      ],
      packageContents: []
    ]

    // See https://developer.ebsco.com/docs#/
    def ebsco_api = new HTTPBuilder('https://sandbox.ebsco.io');


    // Get package header
    ebsco_api.request(Method.GET) { req ->
      headers.'x-api-key' = params.credentials
      uri.path="/rm/rmaccounts/${params.principal}/vendors/${params.vendorid}/packages/${params.packageid}"
      response.success = { resp, json ->
        println("Package header: ${json}");
        result.header.reference = json.packageId;
        result.header.packageSlug = json.packageId;
        result.header.packageName = json.packageName
        result.header.packageProvider.name = json.vendorName
        result.header.packageProvider.reference = json.vendorId
        // {"isCustom":false,"titleCount":1262,"isSelected":false,"visibilityData":{"isHidden":false,"reason":""},"selectedCount":0,"isTokenNeeded":true,
        // "contentType":"AggregatedFullText","customCoverage":{"beginCoverage":"","endCoverage":""},"proxy":{"id":"<n>","inherited":true},
        // "allowEbscoToAddTitles":false,"packageToken":null,"packageType":"Complete"}
      }
    }


    int pageno = 1;

    def query_params = [
        'search': '',
        'count': '100',
        'searchField': 'titlename',
        'orderBy': 'titlename'
    ]

    def found_records = true;

    // Ebsco API uses pagination in blocks of count, offset by page. offset is PAGE offset, not record number, so count 100, offset 3 = records from 301-400
    // Last page will return 0
    while ( found_records ) {

      println("Fetch page ${pageno} - records ${((pageno-1)*100)+1} to ${pageno*100} -- ${query_params}");
      query_params.offset = pageno;

      ebsco_api.request(Method.GET) { req ->
        // headers.Accept = 'application/json'
        headers.'x-api-key' = params.credentials
        uri.path="/rm/rmaccounts/${params.principal}/vendors/${params.vendorid}/packages/${params.packageid}/titles"
        uri.query=query_params

        response.success = { resp, json ->
          println("OK");
          if ( json.titles.size() == 0 ) {
            found_records = false;
          }
          else {
            println("Process ${json.titles.size()} titles (total=${json.totalResults})");
            println("First title: ${json.titles[0].titleId} ${json.titles[0].titleName}");
            json.titles[0].identifiersList.each { id ->
              println("  -> id ${id.id} (${id.source} ${id.type} ${id.subtype})");
            }

            json.titles.each { title ->

              def instance_identifiers = [];
              title.identifiersList.each { id ->
                switch ( id.type ) {
                  case 0: //ISSN
                    switch( id.subtype ) {
                      case 0:
                        break;
                      case 1: // PRINT
                        instance_identifiers.add([namespace:'ISSN',value:id.id])
                        break;
                      case 2: // ONLINE
                        instance_identifiers.add([namespace:'eISSN',value:id.id])
                        break;
                      case 7: // INVALID
                        break;
                    }
                    break;
                  case 1: //ISBN
                    break;
                  case 6: //ZDBID
                    break;
                  default:
                    break;
                }
              }

              result.packageContents.add([
                "title": title.titleName,
                // "instanceMedium": tipp_medium,
                // "instanceMedia": tipp_media,
                "instanceIdentifiers": instance_identifiers,
                // "siblingInstanceIdentifiers": tipp_sibling_identifiers,
                // "coverage": tipp_coverage,
                // "embargo": null,
                // "coverageDepth": tipp_coverage_depth,
                // "coverageNote": tipp_coverage_note,
                // "platformUrl": tipp_platform_url,
                // "platformName": tipp_platform_name,
                // "url": tipp_url
              ])
            }

            pageno++;
          }
        }

        response.failure = { resp ->
          println "Unexpected error: ${resp.status} : ${resp.statusLine.reasonPhrase}"
          println "Unexpected error 2: ${resp.statusLine}"
          found_records = false;
        }
      }
    }

    return result;
  }

}


