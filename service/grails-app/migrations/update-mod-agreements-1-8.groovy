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
}