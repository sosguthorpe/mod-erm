package org.olf.general

import org.olf.general.StringTemplate

public class StringTemplatingService {

  /*
   * This method will take in an id of the form f311d130-8024-47c4-8a86-58f817dbefde
   * It will return a Map of StringTemplates grouped by context, that are relevant for this id 
  */
  Map<String, Set<StringTemplate>> findStringTemplatesForId(String id) {
    Map templates = [:]

    println("findStringTemplatesForId called with id (${id})")
    // Below we build up the relevant StringTemplates scope by scope

    // urlProxiers treat the StringTemplate's idScopes as a blacklist,  use queryNotInScope...
    templates.urlProxiers = queryNotInScopeWithContext(id, 'urlproxier')

    // urlCustomisers treat the StringTemplate's idScopes as a whitelist, use queryInScope...
    templates.urlCustomisers = queryInScopeWithContext(id, 'urlcustomiser')

    return templates
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
