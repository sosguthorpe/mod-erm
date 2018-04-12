pushd ../service
curl -XDELETE http://localhost:9130/_/proxy/tenants/diku/modules/service-1.0.0
curl -XDELETE http://localhost:9130/_/discovery/modules/service-1.0.0/localhost-service-1.0.0
curl -XDELETE http://localhost:9130/_/proxy/modules/service-1.0.0
./gradlew generateDescriptors
curl -XPOST http://localhost:9130/_/proxy/modules -d @../service/build/resources/okapi/ModuleDescriptor.json
curl -XPOST http://localhost:9130/_/discovery/modules -d @../service/build/resources/okapi/DeploymentDescriptor.json
curl -XPOST http://localhost:9130/_/proxy/tenants/diku/modules -d '{"id": "service-1.0.0"}'
popd