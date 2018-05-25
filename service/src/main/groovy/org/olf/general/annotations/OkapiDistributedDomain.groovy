package org.olf.general.annotations;

import java.lang.annotation.ElementType
import java.lang.annotation.Target
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy


/**
 * This annotation declares an Okapi Distributed Domain Object - an object which is a first class
 * citizen in the domain driven model for this module BUT for whom control resides in an external
 * service. 
 *
 * Other application services will deal with looking up from the remote service, and creating entries
 * in the domain table for the remote item. This annotation marks the domain class so that those services
 * can produce the right kind of interface and take the right actions -- IE using the remote services
 * as a lookup source, and NOT the internal table, but using the internal table for participation in
 * join queries etc.
 *
 */
@Target( ElementType.TYPE )
@Retention(RetentionPolicy.RUNTIME)     
public @interface OkapiDistributedDomain {

  /** 
   * Information for services to know where to look for the particular kinds of thing. The config
   * may point to many services, and need to aggregate results.
   */
  String config()

}

