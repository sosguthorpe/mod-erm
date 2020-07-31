package org.olf

import static groovy.transform.TypeCheckingMode.SKIP

import grails.plugin.json.view.JsonViewTemplateEngine
import groovy.text.Template
import java.time.LocalDate
import org.grails.orm.hibernate.cfg.GrailsHibernateUtil
import org.olf.erm.ComparisonPoint
import org.olf.erm.SubscriptionAgreement
import org.olf.kb.ErmTitleList
import org.olf.kb.Pkg
import org.olf.kb.TitleInstance

import groovy.transform.CompileStatic

@CompileStatic
class ComparisonService {
  @Autowired
  JsonViewTemplateEngine jsonViewTemplateEngine
  
  public void compare ( OutputStream out, ComparisonPoint... titleLists ) {
    def sw = new OutputStreamWriter(out)
    
    try {
      List results = compare (titleLists)
      // Write to output stream..
      Template t = jsonViewTemplateEngine.resolveTemplate('/comparison/compare')
      def writable = t.make(comparison: results)
      writable.writeTo( sw )
      
    } finally {
      sw.close()
    }
  }
  
  public List compare ( ComparisonPoint... comparisonPoints ) {
    if (comparisonPoints.length < 1) throw new IllegalArgumentException("Require at least 2 Comparison Points to compare")
    comparisonPoints.collect { ComparisonPoint cp -> [ 'comparisonPoint': cp, 'results': queryForComparisonResults(cp)] }
  }
  
  private List queryForComparisonResults( ComparisonPoint comparisonPoints ) {
    Class<? extends ErmTitleList> type = GrailsHibernateUtil.unwrapIfProxy(comparisonPoints.titleList).class
    
    switch (type) {
      case SubscriptionAgreement:
        return queryForAgreementTitles(comparisonPoints.titleList.id, comparisonPoints.date)
        break
      case Pkg:
        return queryForPackageTitles(comparisonPoints.titleList.id, comparisonPoints.date)
        break
        
      default:
        throw new IllegalArgumentException("Invalid class of type ${type}")
    }
  }
  
  @CompileStatic(SKIP)
  List queryForPackageTitles ( final Serializable pkgId, LocalDate onDate = LocalDate.now() ) {
    TitleInstance.executeQuery("""
      SELECT pci, pti, ti, CAST(null as char)
      FROM TitleInstance as ti
        INNER JOIN ti.platformInstances as pti
          INNER JOIN pti.packageOccurences as pci
            ON pci.pkg.id = :pkgId
      WHERE
        COALESCE( pci.accessStart, pci.accessEnd ) IS NULL

        OR ( (pci.accessStart IS NULL OR pci.accessStart <= :onDate) AND pci.accessEnd >= :onDate )
        OR ( (pci.accessEnd IS NULL OR pci.accessEnd >= :onDate) AND pci.accessStart <= :onDate )
      ORDER BY ti.name, ti.id, pci.id, pti.id
    """, ['onDate': onDate, 'pkgId': pkgId], [readOnly: true])
  }
  
  @CompileStatic(SKIP)
  List queryForAgreementTitles ( final Serializable agreementId, LocalDate onDate = LocalDate.now() ) {
    TitleInstance.executeQuery("""
      SELECT
          COALESCE( case when type(link) = PackageContentItem then link else pkg_pci end ) as pci,
          COALESCE( pkg_pci_pti, pci_pti, link ) as pti,
          COALESCE( pkg_pci_pti_ti, pci_pti_ti, pti_ti ) as ti,
          ent
      FROM Entitlement as ent
        INNER JOIN ent.resource as link

        LEFT JOIN link.contentItems as pkg_pci
          LEFT JOIN pkg_pci.pti as pkg_pci_pti
            LEFT JOIN pkg_pci_pti.titleInstance as pkg_pci_pti_ti
        LEFT JOIN link.pti as pci_pti
          LEFT JOIN pci_pti.titleInstance as pci_pti_ti
        LEFT JOIN link.titleInstance as pti_ti
      WHERE
        ent.owner.id = :agreementId
        
        AND ( COALESCE(link.accessStart, pkg_pci.accessStart) IS NULL
              OR ent.activeTo IS NULL
              OR COALESCE(link.accessStart, pkg_pci.accessStart) <= ent.activeTo )
          
        AND ( COALESCE(link.accessEnd, pkg_pci.accessEnd) IS NULL
              OR ent.activeFrom IS NULL
              OR COALESCE( link.accessEnd, pkg_pci.accessEnd) >= ent.activeFrom )

        AND (
          ( (ent.activeTo IS NULL OR ent.activeTo >= :onDate) AND ent.activeFrom <= :onDate)
          OR ( (ent.activeFrom IS NULL OR ent.activeFrom <= :onDate) AND ent.activeTo >= :onDate)
          OR (
            COALESCE( ent.activeTo, ent.activeFrom) IS NULL
            
            AND (
              ( (link.accessStart IS NULL OR link.accessStart <= :onDate) AND link.accessEnd >= :onDate )
              OR ( (link.accessEnd IS NULL OR link.accessEnd >= :onDate) AND link.accessStart <= :onDate )
              OR (
                COALESCE( link.accessStart, link.accessEnd ) IS NULL
                AND (
                  ( (pkg_pci.accessStart IS NULL OR pkg_pci.accessStart <= :onDate) AND pkg_pci.accessEnd >= :onDate )
                  OR ( (pkg_pci.accessEnd IS NULL OR pkg_pci.accessEnd >= :onDate) AND pkg_pci.accessStart <= :onDate )
                  OR (
                    COALESCE( pkg_pci.accessStart, pkg_pci.accessEnd ) IS NULL
                  )
                )
              )
            )
          )
        )
      ORDER BY pkg_pci_pti_ti.name, pkg_pci_pti_ti.id, pci_pti_ti.name, pci_pti_ti.id, pti_ti.name, pti_ti.id, pkg_pci.id, pkg_pci_pti.id, pci_pti.id, link.id, pkg_pci_pti.id, pci_pti.id
    """, ['onDate': onDate, 'agreementId': agreementId], [readOnly: true])
  }
}
