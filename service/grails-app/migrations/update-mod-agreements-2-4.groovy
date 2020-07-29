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

    /** add publicationType, move existing refdata values of TitleInstance.Type to TitleInstance.PublicationType **/
    changeSet(author: "claudia (manual)", id: "202007271150-01") {
      addColumn(tableName: "erm_resource") {
        column(name: "res_publication_type_fk", type: "VARCHAR(36)")
      }
    }

    changeSet(author: "claudia (manual)", id: "202007271150-02") {
      addForeignKeyConstraint(baseColumnNames: "res_publication_type_fk", baseTableName: "erm_resource", constraintName: "pub_type_to_erm", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    }

    // set res_publication_type_fk to the value of res_type_fk for values 'book', 'journal', 'serial'
    changeSet(author: "claudia (manual)", id: "202007271150-03") {
      grailsChange {
        change {
          sql.execute("UPDATE ${database.defaultSchemaName}.erm_resource SET res_publication_type_fk = (SELECT rdv_id FROM ${database.defaultSchemaName}.refdata_value INNER JOIN ${database.defaultSchemaName}.refdata_category ON refdata_value.rdv_owner = refdata_category.rdc_id WHERE rdc_description='TitleInstance.PublicationType' AND rdv_value='book') WHERE res_type_fk=(SELECT rdv_id FROM ${database.defaultSchemaName}.refdata_value INNER JOIN ${database.defaultSchemaName}.refdata_category ON refdata_value.rdv_owner = refdata_category.rdc_id WHERE rdc_description='TitleInstance.Type' AND rdv_value='book')".toString())
        }
      }  
    }

    changeSet(author: "claudia (manual)", id: "202007271150-04") {
      grailsChange {
        change {
          sql.execute("UPDATE ${database.defaultSchemaName}.erm_resource SET res_publication_type_fk = (SELECT rdv_id FROM ${database.defaultSchemaName}.refdata_value INNER JOIN ${database.defaultSchemaName}.refdata_category ON refdata_value.rdv_owner = refdata_category.rdc_id WHERE rdc_description='TitleInstance.PublicationType' AND rdv_value='journal') WHERE res_type_fk=(SELECT rdv_id FROM ${database.defaultSchemaName}.refdata_value INNER JOIN ${database.defaultSchemaName}.refdata_category ON refdata_value.rdv_owner = refdata_category.rdc_id WHERE rdc_description='TitleInstance.Type' AND rdv_value='journal')".toString())
        }
      }  
    }

    changeSet(author: "claudia (manual)", id: "202007271150-05") {
      grailsChange {
        change {
          sql.execute("UPDATE ${database.defaultSchemaName}.erm_resource SET res_publication_type_fk = (SELECT rdv_id FROM ${database.defaultSchemaName}.refdata_value INNER JOIN ${database.defaultSchemaName}.refdata_category ON refdata_value.rdv_owner = refdata_category.rdc_id WHERE rdc_description='TitleInstance.PublicationType' AND rdv_value='serial') WHERE res_publication_type_fk is null AND res_type_fk=(SELECT rdv_id FROM ${database.defaultSchemaName}.refdata_value INNER JOIN ${database.defaultSchemaName}.refdata_category ON refdata_value.rdv_owner = refdata_category.rdc_id WHERE rdc_description='TitleInstance.Type' AND rdv_value='serial')".toString())
        }
      }  
    }

    // set res_type_fk 'book' to 'monograph'
    changeSet(author: "claudia (manual)", id: "202007271150-06") {
      grailsChange {
        change {
          sql.execute("UPDATE ${database.defaultSchemaName}.erm_resource SET res_type_fk = (SELECT rdv_id FROM ${database.defaultSchemaName}.refdata_value INNER JOIN ${database.defaultSchemaName}.refdata_category ON refdata_value.rdv_owner = refdata_category.rdc_id WHERE rdc_description='TitleInstance.Type' AND rdv_value='monograph') WHERE res_type_fk=(SELECT rdv_id FROM ${database.defaultSchemaName}.refdata_value INNER JOIN ${database.defaultSchemaName}.refdata_category ON refdata_value.rdv_owner = refdata_category.rdc_id WHERE rdc_description='TitleInstance.Type' AND rdv_value='book')".toString())
        }
      }  
    }

    // set res_type_fk 'journal' to 'serial'
    changeSet(author: "claudia (manual)", id: "202007271150-07") {
      grailsChange {
        change {
          sql.execute("UPDATE ${database.defaultSchemaName}.erm_resource SET res_type_fk = (SELECT rdv_id FROM ${database.defaultSchemaName}.refdata_value INNER JOIN ${database.defaultSchemaName}.refdata_category ON refdata_value.rdv_owner = refdata_category.rdc_id WHERE rdc_description='TitleInstance.Type' AND rdv_value='serial') WHERE res_type_fk=(SELECT rdv_id FROM ${database.defaultSchemaName}.refdata_value INNER JOIN ${database.defaultSchemaName}.refdata_category ON refdata_value.rdv_owner = refdata_category.rdc_id WHERE rdc_description='TitleInstance.Type' AND rdv_value='journal')".toString())
        }
      }  
    }

    // delete refdata values 'book' and 'journal' for TitleInstance.Type
    changeSet(author: "claudia (manual)", id: "202007271150-08") {
      grailsChange {
        change {
          sql.execute("""
            DELETE from ${database.defaultSchemaName}.refdata_value where rdv_value in ('book', 'journal') and rdv_owner=(select rdc_id from ${database.defaultSchemaName}.refdata_category where rdc_description = 'TitleInstance.Type')
          """.toString())
        }
      }  
    }

    // fix typo for rdv_label capitalization
    changeSet(author: "claudia (manual)", id: "202007271150-09") {
      grailsChange {
        change {
          sql.execute("""
            UPDATE ${database.defaultSchemaName}.refdata_value SET rdv_label = 'Serial' WHERE rdv_label = 'serial';
          """.toString())
        }
      }
    }
}
