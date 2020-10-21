package org.olf.general

import org.olf.general.StringTemplate

import com.k_int.okapi.OkapiTenantAwareController

import grails.gorm.multitenancy.CurrentTenant
import groovy.util.logging.Slf4j


@Slf4j
@CurrentTenant
class StringTemplateController extends OkapiTenantAwareController<StringTemplate> {
  StringTemplateController()  {
    super(StringTemplate)
  }
}
