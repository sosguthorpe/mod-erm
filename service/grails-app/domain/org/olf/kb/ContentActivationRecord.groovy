package org.olf.kb

import grails.gorm.MultiTenant
import org.olf.kb.PlatformTitleInstance;

/**
 * A Proxy object that holds information about the Purchase order line in an external acquisitions system
 *
 */
public class ContentActivationRecord implements MultiTenant<ContentActivationRecord> {

  String id
  Date dateActivation
  Date dateDeactivation
  RemoteKB target
  PlatformTitleInstance pti

  static belongsTo = [
  ]

  static hasMany = [
  ]

  static mappedBy = [
  ]

  static mapping = {
           table 'content_activation_record'
                  id column: 'car_id', generator: 'uuid2', length:36
             version column: 'car_version'
      dateActivation column: 'car_date_activation'
    dateDeactivation column: 'car_date_deactivation'
              target column: 'car_target_kb_fk'
                 pti column: 'car_pti_fk'
  }

  static constraints = {
        dateDeactivation(nullable:true, blank:false)
  }

}
