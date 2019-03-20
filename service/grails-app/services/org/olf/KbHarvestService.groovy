package org.olf

import grails.gorm.transactions.Transactional
import org.springframework.scheduling.annotation.Scheduled
import java.text.SimpleDateFormat
import groovy.transform.CompileStatic
import groovy.transform.CompileDynamic
import groovy.util.logging.Slf4j
import static grails.async.Promises.*
import static groovy.transform.TypeCheckingMode.SKIP

import com.k_int.okapi.OkapiTenantAdminService;
import grails.gorm.multitenancy.Tenants

import org.olf.kb.RemoteKB

/**
 * See http://guides.grails.org/grails-scheduled/guide/index.html for info on this way of
 * scheduling tasks
 */
@Slf4j 
@CompileStatic
@Transactional
class KbHarvestService {

  // WIthout this, the service will not be lazy initialised, and the tasks won't be scheduled until an external 
  // tries to access the instance.
  boolean lazyInit = false 

  OkapiTenantAdminService okapiTenantAdminService
  KnowledgeBaseCacheService knowledgeBaseCacheService

  @Scheduled(fixedDelay = 3600000L, initialDelay = 10000L) // Run task every hour, wait 2 mins before running at startup
  void triggerSync() {
    log.debug "Simple Job every 45 seconds :{}", new SimpleDateFormat("dd/M/yyyy hh:mm:ss").format(new Date())
    okapiTenantAdminService.getAllTenantSchemaIds().each { tenant_id ->
      log.debug "Perform trigger sync for tenant ${tenant_id}";
      Tenants.withId(tenant_id) {
        triggerCacheUpdate();
      }
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
