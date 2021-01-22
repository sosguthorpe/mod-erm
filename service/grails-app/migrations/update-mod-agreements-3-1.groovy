databaseChangeLog = {

  // Track the additions and removals of entitlements so they can be consumed as an event stream by downstream processes
  changeSet(author: "ianibbo (manual)", id: "202010121929-001") {

    createTable(tableName: "entitlement_log_entry") {

      column(name: "ele_id", type: "VARCHAR(36)") {
        constraints(nullable: "false")
      }


      column(name: "ele_seq_id", type: "VARCHAR(36)") {
          constraints(nullable: "false")
      }

      column(name: "version", type: "BIGINT") {
          constraints(nullable: "false")
      }

      column(name: "ele_start_date", type: "date") {
          constraints(nullable: "false")
      }

      column(name: "ele_end_date", type: "date") {
          constraints(nullable: "true")
      }

      column(name: "ele_res", type: "VARCHAR(36)") {
          constraints(nullable: "false")
      }

      column(name: "ele_direct_entitlement", type: "VARCHAR(36)") {
          constraints(nullable: "true")
      }

      column(name: "ele_pkg_entitlement", type: "VARCHAR(36)") {
          constraints(nullable: "true")
      }
    }
    addPrimaryKey(columnNames: "ele_id", constraintName: "entitlement_log_entry_jobPK", tableName: "entitlement_log_entry")
  }

  changeSet(author: "efreestone (manual)", id: "202010211111-001") {
    addColumn (tableName: "platform" ) {
      column(name: "pt_local_code", type: "VARCHAR(255)")
    }
  }
  
  changeSet(author: "efreestone (manual)", id: "202010211324-001") {
    createTable(tableName: "string_template") {
      column(name: "strt_id", type: "VARCHAR(36)") {
        constraints(nullable: "false")
      }
      column(name: "version", type: "BIGINT") {
          constraints(nullable: "false")
      }
      column(name: "strt_name", type: "VARCHAR(255)") {
        constraints(nullable: "false")
      }
      column(name: "strt_rule", type: "TEXT") {
          constraints(nullable: "false")
      }
      column(name: "strt_context", type: "VARCHAR(255)") {
          constraints(nullable: "false")
      }
      column(name: "strt_date_created", type: "timestamp")
      column(name: "strt_last_updated", type: "timestamp")
    }
  }

  changeSet(author: "efreestone (manual)", id: "202010211324-002") {
    createTable(tableName: "string_template_scopes") {
      column(name: "id_scope", type: "VARCHAR(255)") {
          constraints(nullable: "false")
      }
    }
  }

  changeSet(author: "efreestone (manual)", id: "202010211324-003") {
    addColumn(tableName: "string_template_scopes") {
      column(name: "string_template_id", type: "VARCHAR(255)")
    }
  }

  changeSet(author: "efreestone (manual)", id: "202010261056-001") {
    createTable(tableName: "templated_url") {
      column(name: "tu_id", type: "VARCHAR(36)") {
        constraints(nullable: "false")
      }
      column(name: "version", type: "BIGINT") {
          constraints(nullable: "false")
      }
      column(name: "tu_name", type: "VARCHAR(255)") {
        constraints(nullable: "false")
      }
      column(name: "tu_url", type: "TEXT") {
          constraints(nullable: "false")
      }
      column(name: "tu_resource_fk", type: "VARCHAR(36)")
    }
  }

  changeSet(author: "efreestone (manual)", id: "202010261056-002") {
    addForeignKeyConstraint(baseColumnNames: "tu_resource_fk", baseTableName: "templated_url", constraintName: "templated_url_erm_resourceFK", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "erm_resource")
  }

  changeSet(author: "efreestone (manual)", id: "202011101241-001") {
    createTable(tableName: "app_setting") {
      column(name: "st_id", type: "VARCHAR(36)") {
          constraints(nullable: "false")
      }
      column(name: "st_version", type: "BIGINT") {
          constraints(nullable: "false")
      }
      column(name: 'st_section', type: "VARCHAR(255)")
      column(name: 'st_key', type: "VARCHAR(255)")
      column(name: 'st_setting_type', type: "VARCHAR(255)")
      column(name: 'st_vocab', type: "VARCHAR(255)")
      column(name: 'st_default_value', type: "VARCHAR(255)")
      column(name: 'st_value', type: "VARCHAR(255)")
    } 
  }

  changeSet(author: "efreestone (manual)", id: "202011101241-002") {
    addColumn (tableName: "platform" ) {
      column(name: "pt_date_created", type: "timestamp")
    }
    addColumn (tableName: "platform" ) {
      column(name: "pt_last_updated", type: "timestamp")
    }
  }

  changeSet(author: "efreestone (manual)", id: "202101151051-001") {
    modifyDataType(
      tableName: "title_instance",
      columnName: "ti_monograph_volume", type: "VARCHAR(255)",
      newDataType: "VARCHAR(255)",
      confirm: "Successfully updated the ti_monograph_volume column."
    )
    modifyDataType(
      tableName: "title_instance",
      columnName: "ti_monograph_edition", type: "VARCHAR(255)",
      newDataType: "VARCHAR(255)",
      confirm: "Successfully updated the ti_monograph_edition column."
    )
    modifyDataType(
      tableName: "title_instance",
      columnName: "ti_first_author", type: "VARCHAR(255)",
      newDataType: "VARCHAR(255)",
      confirm: "Successfully updated the ti_first_author column."
    )
    modifyDataType(
      tableName: "title_instance",
      columnName: "ti_first_editor", type: "VARCHAR(255)",
      newDataType: "VARCHAR(255)",
      confirm: "Successfully updated the ti_first_editor column."
    )
  }

  changeSet(author: "efreestone (manual)", id: "202101211152-001") {
    addColumn (tableName: "subscription_agreement" ) {
      column(name: "sa_start_date", type: "timestamp")
    }
    addColumn (tableName: "subscription_agreement" ) {
      column(name: "sa_end_date", type: "timestamp")
    }
  }
}
