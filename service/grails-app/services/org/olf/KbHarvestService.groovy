package org.olf

import grails.gorm.transactions.Transactional
import org.springframework.scheduling.annotation.Scheduled
import java.text.SimpleDateFormat
import groovy.transform.CompileStatic
import groovy.transform.CompileDynamic
import groovy.util.logging.Slf4j


/**
 * See http://guides.grails.org/grails-scheduled/guide/index.html for info on this way of
 * scheduling tasks
 */
@Slf4j 
@CompileStatic 
@Transactional
class KbHarvestService {

  @Scheduled(fixedDelay = 45000L, initialDelay = 5000L) 
  void triggerSync() {
    log.info "Simple Job every 45 seconds :{}", new SimpleDateFormat("dd/M/yyyy hh:mm:ss").format(new Date())
  }

  public void triggerCacheUpdate() {
    log.debug("KBHarvestService::triggerCacheUpdate()");
    // org.olf.kb.adapters.KBPlusAdapter kbpa = new org.olf.kb.adapters.KBPlusAdapter()
    RemoteKB.list() { remotekb ->
    
    }
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
