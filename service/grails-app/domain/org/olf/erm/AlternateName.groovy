package org.olf.erm;

import com.k_int.web.toolkit.domain.traits.Clonable

import grails.gorm.MultiTenant

public class AlternateName implements MultiTenant<AlternateName>, Clonable<AlternateName> {
	
	String id
    String name
	
	static belongsTo = [ owner: SubscriptionAgreement ]
  
	  static mapping = {
      // table 'alternate_name'
                        id column: 'an_id', generator: 'uuid2', length:36
                   version column: 'an_version'
                      name column: 'an_name'
                     owner column: 'an_owner_fk'  
	}
  
	static constraints = {
		 owner(nullable:false, blank:false);
	   name(nullable:false, blank:false);
	}
  
  /**
   * Need to resolve the conflict manually and add the call to the clonable method here.
   */
  @Override
  public AlternateName clone () {
    Clonable.super.clone()
  }
}
