package org.olf.kb

import grails.gorm.MultiTenant
import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode(includes=['type', 'length', 'unit'])
class EmbargoStatement implements MultiTenant<EmbargoStatement> {
  // Expose this enum.
  public final enum Type {
    P,
    R
  }
  
  public final enum Unit {
    D, M, Y
  }
  
  String id
  Type type
  Unit unit
  int length
  
  static mapping = {
    id column:'est_id', generator: 'uuid2', length:36
    type column:'est_type'
    unit column:'est_unit'
    length column:'est_length'
  }
  
  static constraints = {
        type (nullable:false)
        unit (nullable:false)
      length (nullable:false, min: 1)
  }
  
  public static final REGEX = /(P|R)(\d+)(D|M|Y)/
  public static final EmbargoStatement parse( String embargoStatement ) {
    def match = embargoStatement ? embargoStatement.toUpperCase() =~ "^${REGEX}\$" : null
    
    // Fail fast if null or invalid formatting.
    if (!match) {
      return null
    }
    
    // Matched.
    EmbargoStatement stmt = new EmbargoStatement(
      type:   Type.valueOf(match[0][1]),
      length: match[0][2] as Integer,
      unit:   Unit.valueOf(match[0][3])
    )
    
    stmt
  }
  
  String toString() {
    "${type}${length}${unit}"
  }
}
