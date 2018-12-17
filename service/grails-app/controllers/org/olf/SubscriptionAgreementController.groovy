package org.olf

import grails.gorm.multitenancy.CurrentTenant
import groovy.util.logging.Slf4j
import com.k_int.okapi.OkapiTenantAwareController
import org.olf.erm.SubscriptionAgreement
import org.olf.erm.SubscriptionAgreementDataService
import org.olf.erm.SubscriptionAgreementService
import org.olf.erm.Entitlement
import org.olf.kb.Pkg
import org.olf.kb.PackageContentItem
import org.olf.kb.PlatformTitleInstance
import grails.converters.JSON


/**
 * Control access to subscription agreements.
 * A subscription agreement (SA) is the connection between a set of resources (Which could be packages or individual titles) and a license. 
 * SAs have start dates, end dates and renewal dates. This controller exposes functions for interacting with the list of SAs
 */
@Slf4j
@CurrentTenant
class SubscriptionAgreementController extends OkapiTenantAwareController<SubscriptionAgreement>  {
  
  // Data service
  SubscriptionAgreementDataService subscriptionAgreementDataService

  SubscriptionAgreementController() {
    super(SubscriptionAgreement)
  }
  
  def resources (String subscriptionAgreementId) {
    subscriptionAgreementService.resourcesFor (subscriptionAgreementId)
  }
}

