package org.olf

import org.olf.erm.Entitlement
import org.olf.kb.PlatformTitleInstance
import org.olf.kb.TitleInstance

import com.k_int.okapi.OkapiTenantAwareController

import grails.converters.JSON
import grails.gorm.multitenancy.CurrentTenant
import groovy.util.logging.Slf4j

import grails.gorm.DetachedCriteria;
import grails.orm.HibernateCriteriaBuilder 
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import grails.orm.PagedResultList


/**
 * Provide a tenant with access to a list of their subscribed content - in essence all the titles and coverage that
 * we have access to, regardless of agreements or access path.
 * 
 * Deprecated. Will be moving this functionality to other existing controllers. Mainly the resource controller.
 */
@Slf4j
@CurrentTenant
@Deprecated
class SubscribedContentController extends OkapiTenantAwareController<TitleInstance> {

  private static Map CQLCFG = [
    baseEntity: TitleInstance,
    associations:[
      'platformInstances' : [ 
        'alias':'pi',
        'children':[
          'packageOccurences':[
            'alias':'pi_po',
            'type':JoinType.LEFT_OUTER_JOIN,
            'children':[
              'pkg':[
                'alias':'pi_po_pkg',
                'type':JoinType.LEFT_OUTER_JOIN
              ]
            ]
          ]
        ]
      ]
    ],
    indexes:[
                  // 'title': [ type:'txtIndexField', criteria: { p, v -> p.ilike('name', v.replaceAll('\\*','%')) } ],
                  'title': [ type:'txtIndexField', criteria: { v -> return Restrictions.ilike('name', v.replaceAll('\\*','%'))  } ],
           'ext.selected': [ type:'boolIndexField', requiredAliases:['pi_po_pkg','pi_po','pi'], criteria: { v ->
                                              if( v?.equalsIgnoreCase('true') ) {
                                                System.out.println("bool is true");
                                                return Restrictions.or (
                                                  Restrictions.isNotEmpty('pi.entitlements'),
                                                  Restrictions.or (
                                                    Restrictions.isNotEmpty('pi_po.entitlements'),
                                                    Restrictions.isNotEmpty('pi_po_pkg.entitlements') ) )
                                              } else {
                                                System.out.println("boolFieldNotTrue ${v} ${v.class.name}")
                                              } } ]
    ]
  ];

  /*
   * Return titles for content we have access to.
   *
   * The first exists block selects all titles where we have an agreement line for a package which contains that title.
   * The second exists lists all titles where there is an explicit agreement line for a title from a package
   * The third exists block lists all titles where there is an explicit agreement line for a title on a platform (But not via a package)
   */
  private static final String TITLES_QUERY = '''from TitleInstance as ti
where exists ( select pci.id 
               from PackageContentItem as pci,
                    Entitlement as ent
               where pci.pti.titleInstance = ti 
               and ent.resource = pci.pkg )
   or exists ( select pci.id 
               from PackageContentItem as pci,
                    Entitlement as ent
               where pci.pti.titleInstance = ti 
               and ent.resource = pci )
   or exists ( select pti.id 
               from PlatformTitleInstance pti,
                    Entitlement as ent
               where pti.titleInstance = ti 
               and ent.resource = pti )
'''

  // Return platform title instance records and agreement line items for content we have access to
  private static final String PLATFORM_TITLES_QUERY = '''from PlatformTitleInstance as pti, Entitlement as ent 
where exists ( select pci.id 
               from PackageContentItem as pci
               where pci.pti = pti
               and ent.resource = pci.pkg )
   or exists ( select pci.id 
               from PackageContentItem as pci
               where pci.pti = pti
               and ent.resource = pci )
   or ent.resource = pti
'''


  public SubscribedContentController() {
    super(TitleInstance, true)
  }


  /**
   * Return titles where we have currently live access through some route....
   */
  def index() {
    respond doTheLookup (TitleInstance.entitled)
  }

