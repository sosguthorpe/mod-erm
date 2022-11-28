package org.olf.general

import static org.grails.datastore.mapping.engine.event.EventType.*
import static org.olf.general.Constants.UUIDs
import static org.springframework.transaction.annotation.Propagation.MANDATORY

import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy
import java.util.stream.Collectors
import java.util.stream.Stream

import javax.annotation.PostConstruct
import org.grails.datastore.gorm.query.criteria.DetachedAssociationCriteria
import org.grails.datastore.mapping.dirty.checking.DirtyCheckable
import org.grails.datastore.mapping.dirty.checking.DirtyCheckableCollection
import org.grails.datastore.mapping.dirty.checking.DirtyCheckingCollection
import org.grails.datastore.mapping.dirty.checking.DirtyCheckingSupport
import org.grails.datastore.mapping.engine.EntityAccess
import org.grails.datastore.mapping.engine.event.AbstractPersistenceEvent
import org.grails.datastore.mapping.engine.event.PostDeleteEvent
import org.grails.datastore.mapping.engine.event.PostInsertEvent
import org.grails.datastore.mapping.engine.event.PostUpdateEvent
import org.grails.datastore.mapping.engine.event.PreInsertEvent
import org.grails.datastore.mapping.engine.event.PreUpdateEvent
import org.grails.datastore.mapping.query.Restrictions
import org.hibernate.criterion.Subqueries
import org.olf.kb.Platform
import org.olf.kb.PlatformTitleInstance
import org.springframework.context.ApplicationEvent
import org.springframework.context.ApplicationListener
import org.springframework.context.ConfigurableApplicationContext

import com.k_int.web.toolkit.refdata.RefdataCategory
import com.k_int.web.toolkit.refdata.RefdataValue
import com.k_int.web.toolkit.utils.GormUtils

import grails.core.GrailsApplication
import grails.gorm.DetachedCriteria
import grails.gorm.multitenancy.CurrentTenant
import grails.gorm.multitenancy.Tenants
import grails.gorm.transactions.Transactional
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import net.sf.ehcache.util.NamedThreadFactory
import services.k_int.core.FolioLockService

@CompileStatic
@Slf4j
public class StringTemplatingService implements ApplicationListener<ApplicationEvent> {

  public static final String CONTEXT_PROXY = 'urlproxier'
  public static final String CONTEXT_CUSTOMIZER = 'urlcustomiser'
  public static final String TEMPLATED_URL_DEFAULT = 'defaultUrl'

  @Autowired(required=true)
  FolioLockService folioLockService
  
  @Autowired(required=true)
  GrailsApplication grailsApplication

  final ThreadPoolExecutor executor = new ThreadPoolExecutor(
    5, 10, 10, TimeUnit.SECONDS,
    new LinkedBlockingQueue<Runnable>(),
    new NamedThreadFactory('Tasks'),
    new CallerRunsPolicy())

  
  @PostConstruct
  public void init() {
    ConfigurableApplicationContext context = grailsApplication.mainContext as ConfigurableApplicationContext
    context.addApplicationListener(this)
  }
  
  private void updatePtiFromEvent (final AbstractPersistenceEvent event) {
    
    final DirtyCheckable dc = event.entityObject as DirtyCheckable
    
    if (dc.hasChanged('templatedUrls')) {
      log.debug 'templatedUrls was changed. Do not act on this event.'
      return // NOOP
    }
    
    PlatformTitleInstance pti = event.entityObject as PlatformTitleInstance
    
    if (!pti.url) {
      log.debug('PTI has no url associated with it. Skipping templating')
      return
    }
    

    final Platform platform = pti.platform

    // Fetch the applicable templates
    final Map<String, List<StringTemplate>> templates = findStringTemplatesForId (platform.id)

    // Bail early if no templates
    if (!templates.values().findResult { it.empty ? null : true }) {
      log.debug('No templates applicable to PTI')
      return
    }

    // Create the root bindings
    final StringTemplateBindings rootBindings = new StringTemplateBindings(
      inputUrl: pti.url,
      platformLocalCode: platform.localCode ?: ''
    )

    // Now run the proxy against all customized urls and the original.
    final Set<TemplatedUrl> generatedUrls = getTeplatedUrlsForRootBinding(templates, rootBindings)

    // IMPORTANT: DO not set properties directly here. It will cause the session to loop.
    // To change values we use the entity access object.
    final EntityAccess ea = event.entityAccess
    
//    final Set<TemplatedUrl> ptiUrls = ea.getProperty("templatedUrls") as Set
//    ptiUrls.clear()
//    ptiUrls.addAll( generatedUrls )
//    
//    ea.setProperty("templatedUrls", ptiUrls)
  }
  
