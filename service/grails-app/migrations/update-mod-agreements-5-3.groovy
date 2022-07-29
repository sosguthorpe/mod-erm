databaseChangeLog = {
  changeSet(author: "sosguthorpe", id: "202207-0001-001") {
    addColumn(tableName: "persistent_job") {
      column(name: "job_runner_id", type: "VARCHAR(36)")
    }
    
    createIndex(indexName: "job_runner_idx", tableName: "persistent_job") {
      column(name: "job_runner_id")
    }
  }
}
