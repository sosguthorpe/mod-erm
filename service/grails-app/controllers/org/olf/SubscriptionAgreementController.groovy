package org.olf

import org.hibernate.sql.JoinType
import org.olf.erm.SubscriptionAgreement
import org.olf.kb.ErmResource
import org.olf.kb.PackageContentItem
import org.olf.kb.PlatformTitleInstance

import com.k_int.okapi.OkapiTenantAwareController

import grails.gorm.multitenancy.CurrentTenant
import groovy.util.logging.Slf4j


/**
 * Control access to subscription agreements.
 * A subscription agreement (SA) is the connection between a set of resources (Which could be packages or individual titles) and a license. 
 * SAs have start dates, end dates and renewal dates. This controller exposes functions for interacting with the list of SAs
 */
@Slf4j
@CurrentTenant
class SubscriptionAgreementController extends OkapiTenantAwareController<SubscriptionAgreement>  {
  
  SubscriptionAgreementController() {
    super(SubscriptionAgreement)
  }
  
  def resources (String subscriptionAgreementId) {
    
    if (subscriptionAgreementId) {
      
      // The in clause below will freak out is the subquery returns an empty list. So we should test for
      // the entitlements list being empty first.
      //
      // Ian: It's now possible for an agreement to have entitlements that do not link to a resource. Need
      // to talk through with steve about how this should work.
      if (SubscriptionAgreement.read(subscriptionAgreementId)?.items?.size() ?: 0 > 0) {
        
        def items = ErmResource.withCriteria {
          
          or {
            eq ('class', PlatformTitleInstance)
            eq ('class', PackageContentItem)
          }
          
          or {
            // Resources linked via a package.
            createAlias 'pkg', 'pci_pkg', JoinType.LEFT_OUTER_JOIN
              createAlias 'pci_pkg.entitlements', 'pci_pkg_ent', JoinType.LEFT_OUTER_JOIN
                eq 'pci_pkg_ent.owner.id', subscriptionAgreementId
            
            // Ptis linked explicitly.
            createAlias 'entitlements', 'pti_ent', JoinType.LEFT_OUTER_JOIN
              eq 'pti_ent.owner.id', subscriptionAgreementId
          }
          
          projections {
            distinct ('id')
          }
        }
        
        // Dedupe in a way that means pagination still works.
        respond doTheLookup (ErmResource) {
          
          'in' 'id', items + ["0"]
        }
        return
      }
      
    }
    
    // If not matched above return with empty collection...
    respond (params.boolean('stats') ? Collections.EMPTY_MAP : Collections.EMPTY_SET)
  }
}

