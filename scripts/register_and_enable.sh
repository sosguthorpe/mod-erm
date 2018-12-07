BASEDIR=$(dirname "$0")
# echo Please make sure you have run ./gradlew clean generateDescriptors before starting this script
pushd "$BASEDIR/../service"

# Check for decriptor target directory.

DESCRIPTORDIR="build/resources/main/okapi"

if [ ! -d "$DESCRIPTORDIR" ]; then
    echo "No descriptors found. Let's try building them."
    ./gradlew generateDescriptors
fi

curl -XDELETE http://localhost:9130/_/proxy/tenants/diku/modules/olf-erm-1.0.3
curl -XDELETE http://localhost:9130/_/discovery/modules/olf-erm-1.0.0/localhost-olf-erm-1.0.3
curl -XDELETE http://localhost:9130/_/proxy/modules/olf-erm-1.0.3
# ./gradlew clean generateDescriptors
curl -XPOST http://localhost:9130/_/proxy/modules -d @"${DESCRIPTORDIR}/ModuleDescriptor.json"
curl -XPOST http://localhost:9130/_/discovery/modules -d @"${DESCRIPTORDIR}/DeploymentDescriptor.json"
curl -XPOST http://localhost:9130/_/proxy/tenants/diku/modules -d '{"id": "olf-erm-1.0.3"}'
popd
