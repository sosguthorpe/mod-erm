package org.olf.erm
import org.olf.general.Org

import com.k_int.web.toolkit.refdata.Defaults
import com.k_int.web.toolkit.refdata.RefdataValue

import grails.gorm.MultiTenant


/**
 * Link a subscription agreement with an org and attach a role
 */
public class SubscriptionAgreementOrg implements MultiTenant<SubscriptionAgreementOrg>{
  
  String id
  Org org

  @Defaults(['Content Provider', 'Subscription Agent', 'Vendor'])
  RefdataValue role
  
  static belongsTo = [
    owner: SubscriptionAgreement
  ]

    static mapping = {
                   id column: 'sao_id', generator: 'uuid2', length:36
              version column: 'sao_version'
                owner column: 'sao_owner_fk'
                  org column: 'sao_org_fk'
                 role column: 'sao_role'
  }

  static constraints = {
    owner(nullable:false, blank:false);
    org(nullable:true, blank:false);
    role(nullable:true, blank:false);
  }
}
