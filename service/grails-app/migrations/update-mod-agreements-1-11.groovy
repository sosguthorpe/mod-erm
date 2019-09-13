databaseChangeLog = {

  changeSet(author: "sosguthorpe (generated)", id: "1568388094555-1") {
    createTable(tableName: "license_amendment_status") {
      column(name: "las_id", type: "VARCHAR(36)") {
        constraints(nullable: "false")
      }
      
      column(name: "las_amendment_id", type: "VARCHAR(36)") {
        constraints(nullable: "false")
      }

      column(name: "version", type: "BIGINT") {
        constraints(nullable: "false")
      }

      column(name: "las_owner", type: "VARCHAR(36)") {
        constraints(nullable: "false")
      }

      column(name: "las_status", type: "VARCHAR(36)") {
        constraints(nullable: "false")
      }

      column(name: "las_note", type: "TEXT")
    }
  }

  changeSet(author: "sosguthorpe (generated)", id: "1568388094555-2") {
    addPrimaryKey(columnNames: "las_id", constraintName: "license_amendment_statusPK", tableName: "license_amendment_status")
  }

  changeSet(author: "sosguthorpe (generated)", id: "1568388094555-3") {
    addForeignKeyConstraint(baseColumnNames: "las_status", baseTableName: "license_amendment_status", constraintName: "FKbjdewcgoyen8p0gbs9npxwuei", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
  }

  changeSet(author: "sosguthorpe (generated)", id: "1568388094555-4") {
    addForeignKeyConstraint(baseColumnNames: "las_owner", baseTableName: "license_amendment_status", constraintName: "FKo66rknrrhqlfoxxpjcq1p9f54", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rol_id", referencedTableName: "remote_license_link")
  }
}