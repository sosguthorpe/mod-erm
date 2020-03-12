package org.olf

import grails.gorm.transactions.Transactional
import grails.web.databinding.DataBinder
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.olf.dataimport.erm.ErmPackageImpl
import org.olf.dataimport.internal.InternalPackageImpl
import org.olf.dataimport.internal.PackageContentImpl
import org.olf.dataimport.internal.HeaderImpl
import org.olf.dataimport.internal.PackageSchema
import org.olf.dataimport.erm.PackageProvider
import org.olf.dataimport.erm.Identifier
import org.slf4j.MDC
import org.springframework.context.MessageSource
import org.springframework.validation.ObjectError
import org.springframework.context.i18n.LocaleContextHolder

import com.opencsv.CSVReader
import org.olf.dataimport.erm.CoverageStatement
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField

@CompileStatic
@Slf4j
class ImportService implements DataBinder {
  
  PackageIngestService packageIngestService
  
  MessageSource messageSource
  
  void importFromFile (final Map envelope) {
    
    final def header = envelope.header
    final def dataSchemaName = header?.getAt('dataSchema')?.getAt('name')
    if (dataSchemaName) {
      
      log.info "dataSchema specified"
      
      // we can use the dataSchema object to lookup the type.
      switch (dataSchemaName) {
        case 'mod-agreements-package':
          log.debug "ERM schema"          
          log.info "${importPackageUsingErmSchema (envelope)} package(s) imported successfully"
          break
          
          // Successfully
        default: 
          log.error "Unknown dataSchema ${dataSchemaName}, ignoring import."
      }
    } else {
      // No dataSchemaName. Examine the rest of the root properties
      if (header && envelope.packageContents) {
        // Looks like it might be the internal schema.
        
        log.debug "Possibly internal schema"
        importPackageUsingInternalSchema (envelope)
      }
    }
  }
  
  Map importPackageUsingErmSchema (final Map envelope) {
    
    log.debug "Called importPackageUsingErmSchema with data ${envelope}"
    int packageCount = 0
    List<String> packageIds = []
    // Erm schema supports multiple packages per document. We should lazily parse 1 by 1.
    envelope.records?.each { Map record ->
      // Ingest 1 package at a time.
      MDC.put('rowNumber', "${packageCount + 1}")
      MDC.put('discriminator', "Package #${packageCount + 1}")
      Map importResult = importPackage (record, ErmPackageImpl)
      
      if (importResult.packageImported) {
        packageCount ++
        String packageId = importResult.packageId
        packageIds << packageId
      }
    }
    
    Map result = [packageCount: packageCount, packageIds: packageIds]
    result
  }
  
  int importPackageUsingInternalSchema (final Map envelope) {
    // The whole envelope is a single package in this format.
    
    MDC.put('rowNumber', "1")
    MDC.put('discriminator', "Package #1")
    Map result = importPackage (envelope, InternalPackageImpl)
    result.packageImported ? 1 : 0
  }
  
  private Map importPackage (final Map record, final Class<? extends PackageSchema> schemaClass) {
    boolean packageImported = true
    String packageId = ""

    final PackageSchema pkg = schemaClass.newInstance()
    bindData(pkg, record)
    // Check for binding errors.
    if (!pkg.errors.hasErrors()) {
      // Validate the actual values now. And check for constraint violations
      pkg.validate()
      if (!pkg.errors.hasErrors()) {
        // Ingest the package.
        Map result = packageIngestService.upsertPackage(pkg)
        String upsertPackagePackageId = result.packageId
        
        packageImported = true
        packageId = upsertPackagePackageId
      } else {
        // Log the errors.
        pkg.errors.allErrors.each { ObjectError error ->
          log.error "${ messageSource.getMessage(error, LocaleContextHolder.locale) }"
        }
      }
    } else {
      // Log the errors.
      pkg.errors.allErrors.each { ObjectError error ->
        log.error "${ messageSource.getMessage(error, LocaleContextHolder.locale) }"
      }
    }

    Map results = [packageImported: packageImported, packageId: packageId]
    results
  }

