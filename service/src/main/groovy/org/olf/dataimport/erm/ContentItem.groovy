package org.olf.dataimport.erm

import java.time.LocalDate

import org.olf.dataimport.internal.PackageSchema.ContentItemSchema
import org.olf.dataimport.internal.PackageSchema.IdentifierSchema
import org.olf.kb.AbstractCoverageStatement

import grails.compiler.GrailsCompileStatic
import grails.validation.Validateable
import groovy.transform.ToString

@ToString
@GrailsCompileStatic
class ContentItem implements ContentItemSchema, Validateable {
  
  String note
  String depth
  LocalDate accessStart
  LocalDate accessEnd
  
  Set<CoverageStatement> coverage
  
  PlatformTitileInstance platformTitleInstance
  
  static hasMany = [
    coverage: CoverageStatement
  ]
  
  static constraints = {
    note          nullable: true, blank: false
    depth         nullable: true, blank: false
    accessStart   nullable: true
    accessEnd     nullable: true
    
    coverage (validator: AbstractCoverageStatement.STATEMENT_COLLECTION_VALIDATOR, sort:'startDate')
    platformTitleInstance nullable: false
  }

  @Override
  public String getTitle() {
    platformTitleInstance?.titleInstance?.name
  }
  
  @Override
  public String getInstanceMedium() {
    platformTitleInstance?.titleInstance?.subType?.value
  }
  
  @Override
  public String getInstanceMedia() {
    platformTitleInstance?.titleInstance?.type?.value
  }
  
  @Override
  public Collection<IdentifierSchema> getInstanceIdentifiers() {
    platformTitleInstance?.titleInstance?.identifiers as Collection<IdentifierSchema>
  }
  
  @Override
  public Collection<IdentifierSchema> getSiblingInstanceIdentifiers() {
    // Returns empty set for this implementation.
    []
  }
  
  @Override
  public String getEmbargo() {
    // Null for this implementation
    null
  }
  
  @Override
  public String getCoverageDepth() {
    depth
  }
  
  @Override
  public String getCoverageNote() {
    note
  }
  
  @Override
  public String getPlatformUrl() {
    platformTitleInstance?.platformUrl
  }
  
  @Override
  public String getPlatformName() {
    platformTitleInstance?.platform
  }
  
  @Override
  public String get_platformId() {
    // Null for this implementation
    null
  }

  @Override
  public String getUrl() {
    platformTitleInstance.url
  }
}
