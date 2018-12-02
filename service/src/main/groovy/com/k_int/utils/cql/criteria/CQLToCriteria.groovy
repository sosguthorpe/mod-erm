package com.k_int.utils.cql.criteria;

import grails.gorm.DetachedCriteria;
import org.z3950.zing.cql.*
import groovy.util.logging.Log4j
import org.hibernate.criterion.*
import org.grails.datastore.mapping.query.api.Criteria

/*
 * See: https://docs.jboss.org/hibernate/orm/3.3/reference/en/html/querycriteria.html
 *
 * The CFG object is a groovy map, consisiting of
 * [
 *   baseEntity: (Class)BaseEntity
 * ]
 */
@Log4j
class CQLToCriteria {

  private static String INDENT = '   ';

  DetachedCriteria build(Map cfg, String query) {

   // Criteria criteria = getSession().createCriteria(Table_a.class);
   // https://stackoverflow.com/questions/12078960/hibernate-criteria-api-joining-tables-with-custom-join

    DetachedCriteria result = new DetachedCriteria(cfg.baseEntity);

   // result has a java.util.List<Criterion>  criteria

    CQLNode cql_root = parseCQL(query);
    log.debug("Result of parse: ${cql_root.toCQL()}");

    Map builder_context = [
      requiredAliases:[]
    ]

    // Walk the CQL tree, creating and decorating a tree of Criteria objects as we go
    visit(cfg, 0, cql_root, builder_context, result);

    List required_aliases = generateRequiredAliases(builder_context.requiredAliases, cfg);

    required_aliases.each { al ->
      log.debug("Adding ${al.parent ? al.parent + '.' : ''}${al.prop} ${al.alias}");
      result.createAlias("${al.parent ? al.parent + '.' : ''}${al.prop}", al.alias) // , al.type ?: org.hibernate.sql.JoinType.INNER_JOIN )
    }

    log.debug("At end ${builder_context} ${required_aliases}");

    return result;
  }

  private void visit(Map cfg, int depth, CQLNode node, Map builder_context, Criteria parent) {

    log.debug("${INDENT*depth}${depth} ${node.class.name} ${parent?.class.name}");
    switch(node.class) {
      case CQLSortNode:
        // Push on to main query tree
        visit(cfg, depth+1,((CQLSortNode)node).getSubtree(), builder_context, parent)
        break;
      case CQLBooleanNode:
        // Recurse into subtrees
        handleBoolean(cfg, depth+1,(CQLBooleanNode)node, builder_context, parent);
        break;
      case CQLTermNode:
        // use the config to add any dependent nodes
        handleTerm(cfg, depth+1,(CQLTermNode)node, builder_context, parent);
        break;
      default:
        log.debug("${INDENT*depth}Unhandled node type: ${node.class.name}");
        break;
    }
  }

  // See: https://github.com/indexdata/cql-java/blob/master/src/main/java/org/z3950/zing/cql/CQLTermNode.java
  private handleTerm(Map cfg, int depth, CQLTermNode node, Map builder_context, Criteria parent) {
    log.debug((INDENT*depth)+"handle term ${node.getIndex()} ${node.getTerm()}");
    String index = node.getIndex()
    String term = node.getTerm()

    def index_config = cfg.indexes[index]
    if ( index_config ) {
      if ( index_config.requiredAliases )
        builder_context.requiredAliases.addAll(index_config.requiredAliases)

      // Call the index_config criteria closure using the parent criteria and the term we are seeking
      index_config.criteria(parent, term)
    }
    else {
      throw new RuntimeException("Unable to locate config for index ${index}");
    }
  }

  private handleBoolean(Map cfg, int depth, CQLBooleanNode node, Map builder_context, Criteria parent) {

    Criteria bool_crit = null;

    switch ( node.getOperator() ) {
      case 'AND':
        bool_crit = parent.and {
          visit(cfg, depth+1,node.getLeftOperand(), builder_context, delegate);
          visit(cfg, depth+1,node.getRightOperand(), builder_context, delegate);
        }
        break;
      case 'OR':
        bool_crit = parent.or {
          visit(cfg, depth+1,node.getLeftOperand(), builder_context, delegate);
          visit(cfg, depth+1,node.getRightOperand(), builder_context, delegate);
        }
        break;
      default:
        throw new RuntimeException('Unhandled operator '+node.getOperator());
    }

  }

  CQLNode parseCQL(String cql) {
    CQLParser parser = new CQLParser();
    CQLNode root = parser.parse(cql);
    return root;
  }

  private List generateRequiredAliases(List required_aliases, Map cfg) {
    // Walk through the tree of namespaces, adding any that are required
    // Visit the leaf nodes first. As leaf nodes are added, they may add their own dependencies
    List result = []

    cfg.associations.each { k,v ->
      addIfRequired(null, k, v, required_aliases, result);
    }
    
    result
  }

  // We go depth first BUT add new aliases to the start of the list
  private boolean addIfRequired(String parent_alias, String prop, Map alias_definition, List required_aliases, List generated_list) {

    boolean result = false;

    log.debug("addIfRequired(${prop},${alias_definition},${required_aliases},${generated_list})");

    alias_definition.children?.each { k,v  ->
      result = result || addIfRequired(alias_definition.alias, k, v, required_aliases, generated_list);
    }

    // The alias we are currently considering is in the required list
    if ( result || required_aliases.contains(alias_definition.alias) ) {
      log.debug("Add ${parent_alias}.${prop} ${alias_definition.alias} ${alias_definition.type}");
      generated_list.add(0,[parent:parent_alias, prop:prop, alias:alias_definition.alias, type:alias_definition.type])
      result = true;
    }

    return result;
  }

}
