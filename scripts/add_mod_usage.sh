#!/bin/bash

echo 'Fetching the latest module'
USAGE_DATA=`curl -sSL "http://folio-registry.aws.indexdata.com/_/proxy/modules?latest=1&provide=usage-data-providers" | jq -rc '.[0].id'`
USAGE_DATA_DESC=`curl -sSL "http://folio-registry.aws.indexdata.com/_/proxy/modules?latest=1&filter=${USAGE_DATA}&full=true" | jq -rc '.[0]'`

echo 'Register with okapi and ask it to deploy in the VM'
curl -XPOST -sSL 'http://localhost:9130/_/proxy/modules' -d "${USAGE_DATA_DESC}"
curl -XPOST -sSL 'http://localhost:9130/_/proxy/tenants/diku/install?deploy=true&tenantParameters=loadSample%3Dtrue,loadReference%3Dtrue' -d `echo $USAGE_DATA_DESC | jq -c '[{id: .id, action: "enable"}]'`

# git clone https://github.com/folio-org/mod-erm-usage
# 
# pushd mod-erm-usage
# 
# for filename in sample-data/vendor-storage/vendors/*.txt; do
#     for ((i=0; i<=3; i++)); do
#         ./MyProgram.exe "$filename" "Logs/$(basename "$filename" .txt)_Log$i.txt"
#     done
# done
