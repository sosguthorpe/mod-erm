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
  @Grab(group='org.slf4j', module='jcl-over-slf4j', version='1.7.6')
])

import groovyx.net.http.*
import static groovyx.net.http.ContentType.URLENC
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*
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

def  default_column_mappings = [
  'vendorID':                   [columns:['vendorID'],                   colNo:null],
  'packageID':                  [columns:['packageID'],                  colNo:null],
  'KBID':                       [columns:['KBID'],                       colNo:null],
  'Title':                      [columns:['Title'],                      colNo:null],
  'AlternateTitle':             [columns:['AlternateTitle'],             colNo:null],
  'PackageName':                [columns:['PackageName'],                colNo:null],
  'URL':                        [columns:['URL'],                        colNo:null],
  'ProxiedURL':                 [columns:['ProxiedURL'],                 colNo:null],
  'Publisher':                  [columns:['Publisher'],                  colNo:null],
  'Edition':                    [columns:['Edition'],                    colNo:null],
  'Author':                     [columns:['Author'],                     colNo:null],
  'Editor':                     [columns:['Editor'],                     colNo:null],
  'Illustrator':                [columns:['Illustrator'],                colNo:null],
  'PrintISSN':                  [columns:['PrintISSN'],                  colNo:null],
  'OnlineISSN':                 [columns:['OnlineISSN'],                 colNo:null],
  'PrintISBN':                  [columns:['PrintISBN'],                  colNo:null],
  'OnlineISBN':                 [columns:['OnlineISBN'],                 colNo:null],
  'DOI':                        [columns:['DOI'],                        colNo:null],
  'PeerReviewed':               [columns:['PeerReviewed'],               colNo:null],
  'ManagedCoverageBegin':       [columns:['ManagedCoverageBegin'],       colNo:null],
  'ManagedCoverageEnd':         [columns:['ManagedCoverageEnd'],         colNo:null],
  'CustomCoverageBegin':        [columns:['CustomCoverageBegin'],        colNo:null],
  'CustomCoverageEnd':          [columns:['CustomCoverageEnd'],          colNo:null],
  'CoverageStatement':          [columns:['CoverageStatement'],          colNo:null],
  'Embargo':                    [columns:['Embargo'],                    colNo:null],
  'CustomEmbargo':              [columns:['CustomEmbargo'],              colNo:null],
  'Description':                [columns:['Description'],                colNo:null],
  'Subject':                    [columns:['Subject'],                    colNo:null],
  'ResourceType':               [columns:['ResourceType'],               colNo:null],
  'PackageContentType':         [columns:['PackageContentType'],         colNo:null],
  'CreateCustom':               [columns:['CreateCustom'],               colNo:null],
  'HideOnPublicationFinder':    [columns:['HideOnPublicationFinder'],    colNo:null],
  'Delete':                     [columns:['Delete'],                     colNo:null],
  'OrderedThroughEBSCO':        [columns:['OrderedThroughEBSCO'],        colNo:null],
  'IsCustom':                   [columns:['IsCustom'],                   colNo:null],
  'UserDefinedField1':          [columns:['UserDefinedField1'],          colNo:null],
  'UserDefinedField2':          [columns:['UserDefinedField2'],          colNo:null],
  'UserDefinedField3':          [columns:['UserDefinedField3'],          colNo:null],
  'UserDefinedField4':          [columns:['UserDefinedField4'],          colNo:null],
  'UserDefinedField5':          [columns:['UserDefinedField5'],          colNo:null],
  'PackageType':                [columns:['PackageType'],                colNo:null],
  'AllowEbscoToAddNewTitles':   [columns:['AllowEbscoToAddNewTitles'],   colNo:null],

]

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
