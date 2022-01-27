package org.olf

import org.olf.kb.IdentifierOccurrence
import org.olf.kb.TitleInstance

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import grails.gorm.transactions.Transactional

@Slf4j
@CompileStatic
public class IdentifierService {

  /*
    This method accepts an ArrayList of Maps of the form:
    [
      [
        identifierNamespace: "ISSN",
        identifierValue: "12345",
        targetTitleInstanceId: "abcde-12345-fghij",
        initialTitleInstanceId: "jihgf-54321-edcba"
      ],
      ...
    ]

    It will attempt to "reassign" each IdentifierOccurence in turn to the new TitleInstance
    Reassignation will actually consist of the IdentifierOccurence in question
    being marked as "ERROR", and a new Occurrence being created on the targetTI

    TODO Ask Ian about this
  */
  def reassignFromFile (final ArrayList<Map> reassignmentQueue) {
    reassignmentQueue.each{ reassignmentMap ->
      IdentifierOccurence.withNewTransaction{
        TitleInstance initialTI = TitleInstance.get(reassignmentMap.initialTitleInstanceId)
        TitleInstance targetTI = TitleInstance.get(reassignmentMap.targetTitleInstanceId)
        
        // Check that we could find the specified titleinstances
        if (targetTI != null & initialTI != null) {
          log.info("LOGDEBUG WE GOT HERE")

          // Now look up an IdentifierOccurrence for the correct set of information
          // TODO SQL FOR THIS
          IdentifierOccurence identifierOccurrence
          //IdentifierOccurence identifierOccurrence = IdentifierOccurence.get(reassignmentMap.identifierOccurrenceId)

          if (identifierOccurrence != null) {
            log.debug("LOGDEBUG AND WE ALSO GOT HERE")
          } else {
            log.error("IdentifierOccurrence could not be found for (${reassignmentMap.identifierNamespace}:${reassignmentMap.identifierValue}) on initial TitleInstance.")
          }
        } else {
          String errorString = "Error(s) fetching specified objects:"
          if (initialTI == null) {
            errorString += " TitleInstance could not be found for initialTitleInstanceId (${initialTitleInstanceId})."
          }
          if (targetTI == null) {
            errorString += " TitleInstance could not be found for targetTitleInstanceId (${targetTitleInstanceId})."
          }

          log.error(errorString)
        }
      }
    }
  }
}

