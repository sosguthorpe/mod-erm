package org.olf.erm
import org.olf.general.Org
import com.k_int.web.toolkit.domain.traits.Clonable
import com.k_int.web.toolkit.refdata.CategoryId
import com.k_int.web.toolkit.refdata.Defaults
import com.k_int.web.toolkit.refdata.RefdataValue

import grails.compiler.GrailsCompileStatic
import grails.gorm.MultiTenant


/**
 * Link a subscription agreement with an org and attach a role
 */
@GrailsCompileStatic
public class SubscriptionAgreementOrg implements MultiTenant<SubscriptionAgreementOrg>, Clonable<SubscriptionAgreementOrg> {
  
  String id
  Org org

  @CategoryId(defaultInternal=true)
  @Defaults(['Content Provider', 'Subscription Agent', 'Vendor'])
  RefdataValue role
  String note
  
  static belongsTo = [
    owner: SubscriptionAgreement
  ]

    static mapping = {
                   id column: 'sao_id', generator: 'uuid2', length:36
              version column: 'sao_version'
                owner column: 'sao_owner_fk'
                  org column: 'sao_org_fk'
                 role column: 'sao_role'
                 note column: 'sao_note', type: 'text'
  }

  static constraints = {
    owner(nullable:false, blank:false);
    org(nullable:true, blank:false);
    role(nullable:true, blank:false);
    note(nullable:true, blank:false);
  }
  
  /**
   * Need to resolve the conflict manually and add the call to the clonable method here. 
   */
  @Override
  public SubscriptionAgreementOrg clone () {
    Clonable.super.clone()
  }
}
