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

  changeSet(author: "efreestone (manual)", id: "2021-03-01-001") {
    addColumn (tableName: "subscription_agreement" ) {
      column(name: "sa_cancellation_deadline", type: "timestamp")
    }
  }

// NEW STRUCTURE OF SA ORG ROLES
// new table subscription_agreement_org_role
  changeSet(author: "claudia (manual)", id: "20210503-001") {
    createTable(tableName: "subscription_agreement_org_role") {
      column(name: "saor_id", type: "VARCHAR(36)") {
        constraints(nullable: "false")
      }

      column(name: "saor_version", type: "BIGINT") {
        constraints(nullable: "false")
      }

      column(name: "saor_role_fk", type: "VARCHAR(36)") {
        constraints(nullable: "false")
      }

      column(name: "saor_owner_fk", type: "VARCHAR(36)") {
        constraints(nullable: "false")
      }

      column(name: "saor_note", type: "text")
    }

    addPrimaryKey(columnNames: "saor_id", constraintName: "subscription_agreement_org_rolePK", tableName: "subscription_agreement_org_role")

    addForeignKeyConstraint(baseColumnNames: "saor_role_fk", baseTableName: "subscription_agreement_org_role", constraintName: "sa_org_role_refdata_valueFK", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")

    addForeignKeyConstraint(baseColumnNames: "saor_owner_fk", baseTableName: "subscription_agreement_org_role", constraintName: "sa_org_role_sa_orgFK", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "sao_id", referencedTableName: "subscription_agreement_org")
  }

  // add boolean flag to subscription_agreement_org to indicate if the org is primary
  // default it to false
  changeSet(author: "claudia (manual)", id: "20210510-001") {
    addColumn(tableName: "subscription_agreement_org") {
      column(name: "sao_primary_org", type: "boolean")
    }
  }

  changeSet(author: "claudia (manual)", id: "20210510-002") {
    grailsChange {
      change {
	      sql.execute("""
	        UPDATE ${database.defaultSchemaName}.subscription_agreement_org SET sao_primary_org = FALSE
            WHERE sao_primary_org is null
	      """.toString())
      }
    }
  }

  changeSet(author: "claudia (manual)", id: "20210510-003") {
    addNotNullConstraint(tableName: "subscription_agreement_org", columnName: "sao_primary_org", columnDataType: "boolean")
  }

  // in subscription_agreement_org set the primary_org to true if the org role is 'vendor'
  changeSet(author: "claudia (manual)", id: "20210510-004") {
    grailsChange {
        change {
          sql.execute("UPDATE ${database.defaultSchemaName}.subscription_agreement_org SET sao_primary_org = TRUE WHERE sao_role in (SELECT rdv_id FROM ${database.defaultSchemaName}.refdata_value WHERE rdv_value='vendor')".toString())
        }
      }
  }

  changeSet(author: "claudia (manual)", id: "20210518-010") {
    // Insert all roles from subscription_agreement_org for one sa org in table subscription_agreement_org_role 
    // and leave only one entry
    // and remove the role column from subscription_agreement_org
    grailsChange {
      change {
        // Return the list of subscriptionAgreementIds and org Ids from subscription_agreement_org
        List<List<String>> agreementAndOrgIds = sql.rows("SELECT distinct sao.sao_owner_fk, sao.sao_org_fk FROM ${database.defaultSchemaName}.subscription_agreement_org as sao ORDER BY sao.sao_owner_fk".toString())
        agreementAndOrgIds.each {
          // For each of those subscriptionAgreement and orgIds, find out if there is an organization with role vendor attached
          def vendorId
          def transferId
          vendorId = sql.rows("SELECT sao.sao_id FROM ${database.defaultSchemaName}.subscription_agreement_org as sao WHERE sao.sao_owner_fk = :ownerId and sao.sao_org_fk = :orgId and sao.sao_role in (SELECT rdv_id FROM ${database.defaultSchemaName}.refdata_value WHERE rdv_value='vendor')".toString(), [ownerId: it.sao_owner_fk, orgId: it.sao_org_fk])
          if (vendorId) {
            transferId = vendorId
          } else {
            // find the first sao_id
            transferId = sql.rows("SELECT sao.sao_id FROM ${database.defaultSchemaName}.subscription_agreement_org as sao WHERE sao.sao_owner_fk = :ownerId and sao.sao_org_fk = :orgId limit 1".toString(), [ownerId: it.sao_owner_fk, orgId: it.sao_org_fk])
          }
          def saoId = transferId.sao_id.join()  // make a string out of the ArrayList
          // transfer lines with transferId.sao_id to subscription_agreement_org_role
          sql.execute("""
          INSERT INTO ${database.defaultSchemaName}.subscription_agreement_org_role(saor_id, saor_version, saor_owner_fk, saor_role_fk, saor_note)
          SELECT md5(random()::text || clock_timestamp()::text)::uuid as id, sao_version, :saoId, sao_role, sao_note FROM ${database.defaultSchemaName}.subscription_agreement_org WHERE sao_owner_fk = :ownerId and sao_org_fk = :orgId;
          """.toString(), [ownerId: it.sao_owner_fk, orgId: it.sao_org_fk, saoId: saoId])
          // keep only the line with the transferId, delete others from subscription_agreement_org
          sql.execute("""
          DELETE FROM ${database.defaultSchemaName}.subscription_agreement_org WHERE sao_owner_fk = :ownerId and sao_org_fk = :orgId AND sao_id != :saoId;
          """.toString(), [ownerId: it.sao_owner_fk, orgId: it.sao_org_fk, saoId: saoId])
          // empty the note field of the remaining line
          sql.execute("""
          UPDATE ${database.defaultSchemaName}.subscription_agreement_org SET sao_note = null WHERE sao_id = :saoId;
          """.toString(), [saoId: saoId])
        }
      }
    }
  }

  changeSet(author: "claudia (manual)", id: "202105181720-003") {
    dropForeignKeyConstraint(baseTableName: "subscription_agreement_org", constraintName: "FKbg8mmpb05wmvjlh7b1uyw8e7a")
  }

  changeSet(author: "claudia (manual)", id: "202105181733-001") {
    dropColumn(columnName: "sao_role", tableName: "subscription_agreement_org")
  }

  // Change category SubscriptionAgreementOrg.Role to 'not internal'
  changeSet(author: "claudia (manual)", id: "202105191710-001") {
    grailsChange {
      change {
        sql.execute("""
          UPDATE ${database.defaultSchemaName}.refdata_category SET internal = false
            WHERE rdc_description='SubscriptionAgreementOrg.Role'
        """.toString())
      }
    }
  }

}
