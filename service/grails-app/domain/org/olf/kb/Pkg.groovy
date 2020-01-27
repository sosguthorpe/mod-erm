package org.olf.kb

import javax.persistence.Transient

import org.olf.general.Org

import com.k_int.web.toolkit.refdata.Defaults
import com.k_int.web.toolkit.refdata.RefdataValue

import grails.gorm.MultiTenant

/**
 * mod-erm representation of a package
 */
public class Pkg extends ErmResource implements MultiTenant<Pkg> {
  String source
  String reference  // Reference contains the KBs authoritative ID for this package - Reference should be unique within KB
  RemoteKB remoteKb
  Platform nominalPlatform
  Org vendor
  
  // Declaring this here will provide defaults for the type defined in ErmResource but not create
  // a subclass specific column
  @Defaults(['Aggregated Full Text', 'Abstracts and Index', 'Package'])
  RefdataValue type

  static hasMany = [
    contentItems: PackageContentItem
    // tags: KIWTTag
  ]

  static mappedBy = [ 
    contentItems: 'pkg'
  ]

  static mapping = {
                table 'package'
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
    long num_items = PackageContentItem.countByPkg (this)
    return num_items;
  }

}
