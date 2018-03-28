# Getting started
Get up-to-speed and set up with a Grails project that can be deployed as an OKAPI backend module. To create a new backend module that you wish to develop locally please clone/fork this repository.

## Pre - reading
The UI module is designed to work within the [FOLIO](https://github.com/folio-org) project behind the [OKAPI gateway](https://github.com/folio-org/okapi).
Our modules are built using the [Grails framework](http://docs.grails.org/latest/guide/single.html).
Modules are compild into single jar application using [Spring boot](https://projects.spring.io/spring-boot/).

There are some pre-installed tools to help with [OKAPI integration](okapi-integration.md).

## Prerequisites
This starter project uses [Postgres](https://www.postgresql.org/) to fit with the rest of the FOLIO project. You will need to install postgres and create a databse for this (and other) modules to use:
There are 3 databses defined in the starter config,
* olfdev;
* olftest;
* and olf.

Each uses the same credentials by defualt to connect:
* un: folio
* pw: folio

These can be set to whatever you like during the creation of the databses but be sure to reflect your changes in the the application config file at `grails-app/conf/application.yml` Example config that can be executed by postgres user:

    CREATE USER folio WITH PASSWORD 'folio' SUPERUSER CREATEDB INHERIT LOGIN;

    DROP DATABASE olfdev;
    CREATE DATABASE olfdev;
    GRANT ALL PRIVILEGES ON DATABASE olfdev to folio;
    DROP DATABASE olftest;
    CREATE DATABASE olftest;
    GRANT ALL PRIVILEGES ON DATABASE olftest to folio;
    DROP DATABASE olf;
    CREATE DATABASE olf;
    GRANT ALL PRIVILEGES ON DATABASE folio to olf;


To install and manage the following pre-requisites I recommend using [SDKMAN](http://sdkman.io/).
- [Groovy](http://groovy-lang.org/)
- [Grails](https://grails.org/)

With sdkman installed as above it's as easy as opening a terminal and typing:
* `sdk install groovy`
* `sdk install grails`

## Running
From the root of your grails project (olf-erm/service) you should be able to start the application by typing:
`grails run-app`

The above command should start the application using the development profile.

## Running the other folio backend modules
There is also a vagrant file at the root of this project that contains the FOLIO backend modules in a virtualised environment. To run that environament you will first need to install [vagrant](https://www.vagrantup.com/)
The default for this file is to use virtualbox as the provider, but vmware should work also. To start the folio backend stack, once vagrant is installed, just open a terminal and type
```
vagrant up
```

## Registering our module with OKAPI in the vagrant machine
Part of the build process of the module produces some OKAPI descriptors. The templates can be found in `service/src/okapi`. The placeholders are substitued for values that are generated as part of the build process and then the descriptors
written to: `build/resources/okapi` with values substituted and the template suffix removed. To compile the app without running you can type:
```grails compile```

You can then use these json descriptors to register and deploy your moudule when it is running. See the [deployment and discovery](https://github.com/folio-org/okapi/blob/master/doc/guide.md#deployment-and-discovery) section of the OKAPI docs.
This allows you to run your module outside of the other core modules (for instance within your IDE) and debug in the normal way while developing.
