package org.olf.general

import org.olf.general.StringTemplate

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
  public Map performStringTemplates(String id, Map binding) {
    Map output = [
      url: binding.inputUrl
    ]

    Map stringTemplates = findStringTemplatesForId(id)

    // First get all customised urls
    output.customisedUrls = performTemplatingByContext(binding, "urlCustomisers", stringTemplates)

    // Then proxy all urls--first the default one
    output.proxiedUrls = performTemplatingByContext(binding, "urlProxiers", stringTemplates)
    
    //Then proxy each customised url
    output.customisedUrls.collect { customiserMap ->
      /*
       customiserMap = [
         name: "customiserName"
         value: "customisedUrl"
       ]
      */
      // Think we only need a shallow copy here
      Map customBinding = binding.clone()
      customBinding.inputUrl = customiserMap.value

      customiserMap.proxiedUrls = performTemplatingByContext(customBinding, "urlProxiers", stringTemplates)
      return customiserMap
    } 

    return output
  }


  // Simpler method which just returns a list of maps for a single StringTemplateContext--used for nested templating
  private ArrayList<Map> performTemplatingByContext(Map binding, String context, Map stringTemplates) {
    return stringTemplates[context]?.collect { st ->
      [
        name: st.name,
        value: st.customiseString(binding)
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


}
