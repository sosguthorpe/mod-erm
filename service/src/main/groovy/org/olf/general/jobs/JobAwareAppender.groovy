package org.olf.general.jobs

import java.time.Instant

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.AppenderBase

public class JobAwareAppender extends AppenderBase<ILoggingEvent> {
  
  @Override
  protected void append(final ILoggingEvent eventObject) {
    try {
      final Serializable jid = JobRunnerService.jobContext.get()?.jobId
      if (jid) {
        switch (eventObject.level) {
          case Level.INFO:
//          case Level.WARN:
          
            final Serializable tid = JobRunnerService.jobContext.get()?.tenantId
            JobLoggingService.handleLogEvent(tid, jid, eventObject.formattedMessage, Level.INFO.levelStr, Instant.ofEpochMilli(eventObject.timeStamp))
            break
          
          case Level.ERROR:
            final Serializable tid = JobRunnerService.jobContext.get()?.tenantId
            JobLoggingService.handleLogEvent(tid, jid, eventObject.formattedMessage, Level.ERROR.levelStr, Instant.ofEpochMilli(eventObject.timeStamp))
            break
        }
      }
    } catch (Throwable t) { 
      t.printStackTrace()
    }
  }
}