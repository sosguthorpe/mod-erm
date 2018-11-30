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
    DetachedCriteria result = new DetachedCriteria(cfg.baseEntity);

   // result has a java.util.List<Criterion>  criteria

    CQLNode cql_root = parseCQL(query);
    log.debug("Result of parse: ${cql_root.toCQL()}");

    Map builder_context = [
      requiredAliases:[]
    ]

    visit(cfg, 0, cql_root, builder_context, result);

    log.debug("At end ${builder_context}");

    return result;
  }

  private void visit(Map cfg, int depth, CQLNode node, Map builder_context, Criteria parent) {

    log.debug("${INDENT*depth}${depth} ${node.class.name} ${parent?.class.name}");
    switch(node.class) {
      case CQLSortNode:
        visit(cfg, depth+1,((CQLSortNode)node).getSubtree(), builder_context, parent)
        break;
      case CQLBooleanNode:
        handleBoolean(cfg, depth+1,(CQLBooleanNode)node, builder_context, parent);
        break;
      case CQLTermNode:
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
}
