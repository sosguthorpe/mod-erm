databaseChangeLog = {

    // Currently, the default folio environment has this for us, but this is left
    // here as a signpost for users who might be running in a different context
    //
    // https://stackoverflow.com/questions/30368724/enable-postgresql-extensions-in-jdbc
    //
    // changeSet(author: "ianibbo (generated)", id: "1527414162857-0a") {
    //   grailsChange {
    //     change {
    //       sql.execute("create extension pg_trgm WITH SCHEMA ${database.defaultSchemaName}".toString())
    //     }
    //   }
    // }

    changeSet(author: "ianibbo (generated)", id: "1527414162857-1") {
        createTable(tableName: "entitlement") {
            column(name: "ent_id", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "ent_version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "ent_pci_fk", type: "VARCHAR(36)")

            column(name: "ent_active_to", type: "TIMESTAMP WITHOUT TIME ZONE")

            column(name: "ent_owner_fk", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "ent_active_from", type: "TIMESTAMP WITHOUT TIME ZONE")

            column(name: "ent_pti_fk", type: "VARCHAR(36)")

            column(name: "ent_pkg_fk", type: "VARCHAR(36)")

            column(name: "ent_enabled", type: "BOOLEAN")
        }
    }

    changeSet(author: "ianibbo (generated)", id: "1527414162857-2") {
        createTable(tableName: "coverage_statement") {
            column(name: "cs_id", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "cs_version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "cs_start_date", type: "VARCHAR(255)")

            column(name: "cs_start_issue", type: "VARCHAR(255)")

            column(name: "cs_pci_fk", type: "VARCHAR(36)")

            column(name: "cs_ti_fk", type: "VARCHAR(36)")

            column(name: "cs_end_issue", type: "VARCHAR(255)")

            column(name: "cs_start_volume", type: "VARCHAR(255)")

            column(name: "cs_end_volume", type: "VARCHAR(255)")

            column(name: "cs_pti_fk", type: "VARCHAR(36)")

            column(name: "cs_end_date", type: "VARCHAR(255)")
        }
    }

    changeSet(author: "ianibbo (generated)", id: "1527414162857-3") {
        createTable(tableName: "holdings_coverage") {
            column(name: "co_id", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "co_version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "co_start_date", type: "VARCHAR(255)")

            column(name: "co_start_issue", type: "VARCHAR(255)")

            column(name: "co_ent_fk", type: "VARCHAR(36)")

            column(name: "co_end_issue", type: "VARCHAR(255)")

            column(name: "co_start_volume", type: "VARCHAR(255)")

            column(name: "co_end_volume", type: "VARCHAR(255)")

            column(name: "co_end_date", type: "VARCHAR(255)")
        }
    }

    changeSet(author: "ianibbo (generated)", id: "1527414162857-4") {
        createTable(tableName: "identifier") {
            column(name: "id_id", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "id_version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "id_ns_fk", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "id_value", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "ianibbo (generated)", id: "1527414162857-4a") {
        createIndex(indexName: "id_idx", tableName: "identifier") {
            column(name: "id_value")
            column(name: "id_ns_fk")
        }
    }


    changeSet(author: "ianibbo (generated)", id: "1527414162857-5") {
        createTable(tableName: "identifier_namespace") {
            column(name: "idns_id", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "idns_version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "idns_value", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "ianibbo (generated)", id: "1527414162857-6") {
        createTable(tableName: "identifier_occurrence") {
            column(name: "io_id", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "io_version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "io_ti_fk", type: "VARCHAR(36)")

            column(name: "io_status_fk", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "io_identifier_fk", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "ianibbo (generated)", id: "1527414162857-7") {
        createTable(tableName: "org") {
            column(name: "org_id", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "org_version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "org_name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "org_vendors_uuid", type: "VARCHAR(36)") {
                constraints(nullable: "true")
            }

            column(name: "org_source_uri", type: "VARCHAR(255)") {
                constraints(nullable: "true")
            }
        }
    }

    changeSet(author: "ianibbo (generated)", id: "1527414162857-8") {
        createTable(tableName: "package") {
            column(name: "pkg_id", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "pkg_version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "pkg_name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "pkg_source", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "pkg_reference", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "pkg_remote_kb", type: "VARCHAR(36)") {
                constraints(nullable: "true")
            }

            column(name: "pkg_nominal_platform_fk", type: "VARCHAR(36)") {
                constraints(nullable: "true")
            }

            column(name: "pkg_vendor_fk", type: "VARCHAR(36)") {
                constraints(nullable: "true")
            }
        }
    }

    changeSet(author: "ianibbo (generated)", id: "1527414162857-8a") {
      grailsChange {
        change {
          // grailsChange gives us an sql variable which inherits the current connection, and hence should
          // get the schema
          // sql.execute seems to get a bit confused when passed a GString. Work it out before
          def cmd = "CREATE INDEX pkg_name_trigram_idx ON ${database.defaultSchemaName}.package USING GIN (pkg_name gin_trgm_ops)".toString()
          sql.execute(cmd);
        }
      }
    }


    changeSet(author: "ianibbo (generated)", id: "1527414162857-9") {
        createTable(tableName: "package_content_item") {
            column(name: "pci_id", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "pci_version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "pci_added_ts", type: "BIGINT")

            column(name: "pci_access_start", type: "TIMESTAMP WITHOUT TIME ZONE")

            column(name: "pci_access_end", type: "TIMESTAMP WITHOUT TIME ZONE")

            column(name: "pci_last_seen_ts", type: "BIGINT")

            column(name: "pci_depth", type: "VARCHAR(255)")

            column(name: "pci_note", type: "text")

            column(name: "pci_pti_fk", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "pci_pkg_fk", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "pci_removed_ts", type: "BIGINT")
        }
    }

    changeSet(author: "ianibbo (generated)", id: "1527414162857-10") {
        createTable(tableName: "platform") {
            column(name: "pt_id", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "pt_version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "pt_name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "ianibbo (generated)", id: "1527414162857-11") {
        createTable(tableName: "platform_locator") {
            column(name: "pl_id", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "pl_version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "pl_owner_fk", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "domain_name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "ianibbo (generated)", id: "1527414162857-12") {
        createTable(tableName: "platform_title_instance") {
            column(name: "pti_id", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "pti_version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "pti_ti_fk", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "pti_pt_fk", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "pti_url", type: "VARCHAR(1024)") {
                constraints(nullable: "true")
            }
        }
    }

    changeSet(author: "ianibbo (generated)", id: "1527414162857-13") {
        createTable(tableName: "refdata_category") {
            column(name: "rdc_id", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "rdc_version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "rdc_description", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "ianibbo (generated)", id: "1527414162857-14") {
        createTable(tableName: "refdata_value") {
            column(name: "rdv_id", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "rdv_version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "rdv_style", type: "VARCHAR(255)")

            column(name: "rdv_value", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "rdv_owner", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "rdv_label", type: "VARCHAR(255)")
        }
    }

    changeSet(author: "ianibbo (generated)", id: "1527414162857-15") {
        createTable(tableName: "remotekb") {
            column(name: "rkb_id", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "rkb_version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "rkb_creds", type: "VARCHAR(255)")

            column(name: "rkb_cursor", type: "VARCHAR(255)")

            column(name: "rkb_name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "rkb_active", type: "BOOLEAN")

            column(name: "rkb_type", type: "VARCHAR(255)")

            column(name: "rkb_principal", type: "VARCHAR(255)")

            column(name: "rkb_list_prefix", type: "VARCHAR(255)")

            column(name: "rkb_full_prefix", type: "VARCHAR(255)")

            column(name: "rkb_uri", type: "VARCHAR(255)")

            column(name: "rkb_rectype", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "rkb_supports_harvesting", type: "BOOLEAN")
        }
    }

    changeSet(author: "ianibbo (generated)", id: "1527414162857-16") {
        createTable(tableName: "subscription_agreement") {
            column(name: "sa_id", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "sa_version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "sa_vendor_reference", type: "VARCHAR(255)")

            column(name: "sa_cancellation_deadline", type: "TIMESTAMP WITHOUT TIME ZONE")

            column(name: "sa_start_date", type: "TIMESTAMP WITHOUT TIME ZONE")

            column(name: "sa_renewal_date", type: "TIMESTAMP WITHOUT TIME ZONE")

            column(name: "sa_next_review_date", type: "TIMESTAMP WITHOUT TIME ZONE")

            column(name: "sa_name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "sa_local_reference", type: "VARCHAR(255)")

            column(name: "sa_agreement_type", type: "VARCHAR(36)")

            column(name: "sa_renewal_priority", type: "VARCHAR(36)")

            column(name: "sa_agreement_status", type: "VARCHAR(36)")

            column(name: "sa_is_perpetual", type: "VARCHAR(36)")

            column(name: "sa_content_review_needed", type: "VARCHAR(36)")

            column(name: "sa_enabled", type: "BOOLEAN")

            column(name: "sa_end_date", type: "TIMESTAMP WITHOUT TIME ZONE")
            
            column(name: "sa_description", type: "TEXT")

            column(name: "sa_vendor_fk", type: "VARCHAR(36)")
        }
    }

    changeSet(author: "ianibbo (generated)", id: "1527414162857-17") {
        createTable(tableName: "tag") {
            column(name: "tag_id", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "tag_version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "tag_owner_domain", type: "VARCHAR(255)")

            column(name: "tag_owner_id", type: "VARCHAR(255)")

            column(name: "tag_value", type: "VARCHAR(255)")
        }
    }

    changeSet(author: "ianibbo (generated)", id: "1527414162857-18") {
        createTable(tableName: "title_instance") {
            column(name: "ti_id", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "ti_version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "ti_title", type: "text") {
                constraints(nullable: "false")
            }

            column(name: "ti_resource_type_fk", type: "VARCHAR(36)")

            column(name: "ti_work_fk", type: "VARCHAR(36)")

            column(name: "ti_medium_fk", type: "VARCHAR(36)")
        }
    }

    changeSet(author: "ianibbo (generated)", id: "1527414162857-18a") {
      grailsChange {
        change {
          // grailsChange gives us an sql variable which inherits the current connection, and hence should
          // get the schema. sql.execute seems to get a bit confused when passed a GString. Work it out before by calling toString
          def cmd = "CREATE INDEX ti_title_trigram_idx ON ${database.defaultSchemaName}.title_instance USING GIN (ti_title gin_trgm_ops)".toString()
          sql.execute(cmd);
        }
      }
    }

    changeSet(author: "ianibbo (generated)", id: "1527414162857-19") {
        createTable(tableName: "work") {
            column(name: "w_id", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "w_version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "w_title", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "ianibbo (generated)", id: "1527414162857-19a") {
      grailsChange {
        change {
          // grailsChange gives us an sql variable which inherits the current connection, and hence should
          // get the schema. sql.execute seems to get a bit confused when passed a GString. Work it out before by calling toString
          def cmd = "CREATE INDEX work_title_trigram_idx ON ${database.defaultSchemaName}.work USING GIN (w_title gin_trgm_ops)".toString()
          sql.execute(cmd);
        }
      }
    }

    changeSet(author: "ianibbo (generated)", id: "1527414162857-19b") {
        createTable(tableName: "node") {
            column(name: "nd_id", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "nd_version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "nd_label", type: "VARCHAR(255)")

            column(name: "nd_node_type", type: "VARCHAR(255)")

            column(name: "nd_parent", type: "VARCHAR(36)")

            column(name: "nd_reference_class", type: "VARCHAR(255)")

            column(name: "nd_reference_id", type: "VARCHAR(36)")
        }
    }

    changeSet(author: "ianibbo (generated)", id: "1527414162857-19c") {
        createTable(tableName: "sa_event_history") {
            column(name: "eh_id", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "eh_version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "eh_owner", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "eh_event_type", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "eh_summary", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "eh_event_data", type: "TEXT")

            column(name: "eh_event_outcome", type: "VARCHAR(36)")

            column(name: "eh_notes", type: "TEXT")

            column(name: "eh_event_date", type: "TIMESTAMP WITHOUT TIME ZONE") {
                constraints(nullable: "false")
            }
        }
    }


    changeSet(author: "ianibbo (generated)", id: "1527414162857-20") {
        addPrimaryKey(columnNames: "ent_id", constraintName: "entitlementPK", tableName: "entitlement")
    }

    changeSet(author: "ianibbo (generated)", id: "1527414162857-21") {
        addPrimaryKey(columnNames: "cs_id", constraintName: "coverage_statementPK", tableName: "coverage_statement")
    }

    changeSet(author: "ianibbo (generated)", id: "1527414162857-22") {
        addPrimaryKey(columnNames: "co_id", constraintName: "holdings_coveragePK", tableName: "holdings_coverage")
    }

    changeSet(author: "ianibbo (generated)", id: "1527414162857-23") {
        addPrimaryKey(columnNames: "id_id", constraintName: "identifierPK", tableName: "identifier")
    }

    changeSet(author: "ianibbo (generated)", id: "1527414162857-24") {
        addPrimaryKey(columnNames: "idns_id", constraintName: "identifier_namespacePK", tableName: "identifier_namespace")
    }

    changeSet(author: "ianibbo (generated)", id: "1527414162857-25") {
        addPrimaryKey(columnNames: "io_id", constraintName: "identifier_occurrencePK", tableName: "identifier_occurrence")
    }

    changeSet(author: "ianibbo (generated)", id: "1527414162857-26") {
        addPrimaryKey(columnNames: "org_id", constraintName: "orgPK", tableName: "org")
    }

    changeSet(author: "ianibbo (generated)", id: "1527414162857-27") {
        addPrimaryKey(columnNames: "pkg_id", constraintName: "packagePK", tableName: "package")
    }

    changeSet(author: "ianibbo (generated)", id: "1527414162857-28") {
        addPrimaryKey(columnNames: "pci_id", constraintName: "package_content_itemPK", tableName: "package_content_item")
    }

    changeSet(author: "ianibbo (generated)", id: "1527414162857-29") {
        addPrimaryKey(columnNames: "pt_id", constraintName: "platformPK", tableName: "platform")
    }

    changeSet(author: "ianibbo (generated)", id: "1527414162857-30") {
        addPrimaryKey(columnNames: "pl_id", constraintName: "platform_locatorPK", tableName: "platform_locator")
    }

    changeSet(author: "ianibbo (generated)", id: "1527414162857-31") {
        addPrimaryKey(columnNames: "pti_id", constraintName: "platform_title_instancePK", tableName: "platform_title_instance")
    }

    changeSet(author: "ianibbo (generated)", id: "1527414162857-32") {
        addPrimaryKey(columnNames: "rdc_id", constraintName: "refdata_categoryPK", tableName: "refdata_category")
    }

    changeSet(author: "ianibbo (generated)", id: "1527414162857-33") {
        addPrimaryKey(columnNames: "rdv_id", constraintName: "refdata_valuePK", tableName: "refdata_value")
    }

    changeSet(author: "ianibbo (generated)", id: "1527414162857-34") {
        addPrimaryKey(columnNames: "rkb_id", constraintName: "remotekbPK", tableName: "remotekb")
    }

    changeSet(author: "ianibbo (generated)", id: "1527414162857-35") {
        addPrimaryKey(columnNames: "sa_id", constraintName: "subscription_agreementPK", tableName: "subscription_agreement")
    }

    changeSet(author: "ianibbo (generated)", id: "1527414162857-36") {
        addPrimaryKey(columnNames: "tag_id", constraintName: "tagPK", tableName: "tag")
    }

    changeSet(author: "ianibbo (generated)", id: "1527414162857-37") {
        addPrimaryKey(columnNames: "ti_id", constraintName: "title_instancePK", tableName: "title_instance")
    }

    changeSet(author: "ianibbo (generated)", id: "1527414162857-38") {
        addPrimaryKey(columnNames: "w_id", constraintName: "workPK", tableName: "work")
    }

    changeSet(author: "ianibbo (generated)", id: "1527414162857-39") {
        createIndex(indexName: "rdv_entry_idx", tableName: "refdata_value") {
            column(name: "rdv_value")

            column(name: "rdv_owner")
        }
    }

    changeSet(author: "ianibbo (generated)", id: "1527414162857-40") {
        addForeignKeyConstraint(baseColumnNames: "io_identifier_fk", baseTableName: "identifier_occurrence", constraintName: "FK124sp9vc5hnix1ufo6wi2vbav", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id_id", referencedTableName: "identifier")
    }

    changeSet(author: "ianibbo (generated)", id: "1527414162857-41") {
        addForeignKeyConstraint(baseColumnNames: "cs_ti_fk", baseTableName: "coverage_statement", constraintName: "FK2ocimr1uh2pogta68xl9ph3n", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "ti_id", referencedTableName: "title_instance")
    }

    changeSet(author: "ianibbo (generated)", id: "1527414162857-42") {
        addForeignKeyConstraint(baseColumnNames: "pci_pkg_fk", baseTableName: "package_content_item", constraintName: "FK4u9t780a3pgjy1wxsdn8r131k", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "pkg_id", referencedTableName: "package")
    }

    changeSet(author: "ianibbo (generated)", id: "1527414162857-43") {
        addForeignKeyConstraint(baseColumnNames: "sa_agreement_type", baseTableName: "subscription_agreement", constraintName: "FK613exmd4qa6bjjdycx9kot0yp", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    }

    changeSet(author: "ianibbo (generated)", id: "1527414162857-44") {
        addForeignKeyConstraint(baseColumnNames: "io_status_fk", baseTableName: "identifier_occurrence", constraintName: "FK930t3v9wtioa9a9j5013au5ci", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    }

    changeSet(author: "ianibbo (generated)", id: "1527414162857-45") {
        addForeignKeyConstraint(baseColumnNames: "ent_owner_fk", baseTableName: "entitlement", constraintName: "FKa7dr5lr4wj3ti2kso4tlc99l5", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "sa_id", referencedTableName: "subscription_agreement")
    }

    changeSet(author: "ianibbo (generated)", id: "1527414162857-46") {
        addForeignKeyConstraint(baseColumnNames: "io_ti_fk", baseTableName: "identifier_occurrence", constraintName: "FKat7yej3qg0w5ppb0t4akj51wl", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "ti_id", referencedTableName: "title_instance")
    }

    changeSet(author: "ianibbo (generated)", id: "1527414162857-47") {
        addForeignKeyConstraint(baseColumnNames: "id_ns_fk", baseTableName: "identifier", constraintName: "FKby5jjtajics8edtt193lwtnwv", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "idns_id", referencedTableName: "identifier_namespace")
    }

    changeSet(author: "ianibbo (generated)", id: "1527414162857-48") {
        addForeignKeyConstraint(baseColumnNames: "cs_pci_fk", baseTableName: "coverage_statement", constraintName: "FKciqq54dwgdmv0ta5ugs58sn36", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "pci_id", referencedTableName: "package_content_item")
    }

    changeSet(author: "ianibbo (generated)", id: "1527414162857-49") {
        addForeignKeyConstraint(baseColumnNames: "cs_pti_fk", baseTableName: "coverage_statement", constraintName: "FKdj82640bdcj4dfrbn0aqdgbfp", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "pti_id", referencedTableName: "platform_title_instance")
    }

    changeSet(author: "ianibbo (generated)", id: "1527414162857-50") {
        addForeignKeyConstraint(baseColumnNames: "pti_ti_fk", baseTableName: "platform_title_instance", constraintName: "FKedoadk035beg5u3vi2232pq9m", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "ti_id", referencedTableName: "title_instance")
    }

    changeSet(author: "ianibbo (generated)", id: "1527414162857-51") {
        addForeignKeyConstraint(baseColumnNames: "pl_owner_fk", baseTableName: "platform_locator", constraintName: "FKfn4ls5f77sc18cq9c8owlkgtp", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "pt_id", referencedTableName: "platform")
    }

    changeSet(author: "ianibbo (generated)", id: "1527414162857-52") {
        addForeignKeyConstraint(baseColumnNames: "co_ent_fk", baseTableName: "holdings_coverage", constraintName: "FKg7ik6sa6xovg5fw2ijwy9kjji", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "ent_id", referencedTableName: "entitlement")
    }

    changeSet(author: "ianibbo (generated)", id: "1527414162857-53") {
        addForeignKeyConstraint(baseColumnNames: "ent_pti_fk", baseTableName: "entitlement", constraintName: "FKgmfigdcxicltbus9fv6h6j9xo", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "pti_id", referencedTableName: "platform_title_instance")
    }

    changeSet(author: "ianibbo (generated)", id: "1527414162857-54") {
        addForeignKeyConstraint(baseColumnNames: "rdv_owner", baseTableName: "refdata_value", constraintName: "FKh4fon2a7k4y8b2sicjm0i6oy8", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdc_id", referencedTableName: "refdata_category")
    }

    changeSet(author: "ianibbo (generated)", id: "1527414162857-55") {
        addForeignKeyConstraint(baseColumnNames: "ent_pkg_fk", baseTableName: "entitlement", constraintName: "FKjukl0v6gkoqx79lndcmn06r4v", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "pkg_id", referencedTableName: "package")
    }

    changeSet(author: "ianibbo (generated)", id: "1527414162857-56") {
        addForeignKeyConstraint(baseColumnNames: "ent_pci_fk", baseTableName: "entitlement", constraintName: "FKmvuf8qwj0wxpgkedxclp3xlc5", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "pci_id", referencedTableName: "package_content_item")
    }

    changeSet(author: "ianibbo (generated)", id: "1527414162857-57") {
        addForeignKeyConstraint(baseColumnNames: "pci_pti_fk", baseTableName: "package_content_item", constraintName: "FKostrwqec52cid7enxbr4b2loe", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "pti_id", referencedTableName: "platform_title_instance")
    }

    changeSet(author: "ianibbo (generated)", id: "1527414162857-58") {
        addForeignKeyConstraint(baseColumnNames: "pti_pt_fk", baseTableName: "platform_title_instance", constraintName: "FKtlecp40x0sb3rd9w4qi16lcu0", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "pt_id", referencedTableName: "platform")
    }

    changeSet(author: "ianibbo (generated)", id: "1527414162857-59") {
      grailsChange {
        change {
          def cmd = "CREATE VIEW ${database.defaultSchemaName}.all_electronic_resources (id, type, pkg_id, ti_id, name ) as ( select pkg_id, 'pkg', pkg_id, null, pkg_name from ${database.defaultSchemaName}.package ) UNION ( select ti_id, 'title', null, ti_id, ti_title from ${database.defaultSchemaName}.title_instance )".toString()
          sql.execute(cmd);
        }
      }
    }
}
