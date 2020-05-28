databaseChangeLog = {
  changeSet(author: "efreestone (manual)", id: "202003231555-1") {
    modifyDataType(
        tableName: "custom_property_definition",
        columnName: "pd_description", type: "text",
        newDataType: "text",
        confirm: "Successfully updated the pd_description column."
        )
  }
  changeSet(author: "claudia (manual)", id: "202004091233-01") {
        createTable(tableName: "alternate_name") {
            column(name: "an_id", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "an_version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "an_owner_fk", type: "VARCHAR(36)")

            column(name: "an_name", type: "VARCHAR(255)")
        }
  }

  changeSet(author: "claudia (manual)", id: "202004091233-02") {
        addForeignKeyConstraint(baseColumnNames: "an_owner_fk", baseTableName: "alternate_name", constraintName: "an_to_sa_fk", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "sa_id", referencedTableName: "subscription_agreement")
  }

  changeSet(author: "sosguthorpe (generated)", id: "1586289817497-1") {
    createTable(tableName: "embargo") {
      column(name: "emb_id", type: "VARCHAR(36)") {
        constraints(nullable: "false")
      }

      column(name: "version", type: "BIGINT") {
        constraints(nullable: "false")
      }

      column(name: "emb_end_fk", type: "VARCHAR(36)")

      column(name: "emb_start_fk", type: "VARCHAR(36)")
    }
  }

  changeSet(author: "sosguthorpe (generated)", id: "1586289817497-2") {
    createTable(tableName: "embargo_statement") {
      column(name: "est_id", type: "VARCHAR(36)") {
        constraints(nullable: "false")
      }

      column(name: "version", type: "BIGINT") {
        constraints(nullable: "false")
      }

      column(name: "est_type", type: "VARCHAR(255)") {
        constraints(nullable: "false")
      }

      column(name: "est_unit", type: "VARCHAR(255)") {
        constraints(nullable: "false")
      }

      column(name: "est_length", type: "INT") {
        constraints(nullable: "false")
      }
    }
  }

  changeSet(author: "sosguthorpe (generated)", id: "1586289817497-3") {
    addColumn(tableName: "package_content_item") {
      column(name: "pci_embargo_fk", type: "varchar(36)")
    }
  }

  changeSet(author: "sosguthorpe (generated)", id: "1586289817497-4") {
    addPrimaryKey(columnNames: "emb_id", constraintName: "embargoPK", tableName: "embargo")
  }

  changeSet(author: "sosguthorpe (generated)", id: "1586289817497-5") {
    addPrimaryKey(columnNames: "est_id", constraintName: "embargo_statementPK", tableName: "embargo_statement")
  }

  changeSet(author: "sosguthorpe (generated)", id: "1586289817497-6") {
    addForeignKeyConstraint(baseColumnNames: "emb_start_fk", baseTableName: "embargo", constraintName: "FKaqsox5q361gjhl1dx9ulb8ra5", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "est_id", referencedTableName: "embargo_statement")
  }

  changeSet(author: "sosguthorpe (generated)", id: "1586289817497-7") {
    addForeignKeyConstraint(baseColumnNames: "emb_end_fk", baseTableName: "embargo", constraintName: "FKd8ml5pj554n8b90km5sa0k07m", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "est_id", referencedTableName: "embargo_statement")
  }

  changeSet(author: "sosguthorpe (generated)", id: "1586289817497-8") {
    addForeignKeyConstraint(baseColumnNames: "pci_embargo_fk", baseTableName: "package_content_item", constraintName: "FKm8g6i6blt58ctbfcf8p6faidu", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "emb_id", referencedTableName: "embargo")
  }
  changeSet(author: "efreestone (manual)", id: "202003250914-1") {
    addColumn(tableName: "remotekb") {
      column(name: "rkb_trusted_source_ti", type: "boolean")
    }
  }
  // Set all external remote KBs to not-trusted
  changeSet(author: "efreestone (manual)", id: "202003250914-2") {
    grailsChange {
      change {
        sql.execute("UPDATE ${database.defaultSchemaName}.remotekb SET rkb_trusted_source_ti=FALSE WHERE rkb_name NOT LIKE 'LOCAL';".toString())
      }
    }
  }
  // Set LOCAL to trusted
  changeSet(author: "efreestone (manual)", id: "202003250914-3") {
    grailsChange {
      change {
        sql.execute("UPDATE ${database.defaultSchemaName}.remotekb SET rkb_trusted_source_ti=TRUE WHERE rkb_name LIKE 'LOCAL';".toString())
      }
    }
  }
  // Add non-nullable constraint
  changeSet(author: "efreestone (manual)", id: "202003250914-4") {
    addNotNullConstraint(tableName: "remotekb", columnName: "rkb_trusted_source_ti", columnDataType: "boolean")
  }

  changeSet(author: "efreestone (manual)", id: "202004081754-001") {
    addColumn(tableName: "kbart_import_job") {
      column(name: "trusted_source_ti", type: "boolean")
    }
  }

  changeSet(author: "doytch (manual)", id: "202004150927-1") {
    addColumn(tableName: "entitlement") {
      column(name: "ent_note", type: "text")
    }
  }

  changeSet(author: "claudia (manual)", id: "202004221850-1") {
    grailsChange {
      change {
        // Change category RemoteLicenseLink.Status to internal  
        sql.execute("""
          UPDATE ${database.defaultSchemaName}.refdata_category SET internal = true
            WHERE rdc_description='RemoteLicenseLink.Status'
        """.toString())
      }
    }
  }


  changeSet(author: "efreestone (manual)", id: "20200519-1102-001") {
     grailsChange {
      change {
        // Return the list of names that have duplicates
        List nonUniqueNames = sql.rows("SELECT sa.sa_name FROM ${database.defaultSchemaName}.subscription_agreement as sa GROUP BY sa.sa_name HAVING COUNT(*) > 1".toString())
        nonUniqueNames.each{
          // For each of those names, return a list of the agreement ids that have that name
          List rowsWithGivenName = sql.rows("SELECT sa_id FROM ${database.defaultSchemaName}.subscription_agreement as sa WHERE sa.sa_name = :name".toString(), [name: it.sa_name])
          rowsWithGivenName.eachWithIndex {agreement, i ->
            // For each of those ids, add an increment, so ["A", "A", "A"] becomes ["A_1", "A_2", "A_3"]
            sql.execute("UPDATE ${database.defaultSchemaName}.subscription_agreement SET sa_name = CONCAT(sa_name, CONCAT('_', :index)) WHERE sa_id = :id", [id: agreement.sa_id, index: i + 1])
          }
        }
      }
    }
  }

  changeSet(author: "efreestone (manual)", id: "20200519-1626-001") {
    addUniqueConstraint(tableName: "subscription_agreement", constraintName: "UC_SUBSCRIPTION_AGREEMENT_NAME_COL", columnNames: "sa_name")
  }

  changeSet(author: "claudia (manual)", id: "202005201645-1") {
    addColumn(tableName: "entitlement") {
      column(name: "ent_suppress_discovery", type: "boolean")
    }
  }
  // Set all existing entitlements to not-suppressed
  changeSet(author: "claudia (manual)", id: "202005201645-2") {
    grailsChange {
      change {
	      sql.execute("""
	        UPDATE ${database.defaultSchemaName}.entitlement SET ent_suppress_discovery = FALSE
            WHERE ent_suppress_discovery is null
	      """.toString())
      }
    }
  }

  changeSet(author: "claudia (manual)", id: "202005201645-3") {
    addNotNullConstraint(tableName: "entitlement", columnName: "ent_suppress_discovery", columnDataType: "boolean")
  }

  changeSet(author: "claudia (manual)", id: "202005221345-1") {
    addColumn(tableName: "erm_resource") {
      column(name: "res_suppress_discovery", type: "boolean")
    }
  }
  // Set all existing resources to not-suppressed
  changeSet(author: "claudia (manual)", id: "202005221345-2") {
    grailsChange {
      change {
	      sql.execute("""
	        UPDATE ${database.defaultSchemaName}.erm_resource SET res_suppress_discovery = FALSE
            WHERE res_suppress_discovery is null
	      """.toString())
      }
    }
  }

  changeSet(author: "claudia (manual)", id: "202005221345-3") {
    addNotNullConstraint(tableName: "erm_resource", columnName: "res_suppress_discovery", columnDataType: "boolean")
  }

  changeSet(author: "claudia (manual)", id: "202005251415-1") {
        createTable(tableName: "entitlement_tag") {
            column(name: "entitlement_tags_id", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "tag_id", type: "BIGINT")
        }
  }

  changeSet(author: "claudia (manual)", id: "202005251415-2") {
        addForeignKeyConstraint(baseColumnNames: "entitlement_tags_id", baseTableName: "entitlement_tag", constraintName: "ent_tag_to_ent", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "ent_id", referencedTableName: "entitlement")
  }

  changeSet(author: "claudia (manual)", id: "202005251415-3") {
        addForeignKeyConstraint(baseColumnNames: "tag_id", baseTableName: "entitlement_tag", constraintName: "ent_tag_to_tag", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "tag")
  }

  changeSet(author: "claudia (manual)", id: "2020052515-4") {
        createTable(tableName: "erm_resource_tag") {
            column(name: "erm_resource_tags_id", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "tag_id", type: "BIGINT")
        }
  }

  changeSet(author: "claudia (manual)", id: "202005251415-5") {
        addForeignKeyConstraint(baseColumnNames: "erm_resource_tags_id", baseTableName: "erm_resource_tag", constraintName: "er_tag_to_er", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "erm_resource")
  }

  changeSet(author: "claudia (manual)", id: "202005251415-6") {
        addForeignKeyConstraint(baseColumnNames: "tag_id", baseTableName: "erm_resource_tag", constraintName: "er_tag_to_tag", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "tag")
  }

  // Migration to remove "packages" from existing GoKBAdapter sources
  changeSet(author: "efreestone (manual)", id: "20200527-1554-001") {
     grailsChange {
      change {
        // Return the list of remoteKBs of type GOKbOAIAdapter
        List goKbRemoteKbs = sql.rows("SELECT * FROM ${database.defaultSchemaName}.remotekb WHERE rkb_type='org.olf.kb.adapters.GOKbOAIAdapter'".toString())
        goKbRemoteKbs.each{
          String newUri = it.rkb_uri.replace("/packages", "")
          sql.execute("UPDATE ${database.defaultSchemaName}.remotekb SET rkb_uri = :uri", [uri: newUri])
        }
      }
    }
  }
}

