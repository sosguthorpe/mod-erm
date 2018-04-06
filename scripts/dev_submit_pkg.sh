#!/bin/bash

# curl --header "X-Okapi-Tenant: RSTestTenantA" http://localhost:8080/hello/index -X GET
# curl --header "X-Okapi-Tenant: RSTestTenantA" http://localhost:8080/_/tenant -X POST

# Create DevTeanantA
curl --header "X-Okapi-Tenant: DevTenantA" http://localhost:8080/_/tenant -X POST
curl --header "X-Okapi-Tenant: DevTenantA" -X POST -F package_file=@../service/src/integration-test/resources/packages/apa_1062.json http://localhost:8080/admin/loadPackage
