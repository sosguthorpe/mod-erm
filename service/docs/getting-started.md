# Getting started
Get up-to-speed and set up with a Grails project that can be deployed as an OKAPI backend module. To create a new backend module that you wish to develop locally please clone/fork this repository.

## Pre - reading
The UI module is designed to work within the [FOLIO](https://github.com/folio-org) project behind the [OKAPI gateway](https://github.com/folio-org/okapi).
Our modules are built using the [Grails framework](http://docs.grails.org/latest/guide/single.html).
Modules are compild into single jar application using [Spring boot](https://projects.spring.io/spring-boot/).

There are some preinstalled tools to help with [OKAPI integration](okapi-integration.md)

## Prerequisites
This starter project uses [Postgres](https://www.postgresql.org/) to fit with the rest of the FOLIO project. You will need to install postgres and create a databse for this (and other) modules to use:
There are 3 databses defined in the starter config,
* olfdev;
* olftest;
* and olf.

Each uses the same credentials by defualt to connect:
* un: folio
* pw: folio

These can be set to whatever you like during the creation of the databses but be sure to reflect your changes in the the application config file at `grails-app/conf/application.yml`

To install and manage the following pre-requisites I recommend using [SDKMAN](http://sdkman.io/).
- [Groovy](http://groovy-lang.org/)
- [Grails](https://grails.org/)

With sdkman installed as above it's as easy as opening a terminal and typing:
* `sdk install groovy`
* `sdk install grails`

## Running
From the root of your project you should be able to start the application by typing:
`grails run-app`

The above command should start the application using the development profile.
