package org.olf.general

import net.sf.json.JSONObject
import grails.async.Promise
import grails.async.Promises

import grails.gorm.multitenancy.Tenants

import org.olf.general.StringTemplate
import org.olf.kb.Platform
import org.olf.kb.PlatformTitleInstance
import org.olf.kb.ErmResource

public class StringTemplatingService {

  /*
   * This method will take in an id of the form f311d130-8024-47c4-8a86-58f817dbefde
   * It will return a Map of StringTemplates grouped by context, that are relevant for this id
  */
  public Map<String, Set<StringTemplate>> findStringTemplatesForId(String id) {
    Map templates = [:]
    // Below we build up the relevant StringTemplates scope by scope

    // urlProxiers treat the StringTemplate's idScopes as a blacklist,  use queryNotInScope...
    templates.urlProxiers = queryNotInScopeWithContext(id, 'urlproxier')

    // urlCustomisers treat the StringTemplate's idScopes as a whitelist, use queryInScope...
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
  public ArrayList<Map> performStringTemplates(Map stringTemplates, Map binding) {
    ArrayList output = [
      [
        name: "defaultUrl",
        url: binding.inputUrl
      ]
    ]

    // First get all customised urls
    ArrayList customisedUrls = performTemplatingByContext(binding, "urlCustomisers", stringTemplates)
    // Then proxy all urls
    ArrayList proxiedUrls = performTemplatingByContext(binding, "urlProxiers", stringTemplates)
    
    // Finally we proxy all the customised urls
    ArrayList proxiedCustomisedUrls = []
    customisedUrls.each{ customiserMap ->
      /*
       customiserMap = [
         name: "customiserName"
         url: "customisedUrl"
       ]
      */
      // Think we only need a shallow copy here to pass to the proxiers
      JSONObject customBinding = new JSONObject()
      customBinding.putAll(binding)
      customBinding.inputUrl = customiserMap.url
      // Add all the proxied-customised urls to a list
      proxiedCustomisedUrls.addAll(performTemplatingByContext(customBinding, "urlProxiers", stringTemplates, customiserMap.name)) 
    }

    // Add all of these to the output List
    output.addAll(proxiedUrls)
    output.addAll(customisedUrls)
    output.addAll(proxiedCustomisedUrls)

    return output
  }


  // Simpler method which just returns a list of maps for a single StringTemplateContext--used for nested templating
  private ArrayList<Map> performTemplatingByContext(Map binding, String context, Map stringTemplates, String nameSuffix = '') {
    return stringTemplates[context]?.collect { st ->
      [
        name: nameSuffix ? "${st.name}-${nameSuffix}" : st.name,
        url: st.customiseString(binding)
      ]
    }
  }

  private Set<StringTemplate> queryNotInScopeWithContext(String id, String context) {
    Set<StringTemplate> stringTemplates = StringTemplate.executeQuery("""
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

  private Set<StringTemplate> queryInScopeWithContext(String id, String context) {
    Set<StringTemplate> stringTemplates = StringTemplate.executeQuery("""
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
  public void generateTemplatedUrlsForPti(final PlatformTitleInstance pti, Map stringTemplates, String platformLocalCode='' ) {
    log.debug "generateTemplatedUrlsForPti called for (${pti.id})"
    
    // First clear existing templatedUrls
    pti.templatedUrls.clear()

    // Then add new ones (If a url exists on this PTI)
    if (pti.url) {
      Map binding = [
        inputUrl: pti.url,
        platformLocalCode: platformLocalCode
      ]
      performStringTemplates(stringTemplates, binding).each { templatedUrl ->
        TemplatedUrl tu = new TemplatedUrl(templatedUrl)
        pti.addToTemplatedUrls(tu)
      }
      pti.save(failOnError: true)
    } else {
      log.warn "No url found for PTI (${pti.id})"
    }
  }

  public void generateTemplatedUrlsForErmResources(final String tenantId) {
    log.debug "generateTemplatedUrlsForErmResources called"
    Promise p = Promises.task {
      Tenants.withId(tenantId) {
        
        /* 
         * Right now we only scope URL templates to Platforms, so fetch a list of Platforms,
         * then for each one fetch the stringTemplates and a list of PTIs with that platform,
         * then perform stringTemplating on each PTI
         */
        Platform.getAll().each {
          Map stringTemplates = findStringTemplatesForId(it.id)
          String platformLocalCode = it.localCode
          /* 
           * Now we have the stringTemplates and platformLocalCode for the platform,
           * find all PTIs on this platform and remove then re-add the templatedUrls
           */
           PlatformTitleInstance.findAllByPlatform(it).each { pti ->
             generateTemplatedUrlsForPti(pti, stringTemplates, platformLocalCode)
           }
        }
      }
      
    }
    p.onError{ Throwable e ->
      log.error "Couldn't generate templated urls", e
    }
  }
}
