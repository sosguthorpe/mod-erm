package org.olf.kb;

/**
 * KBCache. the interface into the KB cache that adapters will call to notify changes
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
 */
public interface KBCache {

}
