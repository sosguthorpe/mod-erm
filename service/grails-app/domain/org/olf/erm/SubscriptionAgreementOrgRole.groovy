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
public class SubscriptionAgreementOrgRole implements MultiTenant<SubscriptionAgreementOrgRole>, Clonable<SubscriptionAgreementOrgRole> {
  
  String id

  @CategoryId(value='SubscriptionAgreementOrg.Role', defaultInternal=false) // make an entry in refdata_category with the same description as it was before the restructuring of sa org roles
  @Defaults(['Content provider']) // we need at least one default value as the category isn't created otherwise
  RefdataValue role
  String note
  
  static belongsTo = [
    owner: SubscriptionAgreementOrg
  ]

    static mapping = {
                   id column: 'saor_id', generator: 'uuid2', length:36
              version column: 'saor_version'
                owner column: 'saor_owner_fk'
                 role column: 'saor_role_fk'
                 note column: 'saor_note', type: 'text'
  }

  static constraints = {
    owner(nullable:false, blank:false);
    role(nullable:false, blank:false);
    note(nullable:true, blank:false);
  }
  
  /**
   * Need to resolve the conflict manually and add the call to the clonable method here. 
   */
  @Override
  public SubscriptionAgreementOrgRole clone () {
    Clonable.super.clone()
  }
}
