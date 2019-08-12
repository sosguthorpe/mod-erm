#!/bin/sh

curl -H 'X-OKAPI-TENANT: diku' -H 'Content-Type: application/json' -XPOST 'http://localhost:8080/erm/packages/import' -d '@../service/src/integration-test/resources/packages/mod-agreement-package-import-sample.json'
