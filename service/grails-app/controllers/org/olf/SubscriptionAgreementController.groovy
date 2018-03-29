package org.olf

import grails.gorm.multitenancy.CurrentTenant
import com.k_int.okapi.OkapiTenantAwareController
import org.olf.erm.SubscriptionAgreement


/**
 * Control access to subscription agreements.
 * A subscription agreement (SA) is the connection between a set of resources (Which could be packages or individual titles) and a license. 
 * SAs have start dates, end dates and renewal dates. This controller exposes functions for interacting with the list of SAs
 */
@CurrentTenant
class SubscriptionAgreementController extends OkapiTenantAwareController<SubscriptionAgreement>  {

  SubscriptionAgreementController() {
    super(SubscriptionAgreement)
  }
}

