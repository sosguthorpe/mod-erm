#!/bin/bash

# curl --header "X-Okapi-Tenant: RSTestTenantA" http://localhost:8080/hello/index -X GET
# curl --header "X-Okapi-Tenant: RSTestTenantA" http://localhost:8080/_/tenant -X POST

# Create DevTeanantA
curl --header "X-Okapi-Tenant: DevTenantA" http://localhost:8080/_/tenant -X POST
curl --header "X-Okapi-Tenant: DevTenantA" -X POST -F package_file=@../service/src/integration-test/resources/packages/apa_1062.json http://localhost:8080/admin/loadPackage

# Create an agreement
curl --header "X-Okapi-Tenant: DevTenantA" -H "Content-Type: application/json" -X POST http://localhost:8080/sas -d '
{
  name: "A new agreement"
}
'


# List agreements
curl --header "X-Okapi-Tenant: DevTenantA" http://localhost:8080/sas -X GET

# List packages
curl --header "X-Okapi-Tenant: DevTenantA" http://localhost:8080/packages -X GET


# Use the list packages link above and get the ID of a package, then substitute it into this command:
AGREEMENT_ID="ff80818162a5e9600162a5ea339c0570"
PACKAGE_ID="ff80818162a5e9600162a5e9ed940002"

curl --header "X-Okapi-Tenant: DevTenantA" -H "Content-Type: application/json" -X POST http://localhost:8080/sas/$AGREEMENT_ID/addToAgreement -d ' {
  content:[
    { "type":"package", "id": "'"$PACKAGE_ID"'" }
  ]
}
'


# If all goes well, you'll get a status message back. After that, try searching your subscribed titles:
curl --header "X-Okapi-Tenant: DevTenantA" http://localhost:8080/content -X GET

# Or try the codex interface instead
curl --header "X-Okapi-Tenant: DevTenantA" http://localhost:8080/codex-instances -X GET

# Pull an ID from that record and ask the codex interface for some details
RECORD_ID="ff80818162a5e9600162a5e9ef63002f"
curl --header "X-Okapi-Tenant: DevTenantA" http://localhost:8080/codex-instances/$RECORD_ID -X GET
