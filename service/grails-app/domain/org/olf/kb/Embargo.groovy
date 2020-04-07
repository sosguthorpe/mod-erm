package org.olf.kb

import grails.gorm.MultiTenant
import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode(includes=['movingWallStart', 'movingWallEnd'])
class Embargo implements MultiTenant<Embargo> {
  String id  
  EmbargoStatement movingWallStart
  EmbargoStatement movingWallEnd
  
  static belongsTo = ['pci', PackageContentItem]
  
  static mapping = {
    id column:'emb_id', generator: 'uuid2', length:36
    movingWallStart column:'emb_start_fk'
    movingWallEnd column:'emb_end_fk'
  }
  
  static constraints = {
    movingWallStart nullable: true
    movingWallEnd nullable: true
  }
  
  public static final String REGEX = /^(((P|R)\d+(D|M|Y))|(R\d+(D|M|Y);P\d+(D|M|Y)))$/
  
  public static final Embargo parse( String embargoString ) {
    
    // Fail fast if null or invalid formatting.
    if (!(embargoString && embargoString =~ REGEX)) return null
    
    String[] statements = embargoString?.split(/\;/)
    
    Embargo embargo = new Embargo()
    for (String statement : statements) {
      EmbargoStatement stmt = EmbargoStatement.parse(statement)
      if (stmt.type == EmbargoStatement.Type.R) {
        embargo.movingWallStart = stmt
      } else {
        embargo.movingWallEnd = stmt
      }
    }
    embargo
  }
  
  String toString() {
    String val = movingWallStart ? "${movingWallStart}" : ""
    val += movingWallEnd ? (val ? ';' : '') + "${movingWallEnd}" : ''
    val
  }
}
