package org.olf.kb;

/**
 * KBCacheUpdater. Adapter pattern - allowing this module to aggregate KB data from multiple external sources.
 *
 * The domain model of this app can be thought of as a local cache of multiple external knowledgebases (KBs)
 * the term KB is problematic, as it can refer in turn to
 *     The "KB" of everything thats been published, by whom and when, including title histories and other info (Publications KB?)
 *     The "KB" of how publications are packaged and bundled for sale by content providers (Packaging KB?)
 *     The "KB" of what I think I have bought (What titles, coverage, terms), from where and for how long (PCA)
 *
 * This interface defines interface which a class must implement in order to collect information from a remote KB
 * and update the local KB Cache.
 *
 * The initial focus will be on the "Packaging KB" - which implies a degree of "Publications KB" functionality.
 *
 * It is anticipated that implementations of this adapter will exist at least for
 *
 *    * A Mock source for tests
 *    * the GOKb system
 *    * EBSCO KB
 *    * Perhaps KB+ as it's a useful test source for k-int
 *
 */
public interface KBCacheUpdater {

  /**
   * freshen the cache - Make calls to the KBCache based on DELTA information about what has changed
   * in the remote KB.
   *
   * @param source_id ID of the remote source
   * @param source_name Name of the remote source
   * @param cursor A Map containing implementation specific data that allows the updater to "Know where it is"
   * @param cache The targer for the update
   *
   */
  public void freshen(String source_id, String source_name, Object cursor, KBCache cache);


}
