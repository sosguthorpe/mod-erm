package org.olf

import org.springframework.boot.context.embedded.undertow.UndertowBuilderCustomizer
import org.springframework.boot.context.embedded.undertow.UndertowEmbeddedServletContainerFactory
import org.springframework.context.annotation.Bean

import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration
import groovy.util.logging.Slf4j
import io.undertow.UndertowOptions
import io.undertow.Undertow.Builder

@Slf4j
class Application extends GrailsAutoConfiguration {

  static void main(String[] args) {
    
//    RuntimeMXBean rt = ManagementFactory.runtimeMXBean
//    for (String arg: rt.inputArguments) {
//      println "${arg}"
//    }

    // Ensure we have force UTC to be the local application TZ.
    final TimeZone utcTz = TimeZone.getTimeZone("UTC")
    if (TimeZone.default != utcTz) {
      log.info "Timezone default is ${TimeZone.default.displayName}. Setting to UTC"
      TimeZone.default = utcTz
    }

    // This should be last...
    GrailsApp.run(Application, args)
  }
  
  @Bean
  UndertowEmbeddedServletContainerFactory embeddedServletContainerFactory(){
    UndertowEmbeddedServletContainerFactory factory = new UndertowEmbeddedServletContainerFactory()
    factory.builderCustomizers << new UndertowBuilderCustomizer() {

      @Override
      public void customize(Builder builder) {
        
        // Min of 2, Max of 4 I/O threads
        builder.ioThreads = Math.min(Math.max(Runtime.getRuntime().availableProcessors(), 2), 4)
        
        final int heap_coef = (Runtime.getRuntime().maxMemory() / 1024 / 1024)/256
        int workers_per_io = 8
        if (heap_coef <= 2) {
          workers_per_io = 6
        } else if (heap_coef <= 1) {
          workers_per_io = 4
        }
        
        // 8 Workers per I/O thread
        builder.workerThreads = builder.workerThreads = builder.ioThreads * workers_per_io
        
        // Enable HTTP2
        builder.setServerOption(UndertowOptions.ENABLE_HTTP2, true)
        
        log.info "Runtime memory reported ${Runtime.getRuntime().maxMemory() / 1024 / 1024} mb"
        log.info "Runtime CPUs reported ${Runtime.getRuntime().availableProcessors()}"
        log.info "Allocated ${builder.ioThreads} IO Threads"
        log.info "Allocated ${builder.workerThreads} worker threads"
        log.info "Allocated ${builder.bufferSize} bytes of ${builder.directBuffers ? 'direct' : 'indirect'} buffer space per thread"
      }
    }
    factory
  }
}