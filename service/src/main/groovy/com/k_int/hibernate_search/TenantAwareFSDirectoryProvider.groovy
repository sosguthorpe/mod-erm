package com.k_int.hibernate_search

import java.nio.file.Path
import java.util.concurrent.ConcurrentHashMap

import org.apache.lucene.store.FSDirectory
import org.hibernate.engine.jdbc.connections.internal.DatasourceConnectionProviderImpl
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider
import org.hibernate.engine.spi.SessionFactoryImplementor
import org.hibernate.search.engine.service.spi.ServiceManager
import org.hibernate.search.exception.SearchException
import org.hibernate.search.indexes.spi.DirectoryBasedIndexManager
import org.hibernate.search.spi.BuildContext
import org.hibernate.search.spi.IndexingMode
import org.hibernate.search.store.impl.DirectoryProviderHelper
import org.hibernate.search.store.impl.FSDirectoryProvider
import org.hibernate.search.store.spi.DirectoryHelper

import grails.gorm.multitenancy.Tenants
import grails.util.Holders
import groovy.util.logging.Slf4j

@Slf4j
class TenantAwareFSDirectoryProvider extends FSDirectoryProvider {
  
  private ConcurrentHashMap<String, FSDirectory> directories = [:]
  private String dirName
  private boolean manual = false
  private ServiceManager serviceManager
  private Properties props

  private static SessionFactoryImplementor sessionFactory
  private static final SessionFactoryImplementor getSessionFactory() {
    if (!this.sessionFactory) {
      this.sessionFactory = Holders.applicationContext.getBean('sessionFactory')
    }
    
    this.sessionFactory
  }
  
  private final String getTenantId() {
    try {
      ConnectionProvider connectionProvider = serviceManager.requestReference(ConnectionProvider.class)
      String schema = connectionProvider?.connection?.schema
    } catch (Exception e ) {
      // NOOP
    }
    
    Tenants.CurrentTenant.get() ?: '' // Empty string instead of null to avoid ambiguity in map key
  }
  
  private final String getTenantDirectoryIndexName() {
    
    // Use this thread global method as the we don't want an exception thrown if null.
    final String tid = tenantId
    
    String path = dirName
    if (path && tid) {
      path = "${tid}${File.separator}${path}" 
    }
    path
  }
  
  @Override
  public void initialize(final String directory, final Properties properties, final BuildContext context) {
    log.info 'initialize'
    this.dirName = directory
    this.serviceManager = context.getServiceManager()
    this.props = properties
    
    // on "manual" indexing skip read-write check on index directory
    this.manual = IndexingMode.MANUAL == context.getIndexingMode()
  }
  
  @Override
  public void stop() {
    log.info 'stop'
    for (Map.Entry<String, FSDirectory> dir : directories) {
      try {
        directories.remove(dir.key)?.close()
      } catch (Exception e) {
        log.error "Error closing directory${dir.key != '' ? ' for tenant ' + dir.key : ''} with path ${dir.value.directory} "
      }
    }
  }
  
  @Override
  public void start(DirectoryBasedIndexManager indexManager) {
    log.info 'Start'
  }
  
  private FSDirectory getOrCreateDirectoryForTenant(final String tid) {
    FSDirectory dir = directories[tid]
    if (!dir) {
      final String tenantAwarePath = getTenantDirectoryIndexName()
      
      Path indexDir = DirectoryHelper.getVerifiedIndexPath( dirName, props, !manual )
      try {
        // Set the tenant entry in the map.
        dir = DirectoryProviderHelper.createFSIndex( indexDir, props, serviceManager )
        directories[tenantId] = dir
      }
      catch (IOException e) {
        throw new SearchException( "Unable to initialize index: " + directory, e )
      }
    }
    
    dir
  }

  @Override
  public FSDirectory getDirectory() {
    log.info 'getDirectory'
    final String tid = tenantId
    
    return getOrCreateDirectoryForTenant(tid);
  }
  
}