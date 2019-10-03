package org.olf.erm

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime

import org.grails.web.servlet.mvc.GrailsWebRequest
import org.grails.web.util.WebUtils
import org.olf.general.DocumentAttachment
import org.olf.general.Org
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.servlet.support.RequestContextUtils
import com.k_int.web.toolkit.refdata.CategoryId
import com.k_int.web.toolkit.refdata.Defaults
import com.k_int.web.toolkit.refdata.RefdataValue
import com.k_int.web.toolkit.tags.Tag

import grails.gorm.MultiTenant
import groovy.util.logging.Slf4j

/**
 * Subscription agreement - object holding details about an SA connecting a resource list (Composed Of packages and platform-titles).
 */
@Slf4j
public class SubscriptionAgreement implements MultiTenant<SubscriptionAgreement> {
  
  static transients = ['cancellationDeadline', 'startDate', 'endDate', 'currentPeriod']
  
  String description
  String id
  String name
  String localReference
  String vendorReference
  String attachedLicenceId
  String licenseNote
  LocalDate renewalDate
  LocalDate nextReviewDate

  /**
   * By default the RefdataCategory would be generated from the concatenation
   * of the class name and the property name. So the below property of agreementType
   * would result in a category named SubscriptionAgreement.AgreementType
   *
   * If we wanted to share a category across multiple Classes (like a global "Yes_No"),
   * or just wanted to specify the category, we can use the \@CategoryId annotation.
   *
   * @CategoryId('AgreementType') - Would create a category named 'AgreementType' for values stored here.
   */
  @Defaults(['Draft', 'Trial', 'Current']) // Defaults to create for this property.
  RefdataValue agreementType

  @Defaults(['Definitely renew', 'For review', 'Definitely cancel'])
  RefdataValue renewalPriority

  @Defaults(['Draft', 'Requested', 'In negotiation', 'Rejected', 'Active', 'Cancelled'])
  RefdataValue agreementStatus

  @CategoryId('Global.Yes_No')
  @Defaults(['Yes', 'No'])
  RefdataValue isPerpetual

  @CategoryId('Global.Yes_No')
  @Defaults(['Yes', 'No'])
  RefdataValue contentReviewNeeded

  Boolean enabled

  Org vendor

//  @BindImmutably
  Set<Entitlement> items
  
  private Period currentPeriod
  Period getCurrentPeriod () {
    log.debug "Get current period"
    if (!currentPeriod) {
      LocalDate ld
      
      // Use the request if possible
      RequestAttributes attributes = RequestContextHolder.getRequestAttributes()
      if(attributes && attributes instanceof GrailsWebRequest) {
        
        GrailsWebRequest gwr = attributes as GrailsWebRequest
        
        log.debug "Is within a request context"
        TimeZone tz = RequestContextUtils.getTimeZone(gwr.currentRequest) ?: TimeZone.getDefault()
        
        log.debug "Using TZ ${tz}"
        ZonedDateTime zdt = ZonedDateTime.ofInstant(Instant.now(), tz.toZoneId())
        
        log.debug "Now in ${tz} is ${zdt}"
        ld = zdt.toLocalDate()
        
        log.debug "LocalDate of ${ld} extracted for query"
      } else {
        log.debug "Is not within a request context, using default TZ (${TimeZone.getDefault()})"
        ld = LocalDate.now()
      }
      
      // Create the query
      def query = Period.where {
         (owner.id == "${this.id}") &&
         (startDate == null || startDate <= ld) && 
           (endDate == null || endDate >= ld)
      }
      
      // Execute.
      currentPeriod = query.find()
    }
    currentPeriod
  }
  
  LocalDate getCancellationDeadline() {
    currentPeriod?.cancellationDeadline
  }
  
  LocalDate getStartDate() {
    if (currentPeriod) {
      return currentPeriod.startDate
    }
    def query = Period.where {
      (owner.id == "${this.id}") && (startDate == null || startDate == min(startDate).of { owner.id == "${this.id}" })
    }
    Period earliest = query.list(sort: 'startDate', max: 1)?.getAt(0)
    
    earliest.startDate
  }
  
