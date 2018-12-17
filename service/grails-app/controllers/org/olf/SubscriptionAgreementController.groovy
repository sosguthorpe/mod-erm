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
      
      respond doTheLookup (ErmResource) {
        or {
          // Resources linked via a package.
          createAlias 'pkg', 'pci_pkg', JoinType.LEFT_OUTER_JOIN
            createAlias 'pci_pkg.entitlements', 'pci_pkg_ent', JoinType.LEFT_OUTER_JOIN
              eq 'pci_pkg_ent.owner.id', subscriptionAgreementId
          
          // Ptis linked explicitly.
          createAlias 'entitlements', 'pti_ent', JoinType.LEFT_OUTER_JOIN
            eq 'pti_ent.owner.id', subscriptionAgreementId    
        }
      }
    } else {
      respond (params.boolean('stats') ? Collections.EMPTY_MAP : Collections.EMPTY_SET)
    }
  }
}

