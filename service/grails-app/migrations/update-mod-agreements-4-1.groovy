databaseChangeLog = {

  // unique indexes for refdata values - particularly to protect against unusual upgrade/clone practices by hosters
  changeSet(author: "ianibbo (manual)", id: "202106021259-001") {
    addUniqueConstraint(columnNames: "rdv_owner,rdv_value", constraintName: "RDV_UNIQUE_VALUE", tableName: "refdata_value")
  }
}