  private void massUpdatePlatformFromEvent(final AbstractPersistenceEvent event) {
    
    // Single platform update after platform has been updated.
    // Only need to act if the properties we care about were changed.
    Platform platform = event.entityObject as Platform
    DirtyCheckable dcPlatform = event.entityObject as DirtyCheckable
    
    if (!dcPlatform.hasChanged('localCode')) {
      log.debug 'localCode not changed for Platform. No need to rebuild templates.'
      return // NOOP
    }
    
    // Add a backgroundTask to update the platform
    final String tenantId = Tenants.currentId()
    if (!tenantId) {
      log.error 'Couldn\'t get tenant ID'
      return
    }
    
    final String platformId = platform.id
    if (!platformId) {
      log.error 'Couldn\'t get Platform ID'
      return
    }
    
    executor.execute({ final String theTenant, final String thePlatform ->
      
      Tenants.withId(theTenant) {
        GormUtils.withTransaction {
          executeTemplatesForSinglePlatform( thePlatform )
        }
      }
      
    }.curry(tenantId, platformId))
  }
  
  private void massUpdatePlatformsFromEvent(final AbstractPersistenceEvent event) {
    // Multiple Platform update when String template added/removed/changed
    StringTemplate template = event.entityObject as StringTemplate
    
    final String tenantId = Tenants.currentId()
    if (!tenantId) {
      log.error 'Couldn\'t get tenant ID'
      return
    }
    
    final String templateId = template.id
    if (!templateId) {
      log.error 'Couldn\'t get StringTemplate ID'
      return
    }
    
    executor.execute({ final String theTenant, final String theTemplate ->
      
      Tenants.withId(theTenant) {
        GormUtils.withTransaction {
          List<Platform> platforms = getAllPlatformsForTemplate(theTemplate)
          
          // For each platform add a background task.
          for (final Platform p : platforms) {
            executor.execute({ final String thePlatformTenant, final String thePlatform ->
              Tenants.withId(thePlatformTenant) {
                GormUtils.withNewTransaction {
                  executeTemplatesForSinglePlatform( thePlatform )
                }
              }
              
            }.curry(theTenant, p.id))
          }
        }
      }
      
    }.curry(tenantId, templateId))
  }

  private Set<TemplatedUrl> getTeplatedUrlsForRootBinding( final Map<String, List<StringTemplate>> templates, final StringTemplateBindings rootBindings ) {
    // Create the list of customized urls and the orginal
    final List<TemplatedUrl> noneProxiedUrls = [].with {
      add(new TemplatedUrl(
        name: TEMPLATED_URL_DEFAULT,
        url: rootBindings.inputUrl
      ))
      addAll(
        addTemplateUrlMappings( templates.get(CONTEXT_CUSTOMIZER).stream(), rootBindings )
          .collect(Collectors.toList()))

      it // return the subject
    }

    // Now run the proxy against all customized urls and the original.
    final Set<TemplatedUrl> generatedUrls = noneProxiedUrls.stream()
      .flatMap({
        final TemplatedUrl turl ->
        final StringTemplateBindings bindings = new StringTemplateBindings(
          inputUrl: turl.url,
          name: turl.name,
          platformLocalCode: rootBindings.platformLocalCode
        )
        
        
        
        final Stream<TemplatedUrl> nested = addTemplateUrlMappings( templates.get(CONTEXT_PROXY).stream(), bindings );
          
        return Stream.concat(Stream.of(turl), nested)
      })
      .toSet()

    return generatedUrls
  }
  
  @CurrentTenant
  @Transactional(propagation = MANDATORY)
  protected void executeTemplatesForSinglePlatform (final String id, final Date notSince = new Date()) {
    // Get a chunk of PTIs and keep repeating until all have been processed
    final Map<String, List<StringTemplate>> templates = findStringTemplatesForId (id)
    
    // This this should allow us to keep paging until finished. It should also skip
    // any update out of band too.
    final int max = 1000
    
    List <PlatformTitleInstance> ptis = getPtisToUpdateForPlatform(id, notSince, max)
    while (ptis.size() > 0) {
      
      // Act on each PTI
      for (PlatformTitleInstance pti : ptis) {
        executeTemplatesForSinglePTI(pti, templates)
      }
      
      // Next page
      ptis = ptis.size() == max ? getPtisToUpdateForPlatform(id, notSince, max) : []
    }
  }

  @CurrentTenant
  @Transactional(propagation = MANDATORY)
  protected List<String> executeTemplatesForSinglePTI (final String id, final Map<String, List<StringTemplate>> stringTemplates = null) {

    final PlatformTitleInstance pti = getPti(id)

    if (!pti) {
      log.warn('No PTI found with id {}', id)
      return Collections.emptyList()
    }

    return executeTemplatesForSinglePTI(pti, stringTemplates)
  }

