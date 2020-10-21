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

}
