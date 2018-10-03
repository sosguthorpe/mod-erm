package org.olf

import org.olf.general.RefdataValue
import org.olf.kb.ErmResource
import org.olf.kb.Pkg
import org.olf.kb.TitleInstance

import com.k_int.okapi.OkapiTenantAwareController

import grails.gorm.multitenancy.CurrentTenant
import groovy.util.logging.Slf4j

@Slf4j
@CurrentTenant
class EresourceController extends OkapiTenantAwareController<ErmResource>  {

  EresourceController() {
    // True means read only. This should block post and puts to this.
    super(ErmResource, true)
  }
  
  def index() {
    final Closure eResourceBase = {
      or {
        and {
          eq 'class', TitleInstance
          eq 'subType', TitleInstance.lookupOrCreateSubType('electronic')
        }
        
        eq 'class', Pkg
      }
    }
    
    final int offset = params.int("offset") ?: 0
    final int perPage = Math.min(params.int('perPage') ?: params.int('max') ?: 100, 100)
    final int page = params.int("page") ?: (offset ? (offset / perPage) + 1 : 1)
    final List<String> filters = params.list("filters[]") ?: params.list("filters")
    final List<String> match_in = params.list("match[]") ?: params.list("match")
    final List<String> sorts = params.list("sort[]") ?: params.list("sort")
    
    if (params.boolean('stats')) {
      respond simpleLookupService.lookupWithStats(this.resource, params.term, perPage, page, filters, match_in, sorts, null, eResourceBase)
    } else {
      respond simpleLookupService.lookup(this.resource, params.term, perPage, page, filters, match_in, sorts, eResourceBase)
    }
  }
}

