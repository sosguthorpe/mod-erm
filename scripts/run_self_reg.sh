#!/bin/bash

# This script is for executing a double check that the endpoint calls that work directly
# also work via okapi.

# Work in progress - needs input from steve

# see https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-external-config.html for info on overriding 
# spring boot app config on the command line

echo Start olf-erm in self-register mode


# curl --header "X-Okapi-Tenant: diku" http://localhost:9130/content -X GET

# These examples DO NOT WORK because the server props don't make it through grails run-app
# grails -Dgrails.server.host=192.168.1.242 -Dgrails.server.port=8090 -DselfRegister=on run-app
# grails --grails.server.host=192.168.1.242 --grails.server.port=8090 --selfRegister=on run-app
# java --grails.server.host=192.168.1.242 --grails.server.port=8090 --selfRegister=on -jar build/libs/olf-erm-1.0.jar

# THis DOES work as expected however - 
# Start up an instance and self register but use the postgres instance inside the vagrant image
java -jar build/libs/olf-erm-1.0.jar --grails.server.host=192.168.1.242 --grails.server.port=8090 --selfRegister=on --dataSource.username=folio_admin --dataSource.password=folio_admin --dataSource.url=jdbc:postgresql://localhost:5432/okapi_modules -jar build/libs/olf-erm-1.0.jar

