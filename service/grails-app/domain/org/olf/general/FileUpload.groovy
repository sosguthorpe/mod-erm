package org.olf.general
import grails.gorm.MultiTenant

class FileUpload implements MultiTenant<FileUpload> {

  String id
  byte[] fileContentBytes
  String fileContentType
  String fileName
  Long fileSize
  Date lastModified
  SingleFileAttachment owner

  static constraints = {
    fileContentBytes nullable: true
    fileContentType nullable: true
    lastModified nullable: true
    owner nullable: true
  }

  static mapping = {
                  id column: 'fu_id', generator: 'uuid2', length: 36
    fileContentBytes column: 'fu_bytes', sqlType: 'longblob'
            fileName column: 'fu_filename'
            fileSize column: 'fu_filesize'
        lastModified column: 'fu_last_mod' 
               owner column: 'fu_owner', type: 'string', length: 36
  }
}
