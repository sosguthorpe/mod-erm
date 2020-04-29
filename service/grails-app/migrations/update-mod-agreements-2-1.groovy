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

  changeSet(author: "peter (generated)", id: "1579093826683-42") {
    addColumn(tableName: "remotekb") {
      column(name: "rkb_readonly", type: "BOOLEAN")
    }

    grailsChange {
      change {
        sql.execute("""
            UPDATE ${database.defaultSchemaName}.remotekb
            SET rkb_readonly=TRUE
            WHERE rkb_name LIKE 'LOCAL'
            """.toString())
      }
    }

    grailsChange {
      change {
        sql.execute("""
              UPDATE ${database.defaultSchemaName}.remotekb
              SET rkb_readonly=FALSE
              WHERE rkb_name NOT LIKE 'LOCAL'
              """.toString())
      }
    }
  }

  changeSet(author: "sosguthorpe (generated)", id: "1580297114943-2") {
    
    // Add the table but do not constrain the column yet.
    addColumn(tableName: "subscription_agreement") {
      column(name: "custom_properties_id", type: "int8")
    }
    
    // Need to create data for the existing agreements.
    grailsChange {
      change {
                
        sql.eachRow("SELECT sa_id FROM ${database.defaultSchemaName}.subscription_agreement".toString()) { def row ->
        
          // Create custom property.
          def results = sql.executeInsert("""
            INSERT INTO ${database.defaultSchemaName}.custom_property (version, internal)
                VALUES (0, TRUE);
          """.toString())
          
          // Save the ID.
          def subs = ['prop_id': (results[0][0])]
          
          // Also add the same ID as a CP container.
          sql.execute("""  
            INSERT INTO ${database.defaultSchemaName}.custom_property_container (id) VALUES(:prop_id);
          """.toString(), subs)
          
          // Update the SA
          sql.execute (
            "UPDATE ${database.defaultSchemaName}.subscription_agreement SET custom_properties_id = :prop_id WHERE sa_id = :sa_id".toString(),
            subs + ['sa_id': row.sa_id]
          )
        }
      }
    }
    
    // Add the constraints
    addNotNullConstraint (tableName: "subscription_agreement", columnName: "custom_properties_id")
    addForeignKeyConstraint(baseColumnNames: "custom_properties_id", baseTableName: "subscription_agreement", constraintName: "FKm0a9f7qqi2asb4ify197q2mak", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "custom_property_container")
    
    // Previous checksum will be valid and present if the agreements table was empty
    validCheckSum ('7:8765333e36004aa2e3721c4634320095')
  }

  changeSet(author: "efreestone (manual)", id: "202002111539-001") {
    createTable(tableName: "kbart_import_job") {
      column(name: "id", type: "VARCHAR(255)") {
        constraints(nullable: "false")
      }
      column(name: "package_name", type: "VARCHAR(255)") {
        constraints(nullable: "false")
      }
      column(name: "package_source", type: "VARCHAR(255)") {
        constraints(nullable: "false")
      }
      column(name: "package_reference", type: "VARCHAR(255)") {
        constraints(nullable: "false")
      }
      column(name: "package_provider", type: "VARCHAR(255)")
    }
  }
  
  changeSet(author: "sosguthorpe (generated)", id: "1588162202202-1") {
    grailsChange {
      change {
        def results
        try {
          results = sql.rows( "SELECT setval('${database.defaultSchemaName}.custom_property_id_seq', max(id)) FROM ${database.defaultSchemaName}.custom_property;".toString() )
        } catch ( Exception ex ) { /* Allow to silently fail. */ }
        
        try {
          results = sql.rows( "SELECT setval('${database.defaultSchemaName}.hibernate_sequence', max(id)) FROM ${database.defaultSchemaName}.custom_property;".toString() )          
        } catch ( Exception ex ) { /* Allow to silently fail. */ }
        
        long max = results[0][0]
        confirm "Updated counter to ${max}"
      }
    }
  }
}
