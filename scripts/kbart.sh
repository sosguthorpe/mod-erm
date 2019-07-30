#!/bin/bash
if test $# -gt 0 ; then
  curl --header "X-Okapi-Tenant: diku" http://localhost:8080/erm/sas/$1/export/kbart -X GET -v
else
  curl --header "X-Okapi-Tenant: diku" http://localhost:8080/erm/export/kbart -X GET -v
fi
