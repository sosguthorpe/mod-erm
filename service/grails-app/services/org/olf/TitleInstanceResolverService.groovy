package org.olf

import grails.gorm.multitenancy.Tenants
import org.olf.kb.RemoteKB
import grails.events.annotation.Subscriber
import grails.gorm.multitenancy.WithoutTenant
import grails.gorm.transactions.Transactional
import org.olf.kb.Package;
import org.olf.kb.TitleInstance
import org.olf.kb.Identifier

/**
 * This service works at the module level, it's often called without a tenant context.
 */
@Transactional
public class TitleInstanceResolverService {

  private static def class_one_namespaces = [
    'isbn',
    'issn',
    'doi'
  ];

  /**
   * Given a -valid- title citation with the minimum properties below, attempt to resolve the citation
   * into a local instance record. If no instance record is located, create one, and perform the necessary
   * cross-matching to create Inventory Instance records. The map contains a representation that is
   * the same as the attached JSON.
   *
   * {
   *   "title": "Nordic Psychology",
   *   "instanceMedium": "electronic",
   *   "instanceMedia": "journal",
   *   "instanceIdentifiers": [ 
   *     {
   *       "namespace": "issn",
   *       "value": "1234-5678"
   *     } ],
   *   "siblingInstanceIdentifiers": [ 
   *     {
   *       "namespace": "issn",
   *       "value": "1901-2276"
   *     } ]
   *   }
   */
  public TitleInstance resolve(Map citation) {
    log.debug("TitleInstanceResolverService::resolve(${citation})");
    TitleInstance result = null;

    def candidate_list = classOneMatch(citation.instanceIdentifiers);

    return result;
  }  


  /**
   * Being passed a map of namespace, value pair maps, attempt to locate any title instances with class 1 identifiers (ISSN, ISBN, DOI)
   */
  private List<TitleInstance> classOneMatch(List identifiers) {
    // We want to build a list of all the title instance records in the system that match the identifiers. Hopefully this will return 0 or 1 records.
    // If it returns more than 1 then we are in a sticky situation, and cleverness is needed.
    List<TitleInstance> result = new ArrayList<TitleInstance>()

    identifiers.each { id ->
      if ( class_one_namespaces.contains(id.namespace) ) {
        // Look up each identifier
        def id_matches = Identifier.executeQuery('select id from Identifier as id where id.value = :value and id.ns.value = :ns',[value:id.value, ns:id.namespace])

        assert ( id_matches.size() <= 1 )

        // For each matched (It should only ever be 1)
        id_matches.each { matched_id ->
          // For each occurrence where the STATUS is APPROVED
          matched_id.occurrences.each { io ->
            if ( io.status?.value == 'APPROVED' ) {
              result << io.title
            }
          }
        }
      }
    }

    log.debug("At end of classOneMatch, resut contains ${result.size()} titles");
    return result;
  }
}
