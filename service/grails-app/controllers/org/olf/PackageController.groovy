package org.olf

import org.olf.dataimport.erm.FolioErmPackageRecord
import org.olf.kb.Pkg

import com.k_int.okapi.OkapiTenantAwareController

import grails.gorm.multitenancy.CurrentTenant
import grails.validation.Validateable
import groovy.util.logging.Slf4j

@Slf4j
@CurrentTenant
class PackageController extends OkapiTenantAwareController<Pkg> {

  PackageController() {
    super(Pkg)
  }
  
  def 'import' () {
    
    final bindObj = this.getObjectToBind()
    if (bindObj) {
      FolioErmPackageRecord pkg = new FolioErmPackageRecord()
      bindData(pkg, bindObj)
      log.debug 'Got pkg'
      if (pkg.validate()) {
        log.debug 'and its valid'
      } else {
        pkg.errors.allErrors.each {
          log.debug "\t${it}"
        }
      }
    }
    
    return render (status: 200)
  }
}

