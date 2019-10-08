databaseChangeLog = {

  changeSet(author: "sosguthorpe (generated)", id: "1568388094555-1") {
    createTable(tableName: "license_amendment_status") {
      column(name: "las_id", type: "VARCHAR(36)") {
        constraints(nullable: "false")
      }
      
      column(name: "las_amendment_id", type: "VARCHAR(36)") {
        constraints(nullable: "false")
      }

      column(name: "version", type: "BIGINT") {
        constraints(nullable: "false")
      }

      column(name: "las_owner", type: "VARCHAR(36)") {
        constraints(nullable: "false")
      }

      column(name: "las_status", type: "VARCHAR(36)") {
        constraints(nullable: "false")
      }

      column(name: "las_note", type: "TEXT")
    }
  }

  changeSet(author: "sosguthorpe (generated)", id: "1568388094555-2") {
    addPrimaryKey(columnNames: "las_id", constraintName: "license_amendment_statusPK", tableName: "license_amendment_status")
  }

  changeSet(author: "sosguthorpe (generated)", id: "1568388094555-3") {
    addForeignKeyConstraint(baseColumnNames: "las_status", baseTableName: "license_amendment_status", constraintName: "FKbjdewcgoyen8p0gbs9npxwuei", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
  }

  changeSet(author: "sosguthorpe (generated)", id: "1568388094555-4") {
    addForeignKeyConstraint(baseColumnNames: "las_owner", baseTableName: "license_amendment_status", constraintName: "FKo66rknrrhqlfoxxpjcq1p9f54", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rol_id", referencedTableName: "remote_license_link")
  }

  // Add new column for Reason for Closure
  changeSet(author: "ethanfreestone (manual)", id: "260920190932-1") {
    addColumn(tableName: "subscription_agreement") {
      column(name: "sa_reason_for_closure", type: "VARCHAR(36)") {
        constraints(nullable: "true")
      }
    }
    addForeignKeyConstraint(baseColumnNames: "sa_reason_for_closure", baseTableName: "subscription_agreement", constraintName: "reasonforclosureforeignkeyconstraint26092019", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
  }

  // Add refdata categories for 'reason for closure' and 'agreement status' (if it does not exist)
  changeSet(author: "ethanfreestone (manual)", id: "260920190932-2") {
    grailsChange {
      change {
        sql.execute("INSERT INTO ${database.defaultSchemaName}.refdata_category (rdc_id, rdc_version, rdc_description) SELECT md5(random()::text || clock_timestamp()::text) as id, 0 as version, 'SubscriptionAgreement.ReasonForClosure' as description WHERE NOT EXISTS (SELECT rdc_description FROM ${database.defaultSchemaName}.refdata_category WHERE (rdc_description)=('SubscriptionAgreement.ReasonForClosure') LIMIT 1);".toString())
      }
    }
  }
  changeSet(author: "ethanfreestone (manual)", id: "260920190932-3") {
    grailsChange {
      change {
        sql.execute("INSERT INTO ${database.defaultSchemaName}.refdata_category (rdc_id, rdc_version, rdc_description) SELECT md5(random()::text || clock_timestamp()::text) as id, 0 as version, 'SubscriptionAgreement.AgreementStatus' as description WHERE NOT EXISTS (SELECT rdc_description FROM ${database.defaultSchemaName}.refdata_category WHERE (rdc_description)=('SubscriptionAgreement.AgreementStatus') LIMIT 1)".toString())
      }  
    }
  }

  // Ensure 'closed' is a refdata value for subscription agreement status
  changeSet(author: "ethanfreestone (manual)", id:"260920190932-4") {
    grailsChange {
      change {
        sql.execute("INSERT INTO ${database.defaultSchemaName}.refdata_value (rdv_id, rdv_version, rdv_value, rdv_owner, rdv_label) SELECT md5(random()::text || clock_timestamp()::text) as id, 0 as version, 'closed' as value, (SELECT rdc_id FROM  ${database.defaultSchemaName}.refdata_category WHERE rdc_description='SubscriptionAgreement.AgreementStatus') as owner, 'Closed' as label WHERE NOT EXISTS (SELECT rdv_id FROM ${database.defaultSchemaName}.refdata_value INNER JOIN ${database.defaultSchemaName}.refdata_category ON refdata_value.rdv_owner = refdata_category.rdc_id WHERE rdc_description='SubscriptionAgreement.AgreementStatus' AND rdv_value='closed' LIMIT 1);".toString())
      }
    }
  }

  // Ensure 'cancelled' is a refdata value for reason for closure
  changeSet(author: "ethanfreestone (manual)", id:"260920190932-4") {
    grailsChange {
      change {
        sql.execute("INSERT INTO ${database.defaultSchemaName}.refdata_value (rdv_id, rdv_version, rdv_value, rdv_owner, rdv_label) SELECT md5(random()::text || clock_timestamp()::text) as id, 0 as version, 'cancelled' as value, (SELECT rdc_id FROM  ${database.defaultSchemaName}.refdata_category WHERE rdc_description='SubscriptionAgreement.ReasonForClosure') as owner, 'Cancelled' as label WHERE NOT EXISTS (SELECT rdv_id FROM ${database.defaultSchemaName}.refdata_value INNER JOIN ${database.defaultSchemaName}.refdata_category ON refdata_value.rdv_owner = refdata_category.rdc_id WHERE rdc_description='SubscriptionAgreement.ReasonForClosure' AND rdv_value='cancelled' LIMIT 1);".toString())
      }
    }
  }
  // Update all agreements with status 'cancelled' to have reason for closure 'cancelled'
  changeSet(author: "ethanfreestone (manual)", id:"260920190932-5") {
    grailsChange {
      change {
          sql.execute("UPDATE ${database.defaultSchemaName}.subscription_agreement SET sa_reason_for_closure=(SELECT rdv_id FROM ${database.defaultSchemaName}.refdata_value INNER JOIN ${database.defaultSchemaName}.refdata_category ON refdata_value.rdv_owner = refdata_category.rdc_id WHERE rdc_description='SubscriptionAgreement.ReasonForClosure' AND rdv_value='cancelled') WHERE sa_agreement_status=(SELECT rdv_id FROM ${database.defaultSchemaName}.refdata_value INNER JOIN ${database.defaultSchemaName}.refdata_category ON refdata_value.rdv_owner = refdata_category.rdc_id WHERE rdc_description='SubscriptionAgreement.AgreementStatus' AND rdv_value='cancelled')".toString())
      }
    }
  }
  // Update all agreements with status 'cancelled' to be status 'closed'
  changeSet(author: "ethanfreestone (manual)", id:"260920190932-6") {
    grailsChange {
      change {
          sql.execute("UPDATE ${database.defaultSchemaName}.subscription_agreement SET sa_agreement_status=(SELECT rdv_id FROM ${database.defaultSchemaName}.refdata_value INNER JOIN ${database.defaultSchemaName}.refdata_category ON refdata_value.rdv_owner = refdata_category.rdc_id WHERE rdc_description='SubscriptionAgreement.AgreementStatus' AND rdv_value='closed') WHERE sa_agreement_status=(SELECT rdv_id FROM ${database.defaultSchemaName}.refdata_value INNER JOIN ${database.defaultSchemaName}.refdata_category ON refdata_value.rdv_owner = refdata_category.rdc_id WHERE rdc_description='SubscriptionAgreement.AgreementStatus' AND rdv_value='cancelled')".toString())
      }
    }
  }
  // Remove refdata value corresponding to the status of 'cancelled'
  changeSet(author: "ethanfreestone (manual)", id:"260920190932-7") {
    grailsChange {
      change {
          sql.execute("DELETE FROM ${database.defaultSchemaName}.refdata_value WHERE rdv_id=(SELECT rdv_id FROM ${database.defaultSchemaName}.refdata_value INNER JOIN ${database.defaultSchemaName}.refdata_category ON refdata_value.rdv_owner = refdata_category.rdc_id WHERE rdc_description='SubscriptionAgreement.AgreementStatus' AND rdv_value='cancelled' LIMIT 1)".toString())
      }
    }
  }



// Ensure 'rejected' is a refdata value for reason for closure
  changeSet(author: "ethanfreestone (manual)", id:"021020191343-1") {
    grailsChange {
      change {
        sql.execute("INSERT INTO ${database.defaultSchemaName}.refdata_value (rdv_id, rdv_version, rdv_value, rdv_owner, rdv_label) SELECT md5(random()::text || clock_timestamp()::text) as id, 0 as version, 'rejected' as value, (SELECT rdc_id FROM  ${database.defaultSchemaName}.refdata_category WHERE rdc_description='SubscriptionAgreement.ReasonForClosure') as owner, 'Rejected' as label WHERE NOT EXISTS (SELECT rdv_id FROM ${database.defaultSchemaName}.refdata_value INNER JOIN ${database.defaultSchemaName}.refdata_category ON refdata_value.rdv_owner = refdata_category.rdc_id WHERE rdc_description='SubscriptionAgreement.ReasonForClosure' AND rdv_value='rejected' LIMIT 1);".toString())
      }
    }
  }
  // Update all agreements with status 'rejected' to have reason for closure 'rejected'
  changeSet(author: "ethanfreestone (manual)", id:"021020191343-2") {
    grailsChange {
      change {
          sql.execute("UPDATE ${database.defaultSchemaName}.subscription_agreement SET sa_reason_for_closure=(SELECT rdv_id FROM ${database.defaultSchemaName}.refdata_value INNER JOIN ${database.defaultSchemaName}.refdata_category ON refdata_value.rdv_owner = refdata_category.rdc_id WHERE rdc_description='SubscriptionAgreement.ReasonForClosure' AND rdv_value='rejected') WHERE sa_agreement_status=(SELECT rdv_id FROM ${database.defaultSchemaName}.refdata_value INNER JOIN ${database.defaultSchemaName}.refdata_category ON refdata_value.rdv_owner = refdata_category.rdc_id WHERE rdc_description='SubscriptionAgreement.AgreementStatus' AND rdv_value='rejected')".toString())
      }
    }
  }
  // Update all agreements with status 'rejected' to be status 'closed'
  changeSet(author: "ethanfreestone (manual)", id:"021020191343-3") {
    grailsChange {
      change {
          sql.execute("UPDATE ${database.defaultSchemaName}.subscription_agreement SET sa_agreement_status=(SELECT rdv_id FROM ${database.defaultSchemaName}.refdata_value INNER JOIN ${database.defaultSchemaName}.refdata_category ON refdata_value.rdv_owner = refdata_category.rdc_id WHERE rdc_description='SubscriptionAgreement.AgreementStatus' AND rdv_value='closed') WHERE sa_agreement_status=(SELECT rdv_id FROM ${database.defaultSchemaName}.refdata_value INNER JOIN ${database.defaultSchemaName}.refdata_category ON refdata_value.rdv_owner = refdata_category.rdc_id WHERE rdc_description='SubscriptionAgreement.AgreementStatus' AND rdv_value='rejected')".toString())
      }
    }
  }
  // Remove refdata value corresponding to the status of 'rejected'
  changeSet(author: "ethanfreestone (manual)", id:"021020191343-4") {
    grailsChange {
      change {
          sql.execute("DELETE FROM ${database.defaultSchemaName}.refdata_value WHERE rdv_id=(SELECT rdv_id FROM ${database.defaultSchemaName}.refdata_value INNER JOIN ${database.defaultSchemaName}.refdata_category ON refdata_value.rdv_owner = refdata_category.rdc_id WHERE rdc_description='SubscriptionAgreement.AgreementStatus' AND rdv_value='rejected' LIMIT 1)".toString())
      }
    }
  }
}

