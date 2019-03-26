package org.olf

import org.olf.erm.SubscriptionAgreement
import org.olf.kb.ErmResource
import org.olf.kb.PackageContentItem
import org.olf.kb.PlatformTitleInstance
import org.olf.kb.Pkg
import org.hibernate.sql.JoinType
import org.olf.erm.Entitlement

import com.k_int.okapi.OkapiTenantAwareController

import grails.gorm.multitenancy.CurrentTenant
import grails.orm.HibernateCriteriaBuilder
import groovy.util.logging.Slf4j
import grails.gorm.DetachedCriteria


/**
 * Control access to subscription agreements.
 * A subscription agreement (SA) is the connection between a set of resources (Which could be packages or individual titles) and a license. 
 * SAs have start dates, end dates and renewal dates. This controller exposes functions for interacting with the list of SAs
 */
@Slf4j
@CurrentTenant
class SubscriptionAgreementController extends OkapiTenantAwareController<SubscriptionAgreement>  {
  
  CoverageService coverageService
  
  SubscriptionAgreementController() {
    super(SubscriptionAgreement)
  }
  
  def resources () {
    
    final String subscriptionAgreementId = params.get("subscriptionAgreementId")
    if (subscriptionAgreementId) {

      // Ian: It's now possible for an agreement to have entitlements that do not link to a resource. Need
      // to talk through with steve about how this should work.
      final def results = doTheLookup (ErmResource) {
        or {
          
          // Direct relations...
          // Use a sub query to avoid multiple entries per item.
          'in' 'id', new DetachedCriteria(ErmResource).build {
            
            createAlias 'entitlements', 'direct_ent'
              ne 'class', Pkg
              eq 'direct_ent.owner.id', subscriptionAgreementId
            
            projections {
              property ('id')
            }
          }
          
          
          // Pci linked via package.
          'in' 'id', new DetachedCriteria(PackageContentItem).build {
        
            'in' 'pkg.id', new DetachedCriteria(Pkg).build {
              createAlias 'entitlements', 'pkg_ent'
                eq 'pkg_ent.owner.id', subscriptionAgreementId
                
                projections {
                  property ('id')
                }
            }
            
            projections {
              property ('id')
            }
          }
        }
        
        readOnly (true)
      }
      
      // This method writes to the web request if there is one (which of course there should be as we are in a controller method)
      coverageService.lookupCoverageOverrides(results, "${subscriptionAgreementId}")
      
      respond results
      return
    }
      
  }

}
