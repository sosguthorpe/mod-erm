package org.olf.general.jobs

import static grails.async.Promises.*

import grails.async.Promise
import grails.events.annotation.Subscriber
import grails.gorm.multitenancy.Tenants
import groovy.util.logging.Slf4j
import java.time.Instant
import java.util.concurrent.TimeUnit
import org.grails.async.factory.future.CachedThreadPoolPromiseFactory

@Slf4j
class JobLoggingService {
  
  static {
    promiseFactory = new CachedThreadPoolPromiseFactory (10, 5L, TimeUnit.SECONDS)
  }
  
  @Subscriber('jobs:log_error')
  void handleLogError(final String tenantId, final String jobId, final String message) {
    handleLogEvent(tenantId, jobId, message, LogEntry.TYPE_ERROR)
  }
  
  @Subscriber('jobs:log_info')
  void handleLogInfo (final String tenantId, final String jobId, final String message) {
    handleLogEvent(tenantId, jobId, message, LogEntry.TYPE_INFO)
  }
  
  private final static Closure addLogEntry = { final Map<String, ?> logProperties, final Serializable jobId ->
    LogEntry le = new LogEntry(logProperties)
    le.save(failOnError: true, flush: true)
  }
  
  static void handleLogEvent ( final String tenantId, final String jobId, final String message, final String type, final Instant timestamp = Instant.now()) {
    Promise p = task {
      final Map<String, ?> jobProperties = [
        'type': type,
        'origin': jobId,
        'message': message,
        'dateCreated': timestamp
      ]
      
      if ( jobId ) {
        if (tenantId) {
          Tenants.withId( tenantId, addLogEntry.curry(jobProperties, jobId) )
        } else {
          addLogEntry(jobProperties, jobId)
        }
      }
    }
    p.onError { Throwable err ->
      log.error "Error saving log message", err
    }
  }
}