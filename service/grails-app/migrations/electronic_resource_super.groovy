databaseChangeLog = {

    changeSet(author: "sosguthorpe (generated)", id: "1537958946536-1") {
        createTable(tableName: "all_electronic_resources") {
            column(name: "id", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "name", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "class", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }

            column(name: "ti_title", type: "VARCHAR(255)")

            column(name: "ti_work_fk", type: "VARCHAR(36)")

            column(name: "ti_medium_fk", type: "VARCHAR(36)")

            column(name: "ti_resource_type_fk", type: "VARCHAR(36)")

            column(name: "pkg_remote_kb", type: "VARCHAR(36)")

            column(name: "pkg_vendor_fk", type: "VARCHAR(36)")

            column(name: "pkg_nominal_platform_fk", type: "VARCHAR(36)")

            column(name: "pkg_source", type: "VARCHAR(255)")

            column(name: "pkg_reference", type: "VARCHAR(255)")
        }
    }

    changeSet(author: "sosguthorpe (generated)", id: "1537958946536-2") {
        addPrimaryKey(columnNames: "id", constraintName: "all_electronic_resourcesPK", tableName: "all_electronic_resources")
    }

    changeSet(author: "sosguthorpe (generated)", id: "1537958946536-3") {
        addPrimaryKey(columnNames: "nd_id", constraintName: "nodePK", tableName: "node")
    }

    changeSet(author: "sosguthorpe (generated)", id: "1537958946536-4") {
        addPrimaryKey(columnNames: "eh_id", constraintName: "sa_event_historyPK", tableName: "sa_event_history")
    }

    changeSet(author: "sosguthorpe (generated)", id: "1537958946536-5") {
        addForeignKeyConstraint(baseColumnNames: "ti_work_fk", baseTableName: "all_electronic_resources", constraintName: "FK22nat3tifb0tt201cskwhmcv7", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "w_id", referencedTableName: "work")
    }

    changeSet(author: "sosguthorpe (generated)", id: "1537958946536-6") {
        addForeignKeyConstraint(baseColumnNames: "nd_parent", baseTableName: "node", constraintName: "FK2x99i2kqqt7g2ik5cn2fmif6t", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "nd_id", referencedTableName: "node")
    }

    changeSet(author: "sosguthorpe (generated)", id: "1537958946536-7") {
        addForeignKeyConstraint(baseColumnNames: "sa_renewal_priority", baseTableName: "subscription_agreement", constraintName: "FK34wtnrq42y7hiab2pg918y7en", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    }

    changeSet(author: "sosguthorpe (generated)", id: "1537958946536-8") {
        addForeignKeyConstraint(baseColumnNames: "sa_content_review_needed", baseTableName: "subscription_agreement", constraintName: "FK4nhteulih6q3nqtsu512ny93x", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    }

    changeSet(author: "sosguthorpe (generated)", id: "1537958946536-9") {
        addForeignKeyConstraint(baseColumnNames: "pkg_remote_kb", baseTableName: "all_electronic_resources", constraintName: "FK5uvakkliv15oef0s6tavfdee3", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rkb_id", referencedTableName: "remotekb")
    }

    changeSet(author: "sosguthorpe (generated)", id: "1537958946536-10") {
        addForeignKeyConstraint(baseColumnNames: "sa_is_perpetual", baseTableName: "subscription_agreement", constraintName: "FK8g7c4fgbop5kuy91di5eh9luq", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    }

    changeSet(author: "sosguthorpe (generated)", id: "1537958946536-11") {
        addForeignKeyConstraint(baseColumnNames: "eh_event_outcome", baseTableName: "sa_event_history", constraintName: "FK9hxymcjll2kctbwvm60j8ywxv", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    }

    changeSet(author: "sosguthorpe (generated)", id: "1537958946536-12") {
        addForeignKeyConstraint(baseColumnNames: "pkg_nominal_platform_fk", baseTableName: "all_electronic_resources", constraintName: "FKbg75qrmi2habcb5yquaq746jp", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "pt_id", referencedTableName: "platform")
    }

    changeSet(author: "sosguthorpe (generated)", id: "1537958946536-13") {
        addForeignKeyConstraint(baseColumnNames: "ti_medium_fk", baseTableName: "all_electronic_resources", constraintName: "FKblwbkw4d6p0330lfa7kbhiw8q", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    }

    changeSet(author: "sosguthorpe (generated)", id: "1537958946536-14") {
        addForeignKeyConstraint(baseColumnNames: "io_ti_fk", baseTableName: "identifier_occurrence", constraintName: "FKe7yae1x83of4yct3w26ufkloh", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "all_electronic_resources")
    }

    changeSet(author: "sosguthorpe (generated)", id: "1537958946536-15") {
        addForeignKeyConstraint(baseColumnNames: "eh_owner", baseTableName: "sa_event_history", constraintName: "FKeopwa26u1tipqav4a0y01i9sr", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "sa_id", referencedTableName: "subscription_agreement")
    }

    changeSet(author: "sosguthorpe (generated)", id: "1537958946536-16") {
        addForeignKeyConstraint(baseColumnNames: "pti_ti_fk", baseTableName: "platform_title_instance", constraintName: "FKgl6ruwqava2fn7k4ixwko7gpb", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "all_electronic_resources")
    }

    changeSet(author: "sosguthorpe (generated)", id: "1537958946536-17") {
        addForeignKeyConstraint(baseColumnNames: "ti_resource_type_fk", baseTableName: "all_electronic_resources", constraintName: "FKhpvkejcur56oij5l7kd45miq3", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    }

    changeSet(author: "sosguthorpe (generated)", id: "1537958946536-18") {
        addForeignKeyConstraint(baseColumnNames: "sa_agreement_status", baseTableName: "subscription_agreement", constraintName: "FKiivriw3306iouwpg8e65t3ff0", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    }

    changeSet(author: "sosguthorpe (generated)", id: "1537958946536-19") {
        addForeignKeyConstraint(baseColumnNames: "pci_pkg_fk", baseTableName: "package_content_item", constraintName: "FKli8ftmu7i6h28o3dkq79ya30n", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "all_electronic_resources")
    }

    changeSet(author: "sosguthorpe (generated)", id: "1537958946536-20") {
        addForeignKeyConstraint(baseColumnNames: "ent_pkg_fk", baseTableName: "entitlement", constraintName: "FKmkv68v3e7hvrqi06fdsvv5tlt", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "all_electronic_resources")
    }

    changeSet(author: "sosguthorpe (generated)", id: "1537958946536-21") {
        addForeignKeyConstraint(baseColumnNames: "pkg_vendor_fk", baseTableName: "all_electronic_resources", constraintName: "FKpkv10n6e1dgcd835umq08gaa6", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "org_id", referencedTableName: "org")
    }

    changeSet(author: "sosguthorpe (generated)", id: "1537958946536-22") {
        addForeignKeyConstraint(baseColumnNames: "sa_vendor_fk", baseTableName: "subscription_agreement", constraintName: "FKppeugnj4xts3ah8tjmeg232db", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "org_id", referencedTableName: "org")
    }

    changeSet(author: "sosguthorpe (generated)", id: "1537958946536-23") {
        addForeignKeyConstraint(baseColumnNames: "cs_ti_fk", baseTableName: "coverage_statement", constraintName: "FKqj3xoc68dh85442aby76xctbd", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "id", referencedTableName: "all_electronic_resources")
    }

    changeSet(author: "sosguthorpe (generated)", id: "1537958946536-24") {
        addForeignKeyConstraint(baseColumnNames: "eh_event_type", baseTableName: "sa_event_history", constraintName: "FKs8nxucxkesrpshhxsh15wxdn0", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    }

    changeSet(author: "sosguthorpe (generated)", id: "1537958946536-25") {
        dropForeignKeyConstraint(baseTableName: "coverage_statement", constraintName: "FK2ocimr1uh2pogta68xl9ph3n")
    }

    changeSet(author: "sosguthorpe (generated)", id: "1537958946536-26") {
        dropForeignKeyConstraint(baseTableName: "package_content_item", constraintName: "FK4u9t780a3pgjy1wxsdn8r131k")
    }

    changeSet(author: "sosguthorpe (generated)", id: "1537958946536-27") {
        dropForeignKeyConstraint(baseTableName: "identifier_occurrence", constraintName: "FKat7yej3qg0w5ppb0t4akj51wl")
    }

    changeSet(author: "sosguthorpe (generated)", id: "1537958946536-28") {
        dropForeignKeyConstraint(baseTableName: "platform_title_instance", constraintName: "FKedoadk035beg5u3vi2232pq9m")
    }

    changeSet(author: "sosguthorpe (generated)", id: "1537958946536-29") {
        dropForeignKeyConstraint(baseTableName: "entitlement", constraintName: "FKjukl0v6gkoqx79lndcmn06r4v")
    }

    changeSet(author: "sosguthorpe (generated)", id: "1537958946536-30") {
        dropView(viewName: "all_electronic_resources")
    }

    changeSet(author: "sosguthorpe (generated)", id: "1537958946536-31") {
        dropTable(tableName: "package")
    }

    changeSet(author: "sosguthorpe (generated)", id: "1537958946536-32") {
        dropTable(tableName: "title_instance")
    }

    changeSet(author: "sosguthorpe (generated)", id: "1537958946536-33") {
        dropColumn(columnName: "rdv_style", tableName: "refdata_value")
    }

    changeSet(author: "sosguthorpe (generated)", id: "1537958946536-34") {
        addNotNullConstraint(columnDataType: "varchar(36)", columnName: "eh_event_outcome", tableName: "sa_event_history")
    }

    changeSet(author: "sosguthorpe (generated)", id: "1537958946536-35") {
        dropNotNullConstraint(columnDataType: "varchar(36)", columnName: "ent_owner_fk", tableName: "entitlement")
    }

    changeSet(author: "sosguthorpe (generated)", id: "1537958946536-36") {
        addNotNullConstraint(columnDataType: "varchar(255)", columnName: "nd_label", tableName: "node")
    }

    changeSet(author: "sosguthorpe (generated)", id: "1537958946536-37") {
        addNotNullConstraint(columnDataType: "varchar(255)", columnName: "nd_node_type", tableName: "node")
    }

    changeSet(author: "sosguthorpe (generated)", id: "1537958946536-38") {
        addNotNullConstraint(columnDataType: "varchar(255)", columnName: "nd_reference_class", tableName: "node")
    }

    changeSet(author: "sosguthorpe (generated)", id: "1537958946536-39") {
        addNotNullConstraint(columnDataType: "varchar(255)", columnName: "nd_reference_id", tableName: "node")
    }

    changeSet(author: "sosguthorpe (generated)", id: "1537958946536-40") {
        dropIndex(indexName: "rdv_entry_idx", tableName: "refdata_value")

        createIndex(indexName: "rdv_entry_idx", tableName: "refdata_value") {
            column(name: "rdv_value")

            column(name: "rdv_owner")
        }
    }
}
