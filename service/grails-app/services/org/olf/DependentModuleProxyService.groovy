package org.olf

import com.k_int.okapi.OkapiClient
import groovy.util.logging.Slf4j
import groovyx.net.http.HttpException
import org.olf.general.Org

@Slf4j
public class DependentServiceProxyService {

  def grailsApplication
  OkapiClient okapiClient

  public Org coordinateOrg(String orgName) {
//    if ( grailsApplication.config.mode=='folio' ) {
//      // throw new RuntimeException("Vendors lookup not yet implemented")
//      
//      log.info "Not implemented yet"
//      return null
    
    // Simply call the verb method on the client (get, post, put, delete). The client itself should take care of everything else.
    // Get and Delete take the uri with optional params map for the query string.
    // Post, Put and Patch take in the uri, data that can be converted to json (String or map) and optional params map for the query string.
    Org org = Org.findByName(orgName)
    
    if (!org) {
      log.debug "No local org for ${orgName}. Check vendors."
      
      // This fetches a max of 2 (we should decide how to handle multiple matches) vendors with an exact name match.
      def resp = okapiClient.get("/vendor", [
        limit: 2,
        query: ('(name=="' + orgName + '")') // CQL
      ])
      
      // Resp is a lazy map representation of the JSON returned by the module.
      /*
        {
          "vendors" : [ ... ],
          "total_records" : 0,
          "first" : 0,
          "last" : 0
        }
       */
      
      switch (resp.total_records) {
        case 1:
          // Exact match
          def result = resp.vendors[0]
          
          org = new Org(
            name: result.name,
            vendorsUuid: result.id,
            sourceURI: "/vendor/${result.id}"
          ).save( flush:true, failOnError:true )
          break
          
        case 0:
          // Create a new local one.
          log.debug "No vendor found. Adding local org for ${orgName}"
          org = (new Org(name:orgName)).save(flush:true, failOnError:true)
          // No match
          break
          
        default:
          // Multiples.
          log.debug "Multiple matches for vendor with name ${orgName}"
      }
    }
    
    return org
  }
}
