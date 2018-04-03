databaseChangeLog = {

    changeSet(author: "ianibbo (generated)", id: "1522772202812-1") {
        createTable(tableName: "coverage_statement") {
            column(name: "cs_id", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "cs_version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "cs_pci_fk", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "cs_ti_fk", type: "VARCHAR(36)")

            column(name: "cs_pti_fk", type: "VARCHAR(36)")
        }
    }

    changeSet(author: "ianibbo (generated)", id: "1522772202812-2") {
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

    changeSet(author: "ianibbo (generated)", id: "1522772202812-3") {
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

    changeSet(author: "ianibbo (generated)", id: "1522772202812-4") {
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

    changeSet(author: "ianibbo (generated)", id: "1522772202812-5") {
        createTable(tableName: "platform_locator") {
            column(name: "pl_id", type: "VARCHAR(36)") {
                constraints(nullable: "false")
            }

            column(name: "pl_version", type: "BIGINT") {
                constraints(nullable: "false")
            }

            column(name: "pl_domain_name_regex", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "ianibbo (generated)", id: "1522772202812-6") {
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

    changeSet(author: "ianibbo (generated)", id: "1522772202812-7") {
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

    changeSet(author: "ianibbo (generated)", id: "1522772202812-8") {
        createTable(tableName: "subscription_agreement") {
            column(name: "sa_identifier", type: "VARCHAR(36)") {
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

            column(name: "sa_end_date", type: "timestamp")
        }
    }

    changeSet(author: "ianibbo (generated)", id: "1522772202812-9") {
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

    changeSet(author: "ianibbo (generated)", id: "1522772202812-10") {
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

    changeSet(author: "ianibbo (generated)", id: "1522772202812-11") {
        addPrimaryKey(columnNames: "cs_id", constraintName: "coverage_statementPK", tableName: "coverage_statement")
    }

    changeSet(author: "ianibbo (generated)", id: "1522772202812-12") {
        addPrimaryKey(columnNames: "pkg_id", constraintName: "packagePK", tableName: "package")
    }

    changeSet(author: "ianibbo (generated)", id: "1522772202812-13") {
        addPrimaryKey(columnNames: "pci_id", constraintName: "package_content_itemPK", tableName: "package_content_item")
    }

    changeSet(author: "ianibbo (generated)", id: "1522772202812-14") {
        addPrimaryKey(columnNames: "pt_id", constraintName: "platformPK", tableName: "platform")
    }

    changeSet(author: "ianibbo (generated)", id: "1522772202812-15") {
        addPrimaryKey(columnNames: "pl_id", constraintName: "platform_locatorPK", tableName: "platform_locator")
    }

    changeSet(author: "ianibbo (generated)", id: "1522772202812-16") {
        addPrimaryKey(columnNames: "pti_id", constraintName: "platform_title_instancePK", tableName: "platform_title_instance")
    }

    changeSet(author: "ianibbo (generated)", id: "1522772202812-17") {
        addPrimaryKey(columnNames: "rkb_id", constraintName: "remotekbPK", tableName: "remotekb")
    }

    changeSet(author: "ianibbo (generated)", id: "1522772202812-18") {
        addPrimaryKey(columnNames: "sa_identifier", constraintName: "subscription_agreementPK", tableName: "subscription_agreement")
    }

    changeSet(author: "ianibbo (generated)", id: "1522772202812-19") {
        addPrimaryKey(columnNames: "ti_id", constraintName: "title_instancePK", tableName: "title_instance")
    }

    changeSet(author: "ianibbo (generated)", id: "1522772202812-20") {
        addPrimaryKey(columnNames: "w_id", constraintName: "workPK", tableName: "work")
    }

    changeSet(author: "ianibbo (generated)", id: "1522772202812-21") {
        addForeignKeyConstraint(baseColumnNames: "cs_ti_fk", baseTableName: "coverage_statement", constraintName: "FK2ocimr1uh2pogta68xl9ph3n", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "ti_id", referencedTableName: "title_instance")
    }

    changeSet(author: "ianibbo (generated)", id: "1522772202812-22") {
        addForeignKeyConstraint(baseColumnNames: "pci_pkg_fk", baseTableName: "package_content_item", constraintName: "FK4u9t780a3pgjy1wxsdn8r131k", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "pkg_id", referencedTableName: "package")
    }

    changeSet(author: "ianibbo (generated)", id: "1522772202812-23") {
        addForeignKeyConstraint(baseColumnNames: "cs_pci_fk", baseTableName: "coverage_statement", constraintName: "FKciqq54dwgdmv0ta5ugs58sn36", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "pci_id", referencedTableName: "package_content_item")
    }

    changeSet(author: "ianibbo (generated)", id: "1522772202812-24") {
        addForeignKeyConstraint(baseColumnNames: "cs_pti_fk", baseTableName: "coverage_statement", constraintName: "FKdj82640bdcj4dfrbn0aqdgbfp", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "pti_id", referencedTableName: "platform_title_instance")
    }

    changeSet(author: "ianibbo (generated)", id: "1522772202812-25") {
        addForeignKeyConstraint(baseColumnNames: "pti_ti_fk", baseTableName: "platform_title_instance", constraintName: "FKedoadk035beg5u3vi2232pq9m", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "ti_id", referencedTableName: "title_instance")
    }

    changeSet(author: "ianibbo (generated)", id: "1522772202812-26") {
        addForeignKeyConstraint(baseColumnNames: "pci_pti_fk", baseTableName: "package_content_item", constraintName: "FKostrwqec52cid7enxbr4b2loe", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "pti_id", referencedTableName: "platform_title_instance")
    }

    changeSet(author: "ianibbo (generated)", id: "1522772202812-27") {
        addForeignKeyConstraint(baseColumnNames: "pti_pt_fk", baseTableName: "platform_title_instance", constraintName: "FKtlecp40x0sb3rd9w4qi16lcu0", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "pt_id", referencedTableName: "platform")
    }
}
