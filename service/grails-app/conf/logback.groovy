import java.nio.charset.StandardCharsets

import org.olf.general.jobs.JobAwareAppender
import org.springframework.boot.logging.logback.ColorConverter
import org.springframework.boot.logging.logback.WhitespaceThrowableProxyConverter

import ch.qos.logback.classic.encoder.PatternLayoutEncoder
import ch.qos.logback.core.ConsoleAppender
import ch.qos.logback.core.FileAppender
import grails.util.BuildSettings
import grails.util.Environment

conversionRule 'clr', ColorConverter
conversionRule 'wex', WhitespaceThrowableProxyConverter

// See http://logback.qos.ch/manual/groovy.html for details on configuration
appender('STDOUT', ConsoleAppender) {
    encoder(PatternLayoutEncoder) {
        charset = StandardCharsets.UTF_8

        pattern =
                '%clr(%d{yyyy-MM-dd HH:mm:ss.SSS}){faint} ' + // Date
                '%clr(%5p) ' + // Log level
                '%clr(---){faint} %clr([%15.15t]){faint} ' + // Thread
                "%clr(%-30.30logger{29} %15(%replace([%X{tenant:-_NO_TENANT_}]){'\\[_NO_TENANT_\\]',''})){cyan} %clr(:){faint} " +
                '%m%n%wex' // Message
    }
}

// Default level should be info.
root(WARN, ['STDOUT'])
logger ('org.hibernate.orm.deprecation', ERROR)
logger ('com.k_int.okapi.OkapiSchemaHandler', INFO)
logger ('org.olf.general.DocumentAttachment', DEBUG);
logger ('com.k_int.web.toolkit.files', DEBUG);

// Just increase verbosity for dev/test.


boolean devEnv = Environment.isDevelopmentMode() || Environment.currentEnvironment.name == 'vagrant-db' || Environment.currentEnvironment.name == 'rancher-desktop-db'

if (devEnv || Environment.currentEnvironment == Environment.TEST) {
  
  // Change default verbosity to INFO for dev/test
  root(INFO, ['STDOUT'])
  
  // Increase specific levels to debug
  logger 'grails.app.init', DEBUG
  logger 'grails.app.controllers', DEBUG
  logger 'grails.app.domains', DEBUG
  logger 'grails.app.jobs', DEBUG
  logger 'grails.app.services', DEBUG
  logger 'com.zaxxer.hikari.HikariConfig', DEBUG
  logger 'com.zaxxer.hikari.pool.HikariPool', WARN
  
  logger 'services.k_int', TRACE
  logger 'com.k_int', DEBUG
  logger 'com.k_int.web.toolkit', DEBUG
  logger 'org.olf', DEBUG
  
  
//  logger 'org.hibernate.SQL', DEBUG
//  logger 'org.hibernate.type.descriptor.sql.BasicBinder', TRACE
  
  
  if (Environment.currentEnvironment == Environment.TEST) {
    // Test only.
    // logger 'org.hibernate', DEBUG
    // logger 'com.k_int.okapi.OkapiClient', TRACE
    // logger 'groovy.net.http.JavaHttpBuilder', DEBUG
    // logger 'org.hibernate.loader.criteria', TRACE
    // logger 'org.hibernate.SQL', DEBUG
    // logger 'org.hibernate.type.descriptor.sql.BasicBinder', TRACE
  }
}
else {
  logger 'org.olf', INFO
}

def targetDir = BuildSettings.TARGET_DIR
if (devEnv && targetDir != null) {
    appender("FULL_STACKTRACE", FileAppender) {
        file = "${targetDir}/stacktrace.log"
        append = true
        encoder(PatternLayoutEncoder) {
            charset = StandardCharsets.UTF_8
            pattern = "%level %logger - %msg%n"
        }
    }
    logger("StackTrace", ERROR, ['FULL_STACKTRACE'], false)
}

// Add the appender for classes we wish to expose within the database.
appender ('JOB', JobAwareAppender)

logger ('org.olf.PackageIngestService', DEBUG, ['JOB'])
logger ('org.olf.TitleIngestService', DEBUG, ['JOB'])
logger ('org.olf.TitleInstanceResolverService', DEBUG, ['JOB'])
logger ('org.olf.TitleEnricherService', DEBUG, ['JOB'])
logger ('org.olf.IdentifierService', DEBUG, ['JOB'])
logger ('org.olf.MatchKeyService', DEBUG, ['JOB'])
logger ('org.olf.kb.adapters.GOKbOAIAdapter', DEBUG, ['JOB'])
logger ('org.olf.CoverageService', DEBUG, ['JOB'])
logger ('org.olf.ImportService', DEBUG, ['JOB'])
logger ('org.olf.DocumentAttachmentService', DEBUG, ['JOB'])
logger ('org.olf.KbManagementService', DEBUG, ['JOB'])
