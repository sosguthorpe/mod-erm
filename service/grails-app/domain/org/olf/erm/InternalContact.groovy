package org.olf.erm
import com.k_int.web.toolkit.refdata.Defaults
import com.k_int.web.toolkit.refdata.RefdataValue

import grails.gorm.MultiTenant

public class InternalContact implements MultiTenant<InternalContact>{
  
  String id
  String user
  @Defaults(['Agreement owner', 'Authorized signatory', 'ERM librarian', 'Subject specialist']) // Defaults to create for this property.
  RefdataValue role
  
  static belongsTo = [
    owner: SubscriptionAgreement
  ]

    static mapping = {
//    table 'internal_contact'
                   id column: 'ic_id', generator: 'uuid2', length:36
              version column: 'ic_version'
                owner column: 'ic_owner_fk'
                 user column: 'ic_user_fk'
                 role column: 'ic_role'
  }

  static constraints = {
       owner(nullable:false, blank:false);
        user(nullable:true, blank:false);
        role(nullable:true, blank:false);
  }
}
