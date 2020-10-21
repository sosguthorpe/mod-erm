package org.olf.general

class StringTemplate {

  String id
  String name
  String rule
  public final enum Context {
    urlProxier,
    urlCustomiser
  }
  Context context

  /*
   * The useage of this list will depend somewhat on context,
   * but the general idea is that this captures the 'scope' of a translation.
   * For certain contexts this list will act as a 'whitelist', for others as a 'blacklist'
   */
  static hasMany = [idScopes: String]


  static mapping = {
    id column:'st_id', generator: 'uuid2', length:36
    name column:'st_name'
    rule column:'st_rule'
    context column:'st_context'
    idScopes cascade: 'all-delete-orphan', joinTable: [name: "string_template_scopes", column: "id_scope"]
  }
}
