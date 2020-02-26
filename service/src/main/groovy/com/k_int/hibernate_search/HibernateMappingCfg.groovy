package com.k_int.hibernate_search

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import org.grails.orm.hibernate.cfg.HibernateMappingContextConfiguration
import org.hibernate.HibernateException
import org.hibernate.SessionFactory
import org.hibernate.SessionFactoryObserver
import org.hibernate.service.spi.ServiceRegistryImplementor

@Slf4j
class HibernateMappingCfg extends HibernateMappingContextConfiguration {
  
  private SessionFactoryObserver overridenObserver
  
  @Override
  void setSessionFactoryObserver(final SessionFactoryObserver ob) {
    
    overridenObserver = new SessionFactoryObserver() {
      public void sessionFactoryCreated(SessionFactory factory) { ob.sessionFactoryCreated(factory) }
      public void sessionFactoryClosed(SessionFactory factory) {
        
        if (this.getServiceRegistry()) {
          // Just call the original.
          ob.sessionFactoryClosed(factory)
        } else {
          // Leniently call the orignal and suppress the null pointers
          try {
            ob.sessionFactoryClosed(factory)
          } catch ( NullPointerException npe) {
            // NOOP.
            log.info('Ignore closed error')
          }
        }
      }
    }
  }
  
  @Override
  SessionFactoryObserver getSessionFactoryObserver() {
    overridenObserver
  }
}
