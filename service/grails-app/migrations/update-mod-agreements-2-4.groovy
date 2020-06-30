databaseChangeLog = {
    changeSet(author: "claudia (manual)", id: "202006291630-1") {
        addColumn(tableName: "subscription_agreement_org") {
            column(name: "sao_note", type: "text")
        }
    }
}