databaseChangeLog = {

  changeSet(author: "sosguthorpe (generated)", id: "1569598352742-1") {
    createTable(tableName: "period") {
      column(name: "per_id", type: "VARCHAR(36)") {
        constraints(nullable: "false")
      }

      column(name: "version", type: "BIGINT") {
        constraints(nullable: "false")
      }

      column(name: "per_start_date", type: "date") {
        constraints(nullable: "false")
      }

      column(name: "per_cancellation_deadline", type: "date")

      column(name: "per_owner", type: "VARCHAR(36)") {
        constraints(nullable: "false")
      }

      column(name: "per_note", type: "TEXT")

      column(name: "per_end_date", type: "date")
    }
  }

  changeSet(author: "sosguthorpe (generated)", id: "1569598352742-2") {
    addPrimaryKey(columnNames: "per_id", constraintName: "periodPK", tableName: "period")
  }

  changeSet(author: "sosguthorpe (generated)", id: "1569598352742-3") {
    addForeignKeyConstraint(baseColumnNames: "per_owner", baseTableName: "period", constraintName: "FK463yiqlo4yoom2s3avrwlbivs", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "sa_id", referencedTableName: "subscription_agreement")
  }
  
  changeSet(author: "sosguthorpe (generated)", id: "1569598352742-4") {
    grailsChange {
      change {
        // Add Period entry for each existing agreement.
        sql.execute("""
            INSERT INTO ${database.defaultSchemaName}.period (per_id, per_owner, version, per_start_date,
              per_end_date, per_cancellation_deadline)
                SELECT
                  md5(random()::text || clock_timestamp()::text)::uuid as id,
                  sa_id as oid,
                  1 as v,
                  coalesce(sa_start_date::date, current_date) as sd,
                  sa_end_date as ed,
                  sa_cancellation_deadline as cd
                FROM 
                  ${database.defaultSchemaName}.subscription_agreement;
        """.toString())
      }
    }
  }

  changeSet(author: "sosguthorpe (generated)", id: "1569598352742-5") {
    dropColumn(columnName: "sa_cancellation_deadline", tableName: "subscription_agreement")
  }

  changeSet(author: "sosguthorpe (generated)", id: "1569598352742-6") {
    dropColumn(columnName: "sa_end_date", tableName: "subscription_agreement")
  }

  changeSet(author: "sosguthorpe (generated)", id: "1569598352742-7") {
    dropColumn(columnName: "sa_start_date", tableName: "subscription_agreement")
  }
  
  // Changeset to rectify incorrect dates without needing a reset.
  changeSet(author: "sosguthorpe (generated)", id: "1569598352742-8") {
    grailsChange {
      change {
        // Add Period entry for each existing agreement.
        sql.execute("""
          UPDATE 
            ${database.defaultSchemaName}.period
          SET per_end_date = NULL
          WHERE
            per_start_date > per_end_date;
        """.toString())
      }
    }
  }
  
  changeSet(author: "sosguthorpe (generated)", id: "1570700103581-1") {
    dropIndex(indexName: "origin_idx", tableName: "log_entry")

    createIndex(indexName: "le_origin_idx", tableName: "log_entry") {
      column(name: "le_origin")
    }
  }

  changeSet(author: "sosguthorpe (generated)", id: "1570700103581-2") {
    createIndex(indexName: "le_type_idx", tableName: "log_entry") {
      column(name: "le_type")
    }
  }

  changeSet(author: "sosguthorpe (generated)", id: "1570722398658-1") {
    createTable(tableName: "agreement_relationship") {
      column(name: "ar_id", type: "VARCHAR(36)") {
        constraints(nullable: "false")
      }

      column(name: "version", type: "BIGINT") {
        constraints(nullable: "false")
      }

      column(name: "ar_outward_fk", type: "VARCHAR(36)") {
        constraints(nullable: "false")
      }

      column(name: "ar_inward_fk", type: "VARCHAR(36)") {
        constraints(nullable: "false")
      }

      column(name: "ar_type", type: "VARCHAR(36)") {
        constraints(nullable: "false")
      }

      column(name: "ar_note", type: "TEXT")
    }
  }
  changeSet(author: "sosguthorpe (generated)", id: "1570722398658-3") {
    addPrimaryKey(columnNames: "ar_id", constraintName: "agreement_relationshipPK", tableName: "agreement_relationship")
  }
  changeSet(author: "sosguthorpe (generated)", id: "1570722398658-5") {
    addForeignKeyConstraint(baseColumnNames: "ar_type", baseTableName: "agreement_relationship", constraintName: "FKdt3oyu37yfeoobvwg17xusdcs", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
  }

  changeSet(author: "sosguthorpe (generated)", id: "1570722398658-7") {
    addForeignKeyConstraint(baseColumnNames: "ar_inward_fk", baseTableName: "agreement_relationship", constraintName: "FKlb6c626qh1fcgqdwa6wl0jqfj", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "sa_id", referencedTableName: "subscription_agreement")
  }

  changeSet(author: "sosguthorpe (generated)", id: "1570722398658-8") {
    addForeignKeyConstraint(baseColumnNames: "ar_outward_fk", baseTableName: "agreement_relationship", constraintName: "FKoqnsbuj52yhq1vqo1qu8xtmr", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "sa_id", referencedTableName: "subscription_agreement")
  }
  
  changeSet(author: "claudia (manual)", id: "2019-11-04-00001") {
	  createTable(tableName: "order_line") {
		  column(name: "pol_id", type: "VARCHAR(36)") {
			  constraints(nullable: "false")
		  }
		  column(name: "pol_version", type: "BIGINT") {
			  constraints(nullable: "false")
		  }
		  column(name: "pol_orders_fk", type: "VARCHAR(50)") {
			  constraints(nullable: "false")
		  }
		  column(name: "pol_owner_fk", type: "VARCHAR(36)") {
			  constraints(nullable: "false")
		  }
	  }
	}
	
	changeSet(author: "claudia (manual)", id: "2019-11-04-00002") {
		addPrimaryKey(columnNames: "pol_id", constraintName: "order_linePK", tableName: "order_line")
	}
	
	changeSet(author: "claudia (manual)", id: "2019-11-04-00003") {
		addForeignKeyConstraint(baseColumnNames: "pol_owner_fk",
								  baseTableName: "order_line",
								constraintName: "pol_to_ent_fk",
								deferrable: "false", initiallyDeferred: "false",
								referencedColumnNames: "ent_id", referencedTableName: "entitlement")
	}
	
	changeSet(author: "claudia (manual)", id: "2019-11-06-0001") {
		grailsChange {
		  change {
			// Add order_line entry for each existing entitlement.
			sql.execute("""
            INSERT INTO ${database.defaultSchemaName}.order_line (pol_id, pol_owner_fk, pol_version, pol_orders_fk )
                SELECT
                  md5(random()::text || clock_timestamp()::text)::uuid as id,
                  ent_id as oid,
                  1 as v,
                  ent_po_line_id as poid
                FROM 
                  ${database.defaultSchemaName}.entitlement
				WHERE
				  ent_po_line_id is not NULL;
        """.toString())
		  }
		}
	  }
	
	changeSet(author: "claudia (manual)", id: "2019-11-06-0002") {
		dropColumn(columnName: "ent_po_line_id", tableName: "entitlement")
	}

}
