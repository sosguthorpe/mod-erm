databaseChangeLog = {

    changeSet(author: "ianibbo (generated)", id: "1523106799959-1") {
        createTable(tableName: "agreement_line_item") {
            column(name: "ali_id", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "ali_version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "ali_pci_fk", type: "VARCHAR(36)")

            column(name: "ali_owner_fk", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "ali_pti_fk", type: "VARCHAR(36)")

            column(name: "ali_pkg_fk", type: "VARCHAR(36)")

            column(name: "ali_enabled", type: "BOOLEAN")
        }
    }

    changeSet(author: "ianibbo (generated)", id: "1523106799959-2") {
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

    changeSet(author: "ianibbo (generated)", id: "1523106799959-3") {
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

            column(name: "value", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "ianibbo (generated)", id: "1523106799959-4") {
        createTable(tableName: "identifier_namespace") {
            column(name: "idns_id", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "idns_version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "value", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "ianibbo (generated)", id: "1523106799959-5") {
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

    changeSet(author: "ianibbo (generated)", id: "1523106799959-6") {
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
        }
    }

    changeSet(author: "ianibbo (generated)", id: "1523106799959-7") {
        createTable(tableName: "package_content_item") {
            column(name: "pci_id", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "pci_version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "pci_pti_fk", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "pci_pkg_fk", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "ianibbo (generated)", id: "1523106799959-8") {
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

    changeSet(author: "ianibbo (generated)", id: "1523106799959-9") {
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

    changeSet(author: "ianibbo (generated)", id: "1523106799959-10") {
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
        }
    }

    changeSet(author: "ianibbo (generated)", id: "1523106799959-11") {
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

    changeSet(author: "ianibbo (generated)", id: "1523106799959-12") {
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

    changeSet(author: "ianibbo (generated)", id: "1523106799959-13") {
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
        }
    }

    changeSet(author: "ianibbo (generated)", id: "1523106799959-14") {
        createTable(tableName: "subscription_agreement") {
            column(name: "sa_id", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "sa_version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "sa_vendor_reference", type: "VARCHAR(255)")

            column(name: "sa_start_date", type: "timestamp")

            column(name: "sa_renewal_date", type: "timestamp")

            column(name: "sa_next_review_date", type: "timestamp")

            column(name: "sa_name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "sa_local_reference", type: "VARCHAR(255)")

            column(name: "sa_agreement_type", type: "VARCHAR(36)")

            column(name: "sa_enabled", type: "BOOLEAN")

            column(name: "sa_end_date", type: "timestamp")
        }
    }

    changeSet(author: "ianibbo (generated)", id: "1523106799959-15") {
        createTable(tableName: "title_instance") {
            column(name: "ti_id", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "ti_version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "ti_title", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "ianibbo (generated)", id: "1523106799959-16") {
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

    changeSet(author: "ianibbo (generated)", id: "1523106799959-17") {
        addPrimaryKey(columnNames: "ali_id", constraintName: "agreement_line_itemPK", tableName: "agreement_line_item")
    }

    changeSet(author: "ianibbo (generated)", id: "1523106799959-18") {
        addPrimaryKey(columnNames: "cs_id", constraintName: "coverage_statementPK", tableName: "coverage_statement")
    }

    changeSet(author: "ianibbo (generated)", id: "1523106799959-19") {
        addPrimaryKey(columnNames: "id_id", constraintName: "identifierPK", tableName: "identifier")
    }

    changeSet(author: "ianibbo (generated)", id: "1523106799959-20") {
        addPrimaryKey(columnNames: "idns_id", constraintName: "identifier_namespacePK", tableName: "identifier_namespace")
    }

    changeSet(author: "ianibbo (generated)", id: "1523106799959-21") {
        addPrimaryKey(columnNames: "io_id", constraintName: "identifier_occurrencePK", tableName: "identifier_occurrence")
    }

    changeSet(author: "ianibbo (generated)", id: "1523106799959-22") {
        addPrimaryKey(columnNames: "pkg_id", constraintName: "packagePK", tableName: "package")
    }

    changeSet(author: "ianibbo (generated)", id: "1523106799959-23") {
        addPrimaryKey(columnNames: "pci_id", constraintName: "package_content_itemPK", tableName: "package_content_item")
    }

    changeSet(author: "ianibbo (generated)", id: "1523106799959-24") {
        addPrimaryKey(columnNames: "pt_id", constraintName: "platformPK", tableName: "platform")
    }

    changeSet(author: "ianibbo (generated)", id: "1523106799959-25") {
        addPrimaryKey(columnNames: "pl_id", constraintName: "platform_locatorPK", tableName: "platform_locator")
    }

    changeSet(author: "ianibbo (generated)", id: "1523106799959-26") {
        addPrimaryKey(columnNames: "pti_id", constraintName: "platform_title_instancePK", tableName: "platform_title_instance")
    }

    changeSet(author: "ianibbo (generated)", id: "1523106799959-27") {
        addPrimaryKey(columnNames: "rdc_id", constraintName: "refdata_categoryPK", tableName: "refdata_category")
    }

    changeSet(author: "ianibbo (generated)", id: "1523106799959-28") {
        addPrimaryKey(columnNames: "rdv_id", constraintName: "refdata_valuePK", tableName: "refdata_value")
    }

    changeSet(author: "ianibbo (generated)", id: "1523106799959-29") {
        addPrimaryKey(columnNames: "rkb_id", constraintName: "remotekbPK", tableName: "remotekb")
    }

    changeSet(author: "ianibbo (generated)", id: "1523106799959-30") {
        addPrimaryKey(columnNames: "sa_id", constraintName: "subscription_agreementPK", tableName: "subscription_agreement")
    }

    changeSet(author: "ianibbo (generated)", id: "1523106799959-31") {
        addPrimaryKey(columnNames: "ti_id", constraintName: "title_instancePK", tableName: "title_instance")
    }

    changeSet(author: "ianibbo (generated)", id: "1523106799959-32") {
        addPrimaryKey(columnNames: "w_id", constraintName: "workPK", tableName: "work")
    }

    changeSet(author: "ianibbo (generated)", id: "1523106799959-33") {
        createIndex(indexName: "rdv_entry_idx", tableName: "refdata_value") {
            column(name: "rdv_value")

            column(name: "rdv_owner")
        }
    }

    changeSet(author: "ianibbo (generated)", id: "1523106799959-34") {
        addForeignKeyConstraint(baseColumnNames: "io_identifier_fk", baseTableName: "identifier_occurrence", constraintName: "FK124sp9vc5hnix1ufo6wi2vbav", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id_id", referencedTableName: "identifier")
    }

    changeSet(author: "ianibbo (generated)", id: "1523106799959-35") {
        addForeignKeyConstraint(baseColumnNames: "cs_ti_fk", baseTableName: "coverage_statement", constraintName: "FK2ocimr1uh2pogta68xl9ph3n", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "ti_id", referencedTableName: "title_instance")
    }

    changeSet(author: "ianibbo (generated)", id: "1523106799959-36") {
        addForeignKeyConstraint(baseColumnNames: "pci_pkg_fk", baseTableName: "package_content_item", constraintName: "FK4u9t780a3pgjy1wxsdn8r131k", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "pkg_id", referencedTableName: "package")
    }

    changeSet(author: "ianibbo (generated)", id: "1523106799959-37") {
        addForeignKeyConstraint(baseColumnNames: "sa_agreement_type", baseTableName: "subscription_agreement", constraintName: "FK613exmd4qa6bjjdycx9kot0yp", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    }

    changeSet(author: "ianibbo (generated)", id: "1523106799959-38") {
        addForeignKeyConstraint(baseColumnNames: "io_status_fk", baseTableName: "identifier_occurrence", constraintName: "FK930t3v9wtioa9a9j5013au5ci", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    }

    changeSet(author: "ianibbo (generated)", id: "1523106799959-39") {
        addForeignKeyConstraint(baseColumnNames: "ali_owner_fk", baseTableName: "agreement_line_item", constraintName: "FKa7dr5lr4wj3ti2kso4tlc99l5", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "sa_id", referencedTableName: "subscription_agreement")
    }

    changeSet(author: "ianibbo (generated)", id: "1523106799959-40") {
        addForeignKeyConstraint(baseColumnNames: "io_ti_fk", baseTableName: "identifier_occurrence", constraintName: "FKat7yej3qg0w5ppb0t4akj51wl", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "ti_id", referencedTableName: "title_instance")
    }

    changeSet(author: "ianibbo (generated)", id: "1523106799959-41") {
        addForeignKeyConstraint(baseColumnNames: "id_ns_fk", baseTableName: "identifier", constraintName: "FKby5jjtajics8edtt193lwtnwv", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "idns_id", referencedTableName: "identifier_namespace")
    }

    changeSet(author: "ianibbo (generated)", id: "1523106799959-42") {
        addForeignKeyConstraint(baseColumnNames: "cs_pci_fk", baseTableName: "coverage_statement", constraintName: "FKciqq54dwgdmv0ta5ugs58sn36", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "pci_id", referencedTableName: "package_content_item")
    }

    changeSet(author: "ianibbo (generated)", id: "1523106799959-43") {
        addForeignKeyConstraint(baseColumnNames: "cs_pti_fk", baseTableName: "coverage_statement", constraintName: "FKdj82640bdcj4dfrbn0aqdgbfp", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "pti_id", referencedTableName: "platform_title_instance")
    }

    changeSet(author: "ianibbo (generated)", id: "1523106799959-44") {
        addForeignKeyConstraint(baseColumnNames: "pti_ti_fk", baseTableName: "platform_title_instance", constraintName: "FKedoadk035beg5u3vi2232pq9m", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "ti_id", referencedTableName: "title_instance")
    }

    changeSet(author: "ianibbo (generated)", id: "1523106799959-45") {
        addForeignKeyConstraint(baseColumnNames: "pl_owner_fk", baseTableName: "platform_locator", constraintName: "FKfn4ls5f77sc18cq9c8owlkgtp", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "pt_id", referencedTableName: "platform")
    }

    changeSet(author: "ianibbo (generated)", id: "1523106799959-46") {
        addForeignKeyConstraint(baseColumnNames: "ali_pti_fk", baseTableName: "agreement_line_item", constraintName: "FKgmfigdcxicltbus9fv6h6j9xo", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "pti_id", referencedTableName: "platform_title_instance")
    }

    changeSet(author: "ianibbo (generated)", id: "1523106799959-47") {
        addForeignKeyConstraint(baseColumnNames: "rdv_owner", baseTableName: "refdata_value", constraintName: "FKh4fon2a7k4y8b2sicjm0i6oy8", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdc_id", referencedTableName: "refdata_category")
    }

    changeSet(author: "ianibbo (generated)", id: "1523106799959-48") {
        addForeignKeyConstraint(baseColumnNames: "ali_pkg_fk", baseTableName: "agreement_line_item", constraintName: "FKjukl0v6gkoqx79lndcmn06r4v", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "pkg_id", referencedTableName: "package")
    }

    changeSet(author: "ianibbo (generated)", id: "1523106799959-49") {
        addForeignKeyConstraint(baseColumnNames: "ali_pci_fk", baseTableName: "agreement_line_item", constraintName: "FKmvuf8qwj0wxpgkedxclp3xlc5", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "pci_id", referencedTableName: "package_content_item")
    }

    changeSet(author: "ianibbo (generated)", id: "1523106799959-50") {
        addForeignKeyConstraint(baseColumnNames: "pci_pti_fk", baseTableName: "package_content_item", constraintName: "FKostrwqec52cid7enxbr4b2loe", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "pti_id", referencedTableName: "platform_title_instance")
    }

    changeSet(author: "ianibbo (generated)", id: "1523106799959-51") {
        addForeignKeyConstraint(baseColumnNames: "pti_pt_fk", baseTableName: "platform_title_instance", constraintName: "FKtlecp40x0sb3rd9w4qi16lcu0", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "pt_id", referencedTableName: "platform")
    }
}
