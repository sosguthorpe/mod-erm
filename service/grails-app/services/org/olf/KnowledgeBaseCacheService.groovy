package org.olf;

import grails.gorm.multitenancy.Tenants;
import grails.gorm.transactions.Transactional
import org.olf.kb.RemoteKB;
import org.olf.kb.KBCacheUpdater;


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
      cache_updater.freshen(rkb.id, rkb.uri, rkb.cursor, this)
    }
  }

  public void updateCursor(String rkb_id, String cursor) {
    log.debug("KnowledgeBaseCacheService::updateCursor(${rkb_id},${cursor})");
    RemoteKB.executeUpdate('update RemoteKB rkb set rkb.cursor = :n where rkb.id = :id',[n:cursor, id:rkb_id]);
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
  public void onPackageChange(String rkb_id, 
                              Object package_data) {
    log.debug("onPackageChange(${rkb_id},...)");
    packageIngestService.upsertPackage(package_data);
  }

  /**
   *  Called when a remote KB package removal is detected
   */
  public void onPackageRemoved(String rkb_id,
                               String authority,
                               String authority_id_of_package) {
  }

}
