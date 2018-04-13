#!/bin/sh


# jq -r '.name'

# Create the tenant
curl --header "X-Okapi-Tenant: diku" http://localhost:8080/_/tenant -X POST

# Prepolpulate with data.
BSEC_PKG_ID=`curl --header "X-Okapi-Tenant: diku" -X POST -F package_file=@../service/src/integration-test/resources/packages/bentham_science_bentham_science_eduserv_complete_collection_2015_2017_1386.json http://localhost:8080/admin/loadPackage | jq -r ".newPackageId"`

APA_PKG_ID=`curl --header "X-Okapi-Tenant: diku" -X POST -F package_file=@../service/src/integration-test/resources/packages/apa_1062.json http://localhost:8080/admin/loadPackage | jq -r ".newPackageId"`

AGREEMENT_TRIAL_RDV=`curl --header "X-Okapi-Tenant: diku" -H "Content-Type: application/json" -X POST http://localhost:8080/refdataValues/lookupOrCreate -d '
{
  category: "AgreementType",
  value: "TRIAL",
  label: "Trial"
}
' | jq -r ".id"`

AGREEMENT_DRAFT_RDV=`curl --header "X-Okapi-Tenant: diku" -H "Content-Type: application/json" -X POST http://localhost:8080/refdataValues/lookupOrCreate -d '
{
  category: "AgreementType",
  value: "DRAFT",
  label: "Draft"
}
' | jq -r ".id"`

# Create an agreement
TRIAL_AGREEMENT_ID=`curl --header "X-Okapi-Tenant: diku" -H "Content-Type: application/json" -X POST http://localhost:8080/sas -d '
{
  name: "A new agreement (TRIAL)",
  type: { id: "'"$AGREEMENT_TRIAL_RDV"'" }
}
' | jq -r ".id"`

# Create an agreement
DRAFT_AGREEMENT_ID=`curl --header "X-Okapi-Tenant: diku" -H "Content-Type: application/json" -X POST http://localhost:8080/sas -d '
{
  name: "A new agreement (DRAFT)",
  type: { id: "'"$AGREEMENT_DRAFT_RDV"'" }
}
' | jq -r ".id"`

# List agreements
# AGREEMENT_ID=`curl --header "X-Okapi-Tenant: diku" http://localhost:8080/sas -X GET | jq ".[0].id"`
# List packages
# We now get the package back when we load the package above, this is still a cool way to work tho
# PACKAGE_ID=`curl --header "X-Okapi-Tenant: diku" http://localhost:8080/packages -X GET | jq ".[0].id"`

curl --header "X-Okapi-Tenant: diku" -H "Content-Type: application/json" -X POST http://localhost:8080/sas/$TRIAL_AGREEMENT_ID/addToAgreement -d ' {
  content:[
    { "type":"package", "id": "'"$APA_PKG_ID"'" }
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
