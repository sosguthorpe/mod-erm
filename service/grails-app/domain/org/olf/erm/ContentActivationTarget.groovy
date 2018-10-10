package org.olf.erm

import grails.gorm.MultiTenant


/**
 * A Proxy object that holds information about the Purchase order line in an external acquisitions system
 *
 */
public class ContentActivationTarget implements MultiTenant<ContentActivationTarget> {

  String id
  boolean enabled=false;

  static belongsTo = [
  ]

  static hasMany = [
  ]

  static mappedBy = [
  ]

  static mapping = {
           table 'content_activation_target'
                   id column: 'cat_id', generator: 'uuid', length:36
              version column: 'cat_version'
              enabled column: 'cat_enabled'
  }

  static constraints = {
           enabled(nullable:false, blank:false)
  }

}
