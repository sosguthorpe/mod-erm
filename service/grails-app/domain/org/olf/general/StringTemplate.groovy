package org.olf.general

import uk.co.cacoethes.handlebars.HandlebarsTemplateEngine
import com.github.jknack.handlebars.Handlebars

import com.github.jknack.handlebars.helper.StringHelpers
import com.github.jknack.handlebars.EscapingStrategy
import org.olf.general.StringTemplateHelpers

import grails.gorm.MultiTenant

import com.k_int.web.toolkit.refdata.Defaults
import com.k_int.web.toolkit.refdata.RefdataValue

class StringTemplate implements MultiTenant<StringTemplate> {

  String id
  String name
  String rule

  @Defaults(['urlProxier', 'urlCustomiser'])
  RefdataValue context

/*   public final enum Context {
    urlProxier,
    urlCustomiser
  }
  Context context */



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
    idScopes cascade: 'all-delete-orphan', joinTable: [name: 'string_template_scopes', key: 'string_template_id', column: 'id_scope']
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




}
