databaseChangeLog = {
  final Map<String, List<String>> table_cols = [
    'content_activation_record': ['car_date_activation', 'car_date_deactivation'],
    'entitlement': ['ent_active_to', 'ent_active_from', 'ent_content_updated'],
    'package_content_item': ['pci_access_start', 'pci_access_end'],
    'sa_event_history': ['eh_event_date'],
    'subscription_agreement': ['sa_cancellation_deadline', 'sa_renewal_date', 'sa_end_date', 'sa_start_date', 'sa_next_review_date']
  ]
  
  changeSet(author: "sosguthorpe", id: "1562063159557-1") {
    
    table_cols.each { String table, List<String> cols ->
      // Add backup columns for all the originals.
      
      addColumn(tableName: table) {
        cols.each { String col ->
          column (name: "${col}_old", type: "TIMESTAMP")
        }
      }
      
      // Copy the data
      cols.each { String col ->
        grailsChange {
          change {
            // Set the dates
            sql.execute("UPDATE ${database.defaultSchemaName}.${table} SET ${col}_old = ${col};".toString())
          }
        }
        
        // Change the column type
        modifyDataType (tableName: table, columnName: "${col}", newDataType: "DATE")
        
        // Success drop the column
      }
    }
  }
  
  // Second changeset so as to only drop when the preceding one has succeeded 
  changeSet(author: "sosguthorpe", id: "1562063159557-2") {
    table_cols.each { String table, List<String> cols ->
      cols.each { String col ->
        // Tidy up by dropping all the backup columns.
        dropColumn(tableName: table, columnName: "${col}_old")
      }
    }
  }
  
  changeSet(author: "sosguthorpe (generated)", id: "1563377308054-1") {
    createTable(tableName: "log_entry") {
      column(name: "le_id", type: "VARCHAR(36)") {
        constraints(nullable: "false")
      }

      column(name: "version", type: "BIGINT") {
        constraints(nullable: "false")
      }

      column(name: "le_message", type: "TEXT")

      column(name: "le_origin", type: "VARCHAR(255)") {
        constraints(nullable: "false")
      }

      column(name: "le_datecreated", type: "TIMESTAMP WITHOUT TIME ZONE")

      column(name: "le_type", type: "VARCHAR(255)") {
        constraints(nullable: "false")
      }
    }
  }

  changeSet(author: "sosguthorpe (generated)", id: "1563377308054-2") {
    createTable(tableName: "package_ingest_job") {
      column(name: "id", type: "VARCHAR(255)") {
        constraints(nullable: "false")
      }
    }
  }

  changeSet(author: "sosguthorpe (generated)", id: "1563377308054-3") {
    createTable(tableName: "persistent_job") {
      column(name: "id", type: "VARCHAR(36)") {
        constraints(nullable: "false")
      }

      column(name: "version", type: "BIGINT") {
        constraints(nullable: "false")
      }

      column(name: "job_date_created", type: "TIMESTAMP WITHOUT TIME ZONE")

      column(name: "job_name", type: "VARCHAR(255)") {
        constraints(nullable: "false")
      }

      column(name: "job_ended", type: "TIMESTAMP WITHOUT TIME ZONE")

      column(name: "job_started", type: "TIMESTAMP WITHOUT TIME ZONE")

      column(name: "job_status_fk", type: "VARCHAR(36)") {
        constraints(nullable: "false")
      }

      column(name: "job_result_fk", type: "VARCHAR(36)")
    }
  }
  
  changeSet(author: "sosguthorpe (generated)", id: "1563377308054-5") {
    addPrimaryKey(columnNames: "le_id", constraintName: "log_entryPK", tableName: "log_entry")
  }

  changeSet(author: "sosguthorpe (generated)", id: "1563377308054-6") {
    addPrimaryKey(columnNames: "id", constraintName: "package_ingest_jobPK", tableName: "package_ingest_job")
  }

  changeSet(author: "sosguthorpe (generated)", id: "1563377308054-7") {
    addPrimaryKey(columnNames: "id", constraintName: "persistent_jobPK", tableName: "persistent_job")
  }

  changeSet(author: "sosguthorpe (generated)", id: "1563377308054-9") {
    createIndex(indexName: "origin_idx", tableName: "log_entry") {
      column(name: "le_origin")
    }
  }

}