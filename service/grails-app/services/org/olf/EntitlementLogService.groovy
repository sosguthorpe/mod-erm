package org.olf

import org.olf.dataimport.internal.PackageSchema
import org.olf.erm.Entitlement
import org.olf.kb.ContentActivationRecord
import org.olf.kb.KBCache
import org.olf.kb.KBCacheUpdater
import org.olf.kb.PlatformTitleInstance
import org.olf.kb.RemoteKB
import org.springframework.transaction.TransactionDefinition

/**
 * This service turns the declarative set of entitlements which reprsent the list of available titles
 * at a point-in-time (today) into a log that can be read in sequence with a cursor to get changes since
 * a last sequence-id. This service detects and records the delta of the list of active titles from one
 * moment (day) to the next enabling consuming services to say "Tell me what titles have become available
 * or been removed since I last asked on X"
 */
public class EntitlementLogService {

  // This HQL is based firmly on the query in ExportService. Changes there need to be reviewed and may
  // need to be applied here. This should be refactored into a single static repo of queries.
  private static final String NEW_ENTITLEMENTS_QUERY = '''
      SELECT res, pkg_ent, direct_ent
        FROM ErmResource as res
          LEFT JOIN res.entitlements as direct_ent
            ON (
              (direct_ent.activeTo IS NULL OR direct_ent.activeTo >= :today)
                AND
                 (direct_ent.activeFrom IS NULL OR direct_ent.activeFrom  <= :today)
            )
          LEFT JOIN res.pkg as pkg

            ON (res.class = PackageContentItem
              AND (
                  (res.accessEnd IS NULL OR res.accessEnd >= :today)
                AND
                  (res.accessStart IS NULL OR res.accessStart <= :today)
              )
            )
            LEFT JOIN pkg.entitlements as pkg_ent
              ON (
                (pkg_ent.activeTo IS NULL OR pkg_ent.activeTo >= :today)
                  AND
                   (pkg_ent.activeFrom IS NULL OR pkg_ent.activeFrom  <= :today)
              )
        WHERE (
          (
            direct_ent.owner IS NOT NULL
            AND
            res.class != Pkg
          ) OR (
            pkg_ent.owner IS NOT NULL
          )
        ) AND (
          NOT EXISTS ( SELECT ele 
                         FROM EntitlementLogEntry as ele 
                        WHERE ele.res = res 
                          AND ele.direct_ent=direct_ent 
                          AND ele.pkg_ent = pkg_ent
                          AND ele.endDate is null ) 
        )
       
   '''

  def triggerUpdate() {
    log.debug("EntitlementLogService::triggerUpdate()");
    return "OK"
  }

}
