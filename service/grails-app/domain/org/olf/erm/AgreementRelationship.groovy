package org.olf.erm

import com.k_int.web.toolkit.refdata.CategoryId
import com.k_int.web.toolkit.refdata.Defaults
import com.k_int.web.toolkit.refdata.RefdataValue

import grails.compiler.GrailsCompileStatic
import grails.gorm.MultiTenant
import groovy.util.logging.Slf4j

@Slf4j
@GrailsCompileStatic
public class AgreementRelationship implements MultiTenant<AgreementRelationship> {
  
  String id
  
  @CategoryId(defaultInternal=true)
  @Defaults(['Supersedes', 'Provides post-cancellation access for', 'Tracks demand-driven acquisitions for', 'Related to', 'Has backfile in'])
  RefdataValue type
  
  SubscriptionAgreement inward
  SubscriptionAgreement outward
  
  String note
  
  static mapping = {
    id column:'ar_id', generator: 'uuid2', length:36
    type column:'ar_type'
    inward column:'ar_inward_fk', lazy: false
    outward column:'ar_outward_fk', lazy: false
    note column:'ar_note', type: 'text'
  }
  
  static constraints = {
        type (nullable:false)
     outward (nullable:false)
      inward (nullable:false, validator: { SubscriptionAgreement sa, AgreementRelationship rel ->
        if (sa?.id) {
          if (rel?.outward?.id == sa.id) {
            return [ 'agreements.not.relate.to.self' ]
          }
        }
       })
        note (nullable:true, blank:false)
  }
}
