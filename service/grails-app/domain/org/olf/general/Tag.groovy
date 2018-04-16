package org.olf.general

import grails.gorm.MultiTenant

class Tag implements MultiTenant<Tag> {

  String id
  String ownerDomain
  String ownerId
  String value

  static belongsTo = [
  ]
  
  static hasMany = [
  ]

  static mapping = {
              id column: 'tag_id', generator: 'uuid', length:36
         version column: 'tag_version'
     ownerDomain column: 'tag_owner_domain'
         ownerId column: 'tag_owner_id'
           value column: 'tag_value'
  }

  static constraints = {
       ownerDomain(nullable: true, blank: false)
           ownerId(nullable: true, blank: false)
             value(nullable: true, blank: false)
  }

}
