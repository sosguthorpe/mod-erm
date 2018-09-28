package org.olf.kb

import grails.gorm.MultiTenant
import javax.persistence.Transient
import org.olf.erm.Entitlement
import org.olf.general.RefdataValue

/**
 * mod-erm representation of a BIBFRAME instance
 */
public class TitleInstance extends ElectronicResource implements MultiTenant<TitleInstance> {

  private static final String ENTITLEMENTS_QUERY = '''from Entitlement as ent 
where exists ( select pci.id 
               from PackageContentItem as pci
               where pci.pti.titleInstance = :title 
               and ent.eResource = pci.pkg )
   or exists ( select pci.id 
               from PackageContentItem as pci
               where pci.pti.titleInstance = :title
               and ent.eResource = pci )
   or exists ( select pti.id 
               from PlatformTitleInstance as pti
               where pti.titleInstance = :title
               and ent.eResource = pti )
'''

  String id
  // Title IN ORIGINAL LANGUAGE OF PUBLICATION
  String title

  // Journal/Book/...
  RefdataValue resourceType

  // Print/Electronic
  RefdataValue medium

  // For grouping sibling title instances together - EG Print and Electronic editions of the same thing
  Work work

  static mapping = {
                title column:'ti_title'
                 work column:'ti_work_fk'
               medium column:'ti_medium_fk'
         resourceType column:'ti_resource_type_fk'
  }

  static constraints = {
    resourceType (nullable:true, blank:false)
           title (nullable:false, blank:false)
          medium (nullable:true, blank:false)
            work (nullable:true, blank:false)
  }

  static hasMany = [
    identifiers: IdentifierOccurrence
  ]

  static mappedBy = [
    identifiers: 'title'
  ]


  /**
   * Return the list of entitlements that grant us access to this title.
   */
  @Transient
  List<Entitlement> getEntitlements() {
    List<Entitlement> result = Entitlement.executeQuery('select ent '+ENTITLEMENTS_QUERY,[title:this],[max:20, offset:0]);
    return result;
  }
}
