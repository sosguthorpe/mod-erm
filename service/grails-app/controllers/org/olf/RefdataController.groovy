package org.olf

import com.k_int.okapi.OkapiTenantAwareController
import com.k_int.web.toolkit.refdata.GrailsDomainRefdataHelpers
import com.k_int.web.toolkit.refdata.RefdataCategory
import com.k_int.web.toolkit.utils.DomainUtils
import grails.gorm.multitenancy.CurrentTenant
import groovy.util.logging.Slf4j

@Slf4j
@CurrentTenant
class RefdataController extends OkapiTenantAwareController<RefdataCategory> {
  
  RefdataController() {
    super(RefdataCategory)
  }
}