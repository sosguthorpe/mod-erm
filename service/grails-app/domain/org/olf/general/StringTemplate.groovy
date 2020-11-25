package org.olf.general

import uk.co.cacoethes.handlebars.HandlebarsTemplateEngine
import com.github.jknack.handlebars.Handlebars

import com.github.jknack.handlebars.helper.StringHelpers
import com.github.jknack.handlebars.EscapingStrategy
import org.olf.general.StringTemplateHelpers

import grails.gorm.MultiTenant

import com.k_int.web.toolkit.refdata.Defaults
import com.k_int.web.toolkit.refdata.RefdataValue
import com.k_int.web.toolkit.databinding.BindImmutably

class StringTemplate implements MultiTenant<StringTemplate> {

  String id
  String name
  String rule

  Date dateCreated
  Date lastUpdated

  @Defaults(['urlProxier', 'urlCustomiser'])
  RefdataValue context

  /*
   * The useage of this list will depend somewhat on context,
   * but the general idea is that this captures the 'scope' of a translation.
   * For certain contexts this list will act as a 'allowlist', for others as a 'denylist'
   */
  @BindImmutably
  Set<String> idScopes = []
  static hasMany = [idScopes: String]


  static mapping = {
    id column:'strt_id', generator: 'uuid2', length:36
    name column:'strt_name'
    rule column:'strt_rule'
    context column:'strt_context'
    dateCreated column: 'strt_date_created'
    lastUpdated column: 'strt_last_updated'
    idScopes cascade: 'all-delete-orphan', joinTable: [name: 'string_template_scopes', key: 'string_template_id', column: 'id_scope']
  }

  static constraints = {
    rule(validator: {rule, obj ->
      return obj.checkValidTemplate()
    })
  }

  String customiseString(Map binding) {

    // Set up handlebars configuration

    EscapingStrategy noEscaping = new EscapingStrategy() {
      public String escape(final CharSequence value) {
        return value.toString()
      }
    };

    def handlebars = new Handlebars().with(noEscaping)
    
    handlebars.registerHelpers(StringHelpers)
    handlebars.registerHelpers(StringTemplateHelpers)
    def engine = new HandlebarsTemplateEngine()
    engine.handlebars = handlebars


    String outputString = ''
    def template = engine.createTemplate(rule).make(binding)
    StringWriter sw = new StringWriter()
    template.writeTo(sw)
    outputString = sw.toString()

    return outputString
  }

  public ArrayList<String> checkValidTemplate() {
    String output
    try {
      output = this.customiseString([
        inputUrl: "testing",
        platformLocalCode: "12345"
      ])
    } catch (Exception e) {
      return ["invalidTemplate", e.message]
    }
    if (output && output?.length() > 0) {
      return null
    } else {
      return ["invalidTemplate", "Output is null or empty"]
    }
  }


}
