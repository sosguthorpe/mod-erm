package org.olf

import org.olf.dataimport.erm.ErmPackageImpl
import org.olf.kb.Pkg

import com.k_int.okapi.OkapiTenantAwareController

import grails.gorm.multitenancy.CurrentTenant
import groovy.util.logging.Slf4j

@Slf4j
@CurrentTenant
class PackageController extends OkapiTenantAwareController<Pkg> {

  PackageController() {
    super(Pkg)
  }
  
  PackageIngestService packageIngestService
  
  def 'import' () {
    
    final bindObj = this.getObjectToBind()
    if (bindObj) {
      ErmPackageImpl pkg = new ErmPackageImpl()
      bindData(pkg, bindObj)
      log.debug 'Got pkg'
      if (pkg.validate()) {
        log.debug 'and its valid'
      } else {
        pkg.errors.allErrors.each {
          log.debug "\t${it}"
        }
        return
      }
      
      // Else do the ingest.
      render packageIngestService.upsertPackage(pkg)
    }
    
    return render (status: 200)
  }
}

