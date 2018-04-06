package org.olf

import grails.gorm.multitenancy.CurrentTenant
import groovy.json.JsonSlurper
import grails.converters.JSON

@CurrentTenant
class AdminController {

  def packageIngestService

  public AdminController() {
  }

  /**
   * Expose a load package endpoint so developers can use curl to upload package files in their development systems
   * submit a form with the sinle file upload parameter "package_file".
   */
  public loadPackage() {
    // Single file
    def file = request.getFile("package_file")
    if ( file ) {
      def jsonSlurper = new JsonSlurper()
      def package_data = jsonSlurper.parse(new File(test_package_file))
      def result = packageIngestService.internalUpsertPackage(package_data);
    }

    render result as JSON
  }
}

