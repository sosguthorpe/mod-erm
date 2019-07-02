package org.olf

import com.k_int.okapi.OkapiTenantAwareController

import grails.gorm.multitenancy.CurrentTenant
import groovy.util.logging.Slf4j 
import grails.converters.JSON
import org.olf.kb.TitleInstance


/**
 * The ExportController provides endpoints for exporting content in specific formats
 * harvested by the erm module.
 */
@Slf4j
@CurrentTenant
class ExportController extends OkapiTenantAwareController<TitleInstance> {

  

  ExportController()  {
	  super(TitleInstance, true)
  }

  public static final String TENANT = "X-Okapi-Tenant";
  
  ExportService exportService
	
  /**
   * main index method (by default, return titles as json)
   */
  def index() {
      log.debug("ExportController::index");
	  def results = exportService.titles()
	  respond results 
  }

  /*
   * kbart export (placeholder)
   */
  def kbart() {
      log.debug("ExportController::kbart"); 
	  def result = ['ExportController':'kbart']
	  respond result
  }

}

