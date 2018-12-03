# No vendor.
# ./okapi-cmd "/codex-instances"
QUERY="(title%20%3D%20fasdfasdf*)%20and%20(ext.selected%20%3D%20true)%20sortby%20title"
# curl --header "X-Okapi-Tenant: diku" "http://localhost:8080/codex-instances?query=$QUERY&limit=90"

curl --header "X-Okapi-Tenant: diku" "http://localhost:8080/codex-instances?query=title%20%3D%20clinical*&limit=90"