  LocalDate getEndDate() {
    if (currentPeriod) {
      return currentPeriod.endDate
    }
    def query = Period.where {
      (owner.id == "${this.id}") && (endDate == null || endDate == max(endDate).of { owner.id == "${this.id}" })
    }
    Period latest = query.list(sort: 'endDate', max: 1)?.getAt(0)
    
    latest.endDate
  }
  
  static hasMany = [
                  items: Entitlement,
           historyLines: SAEventHistory,
               contacts: InternalContact,
                   tags: Tag,
                   orgs: SubscriptionAgreementOrg,
    externalLicenseDocs: DocumentAttachment,
                   docs: DocumentAttachment,
         linkedLicenses: RemoteLicenseLink,
      supplementaryDocs: DocumentAttachment,
     usageDataProviders: UsageDataProvider,
                periods: Period
  ]

  static mappedBy = [
    items: 'owner',
    historyLines: 'owner',
    contacts: 'owner',
    orgs: 'owner',
    linkedLicenses: 'owner',
    usageDataProviders: 'owner',
    periods: 'owner'
  ]

  static mapping = {
             description column:'sa_description', type: 'text'
                      id column:'sa_id', generator: 'uuid2', length:36
                 version column:'sa_version'
                    name column:'sa_name'
          localReference column:'sa_local_reference'
         vendorReference column:'sa_vendor_reference'
             renewalDate column:'sa_renewal_date'
          nextReviewDate column:'sa_next_review_date'
           agreementType column:'sa_agreement_type'
         renewalPriority column:'sa_renewal_priority'
         agreementStatus column:'sa_agreement_status'
             isPerpetual column:'sa_is_perpetual'
     contentReviewNeeded column:'sa_content_review_needed'
                 enabled column:'sa_enabled'
                  vendor column:'sa_vendor_fk'
       attachedLicenceId column:'sa_licence_fk'
	   		     licenseNote column:'sa_license_note'
                   items cascade: 'all-delete-orphan'
                contacts cascade: 'all-delete-orphan'
            historyLines cascade: 'all-delete-orphan'
                    tags cascade: 'save-update'
                 periods cascade: 'all-delete-orphan'
                    orgs cascade: 'all-delete-orphan'
                    docs cascade: 'all-delete-orphan'
     externalLicenseDocs cascade: 'all-delete-orphan',  joinTable: [name: 'subscription_agreement_ext_lic_doc', key: 'saeld_sa_fk', column: 'saeld_da_fk']
          linkedLicenses cascade: 'all-delete-orphan'
       supplementaryDocs cascade: 'all-delete-orphan', joinTable: [name: 'subscription_agreement_supp_doc', key: 'sasd_sa_fk', column: 'sasd_da_fk']
      usageDataProviders cascade: 'all-delete-orphan'
  }

  static constraints = {
                    name(nullable:false, blank:false)
          localReference(nullable:true, blank:false)
         vendorReference(nullable:true, blank:false)
             renewalDate(nullable:true, blank:false)
          nextReviewDate(nullable:true, blank:false)
           agreementType(nullable:true, blank:false)
         renewalPriority(nullable:true, blank:false)
         agreementStatus(nullable:true, blank:false)
             isPerpetual(nullable:true, blank:false)
     contentReviewNeeded(nullable:true, blank:false)
                 enabled(nullable:true, blank:false)
             description(nullable:true, blank:false)
                  vendor(nullable:true, blank:false)
       attachedLicenceId(nullable:true, blank:false)
             licenseNote(nullable:true, blank:false)
                 periods(minSize: 1, validator:Period.PERIOD_COLLECTION_VALIDATOR, sort:'startDate')
              
          linkedLicenses(validator: { Collection<RemoteLicenseLink> license_links ->
            
            int controlling_count = ((license_links?.findAll({ RemoteLicenseLink license -> license.status?.value == 'controlling' })?.size()) ?: 0)
            ( controlling_count > 1 ? [ 'only.one.controlling.license' ] : true )
          })
  }
}
