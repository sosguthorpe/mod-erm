package org.olf

import grails.gorm.multitenancy.CurrentTenant
import com.k_int.okapi.OkapiTenantAwareController
import org.olf.general.*
import grails.converters.JSON


/**
 * 
 */
@CurrentTenant
class RefdataController extends OkapiTenantAwareController<RefdataValue>  {

  RefdataController() {
    super(RefdataValue)
  }

  /**
   *  @param category
   *  @param value
   *  @param label
   */
  def lookupOrCreate() {
    log.debug("RefdataController::lookupOrCreate(${params})");

    def result = null;

    if ( ( request.JSON?.category != null ) &&
         ( request.JSON?.value != null ) ) {

      def cat = RefdataCategory.findByDesc(request.JSON.category) ?: new RefdataCategory(desc:request.JSON.category).save(flush:true, failOnError:true);
      result = RefdataValue.findByOwnerAndValue(cat, request.JSON.value) ?: new RefdataValue(
                                                                                  owner:cat,
                                                                                  value:request.JSON.value,
                                                                                  label:request.JSON.label,
                                                                                  style:null).save(flush:true, failOnError:true);
    }

    render result as JSON
  }
}

