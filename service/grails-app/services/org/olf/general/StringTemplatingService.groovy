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
import javax.sql.DataSource
import org.grails.datastore.gorm.GormInstanceApi
import org.grails.datastore.gorm.GormStaticApi
import org.grails.datastore.mapping.core.Datastore
import org.grails.datastore.mapping.dirty.checking.DirtyCheckable
import org.grails.datastore.mapping.engine.EntityAccess
import org.grails.datastore.mapping.engine.event.AbstractPersistenceEvent
import org.grails.datastore.mapping.engine.event.PostDeleteEvent
import org.grails.datastore.mapping.engine.event.PostInsertEvent
import org.grails.datastore.mapping.engine.event.PostUpdateEvent
import org.grails.datastore.mapping.engine.event.PreDeleteEvent
import org.grails.datastore.mapping.engine.event.PreInsertEvent
import org.grails.datastore.mapping.multitenancy.SchemaMultiTenantCapableDatastore
import org.grails.orm.hibernate.HibernateDatastore
import org.olf.kb.Platform
import org.olf.kb.PlatformTitleInstance
import org.springframework.context.ApplicationEvent
import org.springframework.context.ApplicationListener
import org.springframework.context.ConfigurableApplicationContext

import com.k_int.web.toolkit.utils.GormUtils

import grails.core.GrailsApplication
import grails.gorm.multitenancy.CurrentTenant
import grails.gorm.multitenancy.Tenants
import grails.gorm.transactions.Transactional
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import net.sf.ehcache.util.NamedThreadFactory
import services.k_int.core.FolioLockService

/**
 * @author sosguthorpe
 *
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

  protected List<String> executeTemplatesForSinglePTI (final String id, final Map<String, List<StringTemplate>> stringTemplates = null) {

    final PlatformTitleInstance pti = getPti(id)

    if (!pti) {
      log.warn('No PTI found with id {}', id)
      return Collections.emptyList()
    }

    return executeTemplatesForSinglePTI(pti, stringTemplates)
  }

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
    
    DirtyCheckable dcPti = (DirtyCheckable) pti
    
    // Mark as belonging to this service so that the save below is ignored by the other
    // listeners for PTIs
    dcPti.markDirty(EVENT_IGNORE_FURTHER, true, false)
    
    // Save the PTI
    GormUtils.gormInstanceApi(PlatformTitleInstance).save(pti, [failOnError: true] as Map)
    
    // Should now have an ID. Save the TemplatedUrls
    generatedUrls.each { TemplatedUrl url ->
      url.resource = pti
      GormUtils.gormInstanceApi(TemplatedUrl).save(url, [failOnError: true] as Map)
    }
  }
  
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

  protected PlatformTitleInstance getPti( final String id ) {
    return GormUtils.gormStaticApi(PlatformTitleInstance).get(id)
  }
  
  protected List<PlatformTitleInstance> getPtisToUpdateForPlatform ( final String platformID, final Date notUpdatedSince, final int maximum) {
    final List<PlatformTitleInstance> ptis = GormUtils.gormStaticApi(PlatformTitleInstance).createCriteria().list(max: maximum) {
      readOnly true
      
      eq 'platform.id', platformID
      lt 'lastUpdated', notUpdatedSince
      
    } as List
    
    return ptis
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
    
    AbstractPersistenceEvent event = theEvent as AbstractPersistenceEvent
    
    final Object eo = event.getEntityObject()
    
    switch(event.getEventType()) {       
      
      case PreUpdate:
      case PreInsert:
        // Post Add/Update of PlatformTitleInstance
        if (PlatformTitleInstance.isAssignableFrom(eo.getClass())) {
          log.debug 'Pre-(Insert/Update) event for PTI'
          updatePtiFromEvent(event)
        }
        break
        
      case PostUpdate:
        
//        // PostUpdate of Platform
//        // We don't track Add/Delete as
//        if (Platform.isAssignableFrom(eo.getClass())) {
//          log.debug 'Post-Update event for Platform'
//          massUpdatePlatformFromEvent(event)
//          break
//        } //else drop through...
        
      case PostDelete:
        
        // Post Add/Delete/Update of StringTemplate
//        if (StringTemplate.isAssignableFrom(eo.getClass())) {
//          log.debug 'Post-(Insert/Update/Delete) event for StringTemplate'
//          massUpdatePlatformsFromEvent(event)
//        }
        
        break
      default: // NOOP...
        break
    }
  }
  
  private String ensureTeanant () throws IllegalStateException {
    final String tenantId = Tenants.currentId()
    
    if (!tenantId) {
      throw new IllegalStateException('Could not determine the tenant ID')
    }
    
    tenantId
  }
  
  private void updatePtiFromEvent (final AbstractPersistenceEvent event) {
    
    final PlatformTitleInstance pti = event.entityObject as PlatformTitleInstance   
    
    // Re-read the pti
    final Platform platform = pti.platform
    
    // Default to empty set.
    Set<TemplatedUrl> generatedUrls = []
    if (pti.url) {
      log.debug('PTI has url execute applicable templates')
      
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
  
      // Generate the templates as a set of objects
      generatedUrls = getTeplatedUrlsForRootBinding(templates, rootBindings).collect {
        it.resource = pti
        it
      } as Set
            
    } else {
      log.debug('PTI has no url associated with it, delete template only')
    }
    
    // Delete All urls for this pti.
//    deleteTemplatedUrlsForPTI(pti.id)
    
    // Set the templates too.
    final EntityAccess ptiEa = event.entityAccess
    Set<TemplatedUrl> currentData = (Set<TemplatedUrl>) ptiEa.getProperty('templatedUrls')
    currentData.clear()
    currentData.addAll(generatedUrls)
    ptiEa.setProperty('templatedUrls', currentData)
    
    ptiEa.setProperty('lastUpdated', new Date())
    
//    // Should now have an ID. Save the TemplatedUrls
//    final GormInstanceApi<TemplatedUrl> urlApi = GormUtils.gormInstanceApi(TemplatedUrl)
//    
//    
//    
//    return generatedUrls.collect { TemplatedUrl url ->
//      url.resource = pti
//      urlApi.save(url, [failOnError: true] as Map)
//    }
//    List<TemplatedUrl> savedUrls = doInTenantTransaction( ensureTeanant() ) {
//      updateTemplatesForSinglePti( ptiId )
//    }
  }
}
