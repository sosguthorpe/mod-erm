package org.olf

import org.olf.general.*

import com.k_int.okapi.OkapiTenantAwareController

import grails.converters.JSON
import grails.gorm.multitenancy.CurrentTenant
import groovy.util.logging.Slf4j
import com.k_int.web.toolkit.utils.DomainUtils
import org.olf.general.refdata.GrailsDomainRefdataHelpers

@Slf4j
@CurrentTenant
class RefdataController extends OkapiTenantAwareController<RefdataValue>  {

  RefdataController() {
    super(RefdataValue)
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

