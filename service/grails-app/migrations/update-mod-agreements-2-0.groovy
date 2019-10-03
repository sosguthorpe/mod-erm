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
}
