package org.olf.general

import com.ibm.icu.lang.UCharacter
import com.ibm.icu.text.Normalizer2

import grails.gorm.MultiTenant

class RefdataValue implements MultiTenant<RefdataValue> {
  
  private static final Normalizer2 normalizer = Normalizer2.NFKDInstance

  String id
  String value
  String label

  static belongsTo = [
    owner:RefdataCategory
  ]

  static mapping = {
    id column: 'rdv_id', generator: 'uuid', length:36
    version column: 'rdv_version'
    owner column: 'rdv_owner', index:'rdv_entry_idx'
    value column: 'rdv_value', index:'rdv_entry_idx'
    label column: 'rdv_label'
  }

  static constraints = {
    label (nullable: false, blank: false)
    value (nullable: false, blank: false)
    owner (nullable: false, blank: false)
  }
  
  public static String normValue ( String string ) {
    // Remove all diacritics and substitute for compatibility
    normalizer.normalize( string.trim() ).replaceAll(/\p{M}/, '').replaceAll(/\s+/, '_').toLowerCase()
  }
  
  private static String tidyLabel ( String string ) {
    UCharacter.toTitleCase( string.trim(), null ).replaceAll(/\s{2,}/, ' ')
  }
  
  void setValue (String value) {
    this.value = normValue( value )
  }
  
  void setLabel (String label) {
    this.label = tidyLabel( label )
  }
  
  /**
   * Lookup or create a RefdataValue
   * @param category_name
   * @param value
   * @return
   */
  static <T extends RefdataValue> T lookupOrCreate(final String category_name, final String label, final String value=null, Class<T> clazz = this) {
    final RefdataCategory cat = RefdataCategory.findOrCreateByDesc(category_name).save(flush:true, failOnError:true)
    
    final String norm_value = normValue( value ?: label )
    
    T result = clazz.findByOwnerAndValue(cat, norm_value)
    
    if (!result) {
      result = clazz.newInstance()
      result.label = label
      result.value = norm_value
      result.owner = cat
      result.save(flush:true, failOnError:true)
    }
    result
  }

}
