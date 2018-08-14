#!/bin/sh


# jq -r '.name'

JQTEST=`echo '{  "value":"one" }' | jq -r ".value"`

if [ $JQREST="one" ]
then
  echo JQ installed and working
else
  echo Please install JQ
  exit 1
fi

echo Running

# Prepolpulate with data.
echo Loading k-int test package
KI_PKG_ID=`curl --header "X-Okapi-Tenant: diku" -X POST -F package_file=@../service/src/integration-test/resources/packages/simple_pkg_1.json http://localhost:8080/erm/admin/loadPackage | jq -r ".newPackageId"`

echo loading betham science
BSEC_PKG_ID=`curl --header "X-Okapi-Tenant: diku" -X POST -F package_file=@../service/src/integration-test/resources/packages/bentham_science_bentham_science_eduserv_complete_collection_2015_2017_1386.json http://localhost:8080/erm/admin/loadPackage | jq -r ".newPackageId"`

echo Loading APA
APA_PKG_ID=`curl --header "X-Okapi-Tenant: diku" -X POST -F package_file=@../service/src/integration-test/resources/packages/apa_1062.json http://localhost:8080/erm/admin/loadPackage | jq -r ".newPackageId"`

AGREEMENT_TRIAL_RDV=`curl --header "X-Okapi-Tenant: diku" -H "Content-Type: application/json" -X POST http://localhost:8080/erm/refdataValues/lookupOrCreate -d '
{
  category: "AgreementType",
  value: "TRIAL",
  label: "Trial"
}
' | jq -r ".id"`

AGREEMENT_DRAFT_RDV=`curl --header "X-Okapi-Tenant: diku" -H "Content-Type: application/json" -X POST http://localhost:8080/erm/refdataValues/lookupOrCreate -d '
{
  category: "AgreementType",
  value: "DRAFT",
  label: "Draft"
}
' | jq -r ".id"`

# Create an agreement
TRIAL_AGREEMENT_ID=`curl --header "X-Okapi-Tenant: diku" -H "Content-Type: application/json" -X POST http://localhost:8080/erm/sas -d '
{
  name: "Trial Agreement LR 001",
  agreementType: { id: "'"$AGREEMENT_TRIAL_RDV"'" },
  localReference: "TRIAL_ALR_001",
  vendorReference: "TRIAL_AVR_001",
  startDate: "2018-01-01",
  endDate: "2018-12-31",
  renewalDate: "2019-01-01",
  nextReviewDate: "2018-10-01"
}
' | jq -r ".id"`

# Create an agreement
DRAFT_AGREEMENT_ID=`curl --header "X-Okapi-Tenant: diku" -H "Content-Type: application/json" -X POST http://localhost:8080/erm/sas -d '
{
  name: "Draft Agreement LR 002",
  agreementType: { id: "'"$AGREEMENT_DRAFT_RDV"'" },
  localReference: "AGG_LR_002",
  vendorReference: "AGG_VR_002",
  startDate: "2018-01-01"
}
' | jq -r ".id"`

# List agreements
# AGREEMENT_ID=`curl --header "X-Okapi-Tenant: diku" http://localhost:8080/sas -X GET | jq ".[0].id"`
# List packages
# We now get the package back when we load the package above, this is still a cool way to work tho
# PACKAGE_ID=`curl --header "X-Okapi-Tenant: diku" http://localhost:8080/packages -X GET | jq ".[0].id"`

curl --header "X-Okapi-Tenant: diku" -H "Content-Type: application/json" -X POST http://localhost:8080/erm/sas/$TRIAL_AGREEMENT_ID/addToAgreement -d ' {
  content:[
    { "type":"package", "id": "'"$APA_PKG_ID"'" }
  ]
}
'

# Register a remote source
RS_KBPLUS_ID=`curl --header "X-Okapi-Tenant: diku" -H "Content-Type: application/json" -X POST http://localhost:8080/erm/kbs -d '
{
  name:"KB+",
  type:"org.olf.kb.adapters.KIJPFAdapter", // K-Int Json Package Format Adapter
  cursor:null,
  uri:"https://www.kbplus.ac.uk/kbplus7/publicExport/idx",
  listPrefix:null,
  fullPrefix:null,
  principal:null,
  credentials:null,
  rectype:"1",
  active:true
}
'`


# If all goes well, you'll get a status message back. After that, try searching your subscribed titles:

curl --header "X-Okapi-Tenant: diku" http://localhost:8080/erm/content -X GET


# Or try the codex interface instead
#curl --header "X-Okapi-Tenant: diku" http://localhost:8080/codex-instances -X GET

# Pull an ID from that record and ask the codex interface for some details
#RECORD_ID="ff80818162a5e9600162a5e9ef63002f"
#curl --header "X-Okapi-Tenant: diku" http://localhost:8080/codex-instances/$RECORD_ID -X GET