  @CurrentTenant
  @Transactional(propagation = MANDATORY)
  protected void executeTemplatesForSinglePTI (final PlatformTitleInstance pti, final Map<String, List<StringTemplate>> stringTemplates = null) {

    if (!pti.url) {
      log.warn('PTI has no url associated with it. Skipping templating')
      return
    }

    final Platform platform = pti.platform

    // Use supplied templates or, Fetch the applicable templates if not supplied.
    final Map<String, List<StringTemplate>> templates = stringTemplates ?: findStringTemplatesForId (platform.id)

    // Bail early if no templates
    if (!templates.values().findResult {
      it.empty ? null : true
    }) {
      log.debug('No templates applicable to PTI')
      return
    }

    // Create the root bindings
    final StringTemplateBindings rootBindings = new StringTemplateBindings(
      inputUrl: pti.url,
      platformLocalCode: platform.localCode ?: ''
    )

    // Now run the proxy against all customized urls and the original.
    final Set<TemplatedUrl> generatedUrls = getTeplatedUrlsForRootBinding(templates, rootBindings)
    
    // Delete All urls for this pti. If there's an ID.
    if (pti.id) deleteTemplatedUrlsForPTI(pti.id)
    
    pti.lastUpdated = new Date() // Force timestamp update
    
    // Save the PTI
    GormUtils.gormInstanceApi(PlatformTitleInstance).save(pti, [failOnError: true] as Map)
    
    // Should now have an ID. Save the TemplatedUrls
    generatedUrls.each { TemplatedUrl url ->
      url.resource = pti
      GormUtils.gormInstanceApi(TemplatedUrl).save(url, [failOnError: true] as Map)
    }
  }
  
  @CurrentTenant
  @Transactional(propagation = MANDATORY)
  private void deleteTemplatedUrlsForPTI(String ptiId) {
    final String hql = '''
      DELETE FROM TemplatedUrl del
      WHERE EXISTS (
        SELECT tu.id FROM TemplatedUrl tu
        JOIN tu.resource as pti
        WHERE tu.id = del.id AND pti.id = :ptiId
      )
    '''
    
    final int count = GormUtils.gormStaticApi(Platform).executeUpdate(hql, [
      'ptiId' : ptiId
    ])
    
    log.debug 'Removed {} templates for pti: {}', count, ptiId
  }

  private Stream<TemplatedUrl> addTemplateUrlMappings( final Stream<StringTemplate> templates, final StringTemplateBindings bindings ) {
    return templates
    .map({
      final StringTemplate template ->  new TemplatedUrl(
        name: template.name + (bindings.name ? "-${bindings.name}" : ''),
        url: template.customiseString(bindings)
      )
    })
  }
  
  @CurrentTenant
  @Transactional(propagation = MANDATORY)
  protected List<Platform> getAllPlatformsForTemplate( final String templateId ) {
    
    final String hql = '''
      FROM Platform platform
      WHERE (
        EXISTS (
          SELECT incSt.id FROM StringTemplate incSt
          JOIN incSt.context AS context
          JOIN incSt.idScopes AS includes
          WHERE incSt.id = :theId AND context.value = :cust_context AND includes = platform.id
        )
      ) OR (
        NOT EXISTS (
          SELECT exclSt.id FROM StringTemplate exclSt
          JOIN exclSt.context AS context
          JOIN exclSt.idScopes AS excludes
          WHERE exclSt.id = :theId AND context.value = :proxy_context AND excludes = platform.id
        )
      )
      '''
    
    final List<Platform> platforms = GormUtils.gormStaticApi(Platform).executeQuery(hql, [
      'theId': templateId,
      'cust_context' : CONTEXT_CUSTOMIZER,
      'proxy_context' : CONTEXT_PROXY
    ])
  } 

  /**
   * Return a list of templates grouped by the templates "context".
   * 
   * The semantics of the presence of the `id` in `scope` changes in relation to the context being fetched.
   * For proxies, the scope is a list of excludes and therefore we ensure the supplied id is not in the scope for
   * proxy templates, and include implicity.
   * For customizers, the scope is an explicit include. So we ensure the id is present.
   * 
   * @param id The id to check for in the various scopes
   * @return a map keyed by context with entry sets of Lists of applicable string templates
   */
  @CurrentTenant
  @Transactional
  public Map<String, List<StringTemplate>> findStringTemplatesForId(final String resourceID) {
    
    // Convert null to the NIL ID. This will help when we have an unsaved platform that has not yet been assigned an
    // ID. Using null would return the incorrect values bellow. The NIL UUID ensures the right items returned.
    final String theId = resourceID ?: UUIDs.NIL.toString()
    
    final String hql = '''
      SELECT st FROM StringTemplate st
      WHERE (
        EXISTS (
          SELECT incSt.id  FROM StringTemplate incSt
          JOIN incSt.context AS context
          JOIN incSt.idScopes AS includes
          WHERE incSt.id = st.id AND context.value = :cust_context AND includes = :theId
        )
      ) OR (
        NOT EXISTS (
          SELECT exclSt.id FROM StringTemplate exclSt
          JOIN exclSt.context AS context
          JOIN exclSt.idScopes AS excludes
          WHERE exclSt.id = st.id AND context.value = :proxy_context AND excludes = :theId
        )
      )
      '''
    
    final Map<String,List<StringTemplate>> templates = [
      (CONTEXT_PROXY) : [],
      (CONTEXT_CUSTOMIZER): []
    ]
    
    final List<StringTemplate> stringTemplates = GormUtils.gormStaticApi(StringTemplate).executeQuery(hql, [
      'theId': theId,
      'cust_context' : CONTEXT_CUSTOMIZER,
      'proxy_context' : CONTEXT_PROXY
    ])

    templates[CONTEXT_CUSTOMIZER] = stringTemplates.stream()
    .filter({
      StringTemplate tmp ->
      switch (tmp.context?.value) {
        case CONTEXT_CUSTOMIZER:
          return true
          break
        case CONTEXT_PROXY:
          templates[CONTEXT_PROXY].add(tmp)
          return false
          break
        default:
          log.warn 'Unknown context type "{}" in template "{}"', tmp.context?.value, tmp.id
      }
      // Default to exclude
      return false
    }).toList()
    
    return templates
  }

