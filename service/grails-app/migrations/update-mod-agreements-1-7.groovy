databaseChangeLog = {
  changeSet(author: "sosguthorpe (generated)", id: "1558523001530-1") {
    addColumn(tableName: "entitlement") {
      column(name: "ent_po_line_id", type: "varchar(255)")
    }
  }

  changeSet(author: "sosguthorpe (generated)", id: "1558523001530-2") {
    addColumn(tableName: "custom_property") {
      column(name: "note", type: "text")
    }
  }

  changeSet(author: "sosguthorpe (generated)", id: "1558523001530-3") {
    addColumn(tableName: "custom_property_definition") {
      column(name: "pd_label", type: "varchar(255)") {
        constraints(nullable: "false")
      }
    }
  }

  changeSet(author: "sosguthorpe (generated)", id: "1558523001530-4") {
    addColumn(tableName: "custom_property_definition") {
      column(name: "pd_primary", type: "boolean") {
        constraints(nullable: "false")
      }
    }
  }

  changeSet(author: "sosguthorpe (generated)", id: "1558523001530-5") {
    addColumn(tableName: "custom_property_definition") {
      column(name: "pd_weight", type: "int4") {
        constraints(nullable: "false")
      }
    }
  }

  changeSet(author: "sosguthorpe (generated)", id: "1558523001530-6") {
    addUniqueConstraint(columnNames: "org_orgs_uuid", constraintName: "UC_ORGORG_ORGS_UUID_COL", tableName: "org")
  }

  changeSet(author: "sosguthorpe (generated)", id: "1558523001530-7") {
    createIndex(indexName: "td_label_idx", tableName: "custom_property_definition") {
      column(name: "pd_label")
    }
  }

  changeSet(author: "sosguthorpe (generated)", id: "1558523001530-8") {
    createIndex(indexName: "td_primary_idx", tableName: "custom_property_definition") {
      column(name: "pd_primary")
    }
  }

  changeSet(author: "sosguthorpe (generated)", id: "1558523001530-9") {
    createIndex(indexName: "td_weight_idx", tableName: "custom_property_definition") {
      column(name: "pd_weight")
    }
  }

  changeSet(author: "sosguthorpe (generated)", id: "1558523001530-10") {
    addForeignKeyConstraint(baseColumnNames: "da_type_rdv_fk", baseTableName: "document_attachment", constraintName: "FKrggvdxk0jingkcnidb4hfwpi4", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
  }

  changeSet(author: "sosguthorpe (generated)", id: "1558523001530-11") {
    dropForeignKeyConstraint(baseTableName: "po_line_proxy", constraintName: "FK8ufrte3dhjhseabh067wg08lr")
  }

  changeSet(author: "sosguthorpe (generated)", id: "1558523001530-12") {
    dropTable(tableName: "po_line_proxy")
  }

  changeSet(author: "sosguthorpe (generated)", id: "1558523001530-13") {
    dropColumn(columnName: "ent_label", tableName: "entitlement")
  }

  changeSet(author: "sosguthorpe (generated)", id: "1558523001530-14") {
    addNotNullConstraint(columnDataType: "varchar(36)", columnName: "co_ent_fk", tableName: "holdings_coverage")
  }

  changeSet(author: "sosguthorpe (generated)", id: "1558523001530-15") {
    addNotNullConstraint(columnDataType: "date", columnName: "co_start_date", tableName: "holdings_coverage")
  }

  changeSet(author: "sosguthorpe (generated)", id: "1558523001530-16") {
    addNotNullConstraint(columnDataType: "date", columnName: "cs_start_date", tableName: "coverage_statement")
  }

  changeSet(author: "sosguthorpe (generated)", id: "1558523001530-17") {
    dropNotNullConstraint(columnDataType: "varchar(255)", columnName: "org_name", tableName: "org")
  }
}