package org.olf.general.jobs


import com.k_int.okapi.OkapiTenantAwareController

import grails.gorm.multitenancy.CurrentTenant
import grails.gorm.transactions.Transactional
import groovy.util.logging.Slf4j
import org.springframework.http.HttpStatus


@Slf4j
@CurrentTenant
class PersistentJobController extends OkapiTenantAwareController<PersistentJob> {

  // Read only. DOesn't allow posts etc.
  public PersistentJobController() {
    super(PersistentJob, false);
  }
  
  @Transactional
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
}