  @CurrentTenant
  @Transactional(propagation = MANDATORY)
  protected PlatformTitleInstance getPti( final String id ) {
    return GormUtils.gormStaticApi(PlatformTitleInstance).get(id)
  }
  
  @CurrentTenant
  @Transactional(propagation = MANDATORY)
  protected List<PlatformTitleInstance> getPtisToUpdateForPlatform ( final String platformID, final Date notUpdatedSince, final int maximum) {
    final List<PlatformTitleInstance> ptis = GormUtils.gormStaticApi(PlatformTitleInstance).createCriteria().list(max: maximum) {
      readOnly true
      
      eq 'platform.id', platformID
      lt 'lastUpdated', notUpdatedSince
      
    } as List
    
    return ptis
  }

//  @Transactional
//  @CurrentTenant
//  public void generateUrlsForPtiWithId ( final String ptiId ) {
//
//    // Pre-generate the lock name here from the platform id.
//    // This will ensure the correct level of data locking for this operation.
//    final String platformId = getPti( ptiId )?.platform?.id;
//    if (!platformId) {
//      log.error 'Could not determine platform ID. Is {} actually a PTI?', ptiId
//      return
//    }
//
//    final String tenant = Tenants.currentId()
//    if (!tenant) {
//      log.error 'Could not determine current tenant'
//      return
//    }
//
//    final String lockName = "stringTemplate:${tenant}:${platformId}"
//
//    // Create a promise
//    final Promise<List<String>> deferred = WithPromises.task {
//      executor.execute({
//        final String tenantId, final String pti, final String lockId ->
//
//        // This method locks the calling thread. Has to be in a background thread!
//        final List<String> returnList = []
//        folioLockService.federatedLockAndDo(lockId) {
//          try {
//            Tenants.withId(tenantId) {
//              returnList.addAll( executeTemplatesForSinglePTI(pti) )
//            }
//          } catch( Exception e ) {
//            log.error 'Error creating URL templates for {}', pti
//          }
//        }
//      }.curry(tenant, ptiId, lockName))
//    }
//
//    deferred.onError {
//      Throwable e ->
//      log.error 'Could process template URLs for PTI {}. Error: {}', ptiId, e.getMessage()
//    }
//
//    deferred.onComplete {
//      List<String> urls ->
//      log.debug 'Generated Urls for PTI {}, {}', ptiId, urls.stream().collect(Collectors.joining("', '", "'", "'"))
//    }
//  }

  @CurrentTenant
  public void refreshUrls() {
    // find all
    log.debug 'Refresh Urls now NOOP'
  }
  
  private boolean supportsEventType (Class<? extends ApplicationEvent> eventType) {
    if (!AbstractPersistenceEvent.class.isAssignableFrom(eventType)) return false

    switch (eventType) {

      // Post events for when Templates or Platforms
      // are updated and result in changes to many PTIs
      case PostDeleteEvent:
      case PostInsertEvent:
      case PostUpdateEvent:

      // Handle pre(save/update) for PTIs, allowing us to update inline
      // with the same transaction.
      case PreInsertEvent:
      case PreUpdateEvent:
        return true;
        break

      default:
        return false;
    }
  } 
  
  @Override
  public void onApplicationEvent(ApplicationEvent theEvent) {
    if (!supportsEventType( theEvent.class )) {
      log.trace '"Not valid for event type {}', theEvent.class
      return
    }
    
    AbstractPersistenceEvent event = theEvent as AbstractPersistenceEvent
    
    switch(event.getEventType()) {
      case PreInsert:
      case PreUpdate:
        final Object eo = event.getEntityObject()
        
        // Check if this is a PTI
        if (PlatformTitleInstance.isAssignableFrom(eo.getClass())) {
          log.debug 'Pre-(Insert/Update) event for PTI'
          updatePtiFromEvent(event)
        }
        
        break
        
        
      case PostUpdate:
        final Object eo = event.getEntityObject()
        
        // PostUpdate of Platform
        // We don't track Add/Delete as
        if (Platform.isAssignableFrom(eo.getClass())) {
          log.debug 'Post-Update event for Platform'
          massUpdatePlatformFromEvent(event)
          break
        } //else drop through...
      case PostInsert:
      case PostDelete:
        final Object eo = event.getEntityObject()
        
        // Post Add/Delete/Update of StringTemplate
        if (StringTemplate.isAssignableFrom(eo.getClass())) {
          log.debug 'Post-(Insert/Update/Delete) event for StringTemplate'
          massUpdatePlatformsFromEvent(event)
        }
        
        break
      default: // NOOP...
        break
    }
  }

