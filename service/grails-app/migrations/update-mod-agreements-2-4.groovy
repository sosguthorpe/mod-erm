databaseChangeLog = {
  changeSet(author: "claudia (manual)", id: "202004061829-01") {
        createTable(tableName: "subscription_agreement_alternate_names") {
            column(name: "subscription_agreement_id", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }
            column(name: "alternate_names_string", type: "VARCHAR(255)")
        }
  }

  changeSet(author: "claudia (manual)", id: "202004061829-02") {
       addForeignKeyConstraint(baseColumnNames: "subscription_agreement_id", baseTableName: "subscription_agreement_alternate_names", constraintName: "FKbwixs452hfe48k069eip5xgx0", referencedColumnNames: "sa_id", referencedTableName: "subscription_agreement")
  }
}
