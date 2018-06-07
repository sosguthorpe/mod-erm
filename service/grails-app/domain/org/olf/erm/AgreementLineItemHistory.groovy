package org.olf.erm

import grails.gorm.MultiTenant
import org.olf.general.RefdataValue
import org.olf.kb.Package
import org.olf.kb.PackageContentItem
import org.olf.kb.PlatformTitleInstance
import javax.persistence.Transient


/**
 * AgreementLineItemHistory - an event relating to an agreement line item event. Used to provide users a way to roll back
 * events that they do not agree with.
 */
public class AgreementLineItemHistory implements MultiTenant<AgreementLineItemHistory> {

  String id

  static belongsTo = [
  ]

  static hasMany = [
  ]

  static mappedBy = [
  ]

  static mapping = {
                   id column: 'alih_id', generator: 'uuid', length:36
              version column: 'alih_version'
  }


  static constraints = {
  }

}