  // Split these out so that we can skip them in CompileStatic
  // This method can batch fetch all Platforms or just those updated after a certain time
//  @CompileStatic(SKIP)
//  private List<List<String>> batchFetchPlatforms(final int platformBatchSize, int platformBatchCount, Date since = null) {
//    // Fetch the ids and localCodes for all platforms
//    List<List<String>> platforms = Platform.createCriteria().list ([max: platformBatchSize, offset: platformBatchSize * platformBatchCount]) {
//      order 'id'
//      if (since) {
//        gt('lastUpdated', since)
//      }
//      projections {
//        property('id')
//        property('localCode')
//      }
//    }
//    return platforms
//  }
//
//
//  // This method can batch fetch all PTIs on a platform or updated after a certain time (or both)
//  @CompileStatic(SKIP)
//  private List<List<String>> batchFetchPtis(final int ptiBatchSize, int ptiBatchCount, String platformId = null, Date since = null) {
//    List<List<String>> ptis = PlatformTitleInstance.createCriteria().list ([max: ptiBatchSize, offset: ptiBatchSize * ptiBatchCount]) {
//      order 'id'
//      if (platformId) {
//        eq('platform.id', platformId)
//      }
//      if (since) {
//        gt('lastUpdated', since)
//      }
//      projections {
//        property('id')
//        property('url')
//        if (!platformId) {
//          property('platform.id')
//        }
//      }
//    }
//    return ptis
//  }

  //  private List<StringTemplate> queryInScopeWithContext(String id, String context) {
  //    List<StringTemplate> stringTemplates = StringTemplate.executeQuery("""
  //      SELECT st FROM StringTemplate st
  //      WHERE st.context.value = :context
  //      AND st IN (
  //        SELECT st1 FROM StringTemplate st1
  //        JOIN st1.idScopes as scope
  //        WHERE scope = :id
  //      )
  //      """,
  //      [context: context, id: id]
  //    )
  //    return stringTemplates
  //  }




  //  /*
  //   * This method will take in an id of the form f311d130-8024-47c4-8a86-58f817dbefde, and a binding of the form
  //  [
  //    inputUrl: "http://ebooks.ciando.com/book/index.cfm?bok_id=27955222",
  //    platformLocalCode: "ciando"
  //  ]
  //   * and these will be accessible from the template rule.
  //   * This method will store the business logic determining heirachy of StringTemplate contexts,
  //   * and whether these "stack" or not.
  //   */
  //  private List<LinkedHashMap<String, String>> performStringTemplates(Map stringTemplates, Map binding) {
  //    List<LinkedHashMap<String, String>> output = new ArrayList()
  //    output.add([name: "defaultUrl", url: binding.inputUrl.toString()])
  //
  //    // First get all customised urls
  //    List<LinkedHashMap<String, String>> customisedUrls = performTemplatingByContext(binding, "urlCustomisers", stringTemplates)
  //    // Then proxy all urls
  //    List<LinkedHashMap<String, String>> proxiedUrls = performTemplatingByContext(binding, "urlProxiers", stringTemplates)
  //
  //    // Finally we proxy all the customised urls
  //    List<LinkedHashMap<String, String>> proxiedCustomisedUrls = []
  //    customisedUrls.each{ customiserMap ->
  //      /*
  //       customiserMap = [
  //         name: "customiserName"
  //         url: "customisedUrl"
  //       ]
  //      */
  //
  //      // Think we only need a shallow copy here to pass to the proxiers -- statically typed so rebuild
  //      Map customBinding = [
  //        inputUrl: customiserMap.url ? customiserMap.url.toString() : "",
  //        platformLocalCode: binding.platformLocalCode ? binding.platformLocalCode.toString() : ""
  //      ]
  //
  //      // Add all the proxied-customised urls to a list
  //      proxiedCustomisedUrls.addAll(performTemplatingByContext(customBinding, "urlProxiers", stringTemplates, customiserMap.name.toString()))
  //    }
  //
  //    // Add all of these to the output List
  //    output.addAll(proxiedUrls)
  //    output.addAll(customisedUrls)
  //    output.addAll(proxiedCustomisedUrls)
  //
  //    return output
  //  }
  //
  //
  //  // Simpler method which just returns a list of maps for a single StringTemplateContext--used for nested templating
  //  private List<LinkedHashMap> performTemplatingByContext(Map binding, String context, Map stringTemplates, String nameSuffix = '') {
  //    return stringTemplates[context]?.collect { StringTemplate st ->
  //      [
  //        name: nameSuffix ? "${st['name']}-${nameSuffix}".toString() : st.name,
  //        url: st.customiseString(binding)
  //      ]
  //    }
  //  }
  //
  //  private List<StringTemplate> queryNotInScopeWithContext(String id, String context) {
  //    List<StringTemplate> stringTemplates = StringTemplate.executeQuery("""
  //      SELECT st FROM StringTemplate st
  //      WHERE st.context.value = :context
  //      AND st NOT IN (
  //        SELECT st1 FROM StringTemplate st1
  //        JOIN st1.idScopes as scope
  //        WHERE scope = :id
  //      )
  //      """,
  //      [context: context, id: id]
  //    )
  //    return stringTemplates
  //  }
  //
  //  private List<StringTemplate> queryInScopeWithContext(String id, String context) {
  //    List<StringTemplate> stringTemplates = StringTemplate.executeQuery("""
  //      SELECT st FROM StringTemplate st
  //      WHERE st.context.value = :context
  //      AND st IN (
  //        SELECT st1 FROM StringTemplate st1
  //        JOIN st1.idScopes as scope
  //        WHERE scope = :id
  //      )
  //      """,
  //      [context: context, id: id]
  //    )
  //    return stringTemplates
  //  }
  //
  //  /// We need to lock the refresh task in the same way we need to lock the actual generation,
  //  // since we access/hold/edit an AppSetting value here
  //  static boolean refreshRunning = false

