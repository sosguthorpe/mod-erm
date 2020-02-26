package org.olf

import com.k_int.okapi.OkapiTenantAdminService
import grails.gorm.multitenancy.Tenants
import org.hibernate.internal.SessionImpl
import org.hibernate.search.FullTextSession
import org.hibernate.search.MassIndexer
import org.olf.erm.SubscriptionAgreement
import org.olf.general.jobs.JobRunnerService

class BootStrap {

  def grailsApplication
  OkapiTenantAdminService okapiTenantAdminService
  JobRunnerService jobRunnerService
  
  def sessionFactory

  def init = { servletContext ->
    log.debug("mod-erm::init() ${grailsApplication.config.dataSource}")
//    okapiTenantAdminService.freshenAllTenantSchemas()
    
    jobRunnerService.populateJobQueue()
    
    Tenants.withId('diku_mod_agreements') { final String id, final SessionImpl session ->
      FullTextSession fullTextSession = org.hibernate.search.Search.getFullTextSession(session)
      MassIndexer indexer = fullTextSession.createIndexer()
      
      final String currentTid = Tenants.CurrentTenant.get()
      
      indexer.startAndWait();
    }
  }
  def destroy = {
  }
}
