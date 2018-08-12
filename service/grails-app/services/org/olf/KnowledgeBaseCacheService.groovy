package org.olf;

import grails.gorm.multitenancy.Tenants;
import grails.gorm.transactions.Transactional
import org.olf.kb.RemoteKB;
import org.olf.kb.KBCacheUpdater;
import org.springframework.transaction.TransactionDefinition

/**
 * This service works at the module level, it's often called without a tenant context.
 */
@Transactional
public class KnowledgeBaseCacheService implements org.olf.kb.KBCache {

  def packageIngestService

  public void triggerCacheUpdate() {
    log.debug("KnowledgeBaseCacheService::triggerCacheUpdate()");

  }

  public void runSync(String remotekb_id) {
    log.debug("KnowledgeBaseCacheService::runSync(${remotekb_id})");
    RemoteKB rkb = RemoteKB.read(remotekb_id) 
    if ( rkb ) {
      log.debug("Run remote kb synv:: ${rkb}");
      Class cls = Class.forName(rkb.type)
      KBCacheUpdater cache_updater = cls.newInstance();
      cache_updater.freshen(rkb.name, rkb.uri, rkb.cursor, this)
    }
  }

  public void updateCursor(String rkb_name, String cursor) {
    log.debug("KnowledgeBaseCacheService::updateCursor(${rkb_name},${cursor})");
    RemoteKB.withTransaction([propagationBehavior: TransactionDefinition.PROPAGATION_REQUIRES_NEW]) {
      RemoteKB.executeUpdate('update RemoteKB rkb set rkb.cursor = :n where rkb.name = :name',[n:cursor, name:rkb_name]);
    }
  }



  /**
   *  Called when a remote KB package add/update is detected. It is the responsibility
   *  of the KBCacheUpdater to convert the remote package format into the canonical object map  
   *  defined here
   *  This document: https://docs.google.com/document/d/14KIi4Guhu8r1NM7lr8NH6SI7giyAdmFjjy4Q6x-MMvQ/edit
   *  Drive -> ERM-Project Team -> WorkInProgress -> olf-erm analysis and design -> PackageDescription--JSON format
   *  Examples can be found in src/intergation-test/resources/packages. The function should be called with the
   *  result of parsing that JSON into a map, eg via JsonSlurper
   */
  public void onPackageChange(String rkb_name, 
                              Object package_data) {
    RemoteKB.withTransaction([propagationBehavior: TransactionDefinition.PROPAGATION_REQUIRES_NEW]) {
      log.debug("onPackageChange(${rkb_name},...)");
      packageIngestService.upsertPackage(package_data, rkb_name);
    }
  }

  /**
   *  Called when a remote KB package removal is detected
   */
  public void onPackageRemoved(String rkb_name,
                               String authority,
                               String authority_id_of_package) {
  }

}
