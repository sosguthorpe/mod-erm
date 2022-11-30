package org.olf.general

import static org.grails.datastore.mapping.engine.event.EventType.*
import static org.olf.general.Constants.UUIDs
import static org.springframework.transaction.annotation.Propagation.MANDATORY

import java.time.Instant
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy
import java.util.stream.Collectors
import java.util.stream.Stream

import javax.annotation.PostConstruct
import javax.sql.DataSource

import org.codehaus.groovy.runtime.InvokerHelper
import org.grails.datastore.gorm.GormInstanceApi
import org.grails.datastore.gorm.GormStaticApi
import org.grails.datastore.gorm.finders.MethodExpression.NotInList
import org.grails.datastore.mapping.core.Datastore
import org.grails.datastore.mapping.dirty.checking.DirtyCheckable
import org.grails.datastore.mapping.engine.EntityAccess
import org.grails.datastore.mapping.engine.event.AbstractPersistenceEvent
import org.grails.datastore.mapping.engine.event.PostDeleteEvent
import org.grails.datastore.mapping.engine.event.PostInsertEvent
import org.grails.datastore.mapping.engine.event.PostUpdateEvent
import org.grails.datastore.mapping.engine.event.PreDeleteEvent
import org.grails.datastore.mapping.engine.event.PreInsertEvent
import org.grails.datastore.mapping.engine.event.PreUpdateEvent
import org.grails.datastore.mapping.multitenancy.SchemaMultiTenantCapableDatastore
import org.grails.orm.hibernate.HibernateDatastore
import org.hibernate.criterion.Projections
import org.hibernate.sql.JoinType
import org.olf.kb.Platform
import org.olf.kb.PlatformTitleInstance
import org.slf4j.Logger
import org.springframework.context.ApplicationEvent
import org.springframework.context.ApplicationListener
import org.springframework.context.ConfigurableApplicationContext

import com.k_int.web.toolkit.utils.GormUtils

import grails.core.GrailsApplication
import grails.gorm.multitenancy.CurrentTenant
import grails.gorm.multitenancy.Tenants
import grails.gorm.transactions.Transactional
import grails.orm.HibernateCriteriaBuilder
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import net.sf.ehcache.util.NamedThreadFactory
import services.k_int.core.FolioLockService

/**
 * Service to look after generating URLs based on templates
 * 
 * @author Ethan Freestone
 * @author Steve Osguthorpe
 */
@CompileStatic
@Slf4j
public class StringTemplatingService implements ApplicationListener<ApplicationEvent> {

  public static final String CONTEXT_PROXY = 'urlproxier'
  public static final String CONTEXT_CUSTOMIZER = 'urlcustomiser'
  public static final String TEMPLATED_URL_DEFAULT = 'defaultUrl'
  public static final String EVENT_IGNORE_FURTHER = '$StringTemplatingService::ignore:further'

  @Autowired(required=true)
  FolioLockService folioLockService
  
  @Autowired(required=true)
  GrailsApplication grailsApplication

  // 5 thread max executor for concurrency of background bulk taks.
  // When all 5 threads are consumed we add the work to the Queue
  // WHen the Queue is full (aside from obvious problems) the calling thread will
  // Attempt to run the work. This means the system shouldn't ever reject the work
  // but will instead just slow down.
  final ThreadPoolExecutor executor = new ThreadPoolExecutor(
    5, 5, 2, TimeUnit.MINUTES,
    new LinkedBlockingQueue<Runnable>(),
    new NamedThreadFactory('Tasks'),
    new CallerRunsPolicy())

  
  @PostConstruct
  public void init() {
    ConfigurableApplicationContext context = grailsApplication.mainContext as ConfigurableApplicationContext
    context.addApplicationListener(this)
  }
  
