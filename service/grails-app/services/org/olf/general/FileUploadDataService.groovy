package org.olf.general

import grails.gorm.services.Service

@Service(FileUpload)
interface FileUploadDataService {
  FileUpload get(String id)
  List<FileUpload> list(Map args)
  Number count()
  void delete(Serializable id)
  FileUpload save(String fileContentType, String fileName, Long fileSize, Date lastModified,  byte[] fileContentBytes)
}
