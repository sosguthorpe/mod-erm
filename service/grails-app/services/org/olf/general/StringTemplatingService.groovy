package org.olf.general

import net.sf.json.JSONObject

import grails.gorm.multitenancy.Tenants
import grails.gorm.transactions.Transactional

import org.olf.general.StringTemplate
import org.olf.kb.Platform
import org.olf.kb.PlatformTitleInstance
import org.olf.kb.ErmResource

import static groovy.transform.TypeCheckingMode.SKIP
import groovy.transform.CompileStatic

import com.k_int.web.toolkit.settings.AppSetting

@CompileStatic
@Transactional
public class StringTemplatingService {

  /*
   * This method will take in an id of the form f311d130-8024-47c4-8a86-58f817dbefde
   * It will return a Map of StringTemplates grouped by context, that are relevant for this id
  */
  public Map<String, Set<StringTemplate>> findStringTemplatesForId(String id) {
    Map templates = [:]
    // Below we build up the relevant StringTemplates scope by scope

    // urlProxiers treat the StringTemplate's idScopes as a denylist,  use queryNotInScope...
    templates.urlProxiers = queryNotInScopeWithContext(id, 'urlproxier')

    // urlCustomisers treat the StringTemplate's idScopes as a allowlist, use queryInScope...
    templates.urlCustomisers = queryInScopeWithContext(id, 'urlcustomiser')
    return templates
  }

  /*
   * This method will take in an id of the form f311d130-8024-47c4-8a86-58f817dbefde, and a binding of the form
  [
    inputUrl: "http://ebooks.ciando.com/book/index.cfm?bok_id=27955222",
    platformLocalCode: "ciando"
  ]
   * and these will be accessible from the template rule.
   * This method will store the business logic determining heirachy of StringTemplate contexts,
   * and whether these "stack" or not.
   */
  public List<LinkedHashMap<String, String>> performStringTemplates(Map stringTemplates, Map binding) {
    List<LinkedHashMap<String, String>> output = new ArrayList()
    output.add([name: "defaultUrl", url: binding.inputUrl.toString()])

    // First get all customised urls
    List<LinkedHashMap<String, String>> customisedUrls = performTemplatingByContext(binding, "urlCustomisers", stringTemplates)
    // Then proxy all urls
    List<LinkedHashMap<String, String>> proxiedUrls = performTemplatingByContext(binding, "urlProxiers", stringTemplates)
    
    // Finally we proxy all the customised urls
    List<LinkedHashMap<String, String>> proxiedCustomisedUrls = []
    customisedUrls.each{ customiserMap ->
      /*
       customiserMap = [
         name: "customiserName"
         url: "customisedUrl"
       ]
      */

      // Think we only need a shallow copy here to pass to the proxiers -- statically typed so rebuild
      Map customBinding = [
        inputUrl: customiserMap.url.toString(),
        platformLocalCode: binding.platformLocalCode.toString()
      ]

      // Add all the proxied-customised urls to a list
      proxiedCustomisedUrls.addAll(performTemplatingByContext(customBinding, "urlProxiers", stringTemplates, customiserMap.name.toString())) 
    }

    // Add all of these to the output List
    output.addAll(proxiedUrls)
    output.addAll(customisedUrls)
    output.addAll(proxiedCustomisedUrls)

    return output
  }


  // Simpler method which just returns a list of maps for a single StringTemplateContext--used for nested templating
  private List<LinkedHashMap> performTemplatingByContext(Map binding, String context, Map stringTemplates, String nameSuffix = '') {
    return stringTemplates[context]?.collect { StringTemplate st ->
      [
        name: nameSuffix ? "${st['name']}-${nameSuffix}" : st.name,
        url: st.customiseString(binding)
      ]
    }
  }

  private List<StringTemplate> queryNotInScopeWithContext(String id, String context) {
    List<StringTemplate> stringTemplates = StringTemplate.executeQuery("""
      SELECT st FROM StringTemplate st
      WHERE st.context.value = :context
      AND st NOT IN (
        SELECT st1 FROM StringTemplate st1
        JOIN st1.idScopes as scope
        WHERE scope = :id
      )
      """,
      [context: context, id: id]
    )
    return stringTemplates
  }