  /*
   * This is the actual method which gets called by the endpoint /erm/sts/template on a timer. 
   * Firstly it will look up the cursor indicating the last time the urls were refreshed.
   * Then it will go through and start calling generateTemplatedUrlsForErmResources for each object updated since
   */
  //  @CompileStatic(SKIP)
  //  public void refreshUrls(String tenantId) {
  //    log.debug "stringTemplatingService::refreshUrls called with tenantId (${tenantId})"
  //    folioLockService.federatedLockAndDoWithTimeoutOrSkip("agreements-${tenantId}:stringTemplate:refreshUrls", 0) {
  //
  //      /* Theoretically updates could happen after the process begins but before the url_refresh_cursor gets updated
  //      * So save the time before starting process as the new cursor pt
  //      * IMPORTANT--This only works because LastUpdated on the pti ISN'T triggered for a collection update,
  //      * ie TemplatedUrls.
  //      */
  //
  //      /* TODO In future we may wish to change this, in order to keep track of those PTIs who were updated
  //      * between the last refresh date but not after after the platform updates started.
  //      * Some work would need to be done to make lastUpdated change, and to figure out what to do for manual
  //      * changes in the interval.
  //      */
  //
  //      String new_cursor_value = System.currentTimeMillis()
  //      // Also create container for the current cursor value
  //      Date last_refreshed
  //      AppSetting url_refresh_cursor
  //      AppSetting previous_sts_count
  //      Long previousStsCount
  //
  //      Tenants.withId(tenantId) {
  //        // Start by grabbing the count of StringTemplates currently in the system
  //        Long currentStsCount = StringTemplate.executeQuery("select count(*) from StringTemplate")[0]
  //
  //        // One transaction for fetching the initial values/creating AppSettings
  //        AppSetting.withNewTransaction {
  //          // Need to flush this initially so it exists for first instance
  //          // Set initial cursor to 0 so everything currently in system gets updated
  //          url_refresh_cursor = AppSetting.findByKey('url_refresh_cursor') ?: new AppSetting(
  //            section:'registry',
  //            settingType:'Date',
  //            key: 'url_refresh_cursor',
  //            value: 0
  //          ).save(flush: true, failOnError: true)
  //
  //          previous_sts_count = AppSetting.findByKey('sts_count') ?: new AppSetting(
  //            section:'registry',
  //            settingType:'Number',
  //            key: 'sts_count',
  //            value: 0
  //          ).save(flush: true, failOnError: true)
  //
  //          // Parse setting Strings to Date/Long
  //          last_refreshed = new Date(Long.parseLong(url_refresh_cursor.value))
  //          previousStsCount = Long.parseLong(previous_sts_count.value)
  //        }
  //
  //        // Fetch stringTemplates that have changed since the last refresh
  //        List<String> sts = StringTemplate.createCriteria().list() {
  //          order 'id'
  //          gt('lastUpdated', last_refreshed)
  //          projections {
  //            property('id')
  //          }
  //        }
  //
  //        /*
  //        * If sts.size is not zero, or the counts differ,
  //        * then string templates have been created, deleted or updated
  //        * so run refresh for all.
  //        */
  //        if (
  //          currentStsCount != previousStsCount ||
  //          sts.size() > 0
  //        ) {
  //          // StringTemplates have changed, ignore more granular changes and just refresh everything
  //          generateTemplatedUrlsForErmResources(tenantId)
  //        } else {
  //          // FIRST - refresh all updated PTIs
  //          PlatformTitleInstance.withNewTransaction{
  //            final int ptiBatchSize = 100
  //            int ptiBatchCount = 0
  //            List<List<String>> ptis = batchFetchPtis(ptiBatchSize, ptiBatchCount, null, last_refreshed)
  //            while (ptis && ptis.size() > 0) {
  //              ptiBatchCount ++
  //              ptis.each { pti ->
  //                // Here we send it to the generic case not the specific one to get the queueing behaviour
  //                generateTemplatedUrlsForErmResources(
  //                  tenantId,
  //                  [
  //                    context: 'pti',
  //                    id: pti[0],
  //                    platformId: pti[2]
  //                  ]
  //                )
  //              }
  //              // Next page
  //              ptis = batchFetchPtis(ptiBatchSize, ptiBatchCount, null, last_refreshed)
  //            }
  //          }
  //
  //          // Next - refresh all updated Platforms
  //          Platform.withNewTransaction{
  //            final int platformBatchSize = 100
  //            int platformBatchCount = 0
  //            List<List<String>> platforms = batchFetchPlatforms(platformBatchSize, platformBatchCount, last_refreshed)
  //            while (platforms && platforms.size() > 0) {
  //              platformBatchCount ++
  //              platforms.each { platform ->
  //                // Here we send it to the generic case not the specific one to get the queueing behaviour
  //                generateTemplatedUrlsForErmResources(
  //                  tenantId,
  //                  [
  //                    context: 'platform',
  //                    id: platform[0]
  //                  ]
  //                )
  //                // Next page
  //                platforms = batchFetchPlatforms(platformBatchSize, platformBatchCount, last_refreshed)
  //              }
  //            }
  //          }
  //        }
  //
  //        //One transaction for updating value with new refresh time and new count (count taken from beginning of refresh task)
  //        AppSetting.withNewTransaction {
  //          url_refresh_cursor.value = new_cursor_value
  //          url_refresh_cursor.save(failOnError: true)
  //
  //          previous_sts_count.value = currentStsCount
  //          previous_sts_count.save(failOnError: true)
  //        }
  //      }
  //    }
  //  }

