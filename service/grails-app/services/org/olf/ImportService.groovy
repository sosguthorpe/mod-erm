package org.olf

import grails.gorm.transactions.Transactional
import grails.web.databinding.DataBinder
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.olf.dataimport.erm.ErmPackageImpl
import org.olf.dataimport.internal.InternalPackageImpl
import org.olf.dataimport.internal.PackageSchema
import org.slf4j.MDC
import org.springframework.context.MessageSource
import org.springframework.validation.ObjectError
import org.springframework.context.i18n.LocaleContextHolder

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
  
  int importPackageUsingErmSchema (final Map envelope) {
    
    log.debug "Called importPackageUsingErmSchema with data ${envelope}"
    int packageCount = 0
    // Erm schema supports multiple packages per document. We should lazily parse 1 by 1.
    envelope.records?.each { Map record ->
      // Ingest 1 package at a time.
      
      MDC.put('rowNumber', "${packageCount + 1}")
      MDC.put('discriminator', "Package #${packageCount + 1}")
      if (importPackage (record, ErmPackageImpl)) {
        packageCount ++
      }
    }
    
    packageCount
  }
  
  int importPackageUsingInternalSchema (final Map envelope) {
    // The whole envelope is a single package in this format.
    
    MDC.put('rowNumber', "1")
    MDC.put('discriminator', "Package #1")
    importPackage (envelope, InternalPackageImpl) ? 1 : 0
  }
  
  private boolean importPackage (final Map record, final Class<? extends PackageSchema> schemaClass) {
    boolean packageImported = false
    final PackageSchema pkg = schemaClass.newInstance()
    bindData(pkg, record)
    // Check for binding errors.
    if (!pkg.errors.hasErrors()) {
      // Validate the actual values now. And check for constraint violations
      pkg.validate()
      if (!pkg.errors.hasErrors()) {
        // Ingest the package.
        packageIngestService.upsertPackage(pkg)
        
        packageImported = true
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
    
    packageImported
  }
}
