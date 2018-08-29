#!/bin/bash

# RESOURCE_LIST=`curl --header "X-Okapi-Tenant: diku" "http://localhost:9130/kiwt/config" -X GET`
# echo Got resource list $RESOURCE_LIST

for resource in packageContentItem pkg remoteKB subscriptionAgreement titleInstance
do
  printf "\n\nDocumenting ERM Resource : $resource"
  curl --header "X-Okapi-Tenant: diku" "http://localhost:9130/kiwt/config/schema/$resource" -X GET
done
