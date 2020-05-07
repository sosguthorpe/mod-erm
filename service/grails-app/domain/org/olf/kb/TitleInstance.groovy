package org.olf.kb

import javax.persistence.Transient
import org.hibernate.FetchMode
import org.hibernate.sql.JoinType
import org.olf.erm.Entitlement
import com.k_int.web.toolkit.refdata.CategoryId
import com.k_int.web.toolkit.refdata.RefdataValue
import com.k_int.web.toolkit.refdata.Defaults

import grails.gorm.MultiTenant

/**
 * mod-erm representation of a BIBFRAME instance
 */
public class TitleInstance extends ErmResource implements MultiTenant<TitleInstance> {

  String getLongName() {
    String detailString = ''

    if (this.monographVolume != null) {
      detailString = " ${this.monographVolume}"
    }

    if (this.dateMonographPublished != null) {
      detailString += "${detailString ? ', ' : ''}${dateMonographPublished}"
    }

    if (this.monographEdition != null) {
      detailString += " (${this.monographEdition})"
    }
    
    "${name}${ detailString ? '.' + detailString : '' }"
  }

  
  static namedQueries = {
    entitled {
      createAlias ('platformInstances', 'pi')
      createAlias ('pi.packageOccurences', 'pi_po', JoinType.LEFT_OUTER_JOIN)
      createAlias ('pi_po.pkg', 'pi_po_pkg', JoinType.LEFT_OUTER_JOIN)
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
  @CategoryId(defaultInternal=true)
  @Defaults(['Journal', 'Book'])
  RefdataValue type

  // Print/Electronic
  @CategoryId(defaultInternal=true)
  @Defaults(['Print', 'Electronic'])
  RefdataValue subType

  String dateMonographPublished
  
  String firstAuthor
  String firstEditor

  String monographEdition
  String monographVolume

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
        dateMonographPublished column: 'ti_date_monograph_published'
                   firstAuthor column: 'ti_first_author'
                   firstEditor column: 'ti_first_editor'
              monographEdition column: 'ti_monograph_edition'
               monographVolume column: 'ti_monograph_volume'
  }

  static constraints = {
            name (nullable:false, blank:false)
            work (nullable:true, blank:false)
            dateMonographPublished (nullable:true, blank:false, matches: '^\\d{4}(-((0[0-9])|(1[0-2]))(-(([0-2][0-9])|3[0-1]))?)?\$')
            firstAuthor (nullable:true, blank:false)
            firstEditor (nullable:true, blank:false)
            monographEdition (nullable:true, blank:false)
            monographVolume (nullable:true, blank:false)
  }
  
  static transients = ['siblingIdentifiers']
  
  private Set<IdentifierOccurrence> theSiblingIdentifiers = null
  public Set<IdentifierOccurrence> getSiblingIdentifiers() {
    if (theSiblingIdentifiers == null) {
      theSiblingIdentifiers = []
      final String theWork = this.work?.id
      final String me = this.id
      if (me && theWork) {
        
        IdentifierOccurrence.createCriteria().list {
          createAlias ('title', 'the_title')
            createAlias ('the_title.work', 'the_work')
            
          eq ('the_work.id', theWork)
          ne ('the_title.id', me)
        }?.each { IdentifierOccurrence ido -> this.theSiblingIdentifiers << ido }
      }
    }
    
    theSiblingIdentifiers
  }

  public String getCodexSummary() {
    return "${this}";
  }
  
  public String toString() {
    "${name} (${type?.value}/${subType?.value})"
  }
}
