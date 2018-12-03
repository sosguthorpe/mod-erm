package com.k_int.utils.cql.criteria;

import grails.gorm.DetachedCriteria;
import org.z3950.zing.cql.*
import groovy.util.logging.Log4j
import org.hibernate.criterion.*
import org.hibernate.criterion.Criterion
// import org.grails.datastore.mapping.query.api.Criteria
import grails.orm.HibernateCriteriaBuilder;
import org.hibernate.criterion.Restrictions;


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

  Object list(Map cfg, String cql_query_string, Map args) {

    log.debug("CQLToCriteria::build(${cfg},${cql_query_string},${args})");

   // Criteria criteria = getSession().createCriteria(Table_a.class);
   // https://stackoverflow.com/questions/12078960/hibernate-criteria-api-joining-tables-with-custom-join

    // DetachedCriteria result = new DetachedCriteria(cfg.baseEntity);
    HibernateCriteriaBuilder hb = cfg.baseEntity.createCriteria()

   // result has a java.util.List<Criterion>  criteria

    CQLNode cql_root = parseCQL(cql_query_string);
    log.debug("Result of parse: ${cql_root.toCQL()}");

    Map builder_context = [
      requiredAliases:[]
    ]

    // Walk the CQL tree, creating and decorating a tree of Criteria objects as we go
    Criterion built_criteria = visit(cfg, 0, cql_root, builder_context);

    // Work out what alias definitions are required, Only add in those aliases that our where clauses need
    List required_aliases = generateRequiredAliases(builder_context.requiredAliases, cfg);

    // Execute our search closure using the builder and return the query result
    return hb.list(args) { 
      // Actually add the aliases
      required_aliases.each { al ->
        createAlias("${al.parent ? al.parent + '.' : ''}${al.prop}".toString(), al.alias, al.type ?: org.hibernate.sql.JoinType.INNER_JOIN )
      }
      
      criteria.add(built_criteria)
    };
  }

  private Criterion visit(Map cfg, int depth, CQLNode node, Map builder_context) {

    Criterion result = null;

    log.debug("${INDENT*depth}${depth} ${node.class.name}");
    switch(node.class) {
      case CQLSortNode:
        // Push on to main query tree
        result = visit(cfg, depth+1,((CQLSortNode)node).getSubtree(), builder_context);
        break;
      case CQLBooleanNode:
        // Recurse into subtrees
        result = handleBoolean(cfg, depth+1,(CQLBooleanNode)node, builder_context);
        break;
      case CQLTermNode:
        // use the config to add any dependent nodes
        result = handleTerm(cfg, depth+1,(CQLTermNode)node, builder_context);
        break;
      default:
        log.debug("${INDENT*depth}Unhandled node type: ${node.class.name}");
        break;
    }

    return result;
  }

  // See: https://github.com/indexdata/cql-java/blob/master/src/main/java/org/z3950/zing/cql/CQLTermNode.java
  private Criterion handleTerm(Map cfg, int depth, CQLTermNode node, Map builder_context) {

    Criterion result = null;

    log.debug((INDENT*depth)+"handle term ${node.getIndex()} ${node.getTerm()}");
    String index = node.getIndex()
    String term = node.getTerm()

    def index_config = cfg.indexes[index]
    if ( index_config ) {
      if ( index_config.requiredAliases )
        builder_context.requiredAliases.addAll(index_config.requiredAliases)

      // Call the index_config criteria closure using the parent criteria and the term we are seeking
      log.debug((INDENT*depth)+"Calling closure to add required criteria");
      result = index_config.criteria(term)
    }
    else {
      throw new RuntimeException("Unable to locate config for index ${index}");
    }

    return result;
  }

  private Criterion handleBoolean(Map cfg, int depth, CQLBooleanNode node, Map builder_context) {
    log.debug("Handle boolean");

    Criterion result = null;

    Criterion lhs = visit(cfg, depth+1,node.getLeftOperand(), builder_context);
    Criterion rhs = visit(cfg, depth+1,node.getRightOperand(), builder_context);

    log.debug("handleBoolean:: ${node.getOperator()} ${lhs} ${rhs}");

    switch ( node.getOperator() ) {
      case 'AND':
        result = Restrictions.and (lhs,rhs);
        break;
      case 'OR':
        result = Restrictions.or (lhs,rhs);
        break;
      default:
        throw new RuntimeException('Unhandled operator '+node.getOperator());
    }
    return result;
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
