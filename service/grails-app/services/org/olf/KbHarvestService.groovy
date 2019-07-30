package org.olf

import static groovy.transform.TypeCheckingMode.SKIP

import java.time.Instant

import org.olf.general.jobs.PackageIngestJob
import org.olf.kb.RemoteKB
import org.springframework.scheduling.annotation.Scheduled

import com.k_int.okapi.OkapiTenantAdminService
import com.k_int.okapi.OkapiTenantResolver

import grails.events.annotation.Subscriber
import grails.gorm.multitenancy.Tenants
import grails.gorm.transactions.Transactional
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

/**
 * See http://guides.grails.org/grails-scheduled/guide/index.html for info on this way of
 * scheduling tasks
 */
@Slf4j 
@CompileStatic
@Transactional
class KbHarvestService {

  // Without this, the service will be lazy initialised, and the tasks won't be scheduled until an external 
  // tries to access the instance.
  boolean lazyInit = false 

  OkapiTenantAdminService okapiTenantAdminService
  KnowledgeBaseCacheService knowledgeBaseCacheService
  
  @Subscriber('okapi:dataload:sample')
  public void onDataloadSample (final String tenantId, final String value, final String existing_tenant, final String upgrading, final String toVersion, final String fromVersion) {
    log.debug "Perform trigger sync for new tenant ${tenantId} via data load event"
    final String schemaName = OkapiTenantResolver.getTenantSchemaName(tenantId)
    triggerUpdateForTenant(schemaName)
  }
  
//  @Subscriber('okapi:tenant_enabled')
//  public void onTenantEnabled (final String tenant_id) {
//    log.debug "Perform trigger sync for new tenant ${tenant_id} via new tenant event"
//    final String schemaName = OkapiTenantResolver.getTenantSchemaName(tenant_id)
//    triggerUpdateForTenant(schemaName)
//  }
  
  @CompileStatic(SKIP)
  private synchronized void triggerUpdateForTenant(final String tenant_schema_id) {
    Tenants.withId(tenant_schema_id) {
      
      PackageIngestJob job = PackageIngestJob.findByStatusInList([
        PackageIngestJob.lookupStatus('Queued'),
        PackageIngestJob.lookupStatus('In progress')
      ])
      
      if (!job) {
        job = new PackageIngestJob(name: "Scheduled Ingest Job ${Instant.now()}")
        job.setStatusFromString('Queued')
        job.save(failOnError: true, flush: true)
      } else {
        log.debug('Harvester already running or scheduled. Ignore.')
      }
    }
  }

  @Scheduled(fixedDelay = 3600000L, initialDelay = 1000L) // Run task every hour
  void triggerSync() {
    log.debug "Running scheduled KB sync for all tenants :{}", Instant.now()

    // ToDo: Don't think this will work for newly added tenants - need to investigate.
    okapiTenantAdminService.getAllTenantSchemaIds().each { tenant_schema_id ->
      log.debug "Perform trigger sync for tenant schema ${tenant_schema_id}"
      triggerUpdateForTenant(tenant_schema_id as String)
    }
  }
  
  @CompileStatic(SKIP)
  public void triggerCacheUpdate() {
    log.debug("KBHarvestService::triggerCacheUpdate()");
    RemoteKB.executeQuery('select rkb.id from RemoteKB as rkb where rkb.type is not null and rkb.active = :true',['true':true]).each { remotekb_id ->
      log.debug("Run sync on ${remotekb_id}");
      knowledgeBaseCacheService.runSync((String)remotekb_id);
    }
    log.debug("KbHarvestService::triggerCacheUpdate() completed");
  }



  // @CompileDynamic
  // Date startAtDate() { 
  //   Date startAt = new Date()
  //   use(TimeCategory) {
  //     startAt = startAt + 1.minute
  //   }
  //   startAt
  // }

  // void scheduleFollowupEmail(String email, String message) {
    // threadPoolTaskScheduler.schedule(new EmailTask(emailService, email, message), startAtDate()) 
  // }
}