  private List<StringTemplate> queryInScopeWithContext(String id, String context) {
    List<StringTemplate> stringTemplates = StringTemplate.executeQuery("""
      SELECT st FROM StringTemplate st
      WHERE st.context.value = :context
      AND st IN (
        SELECT st1 FROM StringTemplate st1
        JOIN st1.idScopes as scope
        WHERE scope = :id
      )
      """,
      [context: context, id: id]
    )
    return stringTemplates
  }

  // This method generates the templatedUrls for PTIs, given the stringTemplates and platformLocalCode
  public void generateTemplatedUrlsForPti(final List<String> pti, Map stringTemplates, String platformLocalCode='') {
    log.debug "generateTemplatedUrlsForPti called for (${pti[0]})"
    try {
      String ptiId = pti[0]
      String ptiUrl = pti[1]
      PlatformTitleInstance fetchedPti = PlatformTitleInstance.get(ptiId)
      
      // If a url exists on this PTI--check if templatedUrls have changed, then delete and recreate
      if (ptiUrl) {
        Map binding = [
          inputUrl: ptiUrl,
          platformLocalCode: platformLocalCode
        ]
        List<LinkedHashMap<String, String>> newTemplatedUrls = performStringTemplates(stringTemplates, binding)

        //TODO only delete and make new if they differ
        List<String> etus = fetchedPti.templatedUrls.collect { tu -> tu.url }
        List<String> ntus = newTemplatedUrls.collect { tu -> tu.url }
        if (etus.size() != ntus.size() || etus.sort() != ntus.sort()) {
          log.debug "LOGDEBUG CHANGE: (${etus}) -> (${ntus})"
          deleteTemplatedUrlsForPTI(ptiId)

          newTemplatedUrls.each { templatedUrl ->
            TemplatedUrl tu = new TemplatedUrl(templatedUrl)
            tu.resource = fetchedPti
            tu.save(failOnError: true)
          }
        }
      } else {
        log.warn "No url found for PTI (${ptiId})"
      }
    } catch (Exception e) {
      log.error "Failed to update pti (${pti[0]}): ${e.message}"
    }
  }

  // Split these out so that we can skip them in CompileStatic
  // This method can batch fetch all Platforms or just those updated after a certain time
  @CompileStatic(SKIP)
  private List<List<String>> batchFetchPlatforms(final int platformBatchSize, int platformBatchCount, Date since = null) {
    // Fetch the ids and localCodes for all platforms
    List<List<String>> platforms = Platform.createCriteria().list ([max: platformBatchSize, offset: platformBatchSize * platformBatchCount]) {
      order 'id'
      if (since) {
        gt('lastUpdated', since)
      }
      projections {
        property('id')
        property('localCode')
      }
    }
    return platforms
  }


  // This method can batch fetch all PTIs on a platform or updated after a certain time (or both)
  @CompileStatic(SKIP)
  private List<List<String>> batchFetchPtis(final int ptiBatchSize, int ptiBatchCount, String platformId = null, Date since = null) {
    List<List<String>> ptis = PlatformTitleInstance.createCriteria().list ([max: ptiBatchSize, offset: ptiBatchSize * ptiBatchCount]) {
      order 'id'
      if (platformId) {
        eq('platform.id', platformId)
      }
      if (since) {
        gt('lastUpdated', since)
      }
      projections {
        property('id')
        property('url')
        if (!platformId) {
          property('platform.id')
        }
      }
    }
    return ptis
  }

  @CompileStatic(SKIP)
  private void deleteTemplatedUrlsForPTI(String ptiId) {
    TemplatedUrl.executeUpdate("""
      DELETE FROM TemplatedUrl as tu
      WHERE tu.id IN (
        SELECT tu.id FROM TemplatedUrl as tu
        JOIN tu.resource as pti
        WHERE pti.id = :id
      )
      """,
      [id: ptiId]
    )
  }

