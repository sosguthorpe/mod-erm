package com.k_int.hibernate_search;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver

import grails.gorm.multitenancy.Tenants
import groovy.transform.CompileStatic

@CompileStatic
public class TenantResolver implements CurrentTenantIdentifierResolver {

  @Override
  public String resolveCurrentTenantIdentifier() {
    Tenants.CurrentTenant?.get()
  }

  @Override
  public boolean validateExistingCurrentSessions() {
    return false;
  }
  
}