  //  // Returns true if equivalent, false otherwise
  //  private boolean compareTemplatedUrls(List<List<String>> existingTus, List<List<String>> newTus) {
  //    if (existingTus.size() == newTus.size()) {
  //      //Sort existing and new
  //      List<List<String>> newSorted = newTus.toSorted {a,b -> a[0] <=> b[0]}
  //      List<List<String>> existingSorted = existingTus.toSorted {a,b -> a[0] <=> b[0]}
  //
  //      //Using for loop because can't break out of groovy each without throwing exception
  //      for (int i = 0; i < existingSorted.size(); i++) {
  //        List<String> existingSortedIndex = existingSorted[i]
  //        List<String> newSortedIndex = newSorted[i]
  //        if (existingSortedIndex[0] != newSortedIndex[0] || existingSortedIndex[1] != newSortedIndex[1]) {
  //          return false
  //        }
  //      }
  //      return true
  //    }
  //    return false
  //  }

  //  // This method generates the templatedUrls for PTIs, given the stringTemplates and platformLocalCode
  //  private void generateTemplatedUrlsForPti(final List<String> pti, Map stringTemplates, String platformLocalCode='') {
  //    log.debug "generateTemplatedUrlsForPti called for (${pti[0]})"
  //    try {
  //      String ptiId = pti[0]
  //      String ptiUrl = pti[1]
  //      PlatformTitleInstance fetchedPti = PlatformTitleInstance.get(ptiId)
  //
  //      // If a url exists on this PTI--check if templatedUrls have changed, then delete and recreate
  //      if (ptiUrl) {
  //        Map binding = [
  //          inputUrl: ptiUrl,
  //          platformLocalCode: platformLocalCode
  //        ]
  //        List<LinkedHashMap<String, String>> newTemplatedUrls = performStringTemplates(stringTemplates, binding)
  //
  //        //Only delete and make new if they differ
  //        List<List<String>> etus = fetchedPti.templatedUrls.collect { tu -> [tu.name.toString(), tu.url.toString()] }
  //        List<List<String>> ntus = newTemplatedUrls.collect { tu -> [tu.name, tu.url] }
  //        if (!compareTemplatedUrls(etus, ntus)) {
  //          deleteTemplatedUrlsForPTI(ptiId)
  //
  //          newTemplatedUrls.each { templatedUrl ->
  //            TemplatedUrl tu = new TemplatedUrl(templatedUrl)
  //            tu.resource = fetchedPti
  //            tu.save(failOnError: true)
  //          }
  //        }
  //      } else {
  //        log.warn "No url found for PTI (${ptiId})"
  //      }
  //    } catch (Exception e) {
  //      log.error "Failed to update pti (${pti[0]}): ${e.message}"
  //    }
  //  }


