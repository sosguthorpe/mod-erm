package org.olf

import org.olf.kb.PackageContentItem
import org.olf.kb.Pkg

import com.k_int.okapi.OkapiTenantAwareController
import grails.converters.JSON
import grails.gorm.multitenancy.CurrentTenant
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import java.time.LocalDate

@Slf4j
@CurrentTenant
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
  
  def content () {
    respond doTheLookup(PackageContentItem) {
      eq 'pkg.id', params.'packageId'
    }
  }
  
  def currentContent () {
    final LocalDate today = LocalDate.now()
    respond doTheLookup(PackageContentItem) {
      eq 'pkg.id', params.'packageId'
      and {
        or {
          isNull 'accessEnd'
          gte 'accessEnd', today
        }
        or {
          isNull 'accessStart'
          lte 'accessStart', today
        }
      }
    }
  }
  
  def joiningContent () {
    final LocalDate today = LocalDate.now()
    respond doTheLookup(PackageContentItem) {
      eq 'pkg.id', params.'packageId'
      between 'accessStart', today.plusDays(1), today.plusDays(7)
    }
  }
  
  def leavingContent () {
    final LocalDate today = LocalDate.now()
    respond doTheLookup(PackageContentItem) {
      eq 'pkg.id', params.'packageId'
      between 'accessEnd', today.plusDays(1), today.plusDays(7)
    }
  }
}

