#!/bin/bash

curl --header "X-Okapi-Tenant: diku" -H "Content-Type: application/json" -XPOST http://localhost:8080/erm/knowledgebase/triggerCacheUpdate -d'{}'
