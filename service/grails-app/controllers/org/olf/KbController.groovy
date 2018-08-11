package org.olf

import org.olf.kb.RemoteKB

import grails.gorm.multitenancy.CurrentTenant
import groovy.util.logging.Slf4j
import org.olf.kb.PackageContentItem
import grails.converters.JSON

/**
 * The KbController providers read-only accress to the contents of any KB
 * harvested by the erm module.
 */
@Slf4j
@CurrentTenant
class KbController {

  public static final String TENANT = "X-Okapi-Tenant";

  private static String PCI_QRY = '''
select pci.id, 
       pci.pkg.source, 
       pci.pkg.name, 
       pci.pti.titleInstance.title, 
       pci.pti.platform.name 
from PackageContentItem as pci
'''

  def kbHarvestService

  public KbController() {
  }

  /**
   * Search and return  sourceKb, package, title, platform, itemtype, coverage summary
   */
  def index() {
    log.debug("KbController::index");

    def result = [:]
    def package_items = PackageContentItem.executeQuery(PCI_QRY);
    render result as JSON
  }

  /**
   *  Temporary helper method which provides a REST endpoint to trigger an update of the package cache from
   *  remote KBs
   */
  public triggerCacheUpdate() {

    kbHarvestService.triggerCacheUpdate()

    Map result = [
      'status':'KB Sync requested'
    ]

    render result as JSON
  }

}

