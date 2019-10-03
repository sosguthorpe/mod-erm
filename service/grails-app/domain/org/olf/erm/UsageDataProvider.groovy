package org.olf.erm;

import com.k_int.okapi.remote_resources.RemoteOkapiLink
import com.k_int.web.toolkit.refdata.Defaults
import com.k_int.web.toolkit.refdata.RefdataValue

import grails.gorm.MultiTenant

public class UsageDataProvider extends RemoteOkapiLink implements MultiTenant<UsageDataProvider> {

	String usageDataProviderNote
  
	static belongsTo = [ owner: SubscriptionAgreement ]
  
	static mapping = {
	                  owner column: 'udp_owner_fk'
    usageDataProviderNote column: 'udp_note', type: 'text'
                  
	}
  
	static constraints = {
		owner(nullable:false, blank:false)
        usageDataProviderNote(nullable:true, blank:false)
	}

	@Override
	public String remoteUri() {
		return 'usage-data-providers';
	}
}
