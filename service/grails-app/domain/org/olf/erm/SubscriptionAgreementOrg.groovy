package org.olf.erm
import org.olf.general.Org
import org.olf.general.RefdataValue
import org.olf.general.refdata.Defaults
import grails.gorm.MultiTenant
import org.olf.general.Org


/**
 * Link a subscription agreement with an org and attach a role
 */
public class SubscriptionAgreementOrg implements MultiTenant<SubscriptionAgreementOrg>{
  
  String id
  Org org

  @Defaults(['Licensor', 'Licensee', 'Content Provider', 'Licensing Consortium', 'Negotiator', 'Subscriber', 'Provider', 'Subscription Agent', 'Subscription Consortia', 'Package Consortia'])
  RefdataValue role
  
  static belongsTo = [
    owner: SubscriptionAgreement
  ]

    static mapping = {
                   id column: 'sao_id', generator: 'uuid', length:36
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
