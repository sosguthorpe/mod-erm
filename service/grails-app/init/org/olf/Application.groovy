package org.olf

import com.k_int.web.toolkit.refdata.GrailsDomainRefdataHelpers

import org.springframework.boot.context.embedded.undertow.UndertowBuilderCustomizer
import org.springframework.boot.context.embedded.undertow.UndertowEmbeddedServletContainerFactory
import org.springframework.context.annotation.Bean

import com.k_int.okapi.OkapiTenantAdminService

import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration
import grails.core.GrailsClass
import groovy.util.logging.Slf4j
import io.undertow.Undertow.Builder

@Slf4j
class Application extends GrailsAutoConfiguration {

  static void main(String[] args) {

    // Ensure we have force UTC to be the local application TZ.
    final TimeZone utcTz = TimeZone.getTimeZone("UTC")
    if (TimeZone.default != utcTz) {
      log.info "Timezone default is ${TimeZone.default.displayName}. Setting to UTC"
      TimeZone.default = utcTz
    }

    GrailsApp.run(Application, args)
  }
  
  @Bean
  UndertowEmbeddedServletContainerFactory embeddedServletContainerFactory(){
    UndertowEmbeddedServletContainerFactory factory = new UndertowEmbeddedServletContainerFactory()
    factory.builderCustomizers << new UndertowBuilderCustomizer() {

      @Override
      public void customize(Builder builder) {
        
        builder.directBuffers = false                   // Force the buffers to be allocated from heap.
        builder.bufferSize = 512                        // Low buffer size of 512 bytes. This will now be taken from heap so we need to be careful.
        builder.ioThreads = 2                           // I/O Threads hard set to 2
        builder.workerThreads = builder.ioThreads * 4   // Workers limited to 4 * IO Threads
        
        log.info "Runtime memory reported ${Runtime.getRuntime().maxMemory() / 1024 / 1024} mb"
        log.info "Runtime cpus reported ${Runtime.getRuntime().availableProcessors()}"
        log.info "Allocated ${builder.ioThreads} IO Threads"
        log.info "Allocated ${builder.workerThreads} worker threads"
        log.info "Allocated ${builder.bufferSize} bytes of ${builder.directBuffers ? 'direct' : 'indirect'} buffer space"
      }
    }
    factory
  }
}