package org.olf.erm

import org.hibernate.sql.JoinType
import org.olf.kb.ErmResource
import org.olf.kb.PackageContentItem
import org.olf.kb.PlatformTitleInstance

import grails.gorm.services.Service
import grails.gorm.transactions.Transactional

@Service(SubscriptionAgreement)
abstract class SubscriptionAgreementDataService {
  
  @Transactional
  List<ErmResource> resourcesFor (Serializable id) {
    List<ErmResource> list = [] 
    if (id) {
      ErmResource.createCriteria().list {
        or {
          and {
            eq 'class', PackageContentItem.class.name
            
            // Resources linked via a package.
            createAlias 'pkg', 'pci_pkg', JoinType.LEFT_OUTER_JOIN
              createAlias 'pci_pkg.entitlements', 'pci_pkg_ent', JoinType.LEFT_OUTER_JOIN
              
            eq 'pci_pkg_ent.owner', id
          }
          
          and {
            eq 'class', PlatformTitleInstance.class.name
              // Ptis linked explicitly.
              createAlias 'entitlements', 'pti_ent', JoinType.LEFT_OUTER_JOIN
              
            eq 'pti_ent.owner', id
                
          }
        }
      } 
    }
    
    list
  }
}
