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
  public List<LinkedHashMap> performStringTemplates(Map stringTemplates, Map binding) {
    List<LinkedHashMap> output = new ArrayList()
    output.add([name: "defaultUrl", url: binding.inputUrl.toString()])

    // First get all customised urls
    List<LinkedHashMap> customisedUrls = performTemplatingByContext(binding, "urlCustomisers", stringTemplates)
    // Then proxy all urls
    List<LinkedHashMap> proxiedUrls = performTemplatingByContext(binding, "urlProxiers", stringTemplates)
    
    // Finally we proxy all the customised urls
    List<LinkedHashMap> proxiedCustomisedUrls = []
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
    String ptiId = pti[0]
    String ptiUrl = pti[1]
    // Then add new ones (If a url exists on this PTI)
    if (ptiUrl) {
      Map binding = [
        inputUrl: ptiUrl,
        platformLocalCode: platformLocalCode
      ]
      performStringTemplates(stringTemplates, binding).each { templatedUrl ->
        TemplatedUrl tu = new TemplatedUrl(templatedUrl)
        tu.resource = PlatformTitleInstance.get(ptiId)
        tu.save(failOnError: true)
      }
    } else {
      log.warn "No url found for PTI (${ptiId})"
    }
  }

  // Split these out so that we can skip them in CompileStatic
  @CompileStatic(SKIP)
  private List<List<String>> batchFetchPlatforms(final int platformBatchSize, int platformBatchCount) {
    // Fetch the ids and localCodes for all platforms
    List<List<String>> platforms = Platform.createCriteria().list ([max: platformBatchSize, offset: platformBatchSize * platformBatchCount]) {
      order 'id'
      projections {
        property('id')
        property('localCode')
      }
    }
    return platforms
  }

  @CompileStatic(SKIP)
  private List<List<String>> batchFetchPtis(final int ptiBatchSize, int ptiBatchCount, String platformId) {
    List<List<String>> ptis = PlatformTitleInstance.createCriteria().list ([max: ptiBatchSize, offset: ptiBatchSize * ptiBatchCount]) {
      order 'id'
      eq('platform.id', platformId)
      projections {
        property('id')
        property('url')
      }
    }
    return ptis
  }

  @CompileStatic(SKIP)
  private void deleteAllTemplatedUrls() {
    TemplatedUrl.withNewTransaction {
      TemplatedUrl.executeUpdate('DELETE FROM TemplatedUrl')
    }
  }

  public void generateTemplatedUrlsForErmResources(final String tenantId) {
    log.debug "generateTemplatedUrlsForErmResources called"

    log.debug "LOGDEBUG TASK START TIME"
    Tenants.withId(tenantId) {
      // Initially we should clear all templated URLS in the system
      deleteAllTemplatedUrls()
      
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
    }
    log.debug "LOGDEBUG TASK END TIME"
  }
}
