#!/bin/sh
#set -vx


if [ -f ~/folio_privates.sh ]
then
  echo Loading folio_privates script
  . ~/folio_privates.sh
fi

echo Configured::
echo $EBSCO_SANDBOX_CLIENT_ID

# jq -r '.name'

echo Testing for presence of JQ

JQTEST=`echo '{  "value":"one" }' | jq -r ".value" | tr -d '\r'`

if [ $JQREST="one" ]
then
  echo JQ installed and working
else
  echo Please install JQ
  exit 1
fi

echo Running

# Grab all values available for property "role" against the "InternalContact" model.
ROLE_REFDATA=`curl -SsL -XGET -H 'X-OKAPI-TENANT: diku'  http://localhost:8080/erm/refdataValues/InternalContact/role`

# Find the one with the label "Agreement Owner"
ROLE_OWNER_RDV=`echo $ROLE_REFDATA | jq -r '.[] | select(.label=="Agreement Owner") | .id'`

ROLE_SUBJECTSPEC_RDV=`echo $ROLE_REFDATA | jq -r '.[] | select(.label=="Subject Specialist") | .id'`

echo Create an internal contact

# Create an internal contact
OWNER_ID=`curl --header "X-Okapi-Tenant: diku" -X GET "http://localhost:8080/erm/sas?filters=name%3D%3DTrial+Agreement+LR+001" | jq -r ".[0].id" | tr -d '\r'`
INTERNAL_CONTACT_ID=`curl --header "X-Okapi-Tenant: diku" -H "Content-Type: application/json" -X POST http://localhost:8080/erm/contacts -d '
{
  lastName: "Doe",
  firstName: "Jane",
  role: { id: "'"$ROLE_OWNER_RDV"'" },
  owner: "'"$OWNER_ID"'"
}
' | jq -r ".id" | tr -d '\r'`

echo Create another internal contact

# Create another internal contact
OWNER_ID=`curl --header "X-Okapi-Tenant: diku" -X GET "http://localhost:8080/erm/sas?filters=name%3D%3DDraft+Agreement+LR+002" | jq -r ".[0].id" | tr -d '\r'`
INTERNAL_CONTACT_ID2=`curl --header "X-Okapi-Tenant: diku" -H "Content-Type: application/json" -X POST http://localhost:8080/erm/contacts -d '
{
  lastName: "Ray",
  firstName: "Basil",
  role: { id: "'"$ROLE_SUBJECTSPEC_RDV"'" },
  owner: "'"$OWNER_ID"'" 
}

' | jq -r ".id" | tr -d '\r'`

