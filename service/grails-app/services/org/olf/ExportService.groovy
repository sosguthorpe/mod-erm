package org.olf 
import java.time.LocalDate

import org.olf.erm.SubscriptionAgreement
import org.olf.kb.ErmResource

import grails.gorm.transactions.Transactional

/**
 * Service for exporting resources.
 */
@Transactional(readOnly=true)
public class ExportService {
  CoverageService coverageService
  
  SubscriptionAgreement agreement (final String agreementId, boolean currentOnly = false) {
    SubscriptionAgreement agreement = SubscriptionAgreement.read (agreementId)
    if (agreement) {
      // Add the extra resources here.
      if (currentOnly) {
        agreement.metaClass.getResourceList = { current(agreement.id) }
        // Only current resources.
      } else {
        // all.
        agreement.metaClass.getResourceList = { all(agreement.id) }
      }
    }
    
    agreement
  }
   
  List<List> all(final String agreementId = null) {
    def results = null
    if (agreementId) {
      results = ErmResource.executeQuery("""
        SELECT res, pkg_ent, direct_ent
        FROM ErmResource as res
          LEFT JOIN res.entitlements as direct_ent
          LEFT JOIN res.pkg as pkg

            ON (res.class = PackageContentItem) 
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
      """, [id: agreementId], [readOnly: true])
    } else {
      results = ErmResource.executeQuery("""
        SELECT res, pkg_ent, direct_ent
        FROM ErmResource as res
          LEFT JOIN res.entitlements as direct_ent
          LEFT JOIN res.pkg as pkg

            ON res.class = PackageContentItem
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
      """, [readOnly: true])
    }
	
    // At this point we should have a List of results. But instead of each result being an ErmResource we
    // should have a collection
    // of [0]->ErmResource, [1]->Entitlement, [2]->Entitlement.
      
    // The first entitlement will be present if this is a PCI resource and it associated through a 
    // package and the second will be present if this resource was directly associated to an entitlement. 
    // This means that can/will get multiple entries for the same resource if there are multiple 
    // packages, or if the resources is associated directly and also through a package to an 
    // agreement. This behaviour is actually desirable for the export.
      
    // This method writes to the web request if there is one (which of course there should be as we are in a controller method)
    coverageService.lookupCoverageOverrides(results.collect{it[0]}) 
      
    return results  
  }
  
