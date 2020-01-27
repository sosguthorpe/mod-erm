package org.olf.dataimport.erm

import java.time.LocalDate
import javax.validation.constraints.NotNull
import org.olf.dataimport.internal.PackageSchema.ContentItemSchema
import org.olf.dataimport.internal.PackageSchema.IdentifierSchema
import org.olf.kb.AbstractCoverageStatement

import grails.compiler.GrailsCompileStatic
import grails.validation.Validateable
import groovy.transform.Memoized
import groovy.transform.ToString

@ToString(includePackage=false)
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
    accessStart nullable:true, validator: { LocalDate startDate, ContentItemSchema item ->
      if (!startDate && item.accessEnd) {
        return ['null.message']
      }
    }
    
    accessEnd nullable:true, validator: { LocalDate endDate, ContentItemSchema item ->
      
      if (item.accessStart &&
        endDate &&
        ( item.accessStart > endDate) ) {
          return [ 'start.after.end', 'accessStart', item.class.name, item.accessStart, endDate]
      }
    }
    
    coverage (validator: AbstractCoverageStatement.STATEMENT_COLLECTION_VALIDATOR, sort:'startDate')
    platformTitleInstance nullable: false
  }

  @Override
  public String getTitle() {
    platformTitleInstance?.titleInstance?.name
  }
  
  @Override
  public String getInstanceMedium() {
    platformTitleInstance?.titleInstance?.subType
  }
  
  @Override
  public String getInstanceMedia() {
    platformTitleInstance?.titleInstance?.type
  }
  
  private static final Map<String,List<String>> known_id_types = [
    electronic : ['EISSN', 'DOI', 'EZB'],
    print : ['ISSN']
  ]
  
  @Memoized
  private final Collection<String> siblingNamespacesForSubType (@NotNull final String subType) {
    final Set<String> all = []
    known_id_types.each { final String type, final List<String> namespaces ->
      if (type.trim().toLowerCase() != subType.trim().toLowerCase()) {
        all.addAll namespaces
      }
    }
    
    all
  }  
  
  @Override
  public Collection<IdentifierSchema> getInstanceIdentifiers() {
    Collection<IdentifierSchema> ids = platformTitleInstance?.titleInstance?.identifiers as Collection<IdentifierSchema>
    
    def siblings = instanceMedium ? siblingNamespacesForSubType(instanceMedium) : []
    
    ids && siblings ? ids.findAll { IdentifierSchema idSch -> !siblings.contains(idSch.namespace.trim().toUpperCase()) } as Collection<IdentifierSchema> : ids
  }
  
  @Override
  public Collection<IdentifierSchema> getSiblingInstanceIdentifiers() {
    Collection<IdentifierSchema> ids = platformTitleInstance?.titleInstance?.identifiers as Collection<IdentifierSchema>
    
    def siblings = instanceMedium ? siblingNamespacesForSubType(instanceMedium) : []
    
    ids && siblings ? ids.findAll { IdentifierSchema idSch -> siblings.contains(idSch.namespace.trim().toUpperCase()) } as Collection<IdentifierSchema> : ids
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
  public LocalDate getAccessStart() {
    accessStart
  }

  @Override
  public LocalDate getAccessEnd() {
    accessEnd
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

  @Override
  public String getDateMonographPublished() {
    platformTitleInstance?.titleInstance?.dateMonographPublished
  }

  @Override
  public String getFirstAuthor() {
    platformTitleInstance?.titleInstance?.firstAuthor
  }

  @Override
  public String getFirstEditor() {
    platformTitleInstance?.titleInstance?.firstEditor
  }

  @Override
  public String getMonographEdition() {
    platformTitleInstance?.titleInstance?.monographEdition
  }

  @Override
  public String getMonographVolume() {
    platformTitleInstance?.titleInstance?.monographVolume
  }
}
