databaseChangeLog = {
  changeSet(author: "ethanfreestone (manual)", id: "202001141001-001") {
    addColumn(tableName: "title_instance") {
      column(name: "ti_date_monograph_published", type: "VARCHAR(36)")
      column(name: "ti_first_author", type: "VARCHAR(36)")
      column(name: "ti_monograph_edition", type: "VARCHAR(36)")
      column(name: "ti_monograph_volume", type: "VARCHAR(36)")
    }
  }

  changeSet(author: "ethanfreestone (manual)", id: "202001211524-001") {
    addColumn(tableName: "title_instance") {
      column(name: "ti_first_editor", type: "VARCHAR(36)")
    }
  }
}
