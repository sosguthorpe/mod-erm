package org.olf

import grails.gorm.multitenancy.Tenants
import org.olf.kb.RemoteKB
import grails.events.annotation.Subscriber
import grails.gorm.multitenancy.WithoutTenant
import grails.gorm.transactions.Transactional
import org.olf.kb.TitleInstance
import org.olf.kb.Identifier
import org.olf.kb.IdentifierNamespace
import org.olf.kb.IdentifierOccurrence
import org.olf.general.RefdataValue
import org.olf.general.RefdataCategory

/**
 * This service works at the module level, it's often called without a tenant context.
 */
@Transactional
public class TitleInstanceResolverService {

  private static def class_one_namespaces = [
    'isbn',
    'issn',
    'eissn',
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

    List<TitleInstance> candidate_list = classOneMatch(citation.instanceIdentifiers);

    if ( candidate_list != null ) {
      int num_matches = candidate_list.size()
      switch ( num_matches ) {
        case(0):
          log.debug("No title match -- create");
          result = createNewTitleInstance(citation);
          break;
        case(1):
          log.debug("Exact match.");
          result = candidate_list.get(0);
          break;
        default:
          log.error("title matched {num_matches} records. Unable to continue. Matching IDs: ${candidate_list.collect { it.id }}");
          throw new RuntimeException("Title match returned too many items (${num_matches})");
          break;
      }
    }

    return result;
  }  

  private TitleInstance createNewTitleInstance(Map citation) {

    TitleInstance result = null;

    // Validate
    if ( ( citation.title?.length() > 0 ) &&
         ( citation.instanceIdentifiers.size() > 0 ) ) {

      result = new TitleInstance(
         title: citation.title
      )

      result.save(flush:true, failOnError:true);

      // Iterate over all idenifiers in the citation and add them to the title record. We manually create the identifier occurrence 
      // records rather than using the groovy collection, but it makes little difference.
      citation.instanceIdentifiers.each { id ->
        def id_lookup = lookupOrCreateIdentifier(id.value, id.namespace);
        RefdataValue approved_io_status = RefdataCategory.lookupOrCreate('IOStatus','APPROVED')
        def io_record = new IdentifierOccurrence(
                                                 title: result, 
                                                 identifier: id_lookup,
                                                 status:approved_io_status).save(flush:true, failOnError:true);
      }
    }
    else { 
      throw new RuntimeException("Insufficient detail to create title instance record");
    }

    // Refresh the newly minted title so we have access to all the related objects (eg Identifiers)
    result.refresh();
    result;
  }

  /**
   * Given an identifier in a citation { value:'1234-5678', namespace:'isbn' } lookup or create an identifier in the DB to represent that info
   */
  private Identifier lookupOrCreateIdentifier(String value, String namespace) {
    Identifier result = null;
    def identifier_lookup = Identifier.executeQuery('select id from Identifier as id where id.value = :value and id.ns.value = :ns',[value:value, ns:namespace]);
    switch(identifier_lookup.size() ) {
      case 0:
        IdentifierNamespace ns = lookupOrCreateIdentifierNamespace(namespace);
        result = new Identifier(ns:ns, value:value).save(flush:true, failOnError:true);
        break;
      case 1:
        result = identifier_lookup.get(0);
        break;
      default:
        throw new RuntimeException("Matched multiple identifiers for ${id}");
        break;
    }
    return result;
  }

  private IdentifierNamespace lookupOrCreateIdentifierNamespace(String ns) {
    def ns_lookup = IdentifierNamespace.findByValue(ns);
    if ( ns_lookup == null ) {
      ns_lookup = new IdentifierNamespace(value:ns).save(flush:true, failOnError:true);
    }
    return ns_lookup;
  }


  /**
   * Being passed a map of namespace, value pair maps, attempt to locate any title instances with class 1 identifiers (ISSN, ISBN, DOI)
   */
  private List<TitleInstance> classOneMatch(List identifiers) {
    // We want to build a list of all the title instance records in the system that match the identifiers. Hopefully this will return 0 or 1 records.
    // If it returns more than 1 then we are in a sticky situation, and cleverness is needed.
    List<TitleInstance> result = new ArrayList<TitleInstance>()


    identifiers.each { id ->
      if ( class_one_namespaces.contains(id.namespace.toLowerCase()) ) {
        // Look up each identifier
        def id_matches = Identifier.executeQuery('select id from Identifier as id where id.value = :value and id.ns.value = :ns',[value:id.value, ns:id.namespace])

        assert ( id_matches.size() <= 1 )

        // For each matched (It should only ever be 1)
        id_matches.each { matched_id ->
          // For each occurrence where the STATUS is APPROVED
          matched_id.occurrences.each { io ->
            if ( io.status?.value == 'APPROVED' ) {
              if ( result.contains(io.title) ) {
                // We have already seen this title, so don't add it again
              }
              else {
                log.debug("Adding title ${io.title.id} ${io.title.title} to matches for ${matched_id}");
                result << io.title
              }
            }
          }
        }
      }
      else {
        // log.debug("Identifier ${id} not from a class one namespace");
      }
    }

    log.debug("At end of classOneMatch, resut contains ${result.size()} titles");
    return result;
  }
}
