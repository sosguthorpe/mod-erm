package org.olf.erm

import grails.gorm.MultiTenant


/**
 * A Proxy object that holds information about the Purchase order line in an external acquisitions system
 *
 */
public class POLineProxy implements MultiTenant<POLineProxy> {

  String id
  Entitlement owner
  String poLineId
  String label

  static belongsTo = [
  ]

  static hasMany = [
  ]

  static mappedBy = [
  ]

  static mapping = {
           table 'po_line_proxy'
                   id column: 'pop_id', generator: 'uuid2', length:36
              version column: 'pop_version'
                owner column: 'pop_owner'
             poLineId column: 'pop_po_line_id'
                label column: 'pop_label'
  }

  static constraints = {
           owner(nullable:false, blank:false)
        poLineId(nullable:false, blank:false)
           label(nullable:true, blank:false)

  }

}
