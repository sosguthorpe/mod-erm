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
    throw new RuntimeException("Not yet implemented");
  }

  private void internalImportPackage(Map params,
                                     KBCache cache) {
    // curl -X GET --header "Accept: application/json" --header "x-api-key: xxx" "https://sandbox.ebsco.io/rm/rmaccounts/--principal--/vendors/18/packages/2481/titles?search=&count=10&offset=1&searchField=titlename&orderBy=titlename"
    // "Accept: application/json" --header "x-api-key: TVajS16nTi4NYRDM0o3686oMjtP4agtv71jaQ5zt" "https://sandbox.ebsco.io/rm/rmaccounts/apidvacdmc/vendors/18/packages/2481/titles?search=&count=10&offset=1&searchField=titlename&orderBy=titlename"

    // See https://developer.ebsco.com/docs#/
    def ebsco_api = new HTTPBuilder("https://sandbox.ebsco.io")

    def query_params = [
        'search': '',
        'count': '100',
        'offset': '1',
        'searchField': '',
        'orderBy': 'titlename',
    ]

    def found_records = true;

    while ( found_records ) {

      ebsco_api.request(Method.GET) { req ->
        headers.Accept = 'application/json'
        uri.query=query_params
        uri.path="/rm/rmaccounts/${params.custid}/vendors/${params.vendorid}/packages/${params.packageid}/titles"

        response.success = { resp, xml ->
        }
      }
    }

  }

}


