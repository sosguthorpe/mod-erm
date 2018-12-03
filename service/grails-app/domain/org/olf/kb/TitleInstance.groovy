package org.olf.kb

import javax.persistence.Transient
import org.hibernate.sql.JoinType
import org.olf.erm.Entitlement
import org.olf.general.RefdataValue
import org.olf.general.refdata.Defaults

import grails.gorm.MultiTenant

/**
 * mod-erm representation of a BIBFRAME instance
 */
public class TitleInstance extends ErmResource implements MultiTenant<TitleInstance> {
  
  static namedQueries = {
    entitled {
      createAlias ('platformInstances', 'pi')
//        createAlias ('pi.entitlements', 'pi_entitlements', JoinType.LEFT_OUTER_JOIN)
        createAlias ('pi.packageOccurences', 'pi_po', JoinType.LEFT_OUTER_JOIN)
//          createAlias ('pi_po.entitlements', 'pi_po_entitlements', JoinType.LEFT_OUTER_JOIN)
          createAlias ('pi_po.pkg', 'pi_po_pkg', JoinType.LEFT_OUTER_JOIN)
//            createAlias ('pi_po_pkg.entitlements', 'pi_po_pkg_entitlements', JoinType.LEFT_OUTER_JOIN)
            
            
      or {
        isNotEmpty 'pi.entitlements'
        isNotEmpty 'pi_po.entitlements'
        isNotEmpty 'pi_po_pkg.entitlements'
      }
    }
  }
  
  // For grouping sibling title instances together - EG Print and Electronic editions of the same thing
  Work work
  
  // Journal/Book/...
  @Defaults(['Journal', 'Book'])
  RefdataValue type

  // Print/Electronic
  @Defaults(['Print', 'Electronic'])
  RefdataValue subType

  static hasMany = [
    identifiers: IdentifierOccurrence,
    platformInstances: PlatformTitleInstance
  ]

  static mappedBy = [
    identifiers: 'title',
    platformInstances: 'titleInstance'
  ]

  static mapping = {
             work column:'ti_work_fk'
             type column:'ti_type_fk'
          subType column:'ti_subtype_fk'
  }

  static constraints = {
            name (nullable:false, blank:false)
            work (nullable:true, blank:false)
  }
}
