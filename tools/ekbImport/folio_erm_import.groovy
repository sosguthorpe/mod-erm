#!/usr/bin/env groovy

@Grapes([
  @GrabResolver(name='mvnRepository', root='http://central.maven.org/maven2/'),
  @GrabResolver(name='kint', root='http://nexus.k-int.com/content/repositories/releases'),
  @Grab(group='org.slf4j', module='slf4j-api', version='1.7.25'),
  @Grab(group='net.sf.opencsv', module='opencsv', version='2.3'),
  @Grab(group='org.apache.httpcomponents', module='httpmime', version='4.1.2'),
  @Grab(group='org.apache.httpcomponents', module='httpclient', version='4.5.3'),
  @Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7.1'),
  @Grab(group='org.apache.httpcomponents', module='httpmime', version='4.1.2'),
  @Grab(group='org.slf4j', module='slf4j-api', version='1.7.6'),
  @Grab(group='org.slf4j', module='jcl-over-slf4j', version='1.7.6'),
  @Grab(group='net.sourceforge.nekohtml', module='nekohtml', version='1.9.22'),
  @Grab(group='xerces', module='xercesImpl', version='2.11.0')
])

import groovyx.net.http.*
import static groovyx.net.http.ContentType.URLENC
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
import groovyx.net.http.*
import org.apache.http.entity.mime.*
import org.apache.http.entity.mime.content.*
import org.apache.http.*
import org.apache.http.protocol.*
import java.nio.charset.Charset
import static groovy.json.JsonOutput.*
import groovy.util.slurpersupport.GPathResult
import org.apache.log4j.*
import java.text.SimpleDateFormat
import au.com.bytecode.opencsv.CSVReader
import au.com.bytecode.opencsv.CSVWriter
import groovyx.net.http.*
import static groovyx.net.http.ContentType.XML
import static groovyx.net.http.ContentType.JSON
import static groovyx.net.http.Method.GET
import static groovyx.net.http.Method.POST
import java.text.*

TARGET_HOST='http://localhost:9130'
TARGET_CONTEXT='/erm/'

def cli = new CliBuilder(usage: 'folio_erm_import.groovy -h -f agreement_content.tsv')
// Create the list of options.
cli.with {
        h longOpt: 'help', 'Show usage information'
        f longOpt: 'file', args: 1, argName: 'agreement_content.tsv', 'Tab separated file containing new agreement information', required:true
        // f longOpt: 'format-full',   'Use DateFormat#FULL format'
        // l longOpt: 'format-long',   'Use DateFormat#LONG format'
        // m longOpt: 'format-medium', 'Use DateFormat#MEDIUM format (default)'
        // s longOpt: 'format-short',  'Use DateFormat#SHORT format'
}

def options = cli.parse(args)
if (!options) {
  println("No options");
  return
}
else {
  println(options)
}

// Show usage text when -h or --help option is used.
if (options.h) {
  cli.usage()
  return
}

File log_file = new File(options.f+'.log');
File err_file = new File(options.f+'.err');
File bad_file = new File(options.f+'.bad');

// Clear up old log, err and bad files
[log_file, err_file, bad_file].each { f ->
  if ( f.exists() ) {
    f.delete()
  }
}

println("Starting... ${args}");

if ( options.f ) {
  try {
    CSVReader r = new CSVReader( new InputStreamReader( new FileInputStream(options.f), java.nio.charset.Charset.forName('UTF-8') ), '\t' as char)
    // Load header line
    List header = r.readNext();
    // Map columns in the header line - we need a minimum of title and ISSN to run this process
    default_column_mappings.each { k,v ->
      v.columns.each { col_name ->
        println("Attempting to match ${k} ${col_name} -> ${header.indexOf(col_name)}");
        v.colNo = header.indexOf(col_name)
      }
    }

    println("After mapping columns  : ${default_column_mappings}");
    header.each {
      println("Column \"${it}\"");
    }

    List nl

    def rownum = 1 // Start at first line after header
    while ((nl = r.readNext()) != null ) {
    }
  }
  catch ( Exception e ) {
    e.printStackTrace()
  }

  println("Done");
}
else {
  System.err.println("Usage: title_list_validation.groovy file.tsv");
}
