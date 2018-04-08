package org.olf

import grails.gorm.multitenancy.CurrentTenant
import com.k_int.okapi.OkapiTenantAwareController
import org.olf.erm.SubscriptionAgreement
import org.olf.erm.AgreementLineItem
import org.olf.kb.Package
import org.olf.kb.PackageContentItem
import org.olf.kb.PlatformTitleInstance
import grails.converters.JSON


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

  def addToAgreement() {
    log.debug("SubscriptionAgreementController::addToAgreement(${params})");
    def result = [status:[]]

    SubscriptionAgreement sa = SubscriptionAgreement.get(params.subscriptionAgreementId);

    if ( sa != null ) {
      int lineno=0;
      // We should be passed a json document containing a content list which consists of maps of type: and id: entries
      request.JSON.content.each { content_item ->
        switch ( content_item.type ) {
          case 'package':
            Package pkg = Package.get(content_item.id);
            if ( pkg != null ) {
              log.debug("Adding package ${pkg} to agreement ${sa}");
              AgreementLineItem ali = new AgreementLineItem(pkg:pkg, owner:sa, enabled:Boolean.TRUE).save(flush:true, failOnError:true);
              result.status.add([message:"Line ${lineno} - added ${pkg} - line item id is ${ali.id}"])
            }
            break;
          case 'packageItem':
            PackageContentItem pci = PackageContentItem.get(content_item.id);
            if ( pci != null ) {
              log.debug("Adding title from package ${pci} to agreement ${sa}");
              AgreementLineItem ali = new AgreementLineItem(pci:pci, owner:sa, enabled:Boolean.TRUE).save(flush:true, failOnError:true);
              result.status.add([message:"Line ${lineno} - added ${pci} - line item id is ${ali.id}"])
            }
            break;
          case 'platformTitle':
            PlatformTitleInstance pti = PlatformTitleInstance.get(content_item.id);
            if ( pti != null ) {
              log.debug("Adding title ${pti} to agreement ${sa}");
              AgreementLineItem ali = new AgreementLineItem(pti:pti, owner:sa, enabled:Boolean.TRUE).save(flush:true, failOnError:true);
              result.status.add([message:"Line ${lineno} - added ${pti} - line item id is ${ali.id}"])
            }
            break;
          default:
            log.warn("unhandled content type. ${content_item}");
        }
        lineno++
      }
    }

    render result as JSON
  }
}

