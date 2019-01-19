# mod-agreements

Copyright (C) 2018-2019 The Open Library Foundation

This software is distributed under the terms of the Apache License,
Version 2.0. See the file "[LICENSE](LICENSE)" for more information.

## Introduction

This is the main starter repository for the Grails-based OLF - ERM backend modules.

- [Getting started](service/docs/getting-started.md "Getting started")

## Additional information

### Issue tracker

See project [MODERM](https://issues.folio.org/browse/MODERM)
at the [FOLIO issue tracker](https://dev.folio.org/guidelines/issue-tracker/).

### Other documentation

Other [modules](https://dev.folio.org/source-code/#server-side) are described,
with further FOLIO Developer documentation at [dev.folio.org](https://dev.folio.org/)


## Running using grails run-app with the vagrant-db profile

    grails -Dgrails.env=vagrant-db run-app


## initial setup

Most developers will run some variant of the following commands the first time through

### in window #1

Start the vagrant image up from the project root

    vagrant destroy
    vagrant up

Sometimes okapi does not start cleanly in the vagrant image - you can check this with

    vagrant ssh

then once logged in

    doocker ps

should list running images - if no processes are listed, you will need to restart okapi (In the vagrant image) with

    sudo su - root
    service okapi stop
    service okapi start

Finish the part off with

    tail -f /var/log/folio/okapi/okapi.log

### In window #2

Build and run mod-agreements stand alone

    cd service
    grails war
    ../scripts/run_external_reg.sh

### in window #3

Register the module and load some test data

  cd scripts
  ./register_and_enable.sh
  ./dev_submit_pkg.sh
  ./dev_trigger_kb_sync.sh 

### In window #4

Run up a stripes platform containing erm

This section is run in a local setup, not from any particular checked out project, YMMV

    cd ../platform/stripes/platform-erm
    stripes serve ./stripes.config.js --has-all-perms

You should get back

Waiting for webpack to build...
Listening at http://localhost:3000

and then be able to access the app

  

