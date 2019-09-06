package org.olf.dataimport.internal

import java.time.LocalDate

import org.olf.dataimport.erm.CoverageStatement
import org.olf.dataimport.erm.Identifier
import org.olf.dataimport.erm.PackageProvider
import grails.compiler.GrailsCompileStatic
import grails.validation.Validateable
import groovy.transform.ToString

@GrailsCompileStatic
class InternalPackageImpl implements PackageSchema, Validateable {
  HeaderImpl header
  List<PackageContentImpl> packageContents = []
  
  @ToString(includePackage=false)
  @GrailsCompileStatic
  class HeaderImpl implements PackageHeaderSchema, Validateable {
    PackageProvider packageProvider
    String packageSource
    String status
    String packageName
    LocalDate startDate
    LocalDate endDate
    String packageSlug
    String _intenalId
    
    static constraints = {
      packageSource   nullable: false, blank: false
      packageSlug     nullable: false, blank: false
      packageName     nullable: false, blank: false
    }
  }
  
  
  @ToString(includePackage=false)
  @GrailsCompileStatic
  class PackageContentImpl implements ContentItemSchema, Validateable {
    
    List<Identifier> instanceIdentifiers
    List<Identifier> siblingInstanceIdentifiers
    List<CoverageStatement> coverage
    String title
    String instanceMedium
    String instanceMedia
    String embargo
    String coverageDepth
    String coverageNote
    String platformUrl
    String url
    String platformName
    String _platformId
  }
  
  static constraints = {
    header            nullable: false
    packageContents   minSize: 1
    
    platformName nullable:true, blank:false
    platformUrl  blank:false, validator: { String platformUrl, PackageContentImpl instance ->
      if (!platformUrl && !instance.platformName) {
        // If platform is blank then this can't be.
        return ['null.message']
      }
    }
  }
}
