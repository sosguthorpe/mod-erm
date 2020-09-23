
package org.olf.general

/* 
 * This class is ONLY for use as a means to use HQL rather than SQL to delete such links
*/
class SupplementaryDocumentLink implements Serializable{
    String saKey;
    String daKey;
    static mapping = {
          table "subscription_agreement_supp_doc"
          id composite:['saKey', 'daKey']
          saKey column: 'sasd_sa_fk'
          daKey column: 'sasd_da_fk'
    }
}