package org.olf.kb

import grails.gorm.MultiTenant
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
  Boolean active
  Boolean supportsHarvesting

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
  }


  public String toString() {
    return "RemoteKB ${name} - ${type}/${uri}/${cursor}".toString()
  }

}
