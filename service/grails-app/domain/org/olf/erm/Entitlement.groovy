package org.olf.erm

import java.time.Instant
import java.time.LocalDate

import javax.persistence.Transient

import org.hibernate.Hibernate
import org.olf.kb.ErmResource
import org.olf.kb.PackageContentItem
import org.olf.kb.Pkg
import org.olf.kb.PlatformTitleInstance

import com.k_int.okapi.remote_resources.OkapiLookup

import grails.databinding.BindInitializer
import grails.gorm.MultiTenant
import groovy.util.logging.Slf4j


/**
 * Entitlement (A description of a right to access a specific digital resource, which can be an 
 * title on a platform (But not listed in a package), a title named in a package, a full package of resources
 *
 * OFTEN attached to an agreement, but it's possible we know we have the right to access a resource
 * without perhaps knowing which agreement controls that right.
 *
 */
@Slf4j
public class Entitlement implements MultiTenant<Entitlement> {
  public static final Class<? extends ErmResource>[] ALLOWED_RESOURCES = [Pkg, PackageContentItem, PlatformTitleInstance] as Class[]

  String id

  ErmResource resource

  // The date ranges on which this line item is active. These date ranges allow the system to determine
  // what content is "Live" in an agreement. Content can be "Live" without being switched on, and 
  // vice versa. The dates indicate that we believe the agreement is in force for the items specified.
  // For Trials, these dates will indicate the dates of the trial, for live agreements the agreement item dates
  LocalDate activeFrom
  LocalDate activeTo

  LocalDate contentUpdated

  // Type - must be set to external for externally defined packages, null or local for things defined in the local DB
  String type

  // These three properties allow us to create an entitlement which is externally defined. An externally defined
  // entitlement does not link to a resource in the tenant database, but instead will use API calls to define its contents
  String authority
  
  @OkapiLookup(
    value = '${obj.authority?.toLowerCase() == "ekb-package" ? "/eholdings/packages" : "/eholdings/resources" }/${obj.reference}',
    converter = {
      // delegate, owner and thisObject should be the instance of Entitlement
      final Entitlement outerEntitlement = delegate
      
      log.debug "Converter called with delegate: ${outerEntitlement} and it: ${it}"
      
      final String theType = it.data?.attributes?.publicationType ?:
         it.data?.type?.replaceAll(/^\s*([\S])(.*?)s?\s*$/, {match, String firstChar, String nonePlural -> "${firstChar.toUpperCase()}${nonePlural}"})
      
      def map = [
        label: it.data?.attributes?.name,
        type: (theType),
        provider: it.data?.attributes?.providerName
      ]
      
      def count = it.data?.attributes?.titleCount
      if (count) {
        map.titleCount = count
      }
      
      // Merge external coverages.
      final boolean isPackage = theType?.toLowerCase() == 'package'
      
      log.debug "${isPackage ? 'Is' : 'Is not'} Package"
      outerEntitlement.metaClass.external_customCoverage = false
      
      final def custCoverage = it.data?.attributes?.getAt("customCoverage${isPackage ? '' : 's'}")
      
      log.debug "Custom Coverage: ${custCoverage}"
      if (custCoverage) {
        
        log.debug "Found custom coverage."
        // Simply ensure a collection.
        if (!(custCoverage instanceof Collection)) {
          log.debug "Found single custom coverage entry turn into a collection."
          custCoverage = [custCoverage]
          log.debug "...${custCoverage}"
        }
        
        custCoverage.each { Map <String, String> coverageEntry ->
          if (coverageEntry.beginCoverage) {
            outerEntitlement.coverage << new HoldingsCoverage (startDate: LocalDate.parse(coverageEntry.beginCoverage), endDate: coverageEntry.endCoverage ? LocalDate.parse(coverageEntry.endCoverage): null)
            outerEntitlement.metaClass.external_customCoverage = true
          }
        }
        
      } else if (!isPackage) {
        log.debug "Adding managed title coverages."
        it.data?.attributes?.managedCoverages?.each { Map <String, String> coverageEntry ->
          if (coverageEntry.beginCoverage) {
            outerEntitlement.coverage << new HoldingsCoverage (startDate: LocalDate.parse(coverageEntry.beginCoverage), endDate: coverageEntry.endCoverage ? LocalDate.parse(coverageEntry.endCoverage): null)
          }
        }
      }
      
      map
    }
  )
  String reference

