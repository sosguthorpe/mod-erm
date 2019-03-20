package org.olf

import java.time.LocalDate
import javax.servlet.http.HttpServletRequest
import org.grails.web.servlet.mvc.GrailsWebRequest
import org.hibernate.sql.JoinType
import org.olf.erm.Entitlement
import org.olf.kb.AbstractCoverageStatement
import org.olf.kb.CoverageStatement
import org.olf.kb.ErmResource
import org.olf.kb.Pkg
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.RequestContextHolder
import grails.gorm.transactions.Transactional

/**
 * This service works at the module level, it's often called without a tenant context.
 */
public class CoverageService {
  
  private LocalDate ensureLocalDate ( final def dateObject ) {
    
    if (dateObject == null) return null
    if (dateObject instanceof LocalDate) return dateObject 
    
    // First we should clean any strings
    String dateStr = "${dateObject}".trim()
    
    // Empty string is Null equivalent
    if (dateStr == '') return null
    
    // Just trim the long dates. Crude but should work for standards
    if (dateStr.length() > 10) dateStr = dateStr.substring(0, 10)
    LocalDate.parse(dateStr)
  }
  
  private Map<String, Set<AbstractCoverageStatement>> addToRequestIfPresent (statements) {
    
    if (statements) {
      GrailsWebRequest rAtt = (GrailsWebRequest)RequestContextHolder.getRequestAttributes()
      if (rAtt) {
        final String controllerName = rAtt.controllerName
        final String actionName = rAtt.actionName
        final HttpServletRequest request = rAtt.request
        
        final String key = "${controllerName}.${actionName}.customCoverage"
        final Map<String, Set<AbstractCoverageStatement>> current = request.getAttribute(key) ?: [:]
        current.putAll(statements)
        request.setAttribute(key, current)
      }
    }
    
    statements
  }
  
  public Map<String, List<AbstractCoverageStatement>> lookupCoverageOverrides (final Map resultsMap, final String agreementId) {
    final List<ErmResource> resources = resultsMap?.get('results')
    
    resources ? lookupCoverageOverrides(resources, agreementId) : [:]
  }
  
  public Map<String, List<AbstractCoverageStatement>> lookupCoverageOverrides (final List<ErmResource> resources, final String agreementId) {
    
    if (!resources || resources.size() < 1) return [:]
    
    // Grab the resources
    final List statementQuery = Entitlement.createCriteria().list {
      
      createAlias 'resource', 'ermResource'
      createAlias 'ermResource.contentItems', 'pcis', JoinType.LEFT_OUTER_JOIN
      eq 'owner.id', agreementId
      
      or {
        
        final Set<String> ids = resources.collect{ it.id }
        
        // Linked to package.
        'in' 'resource.id', ids
        
        and {
          eq 'ermResource.class', Pkg
          'in' 'pcis.id', ids
        }
      }
      
      projections {
        property ('id')
        property ('resource.id')
        property ('pcis.id')
      }
    }
    
    Entitlement ent
    final Map<String, Set<AbstractCoverageStatement>> statements = statementQuery.collectEntries {
      if (!ent || ent.id != it[0]) {
        // Change the entitlement.
        ent = Entitlement.read (it[0])
      }
      
      // Add the coverage from the entitlement. Call collect to create a copy of the collection.
      [ "${it[2] ?: it[1]}" : ent.coverage.collect() ]
    }

    // Add to the request (if there is one) and return.
    addToRequestIfPresent (statements)
  }

