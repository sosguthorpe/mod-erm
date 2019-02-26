databaseChangeLog = {

  changeSet(author: "ibbo (generated)", id: "1551106615373-14") {
    createTable(tableName: "document_attachment") {
      column(name: "da_id", type: "VARCHAR(36)") {
        constraints(nullable: "false")
      }
      column(name: "da_version", type: "BIGINT") {
        constraints(nullable: "false")
      }
      column(name: "da_date_created", type: "timestamp")
      column(name: "da_last_updated", type: "timestamp")
      column(name: "da_location", type: "VARCHAR(255)")
      column(name: "da_type_rdv_fk", type: "VARCHAR(36)")
      column(name: "da_name", type: "VARCHAR(255)")
      column(name: "da_note", type: "CLOB")
    }
  }

  changeSet(author: "ibbo (generated)", id: "1551106615373-35") {
    createTable(tableName: "subscription_agreement_document_attachment") {
      column(name: "subscription_agreement_docs_id", type: "VARCHAR(36)") {
        constraints(nullable: "false")
      }
      column(name: "document_attachment_id", type: "VARCHAR(36)")
    }
  }

  changeSet(author: "ibbo (generated)", id: "1551106615373-52") {
    addPrimaryKey(columnNames: "da_id", constraintName: "document_attachmentPK", tableName: "document_attachment")
  }

  changeSet(author: "ibbo (generated)", id: "1551106615373-99") {
    addForeignKeyConstraint(baseColumnNames: "subscription_agreement_docs_id", baseTableName: "subscription_agreement_document_attachment", constraintName: "FK9e3veji1y1b7qly5a3gktupeh", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "sa_id", referencedTableName: "subscription_agreement")
  }

  changeSet(author: "ibbo (generated)", id: "1551106615373-117") {
    addForeignKeyConstraint(baseColumnNames: "document_attachment_id", baseTableName: "subscription_agreement_document_attachment", constraintName: "FKgwss7xoeluk8be4btg3eljeui", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "da_id", referencedTableName: "document_attachment")
  }

}
