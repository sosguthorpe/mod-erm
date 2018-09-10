package org.olf.kb

import grails.gorm.MultiTenant


/**
 * mod-erm representation of a platform - a venue for publishing an electronic resource
 */
public class Platform implements MultiTenant<Platform> {

  String id
  String name

  static mapping = {
                   id column:'pt_id', generator: 'uuid', length:36
              version column:'pt_version'
                 name column:'pt_name'
  }

  static constraints = {
          name(nullable:false, blank:false)
  }

  public static Platform resolve(String url) {
    return resolve(url, null);
  }

  public static Platform resolve(String url, String name) {

    Platform result = null;

    if ( ( url != null ) && ( url.length() > 0 ) ) {
      result = resoveViaURL(url,name)
    }
    else if ( ( name != null ) && ( name.length() > 0 ) ) {
      result = Platform.findByName(name)
    }
    else {
      throw new RuntimeException("Unable to locate platform record unless a name OR url is supplied");
    }

    return result

    

  }

  private static Platform resoveViaURL(String url, String plat_name) {

    Platform result = null;
    
    def parsed_url = new java.net.URL(url);
    def platform_host = parsed_url.getHost()

    // Work out what the domain is - So link.springer.com -> springer.com. We do this so that an admminstrator can manually add a domain
    // which can catch multiple child records. This must be done at the discretion of an admin, not automatically.
    def platform_domain = platform_host.substring(platform_host.indexOf('.'), platform_host.length());

    // Can we get a direct match?
    def matching_platforms = PlatformLocator.executeQuery('select distinct p.owner from PlatformLocator as p where p.domainName = :host',[host:platform_host]);

    // This is exploratory - Investigating the idea that we can have the code that applies a pattern of a register of resolvers that use different
    // strategies to try and locate a domain object given (Probably increasingly fuzzy) match criteria, and maybe adding filters later on.
    def platform = applyResolverStrategy(
      [ 
        { ctx -> Platform.executeQuery('select distinct p.owner from PlatformLocator as p where p.domainName = :host',[host:ctx.host]) },
        { ctx -> Platform.executeQuery('select distinct p.owner from PlatformLocator as p where p.domainName = :host',[host:ctx.domain]) }
      ],
      [ host: platform_host, domain: platform_domain]
    );

    if ( platform) {
      result = platform;
    }
    else {
      result = new Platform(name: ( plat_name ?: platform_host ) ).save(flush:true, failOnError:true);
      PlatformLocator pl = new PlatformLocator(owner:result, domainName:platform_host).save(flush:true, failOnError:true);
    }

    return result;
  }

  /**
   * apply a list of resolvers to an context to see if we can locate a record.
   *
   * @param strategy - an array of closures capable of locating an item
   * @param context - 
   * strategy - [
   *     { ctx -> a closure that tries to lookup a list based on ctx },
   *     { ctx -> a closure that tries to lookup a list based on ctx },
   *     { ctx -> a closure that tries to lookup a list based on ctx }
   *   ]
   *
   *
   * ]
   */
  public static applyResolverStrategy(strategy, context) {

    def result = null;
    def primary_resolver_iterator = strategy.iterator();

    while ( ( result == null ) && ( primary_resolver_iterator.hasNext() ) )  {

      def next_resolver = primary_resolver_iterator.next()

      // A resolver must return a list of matched objects
      def interim_result = next_resolver.call(context)

      switch ( interim_result.size() ) {
        case 0:
          result = null;
          break;
        case 1:
          result = interim_result.get(0);
          break;
        default:
          result = null;
          break;
      }
    }

    return result;
  }


}
