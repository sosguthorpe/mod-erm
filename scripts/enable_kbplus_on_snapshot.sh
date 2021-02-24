#!/bin/bash

FOLIO_BASE_URL="https://folio-snapshot-okapi.dev.folio.org"
TENANT="diku"
# https://folio-snapshot-okapi.dev.folio.org/erm/sas?filters=agreementStatus.value%3D%3Dactive%7C%7CagreementStatus.value%3D%3Ddraft%7C%7CagreementStatus.value%3D%3Din_negotiation%7C%7CagreementStatus.value%3D%3Drequested&match=name&match=alternateNames.name&match=description&perPage=100&sort=name%3Basc&stats=true&term=a

FOLIO_AUTH_TOKEN=`./okapi-login -o "https://folio-snapshot-okapi.dev.folio.org" -u diku_admin -p admin -t diku`

echo $FOLIO_AUTH_TOKEN


RS_KBPLUS_ID=`curl -H "X-Okapi-Tenant: diku" \
                   -H "Content-Type: application/json" \
                   -H 'accept: application/json' \
                   --connect-timeout 5 --max-time 30 \
                   -H "X-Okapi-Token: ${FOLIO_AUTH_TOKEN}" \
                   --http1.1 \
                   -X POST https://folio-snapshot-okapi.dev.folio.org/erm/kbs -d '{
  name:"KB+",
  type:"org.olf.kb.adapters.KIJPFAdapter",
  cursor:null,
  uri:"https://www.kbplus.ac.uk/kbplus7/publicExport/idx",
  listPrefix:null,
  fullPrefix:null,
  principal:null,
  credentials:null,
  rectype:"1",
  active:false,
  supportsHarvesting:true
}
'`

echo Result: $RS_KBPLUS_ID
