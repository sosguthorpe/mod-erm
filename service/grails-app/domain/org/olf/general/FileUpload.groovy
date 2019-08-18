package org.olf.general
import grails.gorm.MultiTenant
import grails.gorm.multitenancy.Tenants

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

  def afterUpdate() {
    log.info("afterUpdate() for FileUpload")

    if (this.owner == null) {
      final String toDelete = this.id
      final Serializable currentTenantId = Tenants.currentId()
      Tenants.withId(currentTenantId) {
        try {
          FileUpload.get(toDelete).delete()
        } catch(Exception e) {
          log.error("Error trying to delete ownerless fileUpload objects: ${e.getMessage()}")
        }

      }
    }
  }

}
