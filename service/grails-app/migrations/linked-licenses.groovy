databaseChangeLog = {

  changeSet(author: "sosguthorpe", id: "20190306-1") {
    createTable(tableName: "remote_license_link") {
      column(name: "rol_id", type: "VARCHAR(36)") {
        constraints(nullable: "false")
      }

      column(name: "rol_version", type: "BIGINT") {
        constraints(nullable: "false")
      }

      column(name: "rol_remote_id", type: "VARCHAR(50)") {
        constraints(nullable: "false")
      }

      column(name: "rll_owner", type: "VARCHAR(36)") {
        constraints(nullable: "false")
      }

      column(name: "rll_status", type: "VARCHAR(36)") {
        constraints(nullable: "false")
      }

      column(name: "rll_note", type: "CLOB")
    }
    
    addPrimaryKey(columnNames: "rol_id", constraintName: "remote_license_linkPK", tableName: "remote_license_link")
    addForeignKeyConstraint(baseColumnNames: "rll_status", baseTableName: "remote_license_link", constraintName: "FKlsj76qkjait9qftvhfxcjdgnn", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    addForeignKeyConstraint(baseColumnNames: "rll_owner", baseTableName: "remote_license_link", constraintName: "FKm3epwmvt7c9kdgc5f0fm5bhxp", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "sa_id", referencedTableName: "subscription_agreement")
  }
}