  boolean importPackageFromKbart (CSVReader file, Map packageInfo) {
    boolean packageImported = false
    log.debug("Attempting to import package from KBART file")

    String packageName
    String packageSource
    String packageReference
    String packageProvider

    if ( packageInfo.packageName == null ||
         packageInfo.packageSource == null ||
         packageInfo.packageReference == null ) {
        log.error("Import is missing key package information")
        return packageImported
      } else {
        packageName = packageInfo.packageName
        packageSource = packageInfo.packageSource
        packageReference = packageInfo.packageReference
      }

    if (packageInfo.packageProvider != null) {
      packageProvider = packageInfo.packageProvider
    }

    // peek gets line without removing from iterator
    // readNext gets line and removes it from the csvReader object
    String[] header = file.readNext()

    // Create an object containing fields we can accept and their mappings in our domain structure, as well as indices in the imported file, with -1 if not found
    Map acceptedFields = [
      publication_title: [field: 'title', index: -1],
      print_identifier: [field: 'siblingInstanceIdentifiers', index: -1],
      online_identifier: [field: 'instanceIdentifiers', index: -1],
      date_first_issue_online: [field: 'CoverageStatement.startDate', index: -1],
      num_first_vol_online: [field: 'CoverageStatement.startVolume', index: -1],
      num_first_issue_online: [field: 'CoverageStatement.startIssue', index: -1],
      date_last_issue_online: [field: 'CoverageStatement.endDate', index: -1],
      num_last_vol_online: [field: 'CoverageStatement.endVolume', index: -1],
      num_last_issue_online: [field: 'CoverageStatement.endIssue', index: -1],
      title_url: [field: 'url', index: -1],
      first_author: [field: 'firstAuthor', index: -1],
      title_id: [field: null, index: -1],
      embargo_info: [field: 'embargo', index: -1],
      coverage_depth: [field: 'coverageDepth', index: -1],
      notes: [field: 'coverageNote', index: -1],
      publisher_name: [field: null, index: -1],
      publication_type: [field: 'instanceMedia', index: -1],
      date_monograph_published_print: [field: 'dateMonographPublishedPrint', index: -1],
      date_monograph_published_online: [field: 'dateMonographPublished', index: -1],
      monograph_volume: [field: 'monographVolume', index: -1],
      monograph_edition: [field: 'monographEdition', index: -1],
      first_editor: [field: 'firstEditor', index: -1],
      parent_publication_title_id: [field: null, index: -1],
      preceding_publication_title_id: [field: null, index: -1],
      access_type : [field: null, index: -1]
    ]

    // Map each key to its location in the header
    for (int i=0; i<header.length; i++) {
      final String key = header[i]
      if (acceptedFields.containsKey(key)) {
        acceptedFields[key]['index'] = i
      }
    }

    // At this point we have a mapping of internal fields to KBART fields and their indexes in the imported file
    // Mandatory fields' existence should be checked
    List mandatoryFields = ['title', 'instanceIdentifiers', 'url', 'instanceMedia']

    def missingFields = mandatoryFields.findAll {field ->
      !shouldExist(acceptedFields, field)[0]
    }.collect { f ->
      shouldExist(acceptedFields, f)[1]
    }
    if (missingFields.size() != 0) {
      log.error("The import file is missing the mandatory fields: ${missingFields}")
      return (false);
    }
    
    final InternalPackageImpl pkg = new InternalPackageImpl()
    final PackageProvider pkgPrv = new PackageProvider()
    pkgPrv.name = packageProvider

    pkg.header = new HeaderImpl(
      packageName: packageName,
      packageSource: packageSource,
      packageSlug: packageReference,
      packageProvider: pkgPrv
    )

    String[] record;
    while ((record = file.readNext()) != null) {
      def lineAsArray = record

      Identifier siblingInstanceIdentifier = new Identifier()
      Identifier instanceIdentifier = new Identifier()
      List kbartCoverageList

      // Instance/Sibling instance identifiers AND coverage rely on the media type, monograph vs serial 
      String instanceMedia = getFieldFromLine(lineAsArray, acceptedFields, 'instanceMedia')
      if (
        instanceMedia.toLowerCase() == 'monograph' ||
        instanceMedia.toLowerCase() == 'book'
      ) {
          siblingInstanceIdentifier.namespace = 'ISBN'
          instanceIdentifier.namespace = 'ISBN'
          if (getFieldFromLine(lineAsArray, acceptedFields, 'CoverageStatement.startDate') != '') {
            log.error("Unexpected coverage information for for title: ${getFieldFromLine(lineAsArray, acceptedFields, 'title')} of type: ${instanceMedia}")
          }
          kbartCoverageList = []

      } else {          
          siblingInstanceIdentifier.namespace = 'ISSN'
          instanceIdentifier.namespace = 'ISSN'
          kbartCoverageList = buildKBARTCoverage(lineAsArray, acceptedFields)
      }

      siblingInstanceIdentifier.value = getFieldFromLine(lineAsArray, acceptedFields, 'siblingInstanceIdentifiers')
      instanceIdentifier.value = getFieldFromLine(lineAsArray, acceptedFields, 'instanceIdentifiers')

      // Check that these aren't invalid identifiers, if they are, return an empty list
      List instanceIdentifiers = identifierValidator(instanceIdentifier)
      List siblingInstanceIdentifiers = identifierValidator(siblingInstanceIdentifier)
      
      PackageContentImpl pkgLine = new PackageContentImpl(
        title: getFieldFromLine(lineAsArray, acceptedFields, 'title'),
        siblingInstanceIdentifiers: siblingInstanceIdentifiers,
        instanceIdentifiers: instanceIdentifiers,
        coverage: kbartCoverageList,
        url: getFieldFromLine(lineAsArray, acceptedFields, 'url'),
        firstAuthor: getFieldFromLine(lineAsArray, acceptedFields, 'firstAuthor'),
        embargo: getFieldFromLine(lineAsArray, acceptedFields, 'embargo'),
        coverageDepth: getFieldFromLine(lineAsArray, acceptedFields, 'coverageDepth'),
        coverageNote: getFieldFromLine(lineAsArray, acceptedFields, 'coverageNote'),
        instanceMedia: getFieldFromLine(lineAsArray, acceptedFields, 'instanceMedia'),
        instanceMedium: "electronic",

        dateMonographPublished: getFieldFromLine(lineAsArray, acceptedFields, 'dateMonographPublished'),
        dateMonographPublishedPrint: getFieldFromLine(lineAsArray, acceptedFields, 'dateMonographPublishedPrint'),

        monographVolume: getFieldFromLine(lineAsArray, acceptedFields, 'monographVolume'),
        monographEdition: getFieldFromLine(lineAsArray, acceptedFields, 'monographEdition'),
        firstEditor: getFieldFromLine(lineAsArray, acceptedFields, 'firstEditor')
      )

      pkg.packageContents << pkgLine
    }

    if (pkg.packageContents.size() > 0) {
      def result = packageIngestService.upsertPackage(pkg)
      //TODO Use this information to return true if the package imported successfully or false otherwise
      packageImported = true
    } else {
      log.error("Package contents empty, skipping package creation")
    }
    
    return (packageImported)
  }