  /**
   * Given a list of coverage statements, check that we already record the extents of the coverage
   * for the given title.
   * Shared functionality between title objects like
   * org.olf.kb.TitleInstance; org.olf.kb.PlatformTitleInstance; org.olf.kb.PackageContentItem; which
   * allows a coverage block to grow and expand as we see more coverage statements.
   * handles split coverage and other edge cases.
   * @param title The title (org.olf.kb.TitleInstance, org.olf.kb.PlatformTitleInstance, org.olf.kb.PackageContentItem, etc) we are talking about
   * @param coverage_statements The array of coverage statements [ [ startDate:YYYY-MM-DD, startVolume:...], [ stateDate:....
   */
  @Transactional
  public void extend(final ErmResource title, final List<Map> coverage_statements) {
    log.debug("Extend coverage statements on ${title} with ${coverage_statements}")

    coverage_statements.each { Map cs ->
      
      // First we should convert any string dates
      cs.startDate = ensureLocalDate (cs.startDate)
      cs.endDate = ensureLocalDate (cs.endDate)
      
      if (cs.startDate != null) {
        final List<CoverageStatement> existing_coverage = CoverageStatement.createCriteria().list {
          eq 'resource', title
          or {
            and {
              lte ( 'startDate', cs.startDate )
              or {
                isNull ( 'endDate' )
                gte ( 'endDate', cs.startDate )
              }
            }
            
            if (cs.endDate != null) {
              and {
                lte ( 'startDate', cs.endDate )
                or {
                  isNull ( 'endDate' )
                  gte ( 'endDate', cs.endDate )
                }
              }
            }
          }
        }
      
      // Do we already have any coverage statements for this item that overlap in any way with the coverage supplied?
      // If the start date or end date lies within any existing statement,
//      final List<CoverageStatement> existing_coverage = CoverageStatement.executeQuery(
//        "select cs from CoverageStatement as cs where cs.resource = :t " +
//          "and (" +
//            "(:start between cs.startDate and cs.endDate ) "
//              "OR "
//            "( :end between cs.startDate and cs.endDate )
//              "OR "
//            "( :start <= cs.startDate AND cs.endDate is null )
//          ") ".toString(),[t:title, start:(cs.startDate as LocalDate), end: (cs.endDate as LocalDate)]);


        if ( existing_coverage.size() > 0 ) {
          log.warn("Located existing coverage, determin extend or create additional")
  
          boolean new_coverage_is_already_subsumed = false
          existing_coverage.each { final CoverageStatement existing_cs ->
  
            // If the new coverage statement starts AFTER the one we are currently considering AND the statement we are currently
            // considering has an open end OR a date < the new statement, then the new coverage statement lies within the range of the first statement.
            if ( ( existing_cs.startDate <= cs.startDate ) && 
                 ( ( existing_cs.endDate == null ) || ( existing_cs.endDate <= cs.endDate ) ) ) {
              new_coverage_is_already_subsumed = true
              // We need to consider if we should set the END DATE in this scenario however!
            }
          }
  
          if ( !new_coverage_is_already_subsumed ) {
            // We need to create a new CS. For now, create as from the source - later on we will need to do something smarter
            // where we coalesce all the statements.
            def new_cs = new CoverageStatement(cs)
  
            new_cs.resource = title
            new_cs.startDate = cs.startDate
            new_cs.endDate = cs.endDate
            new_cs.startVolume = ("${cs.startVolume}".trim() ? cs.startVolume : null)
            new_cs.startIssue = ("${cs.startIssue}".trim() ? cs.startIssue : null)
            new_cs.endVolume = ("${cs.endVolume}".trim() ? cs.endVolume : null)
            new_cs.endIssue = ("${cs.endIssue}".trim() ? cs.endIssue : null)
            new_cs.save(flush:true, failOnError:true)
          }
        }
        else {
          // no existing coverage -- create it
          log.debug("No existing coverage - create new record")
          def new_cs = new CoverageStatement()
  
          new_cs.resource = title
          new_cs.startDate = cs.startDate
          new_cs.endDate = cs.endDate
          new_cs.startVolume = ("${cs.startVolume}".trim() ? cs.startVolume : null)
          new_cs.startIssue = ("${cs.startIssue}".trim() ? cs.startIssue : null)
          new_cs.endVolume = ("${cs.endVolume}".trim() ? cs.endVolume : null)
          new_cs.endIssue = ("${cs.endIssue}".trim() ? cs.endIssue : null)
          new_cs.save(flush:true, failOnError:true)
        }
      } else {
        log.warn("Coverage entry ${cs} contains no startDate")
      }
    }
  }
}
