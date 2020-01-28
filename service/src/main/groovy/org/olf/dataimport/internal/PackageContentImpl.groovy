package org.olf.dataimport.internal

import java.time.LocalDate

import org.olf.dataimport.erm.CoverageStatement
import org.olf.dataimport.erm.Identifier
import org.olf.dataimport.internal.PackageSchema.ContentItemSchema

import grails.compiler.GrailsCompileStatic
import grails.validation.Validateable
import groovy.transform.ToString

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

  String dateMonographPublished
  String firstAuthor
  String firstEditor
  String monographEdition
  String monographVolume
  
  String url
  String platformName
  String _platformId
  LocalDate accessStart
  LocalDate accessEnd

  static constraints = {
    
    platformName nullable:true, blank:false
    platformUrl  blank:false, validator: { String platformUrl, PackageContentImpl instance ->
      if (!platformUrl && !instance.platformName) {
        // If platform is blank then this can't be.
        return ['null.message']
      }
    }
    
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
  }
}