package org.olf.general

import grails.gorm.multitenancy.CurrentTenant
import groovy.util.logging.Slf4j

import com.k_int.okapi.OkapiTenantAwareController

import grails.converters.JSON
import groovy.util.logging.Slf4j


@Slf4j
@CurrentTenant
class FileUploadController {

  FileUploadDataService fileUploadDataService

  def postFileUploadRaw() {
    log.debug("Called postFileUploadRaw")
    def f = request.getFile('upload')
    if(f == null) {
      log.debug("No file found")
      notFound()
      return
    }
    FileUpload fileUpload = fileUploadDataService.save(
      f.contentType,
      f.originalFilename,
      f.size,
      new Date(),
      f.inputStream.bytes
    )

    if(fileUpload == null) {
      log.debug("unable to create file upload")
      notFound()
      return
    }

    if(fileUpload.hasErrors()) {
      log.debug("fileUpload has errors")
      respond(fileUpload.hasErrors())
      return
    }

    response.status = 201
    respond fileUpload
  }

  def getFileUploadList() {
    log.debug("Called getFileUploadList")
    respond fileUploadDataService.list()
  }

  def getFileUpload() {
    log.debug("Called getFileUpload")
    FileUpload fileUpload = fileUploadDataService.get(params.id)
    if(!fileUpload || fileUpload.fileContentBytes == null) {
      notFound()
      return
    }
    respond fileUpload
  }

  def getFileUploadRaw() {
    log.debug("Called getFileUploadRaw")
    FileUpload fileUpload = fileUploadDataService.get(params.id)
    if(!fileUpload || fileUpload.fileContentBytes == null) {
      notFound()
      return
    }
    render file: fileUpload.fileContentBytes, contentType: fileUpload.fileContentType

  }

  def deleteFileUpload() {
    log.debug("Called deleteFileUpload")
    if(params.id == null) {
      notFound()
      return
    }

    fileUploadDataService.delete(params.id)
    response.status = 204
    render ""
  }

  def protected void notFound(String message="Resource not found") {
    render(status:404,text:message,contentType: 'application/json')
  }

}
