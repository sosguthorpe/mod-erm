package org.olf

import org.olf.kb.TitleInstance

import com.k_int.okapi.OkapiTenantAwareController

import grails.gorm.multitenancy.CurrentTenant
import groovy.util.logging.Slf4j

@Slf4j
@CurrentTenant
class TitleController extends OkapiTenantAwareController<TitleInstance>  {

  TitleController() {
    super(TitleInstance, true)
  }
  
  def entitled() {
    respond doTheLookup (TitleInstance.entitled)
  }
}