  List<List> current (final String agreementId = null) {
    final LocalDate today = LocalDate.now()
    
    def results = null
    if (agreementId) {
      results = ErmResource.executeQuery("""
        SELECT res, pkg_ent, direct_ent
        FROM ErmResource as res
          LEFT JOIN res.entitlements as direct_ent
            ON (
              (direct_ent.activeTo IS NULL OR direct_ent.activeTo >= :today)
                AND
                 (direct_ent.activeFrom IS NULL OR direct_ent.activeFrom  <= :today)
            )
          LEFT JOIN res.pkg as pkg

            ON (res.class = PackageContentItem
              AND (
                  (res.accessEnd IS NULL OR res.accessEnd >= :today)
                AND
                  (res.accessStart IS NULL OR res.accessStart <= :today)
              )
            ) 
            LEFT JOIN pkg.entitlements as pkg_ent
              ON (
              (pkg_ent.activeTo IS NULL OR pkg_ent.activeTo >= :today)
                AND
                 (pkg_ent.activeFrom IS NULL OR pkg_ent.activeFrom  <= :today)
            )
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
      """, [id: agreementId, 'today': today], [readOnly: true])
    } else {
      results = ErmResource.executeQuery("""
        SELECT res, pkg_ent, direct_ent
        FROM ErmResource as res
          LEFT JOIN res.entitlements as direct_ent
            ON (
              (direct_ent.activeTo IS NULL OR direct_ent.activeTo >= :today)
                AND
                 (direct_ent.activeFrom IS NULL OR direct_ent.activeFrom  <= :today)
            )
          LEFT JOIN res.pkg as pkg

            ON (res.class = PackageContentItem
              AND (
                  (res.accessEnd IS NULL OR res.accessEnd >= :today)
                AND
                  (res.accessStart IS NULL OR res.accessStart <= :today)
              )
            )
            LEFT JOIN pkg.entitlements as pkg_ent
              ON (
                (pkg_ent.activeTo IS NULL OR pkg_ent.activeTo >= :today)
                  AND
                   (pkg_ent.activeFrom IS NULL OR pkg_ent.activeFrom  <= :today)
              )
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
      """, ['today': today], [readOnly: true])
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
  
//  List<ErmResource> future (final String agreementId = null) {
//    final LocalDate today = LocalDate.now()
//    
//    def results = null
//    if (agreementId) {
//      results = ErmResource.executeQuery("""
//        SELECT res, pkg_ent, direct_ent
//        FROM ErmResource as res
//          LEFT JOIN res.entitlements as direct_ent
//            ON (
//              (direct_ent.activeTo IS NULL OR direct_ent.activeTo >= :today)
//                AND
//                 (direct_ent.activeFrom IS NULL OR direct_ent.activeFrom  <= :today)
//            )
//          LEFT JOIN res.pkg as pkg
//
//            ON (res.class = PackageContentItem
//              AND (
//                  (res.accessEnd IS NULL OR res.accessEnd >= :today)
//                AND
//                  (res.accessStart IS NULL OR res.accessStart <= :today)
//              )
//            ) 
//            LEFT JOIN pkg.entitlements as pkg_ent
//              ON (
//              (pkg_ent.activeTo IS NULL OR pkg_ent.activeTo >= :today)
//                AND
//                 (pkg_ent.activeFrom IS NULL OR pkg_ent.activeFrom  <= :today)
//            )
//        WHERE
//          (
//            direct_ent.owner.id = :id
//            AND
//            res.class != Pkg
//          )
//        OR
//          (
//            pkg_ent.owner.id = :id
//          )
//      """, [id: agreementId, 'today': today], [readOnly: true])
//    } else {
//      results = ErmResource.executeQuery("""
//        SELECT res, pkg_ent, direct_ent
//        FROM ErmResource as res
//          LEFT JOIN res.entitlements as direct_ent
//            ON (
//              (direct_ent.activeTo IS NULL OR direct_ent.activeTo >= :today)
//                AND
//                 (direct_ent.activeFrom IS NULL OR direct_ent.activeFrom  <= :today)
//            )
//          LEFT JOIN res.pkg as pkg
//
//            ON (res.class = PackageContentItem
//              AND (
//                  (res.accessEnd IS NULL OR res.accessEnd >= :today)
//                AND
//                  (res.accessStart IS NULL OR res.accessStart <= :today)
//              )
//            )
//            LEFT JOIN pkg.entitlements as pkg_ent
//              ON (
//                (pkg_ent.activeTo IS NULL OR pkg_ent.activeTo >= :today)
//                  AND
//                   (pkg_ent.activeFrom IS NULL OR pkg_ent.activeFrom  <= :today)
//              )
//        WHERE
//          (
//            direct_ent.owner IS NOT NULL
//            AND
//            res.class != Pkg
//          )
//        OR
//          (
//            pkg_ent.owner IS NOT NULL
//          )
//      """, ['today': today], [readOnly: true])
//    }
//  
//    // At this point we should have a List of results. But instead of each result being an ErmResource we
//    // should have a collection
//    // of [0]->ErmResource, [1]->Entitlement, [2]->Entitlement.
//      
//    // The first entitlement will be present if this is a PCI resource and it associated through a
//    // package and the second will be present if this resource was directly associated to an entitlement.
//    // This means that can/will get multiple entries for the same resource if there are multiple
//    // packages, or if the resources is associated directly and also through a packge to an
//    // agreement. This behaviour is actually desirable for the export.
//      
//    // This method writes to the web request if there is one (which of course there should be as we are in a controller method)
//    coverageService.lookupCoverageOverrides(results.collect{it[0]})
//      
//    return results
//  }

}
