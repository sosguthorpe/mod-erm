databaseChangeLog = {

    changeSet(author: "ianibbo (generated)", id: "1543400138015-1") {
        createSequence(sequenceName: "hibernate_sequence")
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-2") {
        createTable(tableName: "boolean_property") {
            column(name: "id", type: "BIGINT") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-3") {
        createTable(tableName: "content_activation_record") {
            column(name: "car_id", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "car_version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "car_target_kb_fk", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "car_date_activation", type: "timestamp") {
                constraints(nullable: "false")
            }

            column(name: "car_date_deactivation", type: "timestamp")

            column(name: "car_pti_fk", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-4") {
        createTable(tableName: "coverage_statement") {
            column(name: "cs_id", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "cs_version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "cs_start_date", type: "VARCHAR(255)")

            column(name: "cs_start_issue", type: "VARCHAR(255)")

            column(name: "cs_pci_fk", type: "VARCHAR(255)")

            column(name: "cs_ti_fk", type: "VARCHAR(255)")

            column(name: "cs_end_issue", type: "VARCHAR(255)")

            column(name: "cs_start_volume", type: "VARCHAR(255)")

            column(name: "cs_end_volume", type: "VARCHAR(255)")

            column(name: "cs_pti_fk", type: "VARCHAR(255)")

            column(name: "cs_end_date", type: "VARCHAR(255)")
        }
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-5") {
        createTable(tableName: "entitlement") {
            column(name: "ent_id", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "ent_version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "ent_active_to", type: "timestamp")

            column(name: "ent_owner_fk", type: "VARCHAR(36)")

            column(name: "ent_resource_fk", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "ent_active_from", type: "timestamp")

            column(name: "ent_enabled", type: "BOOLEAN")
        }
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-6") {
        createTable(tableName: "erm_resource") {
            column(name: "id", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "res_sub_type_fk", type: "VARCHAR(36)")

            column(name: "res_name", type: "VARCHAR(255)")

            column(name: "res_type_fk", type: "VARCHAR(36)")
        }
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-7") {
        createTable(tableName: "holdings_coverage") {
            column(name: "co_id", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "co_version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "co_start_date", type: "VARCHAR(255)")

            column(name: "co_start_issue", type: "VARCHAR(255)")

            column(name: "co_end_issue", type: "VARCHAR(255)")

            column(name: "co_start_volume", type: "VARCHAR(255)")

            column(name: "co_end_volume", type: "VARCHAR(255)")

            column(name: "co_end_date", type: "VARCHAR(255)")

            column(name: "co_ent_fk", type: "VARCHAR(36)")
        }
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-8") {
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

    changeSet(author: "ianibbo (generated)", id: "1543400138015-9") {
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

    changeSet(author: "ianibbo (generated)", id: "1543400138015-10") {
        createTable(tableName: "identifier_occurrence") {
            column(name: "io_id", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "io_version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "io_ti_fk", type: "VARCHAR(255)")

            column(name: "io_status_fk", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "io_identifier_fk", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-11") {
        createTable(tableName: "internal_contact") {
            column(name: "ic_id", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "ic_version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "ic_role", type: "VARCHAR(36)")

            column(name: "ic_user_fk", type: "VARCHAR(255)")

            column(name: "ic_owner_fk", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-12") {
        createTable(tableName: "node") {
            column(name: "nd_id", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "nd_version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "nd_reference_class", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "nd_node_type", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "nd_reference_id", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "nd_label", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "nd_parent", type: "VARCHAR(36)")
        }
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-13") {
        createTable(tableName: "org") {
            column(name: "org_id", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "org_version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "org_vendors_uuid", type: "VARCHAR(255)")

            column(name: "org_name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "org_source_uri", type: "VARCHAR(255)")

            column(name: "org_reference", type: "VARCHAR(255)")
        }
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-14") {
        createTable(tableName: "package") {
            column(name: "id", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "pkg_vendor_fk", type: "VARCHAR(36)")

            column(name: "pkg_nominal_platform_fk", type: "VARCHAR(36)")

            column(name: "pkg_source", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "pkg_remote_kb", type: "VARCHAR(36)")

            column(name: "pkg_reference", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-15") {
        createTable(tableName: "package_content_item") {
            column(name: "id", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "pci_access_start", type: "timestamp")

            column(name: "pci_depth", type: "VARCHAR(255)")

            column(name: "pci_pti_fk", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "pci_pkg_fk", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "pci_added_ts", type: "BIGINT")

            column(name: "pci_access_end", type: "timestamp")

            column(name: "pci_last_seen_ts", type: "BIGINT")

            column(name: "pci_note", type: "VARCHAR(255)")

            column(name: "pci_removed_ts", type: "BIGINT")
        }
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-16") {
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

    changeSet(author: "ianibbo (generated)", id: "1543400138015-17") {
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

    changeSet(author: "ianibbo (generated)", id: "1543400138015-18") {
        createTable(tableName: "platform_title_instance") {
            column(name: "id", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "pti_pt_fk", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "pti_ti_fk", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "pti_url", type: "VARCHAR(255)")
        }
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-19") {
        createTable(tableName: "po_line_proxy") {
            column(name: "pop_id", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "pop_version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "pop_po_line_id", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "pop_owner", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "pop_label", type: "VARCHAR(255)")
        }
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-20") {
        createTable(tableName: "property") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(primaryKey: "true", primaryKeyName: "propertyPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "value", type: "BYTEA") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-21") {
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

    changeSet(author: "ianibbo (generated)", id: "1543400138015-22") {
        createTable(tableName: "refdata_value") {
            column(name: "rdv_id", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "rdv_version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "rdv_value", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "rdv_owner", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "rdv_label", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-23") {
        createTable(tableName: "remotekb") {
            column(name: "rkb_id", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "rkb_version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "rkb_creds", type: "VARCHAR(255)")

            column(name: "rkb_cursor", type: "VARCHAR(255)")

            column(name: "rkb_activation_supported", type: "BOOLEAN")

            column(name: "rkb_name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "rkb_active", type: "BOOLEAN")

            column(name: "rkb_type", type: "VARCHAR(255)")

            column(name: "rkb_principal", type: "VARCHAR(255)")

            column(name: "rkb_list_prefix", type: "VARCHAR(255)")

            column(name: "rkb_full_prefix", type: "VARCHAR(255)")

            column(name: "rkb_activation_enabled", type: "BOOLEAN")

            column(name: "rkb_uri", type: "VARCHAR(255)")

            column(name: "rkb_supports_harvesting", type: "BOOLEAN")

            column(name: "rkb_rectype", type: "BIGINT") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-24") {
        createTable(tableName: "sa_event_history") {
            column(name: "eh_id", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "eh_version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "eh_event_date", type: "timestamp") {
                constraints(nullable: "false")
            }

            column(name: "eh_summary", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "eh_event_data", type: "VARCHAR(255)")

            column(name: "eh_owner", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "eh_notes", type: "VARCHAR(255)")

            column(name: "eh_event_outcome", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "eh_event_type", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-25") {
        createTable(tableName: "subscription_agreement") {
            column(name: "sa_id", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "sa_version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "sa_cancellation_deadline", type: "timestamp")

            column(name: "sa_renewal_date", type: "timestamp")

            column(name: "sa_licence_fk", type: "VARCHAR(255)")

            column(name: "sa_vendor_fk", type: "VARCHAR(36)")

            column(name: "sa_agreement_type", type: "VARCHAR(36)")

            column(name: "sa_renewal_priority", type: "VARCHAR(36)")

            column(name: "sa_content_review_needed", type: "VARCHAR(36)")

            column(name: "sa_end_date", type: "timestamp")

            column(name: "sa_vendor_reference", type: "VARCHAR(255)")

            column(name: "sa_is_perpetual", type: "VARCHAR(36)")

            column(name: "sa_start_date", type: "timestamp")

            column(name: "sa_next_review_date", type: "timestamp")

            column(name: "sa_name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "sa_local_reference", type: "VARCHAR(255)")

            column(name: "sa_agreement_status", type: "VARCHAR(36)")

            column(name: "sa_enabled", type: "BOOLEAN")

            column(name: "sa_description", type: "VARCHAR(255)")
        }
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-26") {
        createTable(tableName: "subscription_agreement_org") {
            column(name: "sao_id", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "sao_version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "sao_org_fk", type: "VARCHAR(36)")

            column(name: "sao_role", type: "VARCHAR(36)")

            column(name: "sao_owner_fk", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-27") {
        createTable(tableName: "subscription_agreement_tag") {
            column(name: "subscription_agreement_tags_id", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "tag_id", type: "BIGINT")
        }
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-28") {
        createTable(tableName: "tag") {
            column(autoIncrement: "true", name: "id", type: "BIGINT") {
                constraints(primaryKey: "true", primaryKeyName: "tagPK")
            }

            column(name: "version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "norm_value", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "value", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-29") {
        createTable(tableName: "text_property") {
            column(name: "id", type: "BIGINT") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-30") {
        createTable(tableName: "title_instance") {
            column(name: "id", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "ti_work_fk", type: "VARCHAR(36)")
        }
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-31") {
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

    changeSet(author: "ianibbo (generated)", id: "1543400138015-32") {
        addPrimaryKey(columnNames: "id", constraintName: "boolean_propertyPK", tableName: "boolean_property")
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-33") {
        addPrimaryKey(columnNames: "car_id", constraintName: "content_activation_recordPK", tableName: "content_activation_record")
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-34") {
        addPrimaryKey(columnNames: "cs_id", constraintName: "coverage_statementPK", tableName: "coverage_statement")
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-35") {
        addPrimaryKey(columnNames: "ent_id", constraintName: "entitlementPK", tableName: "entitlement")
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-36") {
        addPrimaryKey(columnNames: "id", constraintName: "erm_resourcePK", tableName: "erm_resource")
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-37") {
        addPrimaryKey(columnNames: "co_id", constraintName: "holdings_coveragePK", tableName: "holdings_coverage")
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-38") {
        addPrimaryKey(columnNames: "id_id", constraintName: "identifierPK", tableName: "identifier")
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-39") {
        addPrimaryKey(columnNames: "idns_id", constraintName: "identifier_namespacePK", tableName: "identifier_namespace")
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-40") {
        addPrimaryKey(columnNames: "io_id", constraintName: "identifier_occurrencePK", tableName: "identifier_occurrence")
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-41") {
        addPrimaryKey(columnNames: "ic_id", constraintName: "internal_contactPK", tableName: "internal_contact")
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-42") {
        addPrimaryKey(columnNames: "nd_id", constraintName: "nodePK", tableName: "node")
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-43") {
        addPrimaryKey(columnNames: "org_id", constraintName: "orgPK", tableName: "org")
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-44") {
        addPrimaryKey(columnNames: "id", constraintName: "packagePK", tableName: "package")
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-45") {
        addPrimaryKey(columnNames: "id", constraintName: "package_content_itemPK", tableName: "package_content_item")
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-46") {
        addPrimaryKey(columnNames: "pt_id", constraintName: "platformPK", tableName: "platform")
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-47") {
        addPrimaryKey(columnNames: "pl_id", constraintName: "platform_locatorPK", tableName: "platform_locator")
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-48") {
        addPrimaryKey(columnNames: "id", constraintName: "platform_title_instancePK", tableName: "platform_title_instance")
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-49") {
        addPrimaryKey(columnNames: "pop_id", constraintName: "po_line_proxyPK", tableName: "po_line_proxy")
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-50") {
        addPrimaryKey(columnNames: "rdc_id", constraintName: "refdata_categoryPK", tableName: "refdata_category")
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-51") {
        addPrimaryKey(columnNames: "rdv_id", constraintName: "refdata_valuePK", tableName: "refdata_value")
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-52") {
        addPrimaryKey(columnNames: "rkb_id", constraintName: "remotekbPK", tableName: "remotekb")
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-53") {
        addPrimaryKey(columnNames: "eh_id", constraintName: "sa_event_historyPK", tableName: "sa_event_history")
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-54") {
        addPrimaryKey(columnNames: "sa_id", constraintName: "subscription_agreementPK", tableName: "subscription_agreement")
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-55") {
        addPrimaryKey(columnNames: "sao_id", constraintName: "subscription_agreement_orgPK", tableName: "subscription_agreement_org")
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-56") {
        addPrimaryKey(columnNames: "id", constraintName: "text_propertyPK", tableName: "text_property")
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-57") {
        addPrimaryKey(columnNames: "id", constraintName: "title_instancePK", tableName: "title_instance")
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-58") {
        addPrimaryKey(columnNames: "w_id", constraintName: "workPK", tableName: "work")
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-59") {
        createIndex(indexName: "rdv_entry_idx", tableName: "refdata_value") {
            column(name: "rdv_value")

            column(name: "rdv_owner")
        }
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-60") {
        addForeignKeyConstraint(baseColumnNames: "io_identifier_fk", baseTableName: "identifier_occurrence", constraintName: "FK124sp9vc5hnix1ufo6wi2vbav", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id_id", referencedTableName: "identifier")
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-61") {
        addForeignKeyConstraint(baseColumnNames: "cs_ti_fk", baseTableName: "coverage_statement", constraintName: "FK2ocimr1uh2pogta68xl9ph3n", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "title_instance")
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-62") {
        addForeignKeyConstraint(baseColumnNames: "nd_parent", baseTableName: "node", constraintName: "FK2x99i2kqqt7g2ik5cn2fmif6t", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "nd_id", referencedTableName: "node")
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-63") {
        addForeignKeyConstraint(baseColumnNames: "sa_renewal_priority", baseTableName: "subscription_agreement", constraintName: "FK34wtnrq42y7hiab2pg918y7en", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-64") {
        addForeignKeyConstraint(baseColumnNames: "sa_content_review_needed", baseTableName: "subscription_agreement", constraintName: "FK4nhteulih6q3nqtsu512ny93x", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-65") {
        addForeignKeyConstraint(baseColumnNames: "pci_pkg_fk", baseTableName: "package_content_item", constraintName: "FK4u9t780a3pgjy1wxsdn8r131k", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "package")
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-66") {
        addForeignKeyConstraint(baseColumnNames: "sao_org_fk", baseTableName: "subscription_agreement_org", constraintName: "FK5njhff2krytlio6vtpji5gsg8", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "org_id", referencedTableName: "org")
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-67") {
        addForeignKeyConstraint(baseColumnNames: "sa_agreement_type", baseTableName: "subscription_agreement", constraintName: "FK613exmd4qa6bjjdycx9kot0yp", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-68") {
        addForeignKeyConstraint(baseColumnNames: "ti_work_fk", baseTableName: "title_instance", constraintName: "FK6jfb5y930akyqphqjt55yrga6", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "w_id", referencedTableName: "work")
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-69") {
        addForeignKeyConstraint(baseColumnNames: "ic_role", baseTableName: "internal_contact", constraintName: "FK6njvotbglvhl7adk4hiv1kbsn", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-70") {
        addForeignKeyConstraint(baseColumnNames: "ic_owner_fk", baseTableName: "internal_contact", constraintName: "FK7p34rfl5q8gij3717gpkxq4yt", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "sa_id", referencedTableName: "subscription_agreement")
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-71") {
        addForeignKeyConstraint(baseColumnNames: "co_ent_fk", baseTableName: "holdings_coverage", constraintName: "FK7tx1qaa6hcl1p5kg4n9k8fv4d", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "ent_id", referencedTableName: "entitlement")
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-72") {
        addForeignKeyConstraint(baseColumnNames: "sao_owner_fk", baseTableName: "subscription_agreement_org", constraintName: "FK7y8hfedonx5ywo6oeu2antqcr", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "sa_id", referencedTableName: "subscription_agreement")
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-73") {
        addForeignKeyConstraint(baseColumnNames: "sa_is_perpetual", baseTableName: "subscription_agreement", constraintName: "FK8g7c4fgbop5kuy91di5eh9luq", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-74") {
        addForeignKeyConstraint(baseColumnNames: "subscription_agreement_tags_id", baseTableName: "subscription_agreement_tag", constraintName: "FK8gn0bwh4w9184adsq6sj75ir3", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "sa_id", referencedTableName: "subscription_agreement")
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-75") {
        addForeignKeyConstraint(baseColumnNames: "pop_owner", baseTableName: "po_line_proxy", constraintName: "FK8ufrte3dhjhseabh067wg08lr", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "ent_id", referencedTableName: "entitlement")
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-76") {
        addForeignKeyConstraint(baseColumnNames: "io_status_fk", baseTableName: "identifier_occurrence", constraintName: "FK930t3v9wtioa9a9j5013au5ci", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-77") {
        addForeignKeyConstraint(baseColumnNames: "eh_event_outcome", baseTableName: "sa_event_history", constraintName: "FK9hxymcjll2kctbwvm60j8ywxv", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-78") {
        addForeignKeyConstraint(baseColumnNames: "car_target_kb_fk", baseTableName: "content_activation_record", constraintName: "FK9l1k430yfqn9hft2a27kvrv12", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rkb_id", referencedTableName: "remotekb")
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-79") {
        addForeignKeyConstraint(baseColumnNames: "ent_resource_fk", baseTableName: "entitlement", constraintName: "FK9uj3dokm2wv87kfp3vjphnc83", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "erm_resource")
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-80") {
        addForeignKeyConstraint(baseColumnNames: "io_ti_fk", baseTableName: "identifier_occurrence", constraintName: "FKat7yej3qg0w5ppb0t4akj51wl", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "title_instance")
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-81") {
        addForeignKeyConstraint(baseColumnNames: "sao_role", baseTableName: "subscription_agreement_org", constraintName: "FKbg8mmpb05wmvjlh7b1uyw8e7a", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-82") {
        addForeignKeyConstraint(baseColumnNames: "id_ns_fk", baseTableName: "identifier", constraintName: "FKby5jjtajics8edtt193lwtnwv", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "idns_id", referencedTableName: "identifier_namespace")
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-83") {
        addForeignKeyConstraint(baseColumnNames: "car_pti_fk", baseTableName: "content_activation_record", constraintName: "FKcg8khs1ip5xiibdkp85xoe7ba", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "platform_title_instance")
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-84") {
        addForeignKeyConstraint(baseColumnNames: "cs_pci_fk", baseTableName: "coverage_statement", constraintName: "FKciqq54dwgdmv0ta5ugs58sn36", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "package_content_item")
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-85") {
        addForeignKeyConstraint(baseColumnNames: "cs_pti_fk", baseTableName: "coverage_statement", constraintName: "FKdj82640bdcj4dfrbn0aqdgbfp", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "platform_title_instance")
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-86") {
        addForeignKeyConstraint(baseColumnNames: "tag_id", baseTableName: "subscription_agreement_tag", constraintName: "FKe306eyrll9tc7oxd3dataogyj", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "tag")
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-87") {
        addForeignKeyConstraint(baseColumnNames: "pti_ti_fk", baseTableName: "platform_title_instance", constraintName: "FKedoadk035beg5u3vi2232pq9m", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "title_instance")
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-88") {
        addForeignKeyConstraint(baseColumnNames: "res_type_fk", baseTableName: "erm_resource", constraintName: "FKef3ae6ct52nn3jcmqidhhpwf8", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-89") {
        addForeignKeyConstraint(baseColumnNames: "eh_owner", baseTableName: "sa_event_history", constraintName: "FKeopwa26u1tipqav4a0y01i9sr", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "sa_id", referencedTableName: "subscription_agreement")
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-90") {
        addForeignKeyConstraint(baseColumnNames: "res_sub_type_fk", baseTableName: "erm_resource", constraintName: "FKfc1lm4f2parkaa8en9fmo4sqc", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-91") {
        addForeignKeyConstraint(baseColumnNames: "pl_owner_fk", baseTableName: "platform_locator", constraintName: "FKfn4ls5f77sc18cq9c8owlkgtp", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "pt_id", referencedTableName: "platform")
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-92") {
        addForeignKeyConstraint(baseColumnNames: "rdv_owner", baseTableName: "refdata_value", constraintName: "FKh4fon2a7k4y8b2sicjm0i6oy8", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdc_id", referencedTableName: "refdata_category")
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-93") {
        addForeignKeyConstraint(baseColumnNames: "sa_agreement_status", baseTableName: "subscription_agreement", constraintName: "FKiivriw3306iouwpg8e65t3ff0", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-94") {
        addForeignKeyConstraint(baseColumnNames: "pkg_remote_kb", baseTableName: "package", constraintName: "FKoedx99aeb9ll9v1p7w29htqtl", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rkb_id", referencedTableName: "remotekb")
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-95") {
        addForeignKeyConstraint(baseColumnNames: "pkg_vendor_fk", baseTableName: "package", constraintName: "FKokps4xbl6ipd7unkfq910jn03", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "org_id", referencedTableName: "org")
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-96") {
        addForeignKeyConstraint(baseColumnNames: "ent_owner_fk", baseTableName: "entitlement", constraintName: "FKoocrauwiw6xp7ace0yueywgqy", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "sa_id", referencedTableName: "subscription_agreement")
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-97") {
        addForeignKeyConstraint(baseColumnNames: "pci_pti_fk", baseTableName: "package_content_item", constraintName: "FKostrwqec52cid7enxbr4b2loe", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "platform_title_instance")
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-98") {
        addForeignKeyConstraint(baseColumnNames: "sa_vendor_fk", baseTableName: "subscription_agreement", constraintName: "FKppeugnj4xts3ah8tjmeg232db", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "org_id", referencedTableName: "org")
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-99") {
        addForeignKeyConstraint(baseColumnNames: "eh_event_type", baseTableName: "sa_event_history", constraintName: "FKs8nxucxkesrpshhxsh15wxdn0", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-100") {
        addForeignKeyConstraint(baseColumnNames: "pkg_nominal_platform_fk", baseTableName: "package", constraintName: "FKtji5rpd3emxprdidedl006f9u", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "pt_id", referencedTableName: "platform")
    }

    changeSet(author: "ianibbo (generated)", id: "1543400138015-101") {
        addForeignKeyConstraint(baseColumnNames: "pti_pt_fk", baseTableName: "platform_title_instance", constraintName: "FKtlecp40x0sb3rd9w4qi16lcu0", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "pt_id", referencedTableName: "platform")
    }
}
