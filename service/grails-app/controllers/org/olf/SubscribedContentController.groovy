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
  private static final String TITLES_QUERY = '''from TitleInstance as ti
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

  private static final String PLATFORM_TITLES_QUERY = '''from PlatformTitleInstance as pti, AgreementLineItem as ali 
where exists ( select pci.id 
               from PackageContentItem as pci
               where pci.pti = pti
               and ali.pkg = pci.pkg )
   or exists ( select pci.id 
               from PackageContentItem as pci
               where pci.pti = pti
               and ali.pci = pci )
   or ali.pti = pti
'''


  public SubscribedContentController() {
  }


  /**
   * Discover currently subscribed content, and report the agreement which we believe gives rise to the access.
   * This method searches for instances of a title on a platform and the agreement line item which indicates that the
   * title is currently "subscribed"/"selected"/"available". There are currently three routes to this happening:
   * 1) The agreement line item is for a package, and the package contains a PackageContentItem which relates to
   *    a given title on a platform. Content will be automatically selected as it enters the global package definition.
   *    The AgreementLineItem contains a pkg property which points to the package concerned.
   *
   * 2) The agreement line item is for a specific package content item - IE rather than say "This agreement is for
   *    everythig in "JSTOR Arts and Sciences 1" and I trust the KB implicitly, the user has explicitly listed the
   *    titles from "JSTOR Arts and Sciences 1" that they feel are in the agreement.  Users wishing to track in a
   *    granular way how a package is changing may adopt this strategy.
   *    The AgreementLineItem contains a pci property which points to the package-content-item record for the title in the package
   *
   * 3) The user has selected "Off package" - this is the edge case of a custom package - the user simply knows that "Nature"
   *    is on the platform "Nature.com" and wishes to list that fact in an agreement. No packaging is necessary, we just have
   *    an explicit listing of the title on a platform.
   *    The AgreementLineItem contains a pti property which points directly to a platform title instance record.
   */
  def index() {
    def result = [
      resultCount: TitleInstance.executeQuery('select count(*) '+PLATFORM_TITLES_QUERY).get(0),
      subscribedContent: TitleInstance.executeQuery('select pti, ali '+PLATFORM_TITLES_QUERY,[:],[max:10]).collect{ ['pti': it[0], 'ali': it[1]] }
    ]

    // log.debug("SubscribedContentController::index result ${result}");

    respond result
  }

  def titles() {
    def result = [:]
    result.count = TitleInstance.executeQuery('select count(ti) '+TITLES_QUERY).get(0);
    result.subscribedTitles = TitleInstance.executeQuery('select ti '+TITLES_QUERY,[:],[max:10]);
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
