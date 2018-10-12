package org.olf.kb

import grails.gorm.MultiTenant

/**
 * KB or Knowledgebase is a loaded term in ERM contexts. Originally derived from the more generic computing
 * use of a repository of facts used to drive an inference engine, and the problem space being
 * "Can I access Volume 1 Issue 5 of X on platform Y". The idea was that instead of storring individual issue records (Or article records)
 * a KB would infer from coverage if a user had access to a particular desired content item.
 * 
 * In this context a "RemoteKB" is any repository that makes assertions about titles, platforms, packages and coverage. We also extend
 * this model to include the concept of "Activation" - recording in a KB that we have "Switched on" a particular content item. Recording
 * activation is separate to "Why" we activated something - We may have several agreements that entitle us to access Vol 1 Issue 1 - present of
 * Nature on the Platform Nature.com - But we only activate that title once in each resolver or KB.
 *
 */
public class RemoteKB implements MultiTenant<RemoteKB> {

  String id
  String name
  String type // this is the name of a spring bean which will act 
  String cursor // This may be a datetimestring, transaction or other service specific means of determining where we are up to
  String uri
  String listPrefix
  String fullPrefix
  String principal
  String credentials
  Long rectype  // 1-PACKAGE

  // Harvesting role
  /** Does this remote KB support harvesting */
  Boolean supportsHarvesting
  /** If supported, is harvesting from this remote KB activated */
  Boolean active

  // Content activation
  /** Does this remote KB support content activation */
  Boolean activationSupported
  /** If supported, is recording actication in this remote KB enabled */
  Boolean activationEnabled

  static mapping = {
                     id column:'rkb_id', generator: 'uuid', length:36
                version column:'rkb_version'
                   name column:'rkb_name'
                 cursor column:'rkb_cursor'
                    uri column:'rkb_uri'
             fullPrefix column:'rkb_full_prefix'
             listPrefix column:'rkb_list_prefix'
                   type column:'rkb_type'
              principal column:'rkb_principal'
            credentials column:'rkb_creds'
                rectype column:'rkb_rectype'
                 active column:'rkb_active'
     supportsHarvesting column:'rkb_supports_harvesting'
      activationEnabled column:'rkb_activation_enabled'
    activationSupported column:'rkb_activation_supported'
  }

  static constraints = {
                   name(nullable:false, blank:false)
                 cursor(nullable:true, blank:false)
                    uri(nullable:true, blank:false)
                   type(nullable:true, blank:false)
             fullPrefix(nullable:true, blank:false)
             listPrefix(nullable:true, blank:false)
              principal(nullable:true, blank:false)
            credentials(nullable:true, blank:false)
                 active(nullable:true, blank:false)
     supportsHarvesting(nullable:true, blank:false)
      activationEnabled(nullable:true, blank:false)
    activationSupported(nullable:true, blank:false)
  }


  public String toString() {
    return "RemoteKB ${name} - ${type}/${uri}/${cursor}".toString()
  }

}
