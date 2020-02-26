package com.k_int.hibernate_search

import org.hibernate.engine.spi.SessionFactoryImplementor
import org.hibernate.search.MassIndexer
import org.hibernate.search.batchindexing.impl.DefaultMassIndexerFactory
import org.hibernate.search.spi.SearchIntegrator

import groovy.util.logging.Slf4j

@Slf4j
class TenantAwareMassIndexer extends DefaultMassIndexerFactory {

  
  @Override
  public void initialize(Properties properties) {
    super.initialize(properties)
  }

  @Override
  public MassIndexer createMassIndexer(SearchIntegrator searchIntegrator, SessionFactoryImplementor sessionFactory,
      Class<?>... entities) {
    log.info "createMassIndexer"
    return super.createMassIndexer( searchIntegrator, sessionFactory, types );
  }
}
