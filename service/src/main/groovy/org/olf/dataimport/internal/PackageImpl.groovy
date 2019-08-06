package org.olf.dataimport.internal

import java.time.LocalDate

import org.olf.dataimport.erm.CoverageStatement
import org.olf.dataimport.erm.Identifier
import org.olf.dataimport.erm.PackageProvider

import grails.validation.Validateable

class PackageImpl implements PackageSchema, Validateable {
  HeaderImpl header
  List<PackageContentImpl> packageContents = []
  
  class HeaderImpl implements PackageHeaderSchema, Validateable {
    PackageProvider packageProvider
    String packageSource
    String status
    String packageName
    LocalDate startDate
    LocalDate endDate
    String packageSlug
    String _intenalId
  }
  
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
  }
}