  private String getFieldFromLine(String[] lineAsArray, Map acceptedFields, String fieldName) {
    //ToDo potentially work out how to make this slightly less icky, it worked a lot nicer without @CompileStatic
    String index = getIndexFromFieldName(acceptedFields, fieldName)
    // Remember to discount any instances where the default '-1' still exists, don't want to grab the last index by mistake,
    // We also don't want to return an empty string or whitespace, we'd rather null
    if (index.toInteger() == -1 || lineAsArray[index.toInteger()].trim() == '') {
      return null;
    }
  return lineAsArray[index.toInteger()];
  }

  private String getIndexFromFieldName(Map acceptedFields, String fieldName) {
    String index = (acceptedFields.values().find { it['field']?.equals(fieldName) })['index']
    return index;
  }

  private List shouldExist(Map acceptedFields, String fieldName) {
    boolean result = false
    String importField = acceptedFields.find { it.value['field']?.equals(fieldName) }?.key

    if (getIndexFromFieldName(acceptedFields, fieldName) != '-1') {
      result = true
    }
    return [result, importField];
  }

  private LocalDate parseDate(String date) {
    // We know that data coming in here matches yyyy, yyyy-mm or yyyy-mm-dd
    if (!date?.trim()) {
      return null;
    }

    LocalDate outputDate

    DateTimeFormatter yearFormat = new DateTimeFormatterBuilder()
    .appendPattern("yyyy")
    .parseDefaulting(ChronoField.MONTH_OF_YEAR, 1)
    .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
    .toFormatter();

    DateTimeFormatter monthYearFormat = new DateTimeFormatterBuilder()
    .appendPattern("yyyy-MM")
    .parseDefaulting(ChronoField.DAY_OF_MONTH, 1)
    .toFormatter();

    switch(date) {
      case ~ '^\\d{4}\$':
        outputDate = LocalDate.parse(date, yearFormat);
        break;
      case ~ '^\\d{4}(-(\\d{2}))\$':
        outputDate = LocalDate.parse(date, monthYearFormat);
        break;
      default:
        outputDate = LocalDate.parse(date);
        break;
    }
    return outputDate;
  }

  private List buildKBARTCoverage(String[] lineAsArray, Map acceptedFields) {
    String startDate = getFieldFromLine(lineAsArray, acceptedFields, 'CoverageStatement.startDate')
    String endDate = getFieldFromLine(lineAsArray, acceptedFields, 'CoverageStatement.endDate')
    
    LocalDate startDateLocalDate = parseDate(startDate)
    LocalDate endDateLocalDate = parseDate(endDate)
    
    String instanceMedia = getFieldFromLine(lineAsArray, acceptedFields, 'instanceMedia').toLowerCase()

    CoverageStatement cs = new CoverageStatement(
      startDate: startDateLocalDate,
      startVolume: getFieldFromLine(lineAsArray, acceptedFields, 'CoverageStatement.startVolume'),
      startIssue: getFieldFromLine(lineAsArray, acceptedFields, 'CoverageStatement.startIssue'),
      endDate: endDateLocalDate,
      endVolume: getFieldFromLine(lineAsArray, acceptedFields, 'CoverageStatement.endVolume'),
      endIssue: getFieldFromLine(lineAsArray, acceptedFields, 'CoverageStatement.endIssue')
    )

    cs.validate()
    
    if (!cs.hasErrors()) {
      return ([cs]);
    } else {
      cs.errors.allErrors.each { ObjectError error ->
        log.error "${ messageSource.getMessage(error, LocaleContextHolder.locale) }"
      }
      return [];
    }
  }

  private List identifierValidator(Identifier identifier) {
    identifier.validate();

    List identifiers
    if (!identifier.hasErrors()) {
      identifiers = [identifier]
    } else {
      identifier.errors.allErrors.each { ObjectError error ->
        log.error "${ messageSource.getMessage(error, LocaleContextHolder.locale) }"
      }
      identifiers = []
    }
    return identifiers;
  }
}