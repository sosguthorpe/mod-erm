databaseChangeLog = {

    changeSet(author: "sosguthorpe (generated)", id: "1565611459917-1") {
        createTable(tableName: "package_import_job") {
            column(name: "id", type: "VARCHAR(255)") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "sosguthorpe (generated)", id: "1565611459917-2") {
        addColumn(tableName: "custom_property_definition") {
            column(name: "default_internal", type: "boolean") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "sosguthorpe (generated)", id: "1565611459917-3") {
        addColumn(tableName: "custom_property") {
            column(name: "internal", type: "boolean") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "sosguthorpe (generated)", id: "1565611459917-4") {
        addColumn(tableName: "custom_property") {
            column(name: "public_note", type: "text")
        }
    }

    changeSet(author: "sosguthorpe (generated)", id: "1565611459917-5") {
        addPrimaryKey(columnNames: "id", constraintName: "package_import_jobPK", tableName: "package_import_job")
    }

    changeSet(author: "sosguthorpe (generated)", id: "1565611459917-6") {
        addForeignKeyConstraint(baseColumnNames: "job_result_fk", baseTableName: "persistent_job", constraintName: "FKjxrmx8rlt5vsxgheyjx48pvqg", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    }

    changeSet(author: "sosguthorpe (generated)", id: "1565611459917-7") {
        addForeignKeyConstraint(baseColumnNames: "job_status_fk", baseTableName: "persistent_job", constraintName: "FKr2aj8qee8rpi96dqucwg6fnxv", deferrable: "false", initiallyDeferred: "false", referencedColumnNames: "rdv_id", referencedTableName: "refdata_value")
    }

    changeSet(author: "sosguthorpe (generated)", id: "1565611459917-8") {
        dropColumn(columnName: "da_file_upload", tableName: "document_attachment")
    }

    changeSet(author: "sosguthorpe (generated)", id: "1565611459917-9") {
        dropColumn(columnName: "version", tableName: "persistent_job")
    }
}