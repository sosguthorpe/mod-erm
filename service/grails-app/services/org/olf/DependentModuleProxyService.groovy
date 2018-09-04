package org.olf

import org.olf.general.Org

public class DependentServiceProxyService {

  def grailsApplication

  public Org coordinateOrg(String orgName) {
    if ( grailsApplication.config.mode=='folio' ) {
      throw new RuntimeException("Vendors lookup not yet implemented");
      // https://github.com/folio-org/acq-models/blob/34f05db48075fb28ac58bf8c7ca5bddbbf5f7867/mod-vendors/examples/vendor_collection.sample
      
    }
    else {
      return Org.findByName(orgName) ?: new Org(name:orgName).save(flush:true, failOnError:true);
    }
  }
}
