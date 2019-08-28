package org.olf

import org.olf.kb.Pkg

import com.k_int.okapi.OkapiTenantAwareController

import grails.gorm.multitenancy.CurrentTenant
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

@Slf4j
@CurrentTenant
@CompileStatic
class PackageController extends OkapiTenantAwareController<Pkg> {
  
  ImportService importService

  PackageController() {
    super(Pkg)
  }

  def 'import' () {
    final bindObj = this.getObjectToBind()
    importService.importPackageUsingErmSchema(bindObj as Map)
    return render (status: 200)
  }
}