  /*
   * This is the actual method which gets called by the endpoint /erm/sts/template on a timer. 
   * Firstly it will look up the cursor indicating the last time the urls were refreshed.
   * Then it will go through and start calling generateTemplatedUrlsForErmResources for each object updated since
   */
  @CompileStatic(SKIP)
  void refreshUrls(String tenantId) {
    log.debug "stringTemplatingService::refreshUrls called with tenantId (${tenantId})"

    // Theoretically updates could happen after the process begins but before the url_refresh_cursor gets updated
    // So save the time before starting process as the new cursor pt
    // IMPORTANT--This only works because LastUpdated on the pti ISN'T triggered for a collection update, ie TemplatedUrls
    String new_cursor_value = System.currentTimeMillis()
    // Also create container for the current cursor value
    String last_refreshed
    AppSetting url_refresh_cursor

    Tenants.withId(tenantId) {
      // One transaction for fetching the initial value/creating AppSetting
      AppSetting.withNewTransaction {
        // Need to flush this initially so it exists for first instance
        // Set initial cursor to 0 so everything currently in system gets updated
        url_refresh_cursor = AppSetting.findByKey('url_refresh_cursor') ?: new AppSetting(
          section:'registry',
          settingType:'Date',
          key: 'url_refresh_cursor',
          value: 0
        ).save(flush: true, failOnError: true)
        last_refreshed = url_refresh_cursor.value
      }

      Date last_refreshed_date = new Date(Long.parseLong(last_refreshed))
      // Fetch stringTemplates that have changed since the last refresh
      List<String> sts = StringTemplate.createCriteria().list() {
        order 'id'
        gt('lastUpdated', last_refreshed_date)
        projections {
          property('id')
        }
      }

      if (sts.size() > 0) {
        // StringTemplates have changed, ignore more granular changes and just refresh everything
        generateTemplatedUrlsForErmResources(tenantId)
      } else {
        // FIRST - refresh all updated PTIs
        PlatformTitleInstance.withNewTransaction{
          final int ptiBatchSize = 100
          int ptiBatchCount = 0
          List<List<String>> ptis = batchFetchPtis(ptiBatchSize, ptiBatchCount, null, last_refreshed_date)
          while (ptis && ptis.size() > 0) {
            ptiBatchCount ++
            ptis.each { pti ->
              log.debug "LOGDEBUG CHANGED PTI: ${pti}"
              // Here we send it to the generic case not the specific one to get the queueing behaviour
              generateTemplatedUrlsForErmResources(
                tenantId,
                [
                  context: 'pti',
                  id: pti[0],
                  platformId: pti[2]
                ]
              )
            }
            // Next page
            ptis = batchFetchPtis(ptiBatchSize, ptiBatchCount, null, last_refreshed_date)
          }
        }

        // Next - refresh all updated Platforms
        Platform.withNewTransaction{
          final int platformBatchSize = 100
          int platformBatchCount = 0
          List<List<String>> platforms = batchFetchPlatforms(platformBatchSize, platformBatchCount, last_refreshed_date)
          while (platforms && platforms.size() > 0) {
            platformBatchCount ++
            platforms.each { platform ->
              // Here we send it to the generic case not the specific one to get the queueing behaviour
              generateTemplatedUrlsForErmResources(
                tenantId,
                [
                  context: 'platform',
                  id: platform[0]
                ]
              )
              // Next page
              platforms = batchFetchPlatforms(platformBatchSize, platformBatchCount, last_refreshed_date)
            }
          }
        }
      }

      //One transaction for updating value with new refresh time
      AppSetting.withNewTransaction {
        url_refresh_cursor.value = new_cursor_value
        url_refresh_cursor.save(failOnError: true)
      }
    }
  }

  /*
   * Trigger plan - have a mutex boolean, running, and a queue.
   *
   * When we recieve a "refresh" event, we check if running. If so we add a note to the queue
   * If neither are true we run the task. Once a task is finished we run again if queue non-empty.
  */

  static boolean running = false
  private ArrayList<Map<String, String>> taskQueue = new ArrayList<Map<String, String>>()

  private void addTaskToTaskQueue(Map<String, String> params) {
    if (params.context == 'stringTemplate') {
      // If we're going to run a full system refresh we can just clear the current queue and run that instead
      taskQueue.clear()
      taskQueue.add(params)
    } else {
      taskQueue.add(params)
    }
    log.debug "LOGDEBUG TASK QUEUE SIZE: ${taskQueue.size()}"
  }

