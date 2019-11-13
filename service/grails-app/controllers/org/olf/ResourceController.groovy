package org.olf

import grails.converters.JSON
import org.hibernate.Hibernate
import org.hibernate.sql.JoinType
import org.olf.erm.Entitlement
import org.olf.kb.ErmResource
import org.olf.kb.PackageContentItem
import org.olf.kb.Pkg
import org.olf.kb.PlatformTitleInstance
import org.olf.kb.TitleInstance

import com.k_int.okapi.OkapiTenantAwareController
import grails.gorm.DetachedCriteria
import grails.gorm.multitenancy.CurrentTenant
import groovy.util.logging.Slf4j
import java.time.Duration
import java.time.Instant

@Slf4j
@CurrentTenant
class ResourceController extends OkapiTenantAwareController<ErmResource>  {

  ResourceController() {
    // True means read only. This should block post and puts to this.
    super(ErmResource, true)
  }
  
  def electronic () {
    
    respond doTheLookup ({
      or {
        eq 'class', Pkg
        
        and {
          eq 'class', TitleInstance
          eq 'subType', TitleInstance.lookupOrCreateSubType('electronic')
        }
      }
    })
  }
  
  /**
   * This is an availability checker for the resource. Show me all the places I can get this resource.
   * @return List of resources representing things that can be added to an Entitlement
   */
  def entitlementOptions ( String resourceId ) {
    log.debug("entitlementOptions(${resourceId})");

    // Easiest way to check that this resource is a title is to read it in as one.
    // We use criteria here to ensure
    
    final TitleInstance ti = resourceId ? TitleInstance.findByIdAndSubType ( resourceId, TitleInstance.lookupOrCreateSubType('electronic') ) : null
    
    log.debug("Got ti ${ti?.id}");
    
    //For issue ERM-285
    if (!ti) {
      //Check to see if the resourceId points to a package
      final Pkg pkg = resourceId ? Pkg.read( resourceId ) : null
      if (!pkg) {
        //if not then we return the empty set

        //response.status = 404
        respond ([]);
        return 
      } else {
        respond ([pkg]);
        return
      }
    }
    // We have a matching title. We need to work out where we can get the title from. This means finding all
    // resources that can be added to an Entitlement, that lead back to this title
    // Lets build the base query and pass into the simpleLookupService.
    // This will allow the usual parameters to be used to filter the results even further.
    final Instant start = Instant.now()
    log.debug("Start query ${start}")
    respond doTheLookup ({
      readOnly(true)
      
      or {
          
          // PTIs
//          'in' 'id', new DetachedCriteria(PlatformTitleInstance).build {
//            readOnly (true)
//            
//            eq 'titleInstance', ti
//              
//            projections {
//              property ('id')
//            }
//          }
          
          // PCIs
          'in' 'id', new DetachedCriteria(PackageContentItem).build {
            readOnly (true)
            
            createAlias 'pti', 'pci_pti'
              eq 'pci_pti.titleInstance', ti
              
            isNull 'removedTimestamp'

            projections {
              property ('id')
            }
            
          }
          
          // Packages.
          'in' 'id', new DetachedCriteria(PackageContentItem).build {
            readOnly (true)
            
            createAlias 'pti', 'pci_pti'
              eq 'pci_pti.titleInstance', ti
            
            isNull 'removedTimestamp'

            projections {
              property ('pkg.id')
            }
          }
        }
    })
    log.debug("completed in ${Duration.between(start, Instant.now()).toSeconds()} seconds")
  }
  
  def entitlements (String resourceId) {
    
    // Easiest way to check that this resource is a title is to read it in as one.
    // We use criteria here to ensure
    
    final ErmResource res = resourceId ? ErmResource.read ( resourceId ) : null
    final Class<? extends ErmResource> resClass = res ? Hibernate.getClass( res ) : null
    
    // Not allowed type Just show a 404.
    if (resClass == null || (!(resClass == TitleInstance || Entitlement.ALLOWED_RESOURCES.contains( resClass )))) {
      response.status = 404
      return
    }
    
    // We have a matching resource. Grab all entitlements that lead to this resource.
    final Instant start = Instant.now()
    log.debug("Start query ${start}")
    respond doTheLookup (Entitlement, {
      
      switch (resClass) {
        case TitleInstance:
          or {
            'in' 'resource.id', new DetachedCriteria(PlatformTitleInstance).build {
              readOnly (true)
              
              eq 'titleInstance', res
                
              projections {
                property ('id')
              }
            }
            
            // PCIs
            'in' 'resource.id', new DetachedCriteria(PackageContentItem).build {
              readOnly (true)
              
              createAlias 'pti', 'pci_pti'
                eq 'pci_pti.titleInstance', res
                
              projections {
                property ('id')
              }
            }
            
            // Packages.
            'in' 'resource.id', new DetachedCriteria(PackageContentItem).build {
              readOnly (true)
              
              createAlias 'pti', 'pci_pti'
                eq 'pci_pti.titleInstance', res

              isNull 'removedTimestamp'

              projections {
                property ('pkg.id')
              }
            }
          }
          break
        case PlatformTitleInstance:
          or {
            eq 'resource', res
            
            // PCIs
            'in' 'resource.id', new DetachedCriteria(PackageContentItem).build {
              readOnly (true)
              
              eq 'pti', res
                
              projections {
                property ('id')
              }
            }
            
            // Packages.
            'in' 'resource.id', new DetachedCriteria(PackageContentItem).build {
              readOnly (true)
              
              eq 'pti', res

              isNull 'removedTimestamp'

              projections {
                property ('pkg.id')
              }
            }
          }
          break
        default :
          eq 'resource', res
          break
      }
    })
    log.debug("completed in ${Duration.between(start, Instant.now()).toSeconds()} seconds")
  }
}

