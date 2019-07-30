package org.olf

import com.k_int.okapi.OkapiTenantAwareController
 
import grails.gorm.multitenancy.CurrentTenant
import groovy.util.logging.Slf4j 
import grails.converters.JSON
import org.olf.kb.TitleInstance
import org.olf.kb.PlatformTitleInstance
import org.olf.kb.IdentifierOccurrence
import org.olf.kb.Identifier
import org.olf.kb.IdentifierNamespace
import org.olf.kb.CoverageStatement
import org.olf.kb.Platform
import org.olf.kb.ErmResource
import org.olf.export.KBart
import static org.springframework.http.HttpStatus.OK

/**
 * The ExportController provides endpoints for exporting content in specific formats
 * harvested by the erm module.
 */
@Slf4j
@CurrentTenant
class ExportController extends OkapiTenantAwareController<TitleInstance>  {

  ExportService exportService

  final String csvMimeType = 'text/csv'
  final String tsvMimeType = 'text/tab-separated-value'
  final String encoding = "UTF-8"
  

  ExportController()  {
    super(TitleInstance, true)
  }

  /**
   * main index method (by default, return titles as json)
   */
  def index() {
    log.debug("ExportController::index");
    final String subscriptionAgreementId = params.get("subscriptionAgreementId") 
    log.debug("Getting export for specific agreement: "+ subscriptionAgreementId)
    List<ErmResource> results = exportService.entitled(subscriptionAgreementId)
    log.debug("found this many resources: "+ results.size()) 
    respond results 
  }

  /*
   * kbart export (placeholder)
   */
  def kbart( ) {
    final String filename = 'export.tsv' 
    String headline = exportService.kbartheader()
    final String subscriptionAgreementId = params.get("subscriptionAgreementId")
	  
    List<KBart> kbartList 
    log.debug("Getting export for specific agreement: "+ subscriptionAgreementId)
    def results = exportService.entitled(subscriptionAgreementId) 
    log.debug("found this many resources: "+ results.size())
    kbartList = exportService.mapToKBart(results) 

    withFormat {
      tsv { 
        response.status = OK.value()
        response.contentType = "${tsvMimeType};charset=${encoding}";
        response.setHeader "Content-disposition", "attachment; filename=${filename}"
        def outs = response.outputStream
        outs << headline + "\n"
        // serialize KbartExport list of kbart objects
			   
        kbartList.each { KBart kb ->
          String line = kb.toString()
	      outs << "${line}\n"
	    }

	    outs.flush()
	    outs.close()
      }
    }
  }
}

