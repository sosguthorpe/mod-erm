package org.olf.erm

import javax.persistence.Transient

import org.hibernate.Hibernate
import org.olf.kb.ErmResource
import org.olf.kb.PackageContentItem
import org.olf.kb.Pkg
import org.olf.kb.PlatformTitleInstance

import grails.databinding.BindInitializer
import grails.gorm.MultiTenant


/**
 * Entitlement (A description of a right to access a specific digital resource, which can be an 
 * title on a platform (But not listed in a package), a title named in a package, a full package of resources
 *
 * OFTEN attached to an agreement, but it's possible we know we have the right to access a resource
 * without perhaps knowing which agreement controls that right.
 *
 * An entitlement is for a list of content which is defined elsewhere - it may be defined in our local package
 * cache, it might be defined in eHoldings.
 *
 */
public class Entitlement implements MultiTenant<Entitlement> {

  String id

  String authority
  String reference
  String label

  // The date ranges on which this line item is active. These date ranges allow the system to determine
  // what content is "Live" in an agreement. Content can be "Live" without being switched on, and 
  // vice versa. The dates indicate that we believe the agreement is in force for the items specified.
  // For Trials, these dates will indicate the dates of the trial, for live agreements the agreement item dates
  Date activeFrom
  Date activeTo

  static belongsTo = [
    owner:SubscriptionAgreement
  ]

  static hasMany = [
    coverage: HoldingsCoverage,
    poLines: POLineProxy
  ]

  static mappedBy = [
    coverage: 'entitlement',
    poLines: 'owner'
  ]

  // Allow users to individually switch on or off this content item. If null, should default to the agreement
  // enabled setting. The activeFrom and activeTo dates determine if a content item is "live" or not. This flag
  // determines if we wish live content to be visible to patrons or not. Content can be "Live" but not enabled,
  // although that would be unusual.
  @BindInitializer({
    Boolean.TRUE // Default this value to true when binding.
  })
  Boolean enabled
  

  static mapping = {
                   id column: 'ent_id', generator: 'uuid', length:36
              version column: 'ent_version'
                owner column: 'ent_owner_fk'
            authority column: 'ent_authority'
            reference column: 'ent_reference'
                label column: 'ent_label'
           activeFrom column: 'ent_active_from'
             activeTo column: 'ent_active_to'
  }


  static constraints = {
        owner(nullable:true,  blank:false)
    authority(nullable:true,  blank:false)
    reference(nullable:true,  blank:false)
        label(nullable:true,  blank:false)
      enabled(nullable:true,  blank:false)
   activeFrom(nullable:true,  blank:false)
     activeTo(nullable:true,  blank:false)
  }
  
  @Transient
  public String getExplanation() {
    
    String result = null
    
    if (resource) {
      // Get the class using the hibernate helper so we can
      // be sure we have the target class and not a proxy wrapper.
      Class c = Hibernate.getClass(resource)
      switch (c) {
        case Pkg:
          result = 'Agreement includes a package containing this item'
          break
        case PlatformTitleInstance:
          result = 'Agremment includes this title directly'
          break
        case PackageContentItem:
          result = 'Agreement includes this item from a package specifically'
          break
      }
    }
    result
  }

}