  static belongsTo = [
    owner:SubscriptionAgreement
  ]

  static hasMany = [
    coverage: HoldingsCoverage,
     poLines: OrderLine,
  ]

  Set<HoldingsCoverage> coverage = []
  
  static mappedBy = [
    coverage: 'entitlement',
    poLines: 'owner'
  ]
  
  // We should 
  def beforeValidate() {
    this.type = this.type?.toLowerCase()
    this.authority = this.authority?.toUpperCase()
    
    if (this.type == 'external') {
      // Clear the coverage.
      this.coverage = []
    }
  }
  

  // Allow users to individually switch on or off this content item. If null, should default to the agreement
  // enabled setting. The activeFrom and activeTo dates determine if a content item is "live" or not. This flag
  // determines if we wish live content to be visible to patrons or not. Content can be "Live" but not enabled,
  // although that would be unusual.
  @BindInitializer({
    Boolean.TRUE // Default this value to true when binding.
  })
  Boolean enabled

  static mapping = {
                   id column: 'ent_id', generator: 'uuid2', length:36
              version column: 'ent_version'
                owner column: 'ent_owner_fk'
             resource column: 'ent_resource_fk'
                 type column: 'ent_type'
              enabled column: 'ent_enabled'
       contentUpdated column: 'ent_content_updated'
           activeFrom column: 'ent_active_from'
             activeTo column: 'ent_active_to'
            authority column: 'ent_authority'
            reference column: 'ent_reference'
             poLines cascade: 'all-delete-orphan'
             coverage cascade: 'all-delete-orphan'
  }

  static constraints = {
            owner(nullable:true,  blank:false)

          // Now that resources can be internally or externally defined, the internal resource link CAN be null,
          // but if it is, there should be authorty, and reference properties.
          resource (nullable:true, validator: { val, inst ->
            
            if (inst.type?.toLowerCase() == 'external') {
              // External resource should have null internal resource reference.
              return val != null ? ['externalEntitlement.resource.not.null'] : true
              
            } else if ( val ) {
              Class c = Hibernate.getClass(val)
              if (!Entitlement.ALLOWED_RESOURCES.contains(c)) {
                ['allowedTypes', "${c.name}", "entitlement", "resource"]
              }
            } else {
              
              // Resource is null but type is internal.
              return ['entitlement.resource.is.null']
            }
          })
          
          coverage (validator: HoldingsCoverage.STATEMENT_COLLECTION_VALIDATOR, sort:'startDate')

              type(nullable:true, blank:false)
           enabled(nullable:true, blank:false)
    contentUpdated(nullable:true, blank:false)
        activeFrom(nullable:true, blank:false)
          activeTo(nullable:true, blank:false)
          
         authority(nullable:true, blank:false, validator: { val, inst ->
            
            if (inst.type?.toLowerCase() == 'external') {
              // External resource should have authority.
              return val == null ? ['externalEntitlement.authority.is.null'] : true
              
            } else {
              // Resource is null but type is internal.
              return val != null ? ['externalEntitlement.authority.not.null'] : true
            }
          })
         
         reference (nullable:true, blank:false, validator: { val, inst ->
            
            if (inst.type?.toLowerCase() == 'external') {
              // External resource should have authority.
              return val == null ? ['externalEntitlement.reference.is.null'] : true
              
            } else {
              // Resource is null but type is internal.
              return val != null ?  ['externalEntitlement.reference.not.null'] : true
            }
          })
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

  @Transient
  public boolean getHaveAccess() {
    return haveAccessAsAt(LocalDate.now());
  }

  /**
   * If activeFrom <= date <= activeTo 
   */
  public boolean haveAccessAsAt(LocalDate point_in_time) {
    boolean result = false;
    if ( ( activeFrom != null ) && ( activeTo != null ) ) {
      result = ( ( activeFrom <= point_in_time ) && ( point_in_time <= activeTo ) )
    }
    else if ( activeFrom != null ) {
      result = ( activeFrom <= point_in_time )
    }
    else if ( activeTo != null ) {
      result = ( point_in_time <= activeTo )
    }
    else {
      // activeFrom and activeTo are both null - we assume this is perpetual then, so true
      return true;
    }
    return result;
  }
 
}
