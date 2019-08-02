package org.olf

import org.olf.dataimport.folio.FolioErmPackageRecord
import org.olf.kb.Pkg

import com.k_int.okapi.OkapiTenantAwareController

import grails.gorm.multitenancy.CurrentTenant
import grails.validation.Validateable
import groovy.util.logging.Slf4j

@Slf4j
@CurrentTenant
class PackageController extends OkapiTenantAwareController<Pkg>  {

  PackageController() {
    super(Pkg)
  }
  
  void 'import' (FolioErmPackageRecord pkg) {
    if (pkg) {
      log.debug 'Got pkg'
      if (pkg.validate()) {
        log.debug 'and its valid'
      } else {
        pkg.errors.allErrors.each {
          log.debug "\t{it}"
        }
      }
    }
  }
}

