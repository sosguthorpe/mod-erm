#!/bin/bash
BASEDIR=$(dirname "$0")

# Change to service dir.
pushd "$BASEDIR/../service"

# Check for decriptor target directory.
DESCRIPTORDIR="build/resources/main/okapi"

if [ ! -d "$DESCRIPTORDIR" ]; then
  echo "No descriptors found. Let's try building them."
  ./gradlew generateDescriptors
fi

# Read the descriptor
DEP_DESC=`cat ${DESCRIPTORDIR}/DeploymentDescriptor.json`
popd

# Do the post
curl -XPOST http://localhost:9130/_/proxy/tenants/diku/modules -d `echo $DEP_DESC | jq -rc '{id: .srvcId}'`
