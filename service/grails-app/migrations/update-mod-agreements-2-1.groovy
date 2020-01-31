databaseChangeLog = {
  changeSet(author: "ethanfreestone (manual)", id: "202001141001-001") {
    addColumn(tableName: "title_instance") {
      column(name: "ti_date_monograph_published", type: "VARCHAR(36)")
      column(name: "ti_first_author", type: "VARCHAR(36)")
      column(name: "ti_monograph_edition", type: "VARCHAR(36)")
      column(name: "ti_monograph_volume", type: "VARCHAR(36)")
    }
  }

  changeSet(author: "ethanfreestone (manual)", id: "202001211524-001") {
    addColumn(tableName: "title_instance") {
      column(name: "ti_first_editor", type: "VARCHAR(36)")
    }
  }
  
  changeSet(author: "sosguthorpe (generated)", id: "1580297114943-2") {
    addColumn(tableName: "subscription_agreement") {
      column(name: "custom_properties_id", type: "int8") {
        constraints(nullable: "false")
      }
    }
    addForeignKeyConstraint(baseColumnNames: "custom_properties_id", baseTableName: "subscription_agreement", constraintName: "FKm0a9f7qqi2asb4ify197q2mak", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "custom_property_container")
  }
}
