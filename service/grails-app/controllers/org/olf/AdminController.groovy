package org.olf

import grails.gorm.multitenancy.CurrentTenant
import groovy.json.JsonSlurper
import grails.converters.JSON

@CurrentTenant
class AdminController {

  def packageIngestService
  def knowledgeBaseCacheService

  public AdminController() {
  }

  /**
   * Expose a load package endpoint so developers can use curl to upload package files in their development systems
   * submit a form with the sinle file upload parameter "package_file".
   */
  public loadPackage() {
    def result = null;
    log.debug("AdminController::loadPackage");
    // Single file
    def file = request.getFile("package_file")
    if ( file ) {
      def jsonSlurper = new JsonSlurper()
      def package_data = jsonSlurper.parse(file.inputStream)
      result = packageIngestService.upsertPackage(package_data);
    }
    else {
      log.warn("No file");
    }

    render result as JSON
  }

  /**
   *  Temporary helper method which provides a REST endpoint to trigger an update of the package cache from
   *  remote KBs
   */
  public triggerCacheUpdate() {
    kbCacheService.triggerCacheUpdate()
  }
}

