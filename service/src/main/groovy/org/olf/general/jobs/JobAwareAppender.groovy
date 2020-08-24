package org.olf.general.jobs

import java.time.Instant
import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.classic.spi.LoggingEvent
import ch.qos.logback.core.AppenderBase

public class JobAwareAppender extends AppenderBase<ILoggingEvent> {
  
  @Override
  protected void append(final ILoggingEvent eventObject) {
    try {
      final Serializable jid = JobContext.current.get()?.jobId
      
      // Grab the mdc map.
      Map<String, String> mdc = eventObject instanceof LoggingEvent ? eventObject.getMDCPropertyMap() : null     
      
      if (jid) {
        switch (eventObject.level) {
          case Level.INFO:
//          case Level.WARN:
          
            final Serializable tid = JobContext.current.get()?.tenantId
            JobLoggingService.handleLogEvent(tid, jid, eventObject.formattedMessage, Level.INFO.levelStr, Instant.ofEpochMilli(eventObject.timeStamp), mdc)
            break
          
          case Level.ERROR:
            final Serializable tid = JobContext.current.get()?.tenantId
            JobLoggingService.handleLogEvent(tid, jid, eventObject.formattedMessage, Level.ERROR.levelStr, Instant.ofEpochMilli(eventObject.timeStamp), mdc)
            break
        }
      }
    } catch (Throwable t) { 
      t.printStackTrace()
    }
  }
}