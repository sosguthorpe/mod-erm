#! /bin/sh

AUTH_TOKEN=`../okapi-login`

echo Create a new agreement, and include an entitement for bentham science defined externally by eholdings
echo Note - you could equally post items[] and then add the entitlment to the agreement, as in the RESP2 request below

echo Please note - agreementStatus, isPerpetual and renewalPriority are refdata values, and the module is doing clever lookups to try and
echo make the API more usable. Values CAN be rejected however

RESP1=`curl --header "X-Okapi-Tenant: diku" -H "X-Okapi-Token: ${AUTH_TOKEN}" -H "Content-Type: application/json" -X POST http://localhost:9130/erm/sas -d ' {
  name: "EHTC1: Agreement for Bentham and ASC",
  description: "eHoldings test case 1 - an agreement that will be used to hold entitlements for bentham science and ASC",
  agreementStatus: "Active",
  isPerpetual: "No",
  renewalPriority: "Definitely Renew",
  localReference: "EHTC1",
  startDate: "2019-01-01",
  items: [
    { type:"external", authority:"EKB", reference:"301-3707", label:"Bentham Science via eHoldings" }
  ]
}
'`

# echo Result: $RESP1

echo Parse the response we got back from creating the agreement, and extract the agreement ID
EHTC1=`echo $RESP1 | jq -r ".id" | tr -d '\r'`

echo The ID of our new agreement is $EHTC1


echo update that agreement to add ASC
RESP2=`curl --header "X-Okapi-Tenant: diku" -H "X-Okapi-Token: ${AUTH_TOKEN}" -H "Content-Type: application/json" -X PUT http://localhost:9130/erm/sas/$EHTC1 -d ' {
    items:[
      { type:"external", authority:"EKB", reference:"19-1615", label:"Academic Source Complete via eHoldings" }
    ]
}
'`

echo $RESP2

echo You can delete entitlements from the agreement by posting to the items array with the ID of the entitlement to delete, and the extra property _delete:true

echo Search for all agreements where the entitlement reference is 19-1615
curl --header "X-Okapi-Tenant: diku" "http://localhost:8080/erm/sas?filters=items.reference%3D%3D19-1615&filters=items.authority%3D%3DEKB&stats=true"

