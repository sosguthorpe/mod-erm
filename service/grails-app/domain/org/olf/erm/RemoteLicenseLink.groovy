package org.olf.erm;

import com.k_int.okapi.remote_resources.RemoteOkapiLink
import com.k_int.web.toolkit.databinding.BindImmutably
import com.k_int.web.toolkit.domain.traits.Clonable
import com.k_int.web.toolkit.refdata.CategoryId
import com.k_int.web.toolkit.refdata.Defaults
import com.k_int.web.toolkit.refdata.RefdataValue

import grails.compiler.GrailsCompileStatic
import grails.gorm.MultiTenant

@GrailsCompileStatic
public class RemoteLicenseLink extends RemoteOkapiLink implements MultiTenant<RemoteLicenseLink>, Clonable<RemoteLicenseLink> {
  
  static transients = ['applicableAmendmentParams']
  static copyByCloning = ['amendments']
  
  @CategoryId(defaultInternal=true)
  @Defaults(['Controlling', 'Future', 'Historical'])
  RefdataValue status
  String note
  
  @BindImmutably
  Set<LicenseAmendmentStatus> amendments = []
  
  static belongsTo = [ owner: SubscriptionAgreement ]
  
  static hasMany = [
    amendments: LicenseAmendmentStatus
  ]
  
  static mappedBy = [amendments: 'owner']
  
  static mapping = {
         status column:'rll_status'
           note column:'rll_note', type: 'text'
          owner column:'rll_owner'
     amendments cascade: 'all-delete-orphan'
  }
  
  static constraints = {
               status (nullable:false)
                 note (nullable:true, blank:false)
  }
  
  private String getApplicableAmendmentParams() {
    amendments?.findResults({it.status.value == 'current' ? 'applyAmendment=' + it.amendmentId : '' })?.join('&')
  }

  @Override
  public final def remoteUri() {
    {->
      def amends = applicableAmendmentParams
      "licenses/licenses/${remoteId}${amends ? '?' + amends : ''}"
      
    }
  }
  
  /**
   * Need to resolve the conflict manually and add the call to the clonable method here.
   */
  @Override
  public RemoteLicenseLink clone () {
    Clonable.super.clone()
  }
}
