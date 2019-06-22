databaseChangeLog = {
  changeSet(author: "John Fereira", id: "2019-05-14-ERM-218-1") {
      modifyDataType( 
        tableName: "erm_resource", 
        columnName: "res_description", 
        newDataType: "CLOB",
        confirm: "Successfully updated the res_description column."
      )
  }

  changeSet(author: "John Fereira", id: "2019-05-14-ERM-218-2") {
      modifyDataType( 
        tableName: "subscription_agreement", 
        columnName: "sa_description", 
        newDataType: "CLOB", 
        confirm: "Successfully updated the sa_description column."
      )
  }

}
