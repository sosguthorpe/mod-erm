package org.olf

import grails.gorm.transactions.Transactional 
import org.hibernate.Hibernate
import org.hibernate.sql.JoinType 
import org.olf.kb.* 
import org.olf.erm.* 
// uncomment these when code for ERM-215 is ready
// import org.olf.export.KBartExport
// import org.olf.export.ExportConstants


/**
 * 
 */
@Transactional
public class ExportService { 
	
  private List<TitleInstance> titles() { 
	  List<TitleInstance> titles = TitleInstance.entitled.list()
	  return titles 
  }
  
  private String kbart() {
	  /*
	   The exportshould include:
	  
	  Agreement name (desirable, not required)
	  Agreement ID (desirable, not required)
	  Title
	  Title Identifiers (all known identifiers for a title including ISSN, eISSN, EZB, ZDB,...)
	  Coverage information for a title (start date, start volume, start issue, end date, end volume, end issue) (note that a single title can have multiple coverage statements, all coverage information should be included in the JSON)
	  URL for title in agreement
	  Publisher
	  E-resource type
	  Platform
	  Package
	  */
	  
  }

}
