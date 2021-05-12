package org.olf.general.jobs


import java.time.Instant
import com.k_int.web.toolkit.async.WithPromises
import grails.async.Promise
import grails.events.annotation.Subscriber
import grails.gorm.multitenancy.Tenants
import groovy.util.logging.Slf4j

@Slf4j
class JobLoggingService {

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
    le.setAdditionalinfo(logProperties.additionalInfo)
    le.save(failOnError: true, flush: true)
  }

  static void handleLogEvent ( final String tenantId, final String jobId, final String message, final String type, final Instant timestamp = Instant.now(), final Map<String, String> contextVals = [:]) {

    final Map<String, ?> theProps = [
      'type': type ? '' + type : null,
      'origin': jobId ? '' + jobId : null,
      'message': message ? '' + message : null,
      'dateCreated': timestamp,
      'additionalInfo': new HashMap<String,String>( contextVals )
    ]

    Promise p = WithPromises.task ({ final Map<String, String> jobProperties ->
      if ( jobId ) {
        if ( tenantId ) {
          Tenants.withId( tenantId, addLogEntry.curry(jobProperties, jobId) )
        } else {
          addLogEntry(jobProperties, jobId)
        }
      }
    }.curry ( theProps ))
    p.onError { Throwable err ->
      log.error "Error saving log message", err
    }
  }
}
