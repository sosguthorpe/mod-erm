package org.olf

import org.hibernate.Hibernate
import org.hibernate.sql.JoinType
import org.olf.erm.Entitlement
import org.olf.kb.ErmResource
import org.olf.kb.PackageContentItem
import org.olf.kb.Pkg
import org.olf.kb.PlatformTitleInstance
import org.olf.kb.TitleInstance

import com.k_int.okapi.OkapiTenantAwareController

import grails.gorm.multitenancy.CurrentTenant
import groovy.util.logging.Slf4j

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
        and {
          eq 'class', TitleInstance
          eq 'subType', TitleInstance.lookupOrCreateSubType('electronic')
        }
        
        eq 'class', Pkg
      }
    })
  }
  
  /**
   * This is an availability checker for the resource. Show me all the places I can get this resource.
   * @return List of resources representing things that can be added to an Entitlement
   */
  def entitlementOptions ( String resourceId ) {
    
    // Easiest way to check that this resource is a title is to read it in as one.
    // We use criteria here to ensure
    
    final TitleInstance ti = resourceId ? TitleInstance.findByIdAndSubType ( resourceId, TitleInstance.lookupOrCreateSubType('electronic') ) : null
    
    // Not title. Just show a 404
    if (!ti) {
      response.status = 404
      return 
    }
    
    // We have a matching title. We need to work out where we can get the title from. This means finding all
    // resources that can be added to an Entitlement, that lead back to this title
    // Lets build the base query and pass into the simpleLookupService.
    // This will allow the usual parameters to be used to filter the results even further.
    respond doTheLookup ({
      
      // First check in allowed types... This will allow the query to grow with validation.
      inList ( 'class', Entitlement.ALLOWED_RESOURCES )
      
      
      // Packages
      createAlias( 'contentItems', 'pcis', JoinType.LEFT_OUTER_JOIN )
        createAlias( 'pcis.pti', 'pkg_pti', JoinType.LEFT_OUTER_JOIN )
      
      // PackageContentItem
      createAlias( 'pti', 'pci_pti', JoinType.LEFT_OUTER_JOIN )
      
      // PlatformTitleInstance (no need to join anything here as this is where the link happens.
      
      // Now filter on the various title instances.
      or {
        // Any orphan PTIs
        and {
          eq 'titleInstance', ti
          isEmpty ('packageOccurences')
        }
        
        // PTIs from PCIs
        eq 'pci_pti.titleInstance', ti
        
        // PTIs from Pkg
        eq 'pkg_pti.titleInstance', ti
      }
    })
  }
  
  def entitlements (String resourceId) {
    
    // Easiest way to check that this resource is a title is to read it in as one.
    // We use criteria here to ensure
    
    final ErmResource res = resourceId ? ErmResource.read ( resourceId ) : null
    final Class<? extends ErmResource> resClass = res ? Hibernate.getClass( res ) : null
    
    // Not title. Just show a 404
    if (resClass == null || (!(resClass == TitleInstance || Entitlement.ALLOWED_RESOURCES.contains( resClass )))) {
      response.status = 404
      return
    }
    
    // We have a matching resource. Grab all entitlements that lead to this resource.
    respond doTheLookup (Entitlement, {
      
      switch (res.class) {
        case TitleInstance:
          or {
            createAlias( 'resource', 'res')
              createAlias( 'res.contentItems', 'rcis', JoinType.LEFT_OUTER_JOIN )
                createAlias( 'rcis.pti', 'rci_ptis', JoinType.LEFT_OUTER_JOIN )
                  eq 'rci_ptis.titleInstance', res
                  
              createAlias( 'res.pti', 'rptis', JoinType.LEFT_OUTER_JOIN )
                eq 'rptis.titleInstance', res
              
              createAlias( 'res.titleInstance', 'rtis', JoinType.LEFT_OUTER_JOIN )
                eq 'rtis.id', res.id
          }
          break
        case PlatformTitleInstance:
          or {
            createAlias( 'resource', 'res')
              createAlias( 'res.contentItems', 'rcis', JoinType.LEFT_OUTER_JOIN )
              eq 'rcis.pti', res
                
            createAlias( 'res.pti', 'rptis', JoinType.LEFT_OUTER_JOIN )
              eq 'rptis.id', res.id
              
            eq 'resource', res
          }
          break
        case PackageContentItem:
          or {
            createAlias( 'resource.contentItems', 'rcis', JoinType.LEFT_OUTER_JOIN )
              eq 'rcis.id', res.id
              
            eq 'resource', res
          }
          break
        default :
          eq 'resource', res
          break
      }
    })
  }
}

