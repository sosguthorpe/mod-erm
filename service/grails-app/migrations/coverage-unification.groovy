databaseChangeLog = {
  changeSet(author: "Steve Osguthorpe", id: "2019-03-14-ERM-65-1") {
    addColumn(tableName: "coverage_statement") {
      column (name: "cs_resource_fk", type: "VARCHAR(36)")
    }
    
    // Combine foreign key references into single column.
    grailsChange {
      change {
        // Create default values for the labels.
        sql.execute("UPDATE ${database.defaultSchemaName}.coverage_statement SET cs_resource_fk = COALESCE(cs_pci_fk, cs_ti_fk, cs_pti_fk);".toString())
      }
    }
    
    // Add the constraints after adding the data.
    addNotNullConstraint (tableName: "coverage_statement", columnName: "cs_resource_fk" )
    addForeignKeyConstraint(baseColumnNames: "cs_resource_fk", baseTableName: "coverage_statement", constraintName: "coverageresourcefk", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "erm_resource")
    
    // Rename the existing columns
    renameColumn(tableName: "coverage_statement", oldColumnName: "cs_start_date", newColumnName: "cs_start_date_string")
    renameColumn(tableName: "coverage_statement", oldColumnName: "cs_end_date", newColumnName: "cs_end_date_string")
    
    // Add two new columns with the old names but with the correct type.
    addColumn(tableName: "coverage_statement") {
      column (name: "cs_start_date", type: "DATE")
      column (name: "cs_end_date", type: "DATE")
    }
    grailsChange {
      change {
        // Set the dates
        sql.execute("UPDATE ${database.defaultSchemaName}.coverage_statement SET cs_start_date = TO_DATE ( NULLIF(REGEXP_REPLACE(cs_start_date_string,'^(\\d{4}-\\d{2}-\\d{2}).*','\\1'),''), 'YYYY-MM-DD'), cs_end_date = TO_DATE ( NULLIF(REGEXP_REPLACE(cs_end_date_string,'^(\\d{4}-\\d{2}-\\d{2}).*','\\1'), ''), 'YYYY-MM-DD');".toString())
      }
    }
    
    // REPEAT for HoldingsCoverage
    // Rename the existing columns
    renameColumn(tableName: "holdings_coverage", oldColumnName: "co_start_date", newColumnName: "co_start_date_string")
    renameColumn(tableName: "holdings_coverage", oldColumnName: "co_end_date", newColumnName: "co_end_date_string")
    
    // Add two new columns with the old names but with the correct type.
    addColumn(tableName: "holdings_coverage") {
      column (name: "co_start_date", type: "DATE")
      column (name: "co_end_date", type: "DATE")
    }
    grailsChange {
      change {
        // Set the dates
        sql.execute("UPDATE ${database.defaultSchemaName}.holdings_coverage SET co_start_date = TO_DATE (NULLIF(REGEXP_REPLACE(co_start_date_string,'^(\\d{4}-\\d{2}-\\d{2}).*','\\1'), ''), 'YYYY-MM-DD'), co_end_date = TO_DATE (NULLIF(REGEXP_REPLACE(co_end_date_string,'^(\\d{4}-\\d{2}-\\d{2}).*','\\1'), ''), 'YYYY-MM-DD');".toString())
      }
    }
    
    // Drop superfluous columns
    dropColumn(tableName: "coverage_statement", columnName: "cs_pci_fk")
    dropColumn(tableName: "coverage_statement", columnName: "cs_ti_fk")
    dropColumn(tableName: "coverage_statement", columnName: "cs_pti_fk")
    dropColumn(tableName: "coverage_statement", columnName: "cs_start_date_string")
    dropColumn(tableName: "coverage_statement", columnName: "cs_end_date_string")
    dropColumn(tableName: "holdings_coverage", columnName: "co_start_date_string")
    dropColumn(tableName: "holdings_coverage", columnName: "co_end_date_string")
    
    
  }
}