  /**
   * Discover currently subscribed content, and report the agreement which we believe gives rise to the access.
   * This method searches for instances of a title on a platform and the agreement line item which indicates that the
   * title is currently "subscribed"/"selected"/"available". There are currently three routes to this happening:
   * 1) The agreement line item is for a package, and the package contains a PackageContentItem which relates to
   *    a given title on a platform. Content will be automatically selected as it enters the global package definition.
   *    The Entitlement contains a pkg property which points to the package concerned.
   *
   * 2) The agreement line item is for a specific package content item - IE rather than say "This agreement is for
   *    everythig in "JSTOR Arts and Sciences 1" and I trust the KB implicitly, the user has explicitly listed the
   *    titles from "JSTOR Arts and Sciences 1" that they feel are in the agreement.  Users wishing to track in a
   *    granular way how a package is changing may adopt this strategy.
   *    The Entitlement contains a pci property which points to the package-content-item record for the title in the package
   *
   * 3) The user has selected "Off package" - this is the edge case of a custom package - the user simply knows that "Nature"
   *    is on the platform "Nature.com" and wishes to list that fact in an agreement. No packaging is necessary, we just have
   *    an explicit listing of the title on a platform.
   *    The Entitlement contains a pti property which points directly to a platform title instance record.
   * @Return an object structured as follows::
   * {
   *   resultCount: nnn,
   *   subscribedContent:[
   *     {
   *       id:  'agreement_id',
   *       name: 'agreement_name',
   *       content: [
   *         { data about agreement line item },
   *         { data about agreement line item },
   *       ]
   *     },
   *     {
   *       data about next agreement
   *     },
   *   ]
   * }
   */
  def oldIndex() {
    def result = [:]

    // Can't for the life of me figure out how to escape a json key with the same name as an @Field in the gson view
    // so just cheat and call the key in our result map something different.
    result.resultCount = TitleInstance.executeQuery('select count(*) '+PLATFORM_TITLES_QUERY).get(0);

    def query_params = [:]
    def meta_params = [max:10]

    result.subscribedContent = []
    def current_agreement = null;

    // Run the query, and collect the results into a format more ameinable to the gson processor
    TitleInstance.executeQuery('select pti, ent '+PLATFORM_TITLES_QUERY+' order by ent.owner.id, pti.titleInstance.title', query_params,meta_params).collect { it ->

      PlatformTitleInstance pti = it[0]
      Entitlement ent = it[1]

      if ( ( current_agreement == null ) || 
           ( current_agreement?.id != ent.owner.id ) ) {
        // This is the first agreement line item, OR it's a new agreement. Add the agreement and set the context to that
        current_agreement = [ 
          id: ent.owner.id, 
          name: ent.owner.name,
          agreement: ent.owner,
          content:[] 
        ]
        result.subscribedContent.add(current_agreement);
      }

      current_agreement.content.add([
        pti: pti,
        ent: ent
      ]);
    }

    // log.debug("SubscribedContentController::index result ${result}");

    respond result
  }

  def titles() {
    def result = [:]
    result.count = TitleInstance.executeQuery('select count(ti) '+TITLES_QUERY).get(0);
    result.subscribedTitles = TitleInstance.executeQuery('select ti '+TITLES_QUERY,[:],[max:10]);
    render result as JSON
  }


  // N.B. We arrive here via UrlMappings which quitly adds stats=true on to the request so we get the hit count etc
  def codexSearch() {
    log.debug("SubscribedContentController::codexSearch(${params})");
    // See https://github.com/folio-org/raml/blob/7596a06a9b4ee5c2d296e7d528146d6d30c3151f/examples/codex/instanceCollection.sample

    com.k_int.utils.cql.criteria.CQLToCriteria c = new com.k_int.utils.cql.criteria.CQLToCriteria()
    PagedResultList prl = c.list(CQLCFG, params.query, [max:params.limit, offset:params.offset])
    // log.debug("Result of ${prl.class.name} c.list: ${prl} totalCount:${prl.getTotalCount()}");

    Map result = [ prl: prl ]

    // params.stats=true
    // params.max = params.limit
    // Map codexSearchResponse = doTheLookup(TitleInstance.entitled)
    // render(view:'codexSearch', mode:l:prl);
    respond(result, status:200)
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

