/**
 * Add remote KB sync status and last check fields
 */
databaseChangeLog = {
  changeSet(author: "ian (manual)", id: "2019-08-14-00001") {
    addColumn(tableName: 'remotekb') {
      column(name: "rkb_sync_status", type: "VARCHAR(255)")
    }

    addColumn(tableName: 'remotekb') {
      column(name: "rkb_last_check", type: 'BIGINT')
    }
  }
}

