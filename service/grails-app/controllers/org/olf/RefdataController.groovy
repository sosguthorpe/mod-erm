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
   *  lookupOrCreate - Lookup or create a revdata value in a specific category. 
   *  If the value already exists return it, if not, create it and return the newly minted refdata value.
   *
   *  @param category - The category in which we wish to create the new value - E.G. AgreementType for Agreement Types
   *  @param value - The actual value to create - E.G. DRAFT for DRAFT Agreements
   *  @param label - The label - E.G. "Draft" as the label for AgreementType::DRAFT
   *
   *  @return JSON record representing the looked up or newly created refdata item 
   *
   *  @see grails-app/views/refdataValue/_refdataValue.gson for the template that controls the JSON document returned
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

