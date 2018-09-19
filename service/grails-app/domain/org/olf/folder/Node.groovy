package org.olf.folder

import grails.gorm.MultiTenant


/**
 * 
 */
public class Node implements MultiTenant<Node> {

  String id
  String label
  String nodeType  // 'F'older or 'R'eference
  Node parent // If nodeType is folder null parent=root, otherwise parent folder
  String referenceClass // referenced item class
  String referenceId // referenced item id

  static mapping = {
                   id column:'nd_id', generator: 'uuid', length:36
              version column:'nd_version'
                label column:'nd_label'
             nodeType column:'nd_node_type'
               parent column:'nd_parent'
       referenceClass column:'nd_reference_class'
          referenceId column:'nd_reference_id'
            
  }

  static constraints = {
                label(nullable:false, blank:false)
             nodeType(nullable:false, blank:false)
               parent(nullable:false, blank:false)
       referenceClass(nullable:false, blank:false)
          referenceId(nullable:false, blank:false)
  }


}
