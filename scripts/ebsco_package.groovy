#!/usr/bin/env groovy

// Run this script with 
//     groovy -Dorg.slf4j.simpleLogger.defaultLogLevel=debug ./ebsco_package.groovy  
// to see logging output

@GrabResolver(name='mvnRepository', root='http://central.maven.org/maven2/')
@GrabResolver(name='kint', root='http://nexus.k-int.com/content/repositories/releases')
@Grapes([
  @Grab(group='net.sf.opencsv', module='opencsv', version='2.3'),
  @Grab(group='org.apache.httpcomponents', module='httpmime', version='4.1.2'),
  @Grab(group='org.apache.httpcomponents', module='httpclient', version='4.5.3'),
  @Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7.1'),
  @Grab(group='org.apache.httpcomponents', module='httpmime', version='4.1.2'),
  @Grab(group='org.slf4j', module='slf4j-api', version='1.7.6'),
  @Grab(group='org.slf4j', module='jcl-over-slf4j', version='1.7.6'),
  @Grab(group='org.slf4j', module='slf4j-simple', version='1.7.6'),
  @Grab(group='xerces', module='xercesImpl', version='2.9.1')
])


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
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.text.*


// This script is a handy way to exercise some of the functions of the EBSCO adapter in a stand alone
// utility class

void internalImportPackage(Map params) {

    String base_url = "https://sandbox.ebsco.io/rm/rmaccounts/${params.custid}/vendors/${params.vendorid}/packages/${params.packageid}/titles".toString()

    println("\n\nRequest from ${base_url}");

    // See https://developer.ebsco.com/docs#/
    def ebsco_api = new HTTPBuilder('https://sandbox.ebsco.io');

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
        headers.'x-api-key' = params.apikey
        uri.path="/rm/rmaccounts/${params.custid}/vendors/${params.vendorid}/packages/${params.packageid}/titles"
        uri.query=query_params

        response.success = { resp, json ->
          println("OK");
          if ( json.titles.size() == 0 ) {
            found_records = false;
          }
          else {
            println("Process ${json.titles.size()} titles (total=${json.totalResults})");
            println("First title: ${json.titles[0].titleId} ${json.titles[0].titleName}");
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
}

// logger = LoggerFactory.getLogger("ebsco_package") // or whatever
// logger.level = Level.DEBUG // or whatever

def cli = new CliBuilder(usage: 'ebsco_package.groovy -h -c custid -k apikey')
// Create the list of options.
cli.with {
        h longOpt: 'help', 'Show usage information'
        c longOpt: 'custid', args: 1, argName: 'custid', 'Customer Id', required:true
        k longOpt: 'apikey', args: 1, argName: 'apikey', 'Api Key', required:true
}

def options = cli.parse(args)
if (!options) {
  println("No options");
  return
}
else {
  println(options.custid)
  println(options.apikey)
}

// Search for some packages::
// curl -X GET --header "Accept: application/json" --header "x-api-key: xxx" "https://sandbox.ebsco.io/rm/rmaccounts/xxx/packages?search=bentham&count=100&offset=1&orderby=PackageName"
// internalImportPackage([custid:options.custid,vendorid:'18',packageid:'2481',apikey:options.apikey]);
// Bentham Science Complete
internalImportPackage([custid:options.custid,vendorid:'301',packageid:'3707',apikey:options.apikey]);

