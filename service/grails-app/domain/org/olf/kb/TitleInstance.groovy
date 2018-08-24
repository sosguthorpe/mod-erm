package org.olf.kb

import grails.gorm.MultiTenant
import javax.persistence.Transient
import org.olf.erm.AgreementLineItem

/**
 * mod-erm representation of a BIBFRAME instance
 */
public class TitleInstance implements MultiTenant<TitleInstance> {

  private static final String ENTITLEMENTS_QUERY = '''from AgreementLineItem as ali 
where exists ( select pci.id 
               from PackageContentItem as pci
               where pci.pti.titleInstance = :title 
               and ali.pkg = pci.pkg )
   or exists ( select pci.id 
               from PackageContentItem as pci
               where pci.pti.titleInstance = :title
               and ali.pci = pci )
   or exists ( select single_title from AgreementLineItem as single_title where single_title=ali and single_title.pti.titleInstance = :title )
'''

  String id
  // Title IN ORIGINAL LANGUAGE OF PUBLICATION
  String title

  static mapping = {
                   id column:'ti_id', generator: 'uuid', length:36
              version column:'ti_version'
                title column:'ti_title'
  }

  static constraints = {
          title(nullable:false, blank:false)
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
  List<AgreementLineItem> getEntitlements() {
    List<AgreementLineItem> result = AgreementLineItem.executeQuery('select ali '+ENTITLEMENTS_QUERY,[title:this],[max:20, offset:0]);
    return result;
  }
}
