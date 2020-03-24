databaseChangeLog = {
    changeSet(author: "claudia (manual)", id: "202003191925-1") {
    modifyDataType(
      tableName: "custom_property_definition",
      columnName: "pd_description", type: "text",
      newDataType: "text",
      confirm: "Successfully updated the pd_description column."
    )
  }

  changeSet(author: "claudia (manual)", id: "202003191925-2") {
    addColumn(tableName: "refdata_category") {
      column(name: "internal", type: "boolean")
    }
    addNotNullConstraint (tableName: "refdata_category", columnName: "internal", defaultNullValue: false)
  }
  
  changeSet(author: "claudia (manual)", id: "202003191925-3") {
    grailsChange {
      change {
        // Change all categories to internal where necessary. 
        sql.execute("""
          UPDATE ${database.defaultSchemaName}.refdata_category SET internal = true
            WHERE rdc_description IN ('SubscriptionAgreement.AgreementStatus','SubscriptionAgreementOrg.Role','TitleInstance.SubType','TitleInstance.Type','AgreementRelationship.Type','Global.Yes_No','LicenseAmendmentStatus.Status','PersistentJob.Result','PersistentJob.Status','IdentifierOccurrence.Status')
        """.toString())
      }
    }
  }
}
