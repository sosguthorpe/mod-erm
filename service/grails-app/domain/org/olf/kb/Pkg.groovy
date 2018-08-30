package org.olf.kb

import grails.gorm.MultiTenant
import javax.persistence.Transient


/**
 * mod-erm representation of a package
 */
public class Pkg implements MultiTenant<Pkg> {

  String id
  String name
  String source
  String reference
  RemoteKB remoteKb
  Platform nominalPlatform

  static mapping = {
                table 'package'
                   id column:'pkg_id', generator: 'uuid', length:36
              version column:'pkg_version'
                 name column:'pkg_name'
               source column:'pkg_source'
            reference column:'pkg_reference'
             remoteKb column:'pkg_remote_kb'
      nominalPlatform column:'pkg_nominal_platform_fk'
  }

  static constraints = {
               name(nullable:false, blank:false)
             source(nullable:false, blank:false)
          reference(nullable:false, blank:false)
           remoteKb(nullable:true, blank:false)
    nominalPlatform(nullable:true, blank:false)
  }


  @Transient
  public long getResourceCount() {
    return 0;
  }

}
