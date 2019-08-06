#!/bin/bash
if test $# -gt 0 ; then
  curl --header "X-Okapi-Tenant: diku" http://localhost:8080/erm/sas/$1/export -X GET
else
  curl --header "X-Okapi-Tenant: diku" http://localhost:8080/erm/export/index?stats=true -X GET
fi
