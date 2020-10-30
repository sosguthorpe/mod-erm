package org.olf.general

import grails.gorm.multitenancy.Tenants

import org.olf.general.StringTemplate
import org.olf.general.StringTemplatingService

import org.springframework.context.ApplicationListener
import org.springframework.context.ApplicationEvent

import org.grails.datastore.mapping.engine.event.PostDeleteEvent
import org.grails.datastore.mapping.engine.event.PostInsertEvent
import org.grails.datastore.mapping.engine.event.PostUpdateEvent
import org.grails.datastore.mapping.engine.event.PreUpdateEvent

import org.grails.datastore.mapping.engine.event.AbstractPersistenceEvent

import org.olf.kb.Platform
import org.olf.kb.PlatformTitleInstance
import org.olf.kb.ErmResource

import org.olf.general.StringTemplate

import groovy.transform.CompileStatic

@CompileStatic
public class StringTemplateListeningService implements ApplicationListener {
  StringTemplatingService stringTemplatingService

  void afterInsert(PostInsertEvent event, String tenantId) {
    if (event.entityObject instanceof StringTemplate) {
      // This could have changed ALL templated urls, run full system refresh
      
      stringTemplatingService.generateTemplatedUrlsForErmResources(tenantId, [context: 'stringTemplate'])
    } else if (event.entityObject instanceof PlatformTitleInstance) {
      PlatformTitleInstance pti = (PlatformTitleInstance)event.entityObject;
      // Just refresh for this PTI
      stringTemplatingService.generateTemplatedUrlsForErmResources(tenantId, [context: 'pti', id: pti.id, platformId: pti.platform.id])
    }
  }

  void afterUpdate(PostUpdateEvent event, String tenantId) {
    if (event.entityObject instanceof StringTemplate) {
      // This could have changed ALL templated urls, run full system refresh
      stringTemplatingService.generateTemplatedUrlsForErmResources(tenantId, [context: 'stringTemplate'])
    } else if (event.entityObject instanceof Platform) {
      // This could change a whole host of PTIs, run platform refresh
      Platform p = (Platform)event.entityObject;
      stringTemplatingService.generateTemplatedUrlsForErmResources(tenantId, [context: 'platform', id: p.id])
    } else if (event.entityObject instanceof PlatformTitleInstance) {
      PlatformTitleInstance pti = (PlatformTitleInstance)event.entityObject;
      // Here we ONLY care if the pti url has changed
      if (pti.urlChanged) {
        stringTemplatingService.generateTemplatedUrlsForErmResources(tenantId, [context: 'pti', id: pti.id, platformId: pti.platform.id])
      }
    }
  }

  void afterDelete(PostDeleteEvent event, String tenantId) {
    if (event.entityObject instanceof StringTemplate) {
      // This could have changed ALL templated urls, run full system refresh
      stringTemplatingService.generateTemplatedUrlsForErmResources(tenantId, [context: 'stringTemplate'])
    }
  }

  public void onApplicationEvent(ApplicationEvent event){
    if ( event instanceof AbstractPersistenceEvent ) {
      String tenantId = Tenants.currentId()
      if ( event instanceof PostUpdateEvent ) {
        afterUpdate(event, tenantId);
      }
      else if ( event instanceof PostInsertEvent ) {
        afterInsert(event, tenantId);
      }
      else if ( event instanceof PostDeleteEvent ) {
        afterDelete(event, tenantId);
      }
    }
  }
}
