package org.olf;

import grails.gorm.multitenancy.Tenants;
import grails.gorm.transactions.Transactional


/**
 * This service works at the module level, it's often called without a tenant context.
 */
@Transactional
public class KnowledgeBaseCacheService implements org.olf.kb.KBCache {

  public void triggerCacheUpdate() {
    log.debug("KnowledgeBaseCacheService::triggerCacheUpdate()");

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
  public void onPackageChange(Object canonical_package_definition) {
  }

  /**
   *  Called when a remote KB package removal is detected
   */
  public void onPackageRemoved(String authority,
                               String authority_id_of_package) {
  }

}
