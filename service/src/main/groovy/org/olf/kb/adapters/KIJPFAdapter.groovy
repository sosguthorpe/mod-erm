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


public class KIJPFAdapter implements KBCacheUpdater {


  public Object freshen(String source_name,
                        String base_url,
                        String current_cursor,
                        KBCache cache) {

    println("KIJPFAdapter::freshen - fetching from URI: ${base_url}");
    def jpf_api = new HTTPBuilder(base_url)

    def query_params = [
        'max': '25',
        'format': 'json',
        'order':'lastUpdated'
    ]

    def cursor = null;

    if ( current_cursor  != null ) {
      cursor = current_cursor
      query_params.startDate=cursor;
    }
    else {
      cursor = '';
    }

    boolean cont = true;
    int spin_protection = 0;

    while ( cont ) {

      spin_protection++;
 
      jpf_api.request(Method.GET) { req ->
        // uri.path=''
        // requestContentType = ContentType.JSON
        headers.Accept = 'application/json'
        uri.query=query_params
        response.success = { resp, json ->
          // println "Success! ${resp.status} ${json}"
          Map page_result = processPage(cursor, json, source_name, cache)
          println("processPage returned, processed ${page_result.count} packages");
          cache.updateCursor(source_name,page_result.new_cursor);

          if ( ( page_result.count == 0 ) || ( spin_protection > 50 ) ) {
            cont = false;
          }
          else {
            log.debug("Fetch next page of data - ${page_result.new_cursor}");
            query_params.startDate=page_result.new_cursor;
          }

        }

        response.failure = { resp ->
          println "Request failed with status ${resp.status}"
        }
      }
    }

  }


  private Map processPage(String cursor, Object package_list, String source_name, KBCache cache) {
    def result = [:]
    result.new_cursor = cursor;
    result.count = 0;
    package_list.packages.each { pkg ->
      result.count++;
      System.out.println(result.count)
      System.out.println(pkg.name);
      System.out.println(pkg.packageContentAsJson);

      processPackage(pkg.packageContentAsJson, source_name, cache);

      if ( pkg.lastUpdated > result.new_cursor ) {
        System.out.println("New cursor value - ${pkg.lastUpdated} > ${result.new_cursor} ");
        result.new_cursor = pkg.lastUpdated;
      }
    }
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

}
