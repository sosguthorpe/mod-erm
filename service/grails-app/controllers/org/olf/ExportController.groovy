package org.olf

import static org.springframework.http.HttpStatus.OK

import org.olf.export.KBart
import org.olf.kb.ErmResource
import org.olf.kb.TitleInstance

import com.k_int.okapi.OkapiTenantAwareController
import com.opencsv.CSVWriterBuilder
import com.opencsv.ICSVParser
import com.opencsv.ICSVWriter
import com.opencsv.bean.StatefulBeanToCsv
import com.opencsv.bean.StatefulBeanToCsvBuilder

import grails.gorm.multitenancy.CurrentTenant
import groovy.util.logging.Slf4j



/**
 * The ExportController provides endpoints for exporting content in specific formats
 * harvested by the erm module.
 */
@Slf4j
@CurrentTenant
class ExportController extends OkapiTenantAwareController<TitleInstance>  {


  ExportService exportService

  ExportController()  {
    super(TitleInstance, true)
  }

  def index() {
    log.debug("ExportController::index")
    final String subscriptionAgreementId = params.get("subscriptionAgreementId")
    log.debug("Getting export for specific agreement: "+ subscriptionAgreementId)
    List<List> results = exportService.all(subscriptionAgreementId)
    log.debug("found this many resources: "+ results.size())
    
    respondWithResults ( results )
  }
  
  def current() {
    log.debug("ExportController::current")
    final String subscriptionAgreementId = params.get("subscriptionAgreementId")
    log.debug("Getting export for specific agreement: "+ subscriptionAgreementId)
    List<List> results = exportService.current(subscriptionAgreementId)
    log.debug("found this many resources: "+ results.size())
    
    respondWithResults ( results )
  }
  
  private respondWithResults (List<List> results) {
    
    withFormat {
      'kbart' {
        // Set the file disposition.
        response.setHeader "Content-disposition", "attachment; filename=export.tsv"
    
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
      
      '*' {
        // Normal respond.
        respond results
      }
    }
  }
}