  private Set<TemplatedUrl> getTeplatedUrlsForRootBinding( final Map<String, List<StringTemplate>> templates, final StringTemplateBindings rootBindings ) {
    // Create the list of customized urls and the orginal
    final List<TemplatedUrl> noneProxiedUrls = [].with {
      add(new TemplatedUrl(
        name: TEMPLATED_URL_DEFAULT,
        url: rootBindings.inputUrl
      ))
      
        templates.get(CONTEXT_CUSTOMIZER).stream()
        .map({
          final StringTemplate template ->          
          new TemplatedUrl(
            name: template.name + (rootBindings.name ? "-${rootBindings.name}" : ''),
            url: template.customiseString(rootBindings)
          )
        })
        .forEach(it.&add)

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
        
        final Stream<TemplatedUrl> nested = templates.get(CONTEXT_PROXY).stream()
          .map({
            final StringTemplate template -> 
            
            new TemplatedUrl(
              name: template.name + (bindings.name ? "-${bindings.name}" : ''),
              url: template.customiseString(bindings)
            )
          });
          
        return Stream.concat(Stream.of(turl), nested)
      })
      .toSet()

    return generatedUrls
  }
  
  private void touchPti (String ptiId) {
    final String hql = '''
      UPDATE PlatformTitleInstance
      SET lastUpdated = :updated
      WHERE id = :ptiId
    '''
    
    final int count = GormUtils.gormStaticApi(Platform).executeUpdate(hql, [
      'ptiId'   : ptiId,
      'updated' : new Date()
    ])
    
    log.trace 'Updated timestamp for pti: {}', ptiId
  }
  
  private StringTemplateBindings getRootBindingsForPti ( String ptiId ) {
    List<String[]> result = bulidCriteriaAndList(PlatformTitleInstance) { 
      createAlias('platform', 'plat', JoinType.INNER_JOIN.getJoinTypeValue())
      
      idEq( ptiId )
      
      projectionList.with {
        property('url')
        property('plat.localCode')
      }
    }
    
    return new StringTemplateBindings(
      inputUrl: result[0][0],
      platformLocalCode: result[0][1] ?: ''
    )
  }
  
  private void deleteTemplatedUrlsForPTI (String ptiId) {
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
    
    log.trace 'Removed {} templates for pti: {}', count, ptiId
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
  public Map<String, List<StringTemplate>> findStringTemplatesForId(final String resourceID) {
    
    // Convert null to the NIL ID. This will help when we have an unsaved platform that has not yet been assigned an
    // ID. Using null would return the incorrect values bellow. The NIL UUID ensures the right items returned.
    final String theId = resourceID ?: UUIDs.NIL.toString()
    
    final GormInstanceApi<StringTemplate> tmpInstance = GormUtils.gormInstanceApi(StringTemplate)
    
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
      
      final String context = tmp.context?.value
      
      tmpInstance.discard(tmp)
      
      switch (context) {
        case CONTEXT_CUSTOMIZER:
          return true
          break
        case CONTEXT_PROXY:
          if (!templates[CONTEXT_PROXY].contains(tmp)) templates[CONTEXT_PROXY].add(tmp)
          return false
          break
        default:
          log.warn 'Unknown context type "{}" in template "{}"', context, tmp.id
      }
      // Default to exclude
      return false
    })
    .distinct()
    .toList()
    
    return templates
  }

  protected PlatformTitleInstance getPti( final String id ) {
    return GormUtils.gormStaticApi(PlatformTitleInstance).get(id)
  }
  
  private <T> List<T> bulidCriteriaAndList (Class<?> target, Map<String, ?> params = [:], @DelegatesTo(HibernateCriteriaBuilder) Closure criteria) {
    (List<T>) GormUtils.gormStaticApi(target).createCriteria().list(params, criteria)
  }
  
  private <T> List<T> bulidCriteriaAndListDistinct (Class<?> target, @DelegatesTo(HibernateCriteriaBuilder) Closure criteria) {
    (List<T>) GormUtils.gormStaticApi(target).createCriteria().listDistinct(criteria)
  }
  
  private <T> T bulidCriteriaAndGet (Class<?> target, @DelegatesTo(HibernateCriteriaBuilder) Closure criteria) {
    (T) GormUtils.gormStaticApi(target).createCriteria().get(criteria)
  }
  
  protected List<String> getPtiIdsToUpdateForPlatform ( final String platformID, final Date notUpdatedSince, final int maximum) {
    
    bulidCriteriaAndList(PlatformTitleInstance, [max: maximum]) {
      
      readOnly true
      
      eq 'platform.id', platformID
      lt 'lastUpdated', notUpdatedSince
      lt 'dateCreated', notUpdatedSince
      
      projectionList.with { 
        id()
      }
    }
  }

  @CurrentTenant
  public void refreshUrls() {
    // find all
    log.debug 'Refresh Urls now NOOP'
  }
  
  private boolean supportsEventType (Class<? extends ApplicationEvent> eventType) {
    if (!AbstractPersistenceEvent.class.isAssignableFrom(eventType)) return false

    switch (eventType) {

      // Event classes we care about
      case PreDeleteEvent:
      case PreUpdateEvent:
      case PreInsertEvent:
      case PostDeleteEvent:
      case PostInsertEvent:
      case PostUpdateEvent:
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
    
    final AbstractPersistenceEvent event = theEvent as AbstractPersistenceEvent
    
    final Object eo = event.getEntityObject()
    final Class theClass = eo.getClass()
    
    switch(event.getEventType()) {
        
      case PreUpdate:
      case PreInsert:
        if (PlatformTitleInstance.isAssignableFrom(theClass)) {
          log.trace 'Pre-(Insert/Update) event for PTI'
          updatePtiFromEvent(event)
          break
        }
      
      case PreDelete:
        // Post Add/Delete/Update of StringTemplate
        if (StringTemplate.isAssignableFrom(theClass)) {
          log.trace 'Pre-(Insert/Update/Delete) event for StringTemplate'
          addDeferredPlatformUpdatesFromTemplates(event)
        }
        break
        
      case PostUpdate:
        
        // PostUpdate of Platform
        // We don't track Add/Delete as both will
        // be dealt with by the update/remove of the PTIs.
        if (Platform.isAssignableFrom(theClass)) {
          log.trace 'Post-Update event for Platform'
          updatePlatformFromEvent(event)
          break
        }
        
      case PostInsert:
      case PostDelete:
        
        // Post Add/Delete/Update of StringTemplate
        if (StringTemplate.isAssignableFrom(theClass)) {
          log.trace 'Post-(Insert/Update/Delete) event for StringTemplate'
          updatePlatformsFromTemplateEvent(event)
        }
        
        break
      default: // NOOP...
        break
    }
  }
  
  private String ensureTenant () throws IllegalStateException {
    final String tenantId = Tenants.currentId()
    
    if (!tenantId) {
      throw new IllegalStateException('Could not determine the tenant ID')
    }
    
    tenantId
  }
  
  private void updatePtiFromEvent (final AbstractPersistenceEvent event) {
    
    final PlatformTitleInstance pti = event.entityObject as PlatformTitleInstance   
    
    // Read in the Platform
    final Platform platform = pti.platform
    
    // Default to empty set.
    Set<TemplatedUrl> generatedUrls = []
    if (pti.url) {
      log.trace('PTI has url execute applicable templates')
      
      // Fetch the applicable templates
      final Map<String, List<StringTemplate>> templates = findStringTemplatesForId (platform.id)
  
      // Bail early if no templates
      if (!templates.values().findResult { it.empty ? null : true }) {
        log.trace('No templates applicable to PTI')
        return
      }
  
      // Create the root bindings
      final StringTemplateBindings rootBindings = new StringTemplateBindings(
        inputUrl: pti.url,
        platformLocalCode: platform.localCode ?: ''
      )
  
      // Generate the templates as a set of objects
      generatedUrls = getTeplatedUrlsForRootBinding(templates, rootBindings).collect {
        it.resource = pti
        it
      } as Set
            
    } else {
      log.trace('PTI has no url associated with it, delete template only')
    }
    
    // Set the templates too.
    final EntityAccess ptiEa = event.entityAccess
    Set<TemplatedUrl> currentData = (Set<TemplatedUrl>) ptiEa.getProperty('templatedUrls')
    currentData.clear()
    currentData.addAll(generatedUrls)
    ptiEa.setProperty('templatedUrls', currentData)
    
    ptiEa.setProperty('lastUpdated', new Date())
  }
  
  private void updatePlatformFromEvent(final AbstractPersistenceEvent event) {
    
    // Single platform update after platform has been updated.
    // Only need to act if the properties we care about were changed.
    final Platform platform = event.entityObject as Platform
    final String platformId = platform.id
    
    if (!platformId) {
      log.error "Could not determine platform ID"
      return
    }
    
    // Add a backgroundTask to update the platform
    final String tenantId = ensureTenant()
    
    final Date now = new Date()
    
    executor.execute({ final String theTenant, final String thePlatform ->
      
      Tenants.withId(theTenant) {
        GormUtils.withTransaction {
          executeTemplatesForSinglePlatform( thePlatform, now )
        }
      }
      
    }.curry(tenantId, platformId))
  }
  
  protected void executeTemplatesForSinglePlatform (final String id, final Date notSince, Map<String, Set<StringTemplate>> explicitTemplates = [:]) {
    // Get a chunk of PTIs and keep repeating until all have been processed
    final Map<String, List<StringTemplate>> templates = findStringTemplatesForId(id)
    
    // Add any templates passed explicitly. Helps with the issue that the Template might not have
    // made it into the database before this event fires
    explicitTemplates.each { k, v ->
      v.each {
        if (!templates[k].contains(it)) {
          templates[k].add(it)
        }
      }
    }
    
    // Bail early if no templates
    boolean clearOnly = false
    if (!templates.values().findResult { it.empty ? null : true }) {
      log.trace('No templates applicable to Platform just clear all the URLs')
      clearOnly = true
    }
    
    // Batch PTIs
    final int max = 1000
    
    // This will help the nested closures "see" the logger
    final Logger log = this.log
    
    List <String> ptis = getPtiIdsToUpdateForPlatform(id, notSince, max)
    while (ptis.size() > 0) {
      
      // Act on each PTI
      final GormStaticApi<PlatformTitleInstance> ptiApi = GormUtils.gormStaticApi(PlatformTitleInstance)
      
      ptiApi.with {
        withNewSession {
          withNewTransaction {
            
            for (String ptiId : ptis) {
              final GormInstanceApi<TemplatedUrl> templateInstance = GormUtils.gormInstanceApi(TemplatedUrl)
              
              // This should cause the preUpdate event to fire
              deleteTemplatedUrlsForPTI(ptiId)
              
              if (!clearOnly) {
                // Create the root bindings
                final StringTemplateBindings rootBindings = getRootBindingsForPti(ptiId)
            
                // Generate the templates as a set of objects
                PlatformTitleInstance pti = ptiApi.read(ptiId)
                final int ptiUrlTotal = getTeplatedUrlsForRootBinding(templates, rootBindings).collect {
                  it.resource = pti
                  templateInstance.save(it, [failOnError: true] as Map)
                }?.size() ?: 0
                log.trace ('{} URLs generated for PTI {}', ptiUrlTotal, ptiId)
              }
              touchPti(ptiId)
            }
            // Next page
            ptis = ptis.size() == max ? getPtiIdsToUpdateForPlatform(id, notSince, max) : []
          }
        }
      }
    }
    
    log.debug 'Processed templates for Platform: {}', id 
  }
  
  private static class Stash {
    final Instant created = Instant.now()
    final Closure<?> work 
    public Stash ( Closure<?> work ) {
      this.work = work
    }
  }
  
  private static final Map<String, Stash> cache = new ConcurrentHashMap<String, Stash>(2)
  
  private String getCacheKeyForId( final String id ) {
    return "${ensureTenant()}:${id}"
  }
  
  private Stash getStashForId( final String id ) {
    return cache.remove( getCacheKeyForId(id) )
  }
  
  private void addStashForId ( final String id, final Stash stash ) {
    cache.put( getCacheKeyForId(id), stash )
    
    // Clear any old stashes
    cache.keySet().collect().each {
      
      // Remove if older than 2 mins
      if (cache.get(it)?.getCreated().isBefore(Instant.now().minusSeconds(120))) {
        cache.remove(it)
      }
    }
  }
  
  private void addDeferredPlatformUpdatesFromTemplates ( final AbstractPersistenceEvent event ) {
    // Multiple Platform update when String template added/removed/changed
    final StringTemplate template = event.entityObject as StringTemplate
    
    // Make sure we are in the context of a tenant.
    final String currentTenant = ensureTenant()
    
    // Overrides include any Platforms for update explicitly.
    final Set<String> theScopes = template.idScopes
    
    // Context
    final String theContext = template.context.value
    
    // Fetch the scopes from the Session to get "Previous" values
    final Set<String> previousScopes = getScopesForStringTemplate(template.id)
    
    // Explicitly re-process the differences between the scope now and the previous
    final Set<String> alsoInclude = ( theScopes + previousScopes ) - theScopes.intersect(previousScopes)
    
    // Even though this action is deferred until the commital of the template,
    // there is a good chance that it won't make it into the database for recall
    // from the reading session in the background thread. We add the template below,
    // as it doesn't need a database session for the text processing, for explicit inclusion
    // in the processing.
    final Map<String, Set<StringTemplate>> explicitTemplates = [:]
    if (event.eventType == PreInsert) {
      // explicitly add the template
      explicitTemplates[theContext] = [template] as Set
    }
    
    // Build the work but stash it for running once we see the "post" events
    addStashForId(template.id,
      new Stash({ final String tenantId, final Set<String> scopes, final String context, final Set<String> overrides, final Map<String, Set<StringTemplate>> tmpl ->
      
        Tenants.withId(tenantId) {
          GormStaticApi<Platform> ptiApi = GormUtils.gormStaticApi(Platform)
          List<String> platformIds = getAllPlatformIdsForTemplateParams(scopes, context, overrides)
          
          final Date now = new Date() 
          
          // For each platform add a background task.
          for (final String platformId : platformIds) {
            
            executor.execute({ String tid, String pltf, Date before, Map<String, Set<StringTemplate>> withTemplates ->
            
              Tenants.withId(tid) {
                GormUtils.withTransaction {
                  executeTemplatesForSinglePlatform(pltf, before, withTemplates)
                }
              }
            }.curry(tenantId, platformId, now, tmpl))
          }
        }
        
      }.curry(currentTenant, theScopes, theContext, alsoInclude, explicitTemplates)))
  }
  
  private void updatePlatformsFromTemplateEvent(final AbstractPersistenceEvent event) {
    
    // Multiple Platform update when String template added/removed/changed
    final StringTemplate template = event.entityObject as StringTemplate
    
    Stash theStash = getStashForId( template.id )
    if (!theStash) {
      log.warn 'No deferred updates found for Template {}', template.id
      return
    }
    
    executor.execute(theStash.work)
  }
  
  protected List<String> getAllPlatformIdsForTemplateParams(
    final Set<String> scopes,
    final String context,
    final Set<String> overrides = []) {
    
    List<String> platforms = bulidCriteriaAndListDistinct(Platform) { 
      or {
        if (scopes) {
          if (CONTEXT_CUSTOMIZER == context) {
            inList 'id', scopes
            
          } else if (CONTEXT_PROXY == context) {
            not {
              inList 'id', scopes
            }
          }
        }
        if (overrides) {
          // Overrides always inclusive
          inList 'id', overrides
        }
      }
      
      projectionList.with { 
        id()
      }
    }
    
    return platforms
  }
  
  private Set<String> getScopesForStringTemplate ( final String templateId  ) {
    
    final String hql = '''
      SELECT scope FROM StringTemplate tmp
      JOIN tmp.idScopes AS scope
      WHERE tmp.id = :templateId
    '''

    GormUtils.gormStaticApi(StringTemplate).executeQuery(hql, ['templateId': templateId]) as Set
  }
}
