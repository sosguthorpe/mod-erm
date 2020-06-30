package org.olf.kb

import org.olf.CoverageService
import org.olf.erm.Entitlement
import com.k_int.web.toolkit.refdata.RefdataValue
import com.k_int.web.toolkit.tags.Tag
import grails.async.Promises
import grails.gorm.MultiTenant
import grails.gorm.multitenancy.Tenants
import java.time.LocalDate

/**
 * Represents a resource that yields a list of titles. These resources can be compared.
 */
class ErmTitleList implements MultiTenant<ErmTitleList> { 
  
  String id
  static mapping = {
    tablePerHierarchy false
    id column: 'id', generator: 'uuid2', length:36
  }
}
