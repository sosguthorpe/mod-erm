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
      column(name: "st_id", type: "VARCHAR(36)") {
        constraints(nullable: "false")
      }
      column(name: "version", type: "BIGINT") {
          constraints(nullable: "false")
      }
      column(name: "st_name", type: "VARCHAR(255)") {
        constraints(nullable: "false")
      }
      column(name: "st_rule", type: "TEXT") {
          constraints(nullable: "false")
      }
      column(name: "st_context", type: "VARCHAR(255)") {
          constraints(nullable: "false")
      }
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

}
