#!/bin/bash

# This script is for executing a double check that the endpoint calls that work directly
# also work via okapi.

# Work in progress - needs input from steve

echo Call mod-agreements through okapi

curl --header "X-Okapi-Tenant: diku" http://localhost:9130/erm/content -X GET

curl --header "X-Okapi-Tenant: diku" http://localhost:9130/erm/pci?page=1&perPage=10&stats=true -X GET
curl --header "X-Okapi-Tenant: diku" http://localhost:8080/erm/pci?page=1&perPage=10&stats=true -X GET

curl --header "X-Okapi-Tenant: diku" http://localhost:8080/erm/packages?page=1&perPage=10&stats=true -X GET

