package org.olf 
import org.olf.kb.ErmResource 
import com.k_int.okapi.OkapiTenantAwareController


import grails.gorm.DetachedCriteria
import grails.gorm.multitenancy.CurrentTenant
import groovy.util.logging.Slf4j
import java.time.LocalDate
import grails.gorm.transactions.Transactional 
import org.hibernate.Hibernate
import org.hibernate.sql.JoinType
import grails.converters.JSON



import org.olf.export.KBart
import org.olf.export.KBartExport

/**
 * 
 */
@Transactional
public class ExportService {
  CoverageService coverageService
   
  List<ErmResource> entitled(final String agreementId = null) {
    final LocalDate today = LocalDate.now()
    
    def results = null
    if (agreementId) {
      results = ErmResource.executeQuery("""
        SELECT res, pkg_ent, direct_ent
        FROM ErmResource as res
          LEFT JOIN res.entitlements as direct_ent
          LEFT JOIN res.pkg as pkg

            ON (res.class = PackageContentItem AND ((res.accessEnd IS NULL OR res.accessEnd >= :today) AND (res.accessStart IS NULL OR res.accessStart <= :today))) 
            LEFT JOIN pkg.entitlements as pkg_ent
        WHERE
          (
            direct_ent.owner.id = :id
            AND
            res.class != Pkg
          )
        OR
          (
            pkg_ent.owner.id = :id
          )
      """, [id: agreementId, 'today': "{today}"], [readOnly: true])
    } else {
      results = ErmResource.executeQuery("""
        SELECT res, pkg_ent, direct_ent
        FROM ErmResource as res
          LEFT JOIN res.entitlements as direct_ent
          LEFT JOIN res.pkg as pkg
            ON (res.class = PackageContentItem AND ((res.accessEnd IS NULL OR res.accessEnd >= :today) AND (res.accessStart IS NULL OR res.accessStart <= :today)))
            LEFT JOIN pkg.entitlements as pkg_ent
        WHERE
          (
            direct_ent.owner IS NOT NULL
            AND
            res.class != Pkg
          )
        OR
          (
            pkg_ent.owner IS NOT NULL
          )
      """, ['today': "{today}"], [readOnly: true])
    }
	
    // At this point we should have a List of results. But instead of each result being an ErmResource we
    // should have a collection
    // of [0]->ErmResource, [1]->Entitlement, [2]->Entitlement.
      
    // The first entitlement will be present if this is a PCI resource and it associated through a 
    // package and the second will be present if this resource was directly associated to an entitlement. 
    // This means that can/will get multiple entries for the same resource if there are multiple 
    // packages, or if the resources is associated directly and also through a packge to an 
    // agreement. This behaviour is actually desirable for the export. 
      
    // This method writes to the web request if there is one (which of course there should be as we are in a controller method)
    coverageService.lookupCoverageOverrides(results.collect{it[0]}) 
      
    return results  
  } 

}
