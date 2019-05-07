package org.olf.erm

import org.olf.general.Org
import org.olf.general.DocumentAttachment
import com.k_int.web.toolkit.refdata.RefdataValue
import com.k_int.web.toolkit.refdata.CategoryId
import com.k_int.web.toolkit.refdata.Defaults
import com.k_int.web.toolkit.databinding.BindImmutably
import com.k_int.web.toolkit.tags.Tag
import grails.gorm.MultiTenant

/**
 * Subscription agreement - object holding details about an SA connecting a resource list (Composed Of packages and platform-titles).
 */
public class SubscriptionAgreement implements MultiTenant<SubscriptionAgreement> {
  String description
  String id
  String name
  String localReference
  String vendorReference
  String attachedLicenceId
  String licenseNote
  Date cancellationDeadline
  Date startDate
  Date endDate
  Date renewalDate
  Date nextReviewDate

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

  static hasMany = [
                  items: Entitlement,
           historyLines: SAEventHistory,
               contacts: InternalContact,
                   tags: Tag,
                   orgs: SubscriptionAgreementOrg,
    externalLicenseDocs: DocumentAttachment,
                   docs: DocumentAttachment,
         linkedLicenses: RemoteLicenseLink
  ]

  static mappedBy = [
    items: 'owner',
    historyLines: 'owner',
    contacts: 'owner',
    orgs: 'owner',
    linkedLicenses: 'owner'
  ]

  static mapping = {
             description column:'sa_description'
                      id column:'sa_id', generator: 'uuid2', length:36
                 version column:'sa_version'
                    name column:'sa_name'
          localReference column:'sa_local_reference'
         vendorReference column:'sa_vendor_reference'
    cancellationDeadline column:'sa_cancellation_deadline'
               startDate column:'sa_start_date'
                 endDate column:'sa_end_date'
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
                    orgs cascade: 'all-delete-orphan'
                    docs cascade: 'all-delete-orphan'
     externalLicenseDocs cascade: 'all-delete-orphan',  joinTable: [name: 'subscription_agreement_ext_lic_doc', key: 'saeld_sa_fk', column: 'saeld_da_fk']
          linkedLicenses cascade: 'all-delete-orphan'
  }

  static constraints = {
                    name(nullable:false, blank:false)
          localReference(nullable:true, blank:false)
         vendorReference(nullable:true, blank:false)
    cancellationDeadline(nullable:true, blank:false)
               startDate(nullable:true, blank:false)
                 endDate(nullable:true, blank:false)
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
              
          linkedLicenses(validator: { Collection<RemoteLicenseLink> license_links ->
            
            int controlling_count = ((license_links?.findAll({ RemoteLicenseLink license -> license.status?.value == 'controlling' })?.size()) ?: 0)
            ( controlling_count > 1 ? [ 'only.one.controlling.license' ] : true )
          })
  }
}
