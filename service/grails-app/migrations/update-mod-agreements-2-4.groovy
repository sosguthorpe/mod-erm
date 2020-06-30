databaseChangeLog = {
    changeSet(author: "claudia (manual)", id: "202006291630-1") {
        addColumn(tableName: "subscription_agreement_org") {
            column(name: "sao_note", type: "text")
        }
    }
    changeSet(author: "sosguthorpe (generated)", id: "1593002234734-1") {
      createTable(tableName: "comparison_job") {
        column(name: "id", type: "VARCHAR(36)") {
          constraints(nullable: "false")
        }
        column(name: "cj_file_contents", type: "OID") {
          constraints(nullable: "true")
        }
      }

      addPrimaryKey(columnNames: "id", constraintName: "comparison_jobPK", tableName: "comparison_job")
    }
    
    changeSet(author: "sosguthorpe (generated)", id: "1593002234734-2") {
      createTable(tableName: "comparison_point") {
        column(name: "cp_id", type: "VARCHAR(36)") {
            constraints(nullable: "false")
        }

        column(name: "version", type: "BIGINT") {
            constraints(nullable: "false")
        }

        column(name: "date", type: "date") {
            constraints(nullable: "false")
        }

        column(name: "job_id", type: "VARCHAR(36)") {
            constraints(nullable: "false")
        }

        column(name: "cp_title_list_fk", type: "VARCHAR(36)") {
            constraints(nullable: "false")
        }
      }
      
      addPrimaryKey(columnNames: "cp_id", constraintName: "comparison_pointPK", tableName: "comparison_point")
    }

    changeSet(author: "sosguthorpe (generated)", id: "1593002234734-3") {
      createTable(tableName: "erm_title_list") {
        column(name: "id", type: "VARCHAR(36)") {
          constraints(nullable: "false")
        }

        column(name: "version", type: "BIGINT") {
          constraints(nullable: "false")
        }
      }
    
      addPrimaryKey(columnNames: "id", constraintName: "erm_title_listPK", tableName: "erm_title_list")
    }

    changeSet(author: "sosguthorpe (generated)", id: "1593002234734-4") {
      
      addForeignKeyConstraint(baseColumnNames: "job_id", baseTableName: "comparison_point", constraintName: "FKce030h04m5jso6xgk2bontg3p",
          deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "comparison_job")
      
      addForeignKeyConstraint(baseColumnNames: "cp_title_list_fk", baseTableName: "comparison_point", constraintName: "FKsngmq049d951379bow12ufkno",
          deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "erm_title_list")
    }
    
    changeSet(author: "sosguthorpe", id: "1593002234734-5") {
      // Copy all agreement ID/versions into title list table
      grailsChange {
        change {
          sql.execute("""
            INSERT INTO ${database.defaultSchemaName}.erm_title_list(id, version)
            SELECT sa_id, sa_version FROM ${database.defaultSchemaName}.subscription_agreement;
          """.toString())
        }
      }
      
      dropColumn(columnName: "sa_version", tableName: "subscription_agreement")
    }
    
    
    changeSet(author: "sosguthorpe", id: "1593002234734-6") {
      // Copy all resource ID/versions into title list table
      grailsChange {
        change {
          sql.execute("""
            INSERT INTO ${database.defaultSchemaName}.erm_title_list(id, version)
            SELECT id, version FROM ${database.defaultSchemaName}.erm_resource;
          """.toString())
        }
      }
      dropColumn(columnName: "version", tableName: "erm_resource")
    }
    
    
    /** Tidy missing PK contraint **/
    changeSet(author: "sosguthorpe (generated)", id: "1593002234734-7") {
      addPrimaryKey(columnNames: "id", constraintName: "kbart_import_jobPK", tableName: "kbart_import_job")
    }
}