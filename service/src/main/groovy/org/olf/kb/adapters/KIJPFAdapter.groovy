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


  public Object freshen(String source_id,
                        String base_url,
                        String current_cursor,
                        KBCache cache) {

    println("KIJPFAdapter::freshen - fetching from URI: ${base_url}");
    def jpf_api = new HTTPBuilder(base_url)

    def query_params = [
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
 
    jpf_api.request(Method.GET) { req ->
      // uri.path=''
      // requestContentType = ContentType.JSON
      headers.Accept = 'application/json'
      uri.query=query_params
      response.success = { resp, json ->
        // println "Success! ${resp.status} ${json}"
        cursor = processPage(cursor, json)
        cache.updateCursor(source_id,cursor);
      }

      response.failure = { resp ->
        println "Request failed with status ${resp.status}"
      }
    }

  }


  private String processPage(cursor, package_list) {
    def new_cursor = cursor;
    package_list.packages.each { pkg ->
      System.out.println(pkg.name);
      System.out.println(pkg.identifier);
      System.out.println(pkg.lastUpdated);
      System.out.println(pkg.packageContentAsJson);
      if ( pkg.lastUpdated > new_cursor ) {
        System.out.println("New cursor value");
        new_cursor = pkg.lastUpdated;
      }
    }
    return new_cursor;
  }

}
