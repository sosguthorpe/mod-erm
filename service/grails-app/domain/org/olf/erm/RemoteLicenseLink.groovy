package org.olf.erm;

import com.k_int.okapi.remote_resources.RemoteOkapiLink
import com.k_int.web.toolkit.refdata.Defaults
import com.k_int.web.toolkit.refdata.RefdataValue

import grails.gorm.MultiTenant

public class RemoteLicenseLink extends RemoteOkapiLink implements MultiTenant<RemoteLicenseLink> {
  
  @Defaults(['Controlling', 'Future', 'Historical'])
  RefdataValue status
  String note
  
  
  static belongsTo = [ owner: SubscriptionAgreement ]
  
  static mapping = {
         status column:'rll_status'
           note column:'rll_note', type: 'text'
          owner column:'rll_owner'
  }
  
  static constraints = {
               status (nullable:false)
                 note (nullable:true, blank:false)
  }

  @Override
  public String remoteUri() {
    return 'licenses/licenses';
  }
}
