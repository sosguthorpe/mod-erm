#!/bin/sh

# Create the tenant
curl --header "X-Okapi-Tenant: diku" http://localhost:8080/_/tenant -X POST

# Prepolpulate with data.
curl --header "X-Okapi-Tenant: diku" -X POST -F package_file=@../service/src/integration-test/resources/packages/apa_1062.json http://localhost:8080/admin/loadPackage

# Create an agreement
curl --header "X-Okapi-Tenant: diku" -H "Content-Type: application/json" -X POST http://localhost:8080/sas -d '
{
  name: "A new agreement"
}
'

# List agreements
AGREEMENT_ID=`curl --header "X-Okapi-Tenant: diku" http://localhost:8080/sas -X GET | jq ".[0].id"`

# List packages
PACKAGE_ID=`curl --header "X-Okapi-Tenant: diku" http://localhost:8080/packages -X GET | jq ".[0].id"`

curl --header "X-Okapi-Tenant: diku" -H "Content-Type: application/json" -X POST http://localhost:8080/sas/$AGREEMENT_ID/addToAgreement -d ' {
  content:[
    { "type":"package", "id": "'"$PACKAGE_ID"'" }
  ]
}
'


# If all goes well, you'll get a status message back. After that, try searching your subscribed titles:
curl --header "X-Okapi-Tenant: diku" http://localhost:8080/content -X GET

# Or try the codex interface instead
#curl --header "X-Okapi-Tenant: diku" http://localhost:8080/codex-instances -X GET

# Pull an ID from that record and ask the codex interface for some details
#RECORD_ID="ff80818162a5e9600162a5e9ef63002f"
#curl --header "X-Okapi-Tenant: diku" http://localhost:8080/codex-instances/$RECORD_ID -X GET
