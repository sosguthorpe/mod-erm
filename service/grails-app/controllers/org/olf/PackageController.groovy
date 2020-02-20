package org.olf

import org.olf.kb.PackageContentItem
import org.olf.kb.Pkg

import com.k_int.okapi.OkapiTenantAwareController
import grails.converters.JSON
import grails.gorm.multitenancy.CurrentTenant
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import java.time.LocalDate

import org.springframework.web.multipart.MultipartFile
import org.apache.commons.io.input.BOMInputStream

import com.opencsv.CSVReader
import com.opencsv.CSVReaderBuilder


@Slf4j
@CurrentTenant
class PackageController extends OkapiTenantAwareController<Pkg> {
  
  ImportService importService

  PackageController() {
    super(Pkg)
  }

  def 'import' () {
    final bindObj = this.getObjectToBind()
    log.debug("bindObj: ${bindObj}")
    importService.importPackageUsingErmSchema(bindObj as Map)
    return render (status: 200)
  }
  
  def 'tsvParse' () {
    //TODO Potentially work out how to get PackageProvider information into here
    MultipartFile file = request.getFile('upload')

    Map packageInfo = [
      packageName: request.getParameter("packageName"),
      packageSource: request.getParameter("packageSource"),
      packageReference: request.getParameter("packageReference"),
      packageProvider: request.getParameter("packageProvider")
    ]
    
    BOMInputStream bis = new BOMInputStream(file.getInputStream());
    Reader fr = new InputStreamReader(bis);
    CSVReader csvReader = new CSVReaderBuilder(fr).build();

    def completed = importService.importPackageFromKbart(csvReader, packageInfo)

    if (completed) {
      log.debug("KBART import success")
    } else {
      log.debug("KBART import failed")
    }
    return render (status: 200)
    render ([:] as JSON)
  }

  def content () {
    respond doTheLookup(PackageContentItem) {
      eq 'pkg.id', params.'packageId'
      isNull 'removedTimestamp'
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
      isNull 'removedTimestamp'
    }
  }
  
  def futureContent () {
    final LocalDate today = LocalDate.now()
    respond doTheLookup(PackageContentItem) {
      eq 'pkg.id', params.'packageId'
      gt 'accessStart', today
      isNull 'removedTimestamp'
    }
  }
  
  def droppedContent () {
    final LocalDate today = LocalDate.now()
    respond doTheLookup(PackageContentItem) {
      eq 'pkg.id', params.'packageId'
      lt 'accessEnd', today
      isNull 'removedTimestamp'
    }
  }
}

