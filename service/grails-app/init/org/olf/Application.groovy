package org.olf

import grails.boot.GrailsApp
import grails.boot.config.GrailsAutoConfiguration
import io.undertow.Undertow
import io.undertow.UndertowOptions
import org.springframework.boot.context.embedded.undertow.UndertowBuilderCustomizer
import org.springframework.boot.context.embedded.undertow.UndertowEmbeddedServletContainerFactory
import org.springframework.context.annotation.Bean

class Application extends GrailsAutoConfiguration {
  static void main(String[] args) {
    GrailsApp.run(Application, args)
  }
  
  @Bean
  UndertowEmbeddedServletContainerFactory embeddedServletContainerFactory(){
      UndertowEmbeddedServletContainerFactory factory= new UndertowEmbeddedServletContainerFactory();
      factory.addBuilderCustomizers(new UndertowBuilderCustomizer(){
          @Override public void customize(Undertow.Builder builder){
              builder
                .setWorkerThreads(64)
              // There are many more options, see the documentation for more.
          }
      });
      return factory;
  }
}