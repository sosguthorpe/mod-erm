#! /bin/sh

AUTH_TOKEN=`./okapi-login`

OKAPI="http://localhost:9130"
TENANT="diku"

if [ -f .okapirc ]; then
  . .okapirc
elif [ -f $HOME/.okapirc ]; then
  . $HOME/.okapirc
fi



#  serviceType:"Loan",
AGREE_1=`curl --header "X-Okapi-Tenant: ${TENANT}" -H "X-Okapi-Token: ${AUTH_TOKEN}" -H "Content-Type: application/json" -X POST ${OKAPI}/erm/sas -d ' {
  name: "Agreement Test 1",
  agreementStatus: "cancelled",
  notARealProperty:"Test that this is ignored in accordance with Postels law",
  systemInstanceIdentifier:"893475987348973",
}
'`
AGREE_2=`curl --header "X-Okapi-Tenant: ${TENANT}" -H "X-Okapi-Token: ${AUTH_TOKEN}" -H "Content-Type: application/json" -X POST ${OKAPI}/erm/sas -d ' {
  name: "Agreement Test 2",
  agreementStatus: "closed",
  systemInstanceIdentifier:"893475987348974",
}
'`
AGREE_3=`curl --header "X-Okapi-Tenant: ${TENANT}" -H "X-Okapi-Token: ${AUTH_TOKEN}" -H "Content-Type: application/json" -X POST ${OKAPI}/erm/sas -d ' {
  name: "Agreement Test 3",
  agreementStatus: "active",
  systemInstanceIdentifier:"893475987348975",
}
'`
AGREE_4=`curl --header "X-Okapi-Tenant: ${TENANT}" -H "X-Okapi-Token: ${AUTH_TOKEN}" -H "Content-Type: application/json" -X POST ${OKAPI}/erm/sas -d ' {
  name: "Agreement Test 4",
  agreementStatus: "cancelled",
  systemInstanceIdentifier:"893475987348976",
}
'`

AGREE_5=`curl --header "X-Okapi-Tenant: ${TENANT}" -H "X-Okapi-Token: ${AUTH_TOKEN}" -H "Content-Type: application/json" -X POST ${OKAPI}/erm/sas -d ' {
  name: "Agreement Test 5",
  agreementStatus: "rejected",
  systemInstanceIdentifier:"893475987348977",
}
'`

AGREE_6=`curl --header "X-Okapi-Tenant: ${TENANT}" -H "X-Okapi-Token: ${AUTH_TOKEN}" -H "Content-Type: application/json" -X POST ${OKAPI}/erm/sas -d ' {
  name: "Agreement Test 6",
  agreementStatus: "rejected",
  systemInstanceIdentifier:"893475987348978",
}
'`

echo Result : $AGREE_1
echo Result : $AGREE_2
echo Result : $AGREE_3
echo Result : $AGREE_4
echo Result : $AGREE_5
echo Result : $AGREE_6

echo Parse result to extract request ID
AGREE_1_ID=`echo $AGREE_1 | jq -r ".id" | tr -d '\r'`
AGREE_2_ID=`echo $AGREE_2 | jq -r ".id" | tr -d '\r'`
AGREE_3_ID=`echo $AGREE_3 | jq -r ".id" | tr -d '\r'`
AGREE_4_ID=`echo $AGREE_4 | jq -r ".id" | tr -d '\r'`
AGREE_5_ID=`echo $AGREE_5 | jq -r ".id" | tr -d '\r'`
AGREE_6_ID=`echo $AGREE_6 | jq -r ".id" | tr -d '\r'`
echo Created request 1: $AGREE_1_ID
echo Created request 2: $AGREE_2_ID
echo Created request 3: $AGREE_3_ID
echo Created request 4: $AGREE_4_ID
echo Created request 5: $AGREE_5_ID
echo Created request 6: $AGREE_6_ID
