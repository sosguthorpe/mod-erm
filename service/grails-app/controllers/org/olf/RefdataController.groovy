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
  
  def lookup (String domain, String property) {
    def c = DomainUtils.resolveDomainClass(domain)?.javaClass
    def cat = c ? GrailsDomainRefdataHelpers.getCategoryString(c, property) : null
    
    // Bail if no cat.
    if (!cat) {
      render status: 404
    } else {
      forward action: "index", params: [filters: ["owner.desc==${cat}"]]
    }
  }
}