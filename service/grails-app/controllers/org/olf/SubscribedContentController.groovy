package org.olf

import grails.gorm.multitenancy.CurrentTenant
import groovy.json.JsonSlurper
import grails.converters.JSON
import org.olf.kb.TitleInstance


/**
 * Provide a tenant with access to a list of their subscribed content - in essence all the titles and coverage that
 * we have access to, regardless of agreements or access path.
 */
@CurrentTenant
class SubscribedContentController {

  /*
   * The first exists block selects all titles where we have an agreement that contains a package which contains that title.
   */
  private static final String BASE_QUERY = '''from TitleInstance as ti
where exists ( select pci.id 
               from PackageContentItem as pci,
                    AgreementLineItem as ali
               where pci.pti.titleInstance = ti 
               and ali.pkg = pci.pkg )
'''

  public SubscribedContentController() {
  }

  def index() {
    def result = [:]
    result.count = TitleInstance.executeQuery('select count(ti) '+BASE_QUERY).get(0);
    result.subscribedTitles = TitleInstance.executeQuery('select ti '+BASE_QUERY);
    render result as JSON
  }
}

