package org.olf.erm

import java.time.LocalDate
import org.olf.general.jobs.ComparisonJob
import org.olf.kb.ErmTitleList
import com.k_int.web.toolkit.refdata.RefdataValue

import grails.compiler.GrailsCompileStatic
import grails.gorm.MultiTenant

/**
 * Subscription agreement - object holding details about an SA connecting a resource list (Composed Of packages and platform-titles).
 */
@GrailsCompileStatic
public class ComparisonPoint implements MultiTenant<ComparisonPoint> {

  String id
  LocalDate date
  ErmTitleList titleList

  static belongsTo = [
    job: ComparisonJob
  ]

  static mapping = {
    id column:'cp_id', generator: 'uuid2', length:36
    eventData column:'cp_date'
    titleList column:'cp_title_list_fk'
  }

  static constraints = {
              id(nullable:false, blank:false)
            date(nullable:false)
       titleList(nullable:false)
  }
}
