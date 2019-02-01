package org.olf

import org.hibernate.sql.JoinType
import org.olf.erm.SubscriptionAgreement
import org.olf.kb.ErmResource
import org.olf.kb.PackageContentItem
import org.olf.kb.PlatformTitleInstance
import org.olf.kb.Pkg
import org.olf.erm.Entitlement

import com.k_int.okapi.OkapiTenantAwareController

import grails.gorm.multitenancy.CurrentTenant
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
        // This isn't good - if there are 10000 resources linked to an entitlement, this will serialise the list of ID's out of the withCriteria
        // above, the inject them as a list into the in query below.
        respond doTheLookup (ErmResource) {
          
          'in' 'id', items + ["0"]
        }
        return
      }
      
    }
  }

  def resources2(String subscriptionAgreementId) {

    // String query = '''
    //   select r from ErmResource as r 
    //   where exists ( Select e from Entitlement as e where e.resource.id = r.id )
    //      or exists ( Select pci 
    //                  from PackageContentItem as pci 
    //                  where pci.pti.id = r.id 
    //                    and exists ( select e from Entitlement as e where r.resource.id = pci.pkg.id ) )
    // '''
    // select sa, e, e.resource, nvl(pci, e.resource)
    // from SubscriptionAgreement as sa join sa.entitlements as e left outer join PackageContentItem as pci on pci.pkg = e.resource

    DetachedCriteria resources_with_an_entitlement = ErmResource.where {
      def res = ErmResource
      // Where there is an entitlement directly for this resource
      exists Entitlement.where {
        def e1 = Entitlement
        def r = resource
        r.id == res.id
        // || exists PackageContentItem.where {
        //      def pci = PackageContentItem
        //      return ( pci.pkg.id == r.id && pci.pti.id == res.id )
        // }
      } 
    }

    println("number of resources with an entitlement ${resources_with_an_entitlement.list().size()}");

    DetachedCriteria resources_with_an_entitlement_2 = ErmResource.where {
      id == 'hello'
    }
    println("number of resources with an entitlement_2 ${resources_with_an_entitlement_2.list().size()}");

    respond doTheLookup (ErmResource) { resources_with_an_entitlement_2 }

  }
}

