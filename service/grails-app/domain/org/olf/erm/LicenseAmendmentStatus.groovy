package org.olf.erm;

import com.k_int.okapi.remote_resources.RemoteOkapiLink
import com.k_int.web.toolkit.domain.traits.Clonable
import com.k_int.web.toolkit.refdata.Defaults
import com.k_int.web.toolkit.refdata.RefdataValue

import grails.compiler.GrailsCompileStatic
import grails.gorm.MultiTenant

@GrailsCompileStatic
public class LicenseAmendmentStatus implements MultiTenant<LicenseAmendmentStatus>, Clonable<LicenseAmendmentStatus>  {
  
  String id
  
  String amendmentId
  
  @Defaults(['Current', 'Future', 'Historical', 'Does not apply'])
  RefdataValue status
  String note
  RemoteLicenseLink owner
  
  static belongsTo = [ owner: RemoteLicenseLink ]
  
  static mapping = {
             id column:'las_id', generator: 'uuid2', length:36
    amendmentId column:'las_amendment_id', length:36
         status column:'las_status'
           note column:'las_note', type: 'text'
          owner column:'las_owner'
  }
  
  static constraints = {
    amendmentId (nullable:false, blank:false)
          owner (nullable:false)
         status (nullable:false)
           note (nullable:true, blank:false)
  }
  
  /**
   * Need to resolve the conflict manually and add the call to the clonable method here.
   */
  @Override
  public LicenseAmendmentStatus clone () {
    Clonable.super.clone()
  }
}
