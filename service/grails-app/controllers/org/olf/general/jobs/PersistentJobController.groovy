package org.olf.general.jobs


import org.springframework.http.HttpStatus

import com.k_int.okapi.OkapiTenantAwareController
import com.k_int.web.toolkit.refdata.RefdataValue

import grails.gorm.multitenancy.CurrentTenant
import grails.util.GrailsNameUtils
import groovy.util.logging.Slf4j


@Slf4j
@CurrentTenant
class PersistentJobController extends OkapiTenantAwareController<PersistentJob> {

  // Read only. Doesn't allow posts etc by default.
  public PersistentJobController() {
    super(PersistentJob, true)
  }
  
  def delete() {
    def instance = queryForResource(params.id)
    
    // Not found.
    if (instance == null) {
      transactionStatus.setRollbackOnly()
      notFound()
      return
    }
    
    final disallowedStatus = [
      instance.lookupStatus('In progress').id
    ]
    
    // Return invalid method if the status is disallowed 
    if (disallowedStatus.contains(instance.statusId)) {
      render status: HttpStatus.METHOD_NOT_ALLOWED.value()
      return
    }
    
    deleteResource instance

    render status: HttpStatus.NO_CONTENT
  }
  
  def save () {
    final Class type = params.type ? Class.forName("org.olf.general.jobs.${GrailsNameUtils.getClassName(params.type)}Job") : null
    
    if(!(type && PersistentJob.isAssignableFrom(type))) {
      return render (status: HttpStatus.NOT_FOUND)
    }
    
    // Lookup the default "queued" value here first as session flushes later are causing issues.
    final RefdataValue queuedStatus = PersistentJob.lookupStatus('queued')
    
    final PersistentJob instance = type.newInstance()
    
    bindData instance, getObjectToBind()
    instance.status = queuedStatus

    instance.save(failOnError: true, flush:true)

    respond instance
  }
  
  def fullLog( String persistentJobId ) {
    respond doTheLookup (LogEntry, {
      eq 'origin', persistentJobId

      order 'dateCreated', 'asc'
    })
  }
  
  def infoLog( String persistentJobId ) {
    respond doTheLookup (LogEntry, {
      eq 'origin', persistentJobId
      eq 'type', LogEntry.TYPE_INFO

      order 'dateCreated', 'asc'
    })
  }
  
  def errorLog( String persistentJobId ) {
    respond doTheLookup (LogEntry, 1000, {
      eq 'origin', persistentJobId
      eq 'type', LogEntry.TYPE_ERROR

      order 'dateCreated', 'asc'
    })
  }
  
  protected def doTheLookup (Class res = this.resource, int pageMax = 100, Closure baseQuery) {
    final int offset = params.int("offset") ?: 0
    final int perPage = Math.min(params.int('perPage') ?: params.int('max') ?: 10, pageMax)
    final int page = params.int("page") ?: (offset ? (offset / perPage) + 1 : 1)
    final List<String> filters = params.list("filters[]") ?: params.list("filters")
    final List<String> match_in = params.list("match[]") ?: params.list("match")
    final List<String> sorts = params.list("sort[]") ?: params.list("sort")
    
    if (params.boolean('stats')) {
      return simpleLookupService.lookupWithStats(res, params.term, perPage, page, filters, match_in, sorts, null, baseQuery)
    } else {
      return simpleLookupService.lookup(res, params.term, perPage, page, filters, match_in, sorts, baseQuery)
    }
  }
}
