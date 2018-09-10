package org.olf.kb

import grails.gorm.MultiTenant
import javax.persistence.Transient
import org.olf.general.Org


/**
 * mod-erm representation of a package
 */
public class Pkg implements MultiTenant<Pkg> {

  String id // Our local ID - must be unique over all KBs
  String name
  String source
  String reference  // Reference contains the KBs authoritiative ID for this package - Reference should be unique within KB
  RemoteKB remoteKb
  Platform nominalPlatform
  Org vendor

  static mapping = {
                table 'package'
                   id column:'pkg_id', generator: 'uuid', length:36
              version column:'pkg_version'
                 name column:'pkg_name'
               source column:'pkg_source'
            reference column:'pkg_reference'
             remoteKb column:'pkg_remote_kb'
      nominalPlatform column:'pkg_nominal_platform_fk'
               vendor column:'pkg_vendor_fk'
  }

  static constraints = {
               name(nullable:false, blank:false)
             source(nullable:false, blank:false)
          reference(nullable:false, blank:false)
           remoteKb(nullable:true, blank:false)
    nominalPlatform(nullable:true, blank:false)
             vendor(nullable:true, blank:false)
  }


  @Transient
  public long getResourceCount() {
    return 0;
  }

}
