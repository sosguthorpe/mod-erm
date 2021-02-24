package org.olf.kb.adapters;

import static groovy.json.JsonOutput.*
import static groovyx.net.http.ContentType.*
import static groovyx.net.http.Method.*

import java.text.*

import org.apache.http.*
import org.apache.http.entity.mime.*
import org.apache.http.entity.mime.content.*
import org.apache.http.protocol.*
import org.olf.kb.KBCache;
import org.olf.kb.KBCacheUpdater;

import groovy.util.logging.Slf4j
import groovyx.net.http.*

@Slf4j
public class GenericRemoteKBAdapter implements KBCacheUpdater {


  public void freshenPackageData(String source_name,
                                 String base_url,
                                 String current_cursor,
                                 KBCache cache,
                                 boolean trustedSourceTI = false) {
    throw new RuntimeException("Not supported by this KB provider");
  }

  public void freshenHoldingsData(String cursor,
                                  String source_name,
                                  KBCache cache) {
    throw new RuntimeException("Not yet implemented");
  }

  public String makePackageReference(Map params) {
    throw new RuntimeException("Not yet implemented");
    return null;
  }

  /**
   *
   */
  public Map importPackage(Map params,
                            KBCache cache) {
    throw new RuntimeException("Not yet implemented");
    return null;
  }

  public boolean activate(Map params, KBCache cache) {
    // Nothing to see here yet
    return true;
  }

  public boolean requiresSecondaryEnrichmentCall() {
    false
  }

  public Map getTitleInstance(String source_name,
                              String base_url,
                              String identifier,
                              String type,
                              String publicationType,
                              String subType) {
    throw new RuntimeException("Not supported by this KB provider");
  }

}


