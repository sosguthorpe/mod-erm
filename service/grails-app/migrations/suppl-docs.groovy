databaseChangeLog = {  
  changeSet(author: "claudia (manual)", id: "2019-05-28-00001") {
    createTable(tableName: "subscription_agreement_supp_doc") {
      column(name: "sasd_sa_fk", type: "VARCHAR(36)") {
        constraints(nullable: "false")
      }
      column(name: "sasd_da_fk", type: "VARCHAR(36)")
    }
  }

  // Foreign key constraints for supplementary documents
  changeSet(author: "claudia (manual)", id: "2019-05-28-00002") {
    addForeignKeyConstraint(baseColumnNames: "sasd_sa_fk",
                            baseTableName: "subscription_agreement_supp_doc",
                            constraintName: "sasd_to_sa_fk",
                            deferrable: "false", initiallyDeferred: "false",
                            referencedColumnNames: "sa_id",
                            referencedTableName: "subscription_agreement")
  }

  changeSet(author: "claudia (manual)", id: "2019-05-28-00003") {
    addForeignKeyConstraint(baseColumnNames: "sasd_da_fk",
                            baseTableName: "subscription_agreement_supp_doc",
                            constraintName: "sasd_to_da_fk",
                            deferrable: "false", initiallyDeferred: "false",
                            referencedColumnNames: "da_id",
                            referencedTableName: "document_attachment")
  }
}
