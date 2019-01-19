ALL_CATS=`curl -s --header "X-Okapi-Tenant: diku" http://localhost:8080/erm/refdataValues -X GET`

INTERNAL_CONTACT_CAT=`echo $ALL_CATS | jq -r '.[] | select(.desc=="InternalContact.Role") | .id'`

echo Internal Contact Category: $INTERNAL_CONTACT_CAT


INTERNAL_CONTACT_ROLES=`curl -s --header "X-Okapi-Tenant: diku" http://localhost:8080/erm/refdataValues/$INTERNAL_CONTACT_CAT -X GET`

echo $INTERNAL_CONTACT_ROLES
