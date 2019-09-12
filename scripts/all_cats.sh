#!/bin/bash

ALL_CATS=`curl -s --header "X-Okapi-Tenant: diku" http://localhost:8080/erm/refdataValues -X GET`

for cat_desc in `echo $ALL_CATS | jq -c -r '.[] | { desc, id }'` 
do
  CAT_ID=`echo $cat_desc | jq --raw-output '.id'`
  CAT_NAME=`echo $cat_desc | jq --raw-output '.desc'`
  echo $CAT_NAME
  CAT_DEFN=`curl -s --header "X-Okapi-Tenant: diku" http://localhost:8080/erm/refdataValues/$CAT_ID -X GET`
  for value in `echo $CAT_DEFN | jq -c -r '.values[] | { id, value, "label" }'`
  do
    echo $value
  done
done