  /* IMPORTANT NOTE -- When calling this for PTI/Platforms, wrap in a new transaction. Left out of this block so that
   * many edits can happen in one transaction block if called for multiple.
   */
  public void generateTemplatedUrlsForErmResources(final String tenantId, Map<String, String> params = [context: 'stringTemplate']) {
    log.debug "generateTemplatedUrlsForErmResources called"

    // If running then just add to queue
    synchronized ( this ) {
      if (running == true) {
        addTaskToTaskQueue(params)
        return
      } else {
        running = true
      }
    }

    log.debug "LOGDEBUG TASK START TIME"
    Tenants.withId(tenantId) {
      switch (params.context) {
        case 'stringTemplate':
          /* 
          * Right now we only scope URL templates to Platforms, so fetch a list of Platforms,
          * then for each one fetch the stringTemplates and a list of PTIs with that platform,
          * then perform stringTemplating on each PTI
          */

          final int platformBatchSize = 100
          int platformBatchCount = 0

          Platform.withNewTransaction {
            // Fetch the ids and localCodes for all platforms
            List<List<String>> platforms = batchFetchPlatforms(platformBatchSize, platformBatchCount)

            // This will return [[00998c04-8ab3-49ab-9053-c3e8cff328c2, ciando], [021bfce4-0533-465e-9340-1ceaad2a530f, localCode2], ...]
            while (platforms && platforms.size() > 0) {
              platformBatchCount ++
              platforms.each { platform ->
                Map stringTemplates = findStringTemplatesForId(platform[0])
                String platformLocalCode = platform[1]

                  /* 
                  * Now we have the stringTemplates and platformLocalCode for the platform,
                  * find all PTIs on this platform and remove then re-add the templatedUrls
                  */

                  final int ptiBatchSize = 100
                  int ptiBatchCount = 0
                  List<List<String>> ptis = batchFetchPtis(ptiBatchSize, ptiBatchCount, platform[0])
                  while (ptis && ptis.size() > 0) {
                    ptiBatchCount ++
                    ptis.each { pti ->
                      generateTemplatedUrlsForPti(pti, stringTemplates, platformLocalCode)
                    }

                    // Next page
                    ptis = batchFetchPtis(ptiBatchSize, ptiBatchCount, platform[0])
                  }
                // Next page
                platforms = batchFetchPlatforms(platformBatchSize, platformBatchCount)
              }
            }
          }
          break;
        case 'platform':
          Platform p = Platform.read(params.id)
          Map stringTemplates = findStringTemplatesForId(p.id)
          String platformLocalCode = p.localCode
          /* 
          * We have the stringTemplates and platformLocalCode for the platform,
          * find all PTIs on this platform and remove then re-add the templatedUrls
          */

          final int ptiBatchSize = 100
          int ptiBatchCount = 0
          List<List<String>> ptis = batchFetchPtis(ptiBatchSize, ptiBatchCount, p.id)
          while (ptis && ptis.size() > 0) {
            ptiBatchCount ++
            ptis.each { pti ->
              generateTemplatedUrlsForPti(pti, stringTemplates, platformLocalCode)
            }

            // Next page
            ptis = batchFetchPtis(ptiBatchSize, ptiBatchCount, p.id)
          }
          break;
        case 'pti':
          //In this case we don't have the platform, but we passed the platformId in the params
          Platform platform = Platform.read(params.platformId)
          PlatformTitleInstance pti = PlatformTitleInstance.read(params.id)

          Map stringTemplates = findStringTemplatesForId(params.platformId)
          String platformLocalCode = platform.localCode

          generateTemplatedUrlsForPti([pti.id, pti.url], stringTemplates, platformLocalCode)
          break;
        default:
          log.warn "Don't know what to do with params context (${params.context})"
          break;
      }
    }
    log.debug "LOGDEBUG TASK END TIME"

    synchronized ( this ) {
      // Task finished, turn 'running' boolean off
      running = false

      if (taskQueue.size() > 0) {
        // There are still tasks in the queue, run again
        Map<String, String> runParams = taskQueue.remove(0)
        generateTemplatedUrlsForErmResources(tenantId, runParams)
      }
    }
  }
}
