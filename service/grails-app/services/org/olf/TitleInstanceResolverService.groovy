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

  def sessionFactory

  private static final String TEXT_MATCH_TITLE_QRY_0 = ''' select * from diku_olf_erm.title_instance WHERE ti_title % text('cancer') AND similarity(ti_title, text('cancer')) > 0.35 ORDER BY  similarity(ti_title, text('cancer')) desc LIMIT 20 ''';

  private static final String TEXT_MATCH_TITLE_QRY_2 = 'select * from title_instance where ti_title like :qrytitle and 0 < :threshold'

  private static final String TEXT_MATCH_TITLE_QRY = 'select * from title_instance WHERE ti_title % :qrytitle AND similarity(ti_title, :qrytitle) > :threshold ORDER BY  similarity(ti_title, :qrytitle) desc LIMIT 20'

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

    int num_matches = candidate_list.size()

    if ( num_matches == 0 ) {
      log.debug("No matches on identifier - try a fuzzy text match on title(${citation.title})");
      // No matches - try a simple title match
      candidate_list = titleMatch(citation.title);
      num_matches = candidate_list.size()
    }

    if ( candidate_list != null ) {
      switch ( num_matches ) {
        case(0):
          log.debug("No title match");
          result = createNewTitleInstance(citation);
          break;
        case(1):
          log.debug("Exact match.");
          result = candidate_list.get(0);
          checkForEnrichment(result, citation);
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

    // With the introduction of fuzzy title matching, we are relaxing this constraint and
    // will expect to enrich titles without identifiers when we next see a record. BUT
    // this needs elaboration and experimentation.
    //
    // boolean title_is_valid =  ( ( citation.title?.length() > 0 ) && ( citation.instanceIdentifiers.size() > 0 ) )
    // 
    boolean title_is_valid = ( ( citation.title != null ) &&
                               ( citation.title.length() > 0 ) );

    // Validate
    if ( title_is_valid == true ) {

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
   * Check to see if the citation has properties that we really want to pull through to
   * the DB. In particular, for the case where we have created a stub title record without
   * an identifier, we will need to add identifiers to that record when we see a record that
   * suggests identifiers for that title match.
   */ 
  private void checkForEnrichment(TitleInstance title, Map citation) {
    return;
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
   * Attempt a fuzzy match on the title
   */
  private List<TitleInstance> titleMatch(String title) {
    List<TitleInstance> result = new ArrayList<TitleInstance>()
    final session = sessionFactory.currentSession
    final sqlQuery = session.createSQLQuery(TEXT_MATCH_TITLE_QRY)

    result = sqlQuery.with {
      addEntity(TitleInstance)
      // Set query title - I know this looks a little odd, we have to manually quote this and handle any
      // relevant escaping... So this code will probably not be good enough long term.
      setString('qrytitle',title);
      setFloat('threshold',0.45f)
 
      // Get all results.
      list()
    }
 
    return result
  }

  /**
   * Being passed a map of namespace, value pair maps, attempt to locate any title instances with class 1 identifiers (ISSN, ISBN, DOI)
   */
  private List<TitleInstance> classOneMatch(List identifiers) {
    // We want to build a list of all the title instance records in the system that match the identifiers. Hopefully this will return 0 or 1 records.
    // If it returns more than 1 then we are in a sticky situation, and cleverness is needed.
    List<TitleInstance> result = new ArrayList<TitleInstance>()

    def num_class_one_identifiers = 0;

    identifiers.each { id ->
      if ( class_one_namespaces.contains(id.namespace.toLowerCase()) ) {

        num_class_one_identifiers++;

        // Look up each identifier
        log.debug("${id} - try class one match");
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
            else {
              throw new RuntimeException("Match on non-approved");
            }
          }
        }
      }
      else {
        log.debug("Identifier ${id} not from a class one namespace");
      }
    }

    log.debug("At end of classOneMatch, resut contains ${result.size()} titles");
    return result;
  }
}
