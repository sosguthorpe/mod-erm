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
   * The first exists block selects all titles where we have an agreement line for a package which contains that title.
   * The second exists lists all titles where there is an explicit agreement line for a title from a package
   * The third exists block lists all titles where there is an explicit agreement line for a title on a platform (But not via a package)
   */
  private static final String BASE_QUERY = '''from TitleInstance as ti
where exists ( select pci.id 
               from PackageContentItem as pci,
                    AgreementLineItem as ali
               where pci.pti.titleInstance = ti 
               and ali.pkg = pci.pkg )
   or exists ( select pci.id 
               from PackageContentItem as pci,
                    AgreementLineItem as ali
               where pci.pti.titleInstance = ti 
               and ali.pci = pci )
   or exists ( select pti.id 
               from PlatformTitleInstance pti,
                    AgreementLineItem as ali
               where pti.titleInstance = ti 
               and ali.pti = pti )
'''

  public SubscribedContentController() {
  }

  def index() {
    def result = [:]
    result.count = TitleInstance.executeQuery('select count(ti) '+BASE_QUERY).get(0);
    result.subscribedTitles = TitleInstance.executeQuery('select ti '+BASE_QUERY,[:],[max:10]);
    render result as JSON
  }


  def codexSearch() {
    log.debug("SubscribedContentController::codexSearch(${params})");
    // See https://github.com/folio-org/raml/blob/7596a06a9b4ee5c2d296e7d528146d6d30c3151f/examples/codex/instanceCollection.sample

    def result=[
      instances:[]
    ]

    TitleInstance.executeQuery('select ti '+BASE_QUERY,[:],[max:10]).each { title ->
      result.instances.add(
        [ 
          id: title.id,
          title: title.title
        ]
      );
    }

    render result as JSON
  }

  def codexItem() {
    log.debug("SubscribedContentController::codexItem(${params})");
    // see https://github.com/folio-org/raml/blob/7596a06a9b4ee5c2d296e7d528146d6d30c3151f/examples/codex/instance.sample

    TitleInstance ti = TitleInstance.get(params.id)

    def result=[
      id: ti.id,
      title: ti.title
    ]

    render result as JSON
  }
}

