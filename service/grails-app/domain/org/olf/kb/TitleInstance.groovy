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
               and ent.pkg = pci.pkg )
   or exists ( select pci.id 
               from PackageContentItem as pci
               where pci.pti.titleInstance = :title
               and ent.pci = pci )
   or exists ( select single_title from Entitlement as single_title where single_title=ent and single_title.pti.titleInstance = :title )
'''

  // Title IN ORIGINAL LANGUAGE OF PUBLICATION
  String title

  // Journal/Book/...
  RefdataValue resourceType

  // Print/Electronic
  RefdataValue medium

  // For grouping sibling title instances together - EG Print and Electronic editions of the same thing
  Work work

  static mapping = {
                   id column:'ti_id', generator: 'uuid', length:36
              version column:'ti_version'
                title column:'ti_title'
                 work column:'ti_work_fk'
               medium column:'ti_medium_fk'
         resourceType column:'ti_resource_type_fk'
  }

  static constraints = {
           title(nullable:false, blank:false)
    resourceType(nullable:true, blank:false)
          medium(nullable:true, blank:false)
            work(nullable:true, blank:false)
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
