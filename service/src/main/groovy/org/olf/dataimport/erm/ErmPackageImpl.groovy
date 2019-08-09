package org.olf.dataimport.erm

import java.time.LocalDate

import org.olf.dataimport.internal.PackageSchema
import org.olf.dataimport.internal.PackageSchema.PackageHeaderSchema

import grails.compiler.GrailsCompileStatic
import grails.validation.Validateable

@GrailsCompileStatic
class ErmPackageImpl implements PackageHeaderSchema, PackageSchema, Validateable {
  
  String source
  String reference
  String name
  PackageProvider packageProvider
  Set<ContentItem> contentItems = []
  
  static hasMany = [
    contentItems: ContentItem
  ]
  
  static constraints = {
    source    nullable: false, blank: false
    reference nullable: false, blank: false
    name      nullable: false, blank: false
    packageProvider nullable: true, blank: false
    contentItems minSize: 1
  }

  @Override
  public PackageHeaderSchema getHeader() {
    // This object also implements the header.
    this
  }
  
  @Override
  public String getPackageSource() {
    source
  }
  
  @Override
  public String getPackageName() {
    name
  }
  
  @Override
  public LocalDate getStartDate() {
    // Null for this implementation
    null
  }
  @Override
  public LocalDate getEndDate() {
    // Null for this implementation
    null
  }
  
  @Override
  public String getPackageSlug() {
    reference
  }
  
  @Override
  public String get_intenalId() {
    // Null for this implementation
    return null;
  }
  
  @Override
  public Collection<ContentItem> getPackageContents() {
    contentItems
  }

  @Override
  public String getStatus() {
    // TODO Auto-generated method stub
    return null;
  }
  
}
