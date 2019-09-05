package org.olf

import javax.servlet.http.HttpServletRequest

import org.grails.web.servlet.mvc.GrailsWebRequest
import org.hibernate.sql.JoinType
import org.olf.dataimport.internal.PackageSchema.CoverageStatementSchema
import org.olf.erm.Entitlement
import org.olf.kb.AbstractCoverageStatement
import org.olf.kb.CoverageStatement
import org.olf.kb.ErmResource
import org.olf.kb.Pkg
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.validation.ObjectError
import org.springframework.web.context.request.RequestContextHolder

import grails.gorm.transactions.Transactional
import groovy.util.logging.Slf4j

/**
 * This service works at the module level, it's often called without a tenant context.
 */
@Slf4j
public class CoverageService {
  MessageSource messageSource
  
  private Map<String, Iterable<AbstractCoverageStatement>> addToRequestIfPresent (final Map<String, Iterable<AbstractCoverageStatement>> statements) {
    
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
  
  public Map<String, Iterable<AbstractCoverageStatement>> lookupCoverageOverrides (final Map resultsMap, final String agreementId = null) {
    final List<ErmResource> resources = resultsMap?.get('results')
    
    resources ? lookupCoverageOverrides(resources, agreementId) : [:]
  }
  
  public Map<String, Iterable<AbstractCoverageStatement>> lookupCoverageOverrides (final Iterable<ErmResource> resources, final String agreementId = null) {
    
    if (!resources || resources.size() < 1) return [:]
    
    // Grab the resources
    final List statementQuery = Entitlement.createCriteria().list {
      
      createAlias 'resource', 'ermResource'
      createAlias 'ermResource.contentItems', 'pcis', JoinType.LEFT_OUTER_JOIN
      if (agreementId) {
        eq 'owner.id', agreementId
      } else {
        isNotNull 'owner.id'
      }
     
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
  public void extend(final ErmResource title, final Iterable<CoverageStatementSchema> coverage_statements) {
    log.debug("Extend coverage statements on ${title}(${title.id}) with ${coverage_statements}")

    // Iterate through each of the statements we want to add
    coverage_statements.each { CoverageStatementSchema cs ->
      
      if (cs.validate()) {
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

        if ( existing_coverage.size() > 0 ) {
          log.warn("Located ${existing_coverage.size()} existing coverage statements overlapping with ${cs}, determine extend or create additional")

          // All these coverage statements overlap with the statement in question - we should be able to coalesce all of them to the min() and max() values
          def min_start_date = cs.startDate
          def min_start_issue = cs.startIssue
          def min_start_volume = cs.startVolume
          def max_end_date = cs.endDate
          def max_end_issue = cs.endIssue
          def max_end_volume = cs.endVolume
          
          boolean new_coverage_is_already_subsumed = false
          existing_coverage.each { final CoverageStatement existing_cs ->

            log.debug("Checking existing start: ${existing_cs.startDate} end: ${existing_cs.endDate} new start: ${cs.startDate} new end: ${cs.endDate}")
            log.debug("test ${existing_cs.startDate} < ${min_start_date}")

            // We gather together the extreme ends of all the current coverage statements and the new one (Starting with the new on in the declarations above)
            // For each existing coverage statement, expand out the max and min

            // EG: If this coverage statement has a start date, and the min seen is null or less than the current earliest date, then update the min_start_date
            if ( ( existing_cs.startDate != null ) && ( ( min_start_date == null ) || ( existing_cs.startDate < min_start_date ) ) ) min_start_date = existing_cs.startDate
            if ( ( existing_cs.endDate != null ) && ( ( max_end_date == null ) || ( existing_cs.endDate < max_end_date ) ) ) max_end_date = existing_cs.endDate
            if ( ( existing_cs.startVolume != null ) && ( ( min_start_volume == null ) || ( existing_cs.startVolume < min_start_volume ) ) ) min_start_volume = existing_cs.startVolume
            if ( ( existing_cs.startIssue != null ) && ( ( min_start_issue == null ) || ( existing_cs.startIssue < min_start_issue ) ) ) min_start_issue = existing_cs.startIssue
            if ( ( existing_cs.endVolume != null ) && ( ( max_end_volume == null ) || ( existing_cs.endVolume > max_end_volume ) ) ) max_end_volume = existing_cs.endVolume
            if ( ( existing_cs.endIssue != null ) && ( ( max_end_issue == null ) || ( existing_cs.endIssue > max_end_issue ) ) ) max_end_issue = existing_cs.endIssue

            // If the new coverage statement starts AFTER the one we are currently considering AND the statement we are currently
            // considering has an open end OR a date < the new statement, then the new coverage statement lies within the range of the first statement.
            if ( ( existing_cs.startDate <= cs.startDate ) &&               // If the existing coverage starts before the new
                 ( ( existing_cs.endDate == null ) ||                       // and the existing is open ended
                   ( existing_cs.endDate != null && cs.endDate == null ) || //     or the existing is not null and the new one is not set
                   ( existing_cs.endDate >= cs.endDate ) ) ) {              //     or the existing ends after the new coverage statement
              new_coverage_is_already_subsumed = true
              // We need to consider if we should set the END DATE in this scenario however!
            }
          }
  
          if ( new_coverage_is_already_subsumed ) {
            // The coverage we are being asked to merge is already covered by the existing statements. Do nothing.
            log.debug("Coverage is already subsumed - do nothing");
          }
          else {
            // After checking, we need to create a new coverage statement as no overlap was found
            log.debug("Remove existing coverage ${existing_coverage}");

            // Delete existing coverage statements
            existing_coverage.each { final CoverageStatement existing_cs ->
             existing_cs.delete(flush:true)
            }

            def new_cs = new CoverageStatement()
            new_cs.resource = title
            new_cs.startDate = min_start_date
            new_cs.endDate = max_end_date
            new_cs.startVolume = nullIfBlank(min_start_volume)
            new_cs.startIssue = nullIfBlank(min_start_issue)
            new_cs.endVolume = nullIfBlank(max_end_volume)
            new_cs.endIssue = nullIfBlank(max_end_issue)

            log.debug("   -> Replace with New coverage: ${new_cs}")
            new_cs.save(flush:true, failOnError:true)
          }
        }
        else {
          // no existing coverage -- create it
          log.debug("New coverage is not subsumed by existing - create a new coverage statement");
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
        
        // Not valid coverage statement
        cs.errors.allErrors.each { ObjectError error ->
          log.error (messageSource.getMessage(error, LocaleContextHolder.locale))
        }
      }
    }
  }

  public String nullIfBlank(final String value) {
    return (value?.trim()?.length() ?: 0) > 0 ? value : null
  }

  public void coalesceCoverageStatements() {
    CoverageStatement.executeQuery('select cs.resource.id,count(*) from CoverageStatement as cs group by cs.startDate, cs,endDate').each {
      log.debug("statements for ${it[0]} has ${it[1]} possibly duplicate coverage statements");
    }
  }
}
