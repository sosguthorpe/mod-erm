databaseChangeLog = {
  changeSet(author: "Kurt Nordstrom", id: "2019-05-29-0001") {
    addColumn(tableName: "document_attachment") {
      column(name: "da_file_upload", type: "VARCHAR(36)")
    }
  
  }

  changeSet(author: "Kurt Nordstrom", id: "2019-05-29-0002") {
        createTable(tableName: "file_upload") {
            column(name: "fu_id", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "fu_filesize", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "fu_last_mod", type: "timestamp")

            column(name: "file_content_type", type: "VARCHAR(255)")

            column(name: "fu_filename", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "fu_bytes", type: "bytea")
        }

  }
  
  changeSet(author: "kurt (generated)", id: "2019-05-29-0004") {
        addPrimaryKey(columnNames: "fu_id", constraintName: "file_uploadPK", tableName: "file_upload")
  }

  changeSet(author: "kurt (generated)", id: "2019-05-29-0005") {
        addForeignKeyConstraint(baseColumnNames: "da_file_upload", baseTableName: "document_attachment", constraintName: "FKsn3g0f85naqw4heh0rlqgo8x8", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "fu_id", referencedTableName: "file_upload")
  }

}