  //  @CompileStatic(SKIP)
  //  private void deleteTemplatedUrlsForPTI(String ptiId) {
  //    TemplatedUrl.executeUpdate("""
  //      DELETE FROM TemplatedUrl as tu
  //      WHERE tu.id IN (
  //        SELECT tu.id FROM TemplatedUrl as tu
  //        JOIN tu.resource as pti
  //        WHERE pti.id = :id
  //      )
  //      """,
  //      [id: ptiId]
  //    )
  //  }
  //
  //  /*
  //   * Trigger plan - have a mutex boolean, running, and a queue.
  //   *
  //   * When we receive a "refresh" event, we check if running. If so we add a note to the queue
  //   * If neither are true we run the task. Once a task is finished we run again if queue non-empty.
  //  */
  //
  //  static boolean running = false
  //  private ArrayList<Map<String, String>> taskQueue = new ArrayList<Map<String, String>>()
  //
  //  private void addTaskToTaskQueue(Map<String, String> params) {
  //    if (params.context == 'stringTemplate') {
  //      // If we're going to run a full system refresh we can just clear the current queue and run that instead
  //      taskQueue.clear()
  //      taskQueue.add(params)
  //    } else {
  //      taskQueue.add(params)
  //    }
  //    log.debug "Refresh task queue size: ${taskQueue.size()}"
  //  }
  //
  //  /* IMPORTANT NOTE -- When calling this for PTI/Platforms, wrap in a new transaction. Left out of this block so that
  //   * many edits can happen in one transaction block if called for multiple.
  //   */
  //  private void generateTemplatedUrlsForErmResources(final String tenantId, Map<String, String> params = [context: 'stringTemplate']) {
  //    log.debug "generateTemplatedUrlsForErmResources called"
  //
  //    // If running then just add to queue
  //    synchronized ( this ) {
  //      if (running == true) {
  //        addTaskToTaskQueue(params)
  //        return
  //      } else {
  //        running = true
  //      }
  //    }
  //
  //    log.debug "generateTemplatedUrlsForErmResources task start"
  //    Tenants.withId(tenantId) {
  //      switch (params.context) {
  //        case 'stringTemplate':
  //          /*
  //          * Right now we only scope URL templates to Platforms, so fetch a list of Platforms,
  //          * then for each one fetch the stringTemplates and a list of PTIs with that platform,
  //          * then perform stringTemplating on each PTI
  //          */
  //
  //          final int platformBatchSize = 100
  //          int platformBatchCount = 0
  //
  //          Platform.withNewTransaction {
  //            // Fetch the ids and localCodes for all platforms
  //            List<List<String>> platforms = batchFetchPlatforms(platformBatchSize, platformBatchCount)
  //
  //            // This will return [[00998c04-8ab3-49ab-9053-c3e8cff328c2, ciando], [021bfce4-0533-465e-9340-1ceaad2a530f, localCode2], ...]
  //            while (platforms && platforms.size() > 0) {
  //              platformBatchCount ++
  //              platforms.each { platform ->
  //                Map stringTemplates = findStringTemplatesForId(platform[0])
  //                String platformLocalCode = platform[1]
  //
  //                  /*
  //                  * Now we have the stringTemplates and platformLocalCode for the platform,
  //                  * find all PTIs on this platform and remove then re-add the templatedUrls
  //                  */
  //
  //                  final int ptiBatchSize = 100
  //                  int ptiBatchCount = 0
  //                  List<List<String>> ptis = batchFetchPtis(ptiBatchSize, ptiBatchCount, platform[0])
  //                  while (ptis && ptis.size() > 0) {
  //                    ptiBatchCount ++
  //                    ptis.each { pti ->
  //                      generateTemplatedUrlsForPti(pti, stringTemplates, platformLocalCode)
  //                    }
  //
  //                    // Next page
  //                    ptis = batchFetchPtis(ptiBatchSize, ptiBatchCount, platform[0])
  //                  }
  //                // Next page
  //                platforms = batchFetchPlatforms(platformBatchSize, platformBatchCount)
  //              }
  //            }
  //          }
  //          break;
  //        case 'platform':
  //          Platform p = Platform.read(params.id)
  //          Map stringTemplates = findStringTemplatesForId(p.id)
  //          String platformLocalCode = p.localCode
  //          /*
  //          * We have the stringTemplates and platformLocalCode for the platform,
  //          * find all PTIs on this platform and remove then re-add the templatedUrls
  //          */
  //
  //          final int ptiBatchSize = 100
  //          int ptiBatchCount = 0
  //          List<List<String>> ptis = batchFetchPtis(ptiBatchSize, ptiBatchCount, p.id)
  //          while (ptis && ptis.size() > 0) {
  //            ptiBatchCount ++
  //            ptis.each { pti ->
  //              generateTemplatedUrlsForPti(pti, stringTemplates, platformLocalCode)
  //            }
  //
  //            // Next page
  //            ptis = batchFetchPtis(ptiBatchSize, ptiBatchCount, p.id)
  //          }
  //          break;
  //        case 'pti':
  //          //In this case we don't have the platform, but we passed the platformId in the params
  //          Platform platform = Platform.read(params.platformId)
  //          PlatformTitleInstance pti = PlatformTitleInstance.read(params.id)
  //
  //          Map stringTemplates = findStringTemplatesForId(params.platformId)
  //          String platformLocalCode = platform.localCode
  //
  //          generateTemplatedUrlsForPti([pti.id, pti.url], stringTemplates, platformLocalCode)
  //          break;
  //        default:
  //          log.warn "Don't know what to do with params context (${params.context})"
  //          break;
  //      }
  //    }
  //    log.debug "generateTemplatedUrlsForErmResources task end"
  //
  //    synchronized ( this ) {
  //      // Task finished, turn 'running' boolean off
  //      running = false
  //
  //      if (taskQueue.size() > 0) {
  //        // There are still tasks in the queue, run again
  //        Map<String, String> runParams = taskQueue.remove(0)
  //        generateTemplatedUrlsForErmResources(tenantId, runParams)
  //      }
  //    }
  //  }
}
