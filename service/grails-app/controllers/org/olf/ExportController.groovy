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


import com.opencsv.CSVWriter
import com.opencsv.CSVWriterBuilder
import com.opencsv.ICSVParser
import com.opencsv.ICSVWriter
import com.opencsv.bean.StatefulBeanToCsv
import com.opencsv.bean.StatefulBeanToCsvBuilder
import com.opencsv.bean.ColumnPositionMappingStrategy;



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
  def kbart() {
	final String subscriptionAgreementId = params.get("subscriptionAgreementId")
    final String filename = 'export.tsv'
	
	 
    log.debug("Getting export for specific agreement: "+ subscriptionAgreementId)
    def results = exportService.entitled(subscriptionAgreementId) 
    log.debug("found this many resources: "+ results.size()) 
   
    response.status = OK.value()
    response.contentType = "${tsvMimeType};charset=${encoding}";
    response.setHeader "Content-disposition", "attachment; filename=${filename}"
	 
		
    def outs = response.outputStream 
    OutputStream buffOs = new BufferedOutputStream(outs)
    OutputStreamWriter osWriter = new OutputStreamWriter(buffOs)

    ICSVWriter csvWriter = new CSVWriterBuilder(osWriter)
      .withSeparator('\t' as char)
      .withQuoteChar(ICSVParser.NULL_CHARACTER)
      .withEscapeChar(ICSVParser.NULL_CHARACTER)
      .withLineEnd(ICSVWriter.DEFAULT_LINE_END)
      .build(); 
	  
	StatefulBeanToCsv<KBart> sbc = new StatefulBeanToCsvBuilder<KBart>(csvWriter)
	  .build()
	
	// display the header then use sbc to serialize the list of kbart objects
	csvWriter.writeNext(KBart.header())
    List<KBart> kbartList = KBart.transform(results)
	 
    sbc.write(kbartList)
	osWriter.close() 

  }
}

