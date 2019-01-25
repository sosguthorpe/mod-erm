#! /bin/sh

AUTH_TOKEN=`../okapi-login`

STATUS_ACTIVE_RDV=`curl --header "X-Okapi-Tenant: diku" -H "X-Okapi-Token: ${AUTH_TOKEN}" -H "Content-Type: application/json" http://localhost:9130/erm/refdataValues/SubscriptionAgreement/agreementStatus | jq -r '.[] | select(.label=="Active") | .id'`

ISPERPETUAL_NO_RDV=`curl --header "X-Okapi-Tenant: diku" -H "X-Okapi-Token: ${AUTH_TOKEN}" -H "Content-Type: application/json" http://localhost:9130/erm/refdataValues/SubscriptionAgreement/isPerpetual | jq -r '.[] | select(.label=="No") | .id'`

RENEW_DEFRENEW_RDV=`curl --header "X-Okapi-Tenant: diku" -H "X-Okapi-Token: ${AUTH_TOKEN}" -H "Content-Type: application/json" http://localhost:9130/erm/refdataValues/SubscriptionAgreement/renewalPriority | jq -r '.[] | select(.label=="Definitely Renew") | .id'`

echo Looked up the following refdata
echo active status $STATUS_ACTIVE_RDV
echo is perpetual $ISPERPETUAL_YES_RDV
echo renewal $RENEW_DEFRENEW_RDV

echo Create a new agreement, and include an entitement for bentham science defined externally by eholdings

RESP1=`curl --header "X-Okapi-Tenant: diku" -H "X-Okapi-Token: ${AUTH_TOKEN}" -H "Content-Type: application/json" -X POST http://localhost:9130/erm/sas -d ' {
  name: "EHTC1: Agreement for Bentham and ASC",
  description: "eHoldings test case 1 - an agreement that will be used to hold entitlements for bentham science and ASC",
  agreementStatus: { id: "'"$STATUS_ACTIVE_RDV"'" },
  isPerpetual: { id: "'"$ISPERPETUAL_NO_RDV"'" },
  renewalPriority: { id: "'"$RENEW_DEFRENEW_RDV"'" },
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

