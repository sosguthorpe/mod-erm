package org.olf

class UrlMappings {

  static mappings = {

    "/"(controller: 'application', action:'index')
    "/_/tenant"(controller: 'okapi', action:'tenant')
    "500"(view: '/error')
    "404"(view: '/notFound')

    // Map /kbs to RemoteKBController
    '/erm/kbs'(resources: 'remoteKB')

    // Map /sas to SubscriptionAgreementController
    '/erm/sas'(resources: 'subscriptionAgreement') {
       "/addToAgreement"(action:'addToAgreement')
    }
    '/erm/titles'(resources: 'title')
    '/erm/packages'(resources: 'package')
    "/erm/pci"(resources:'packageContentItem')

    '/erm/refdataValues'(resource: 'refdata') {
      "/lookupOrCreate"(controller:'refdata', action:'lookupOrCreate')
    }

    "/erm/admin/$action"(controller:'admin')

    "/erm/content"(controller:'subscribedContent', action:'index')
    "/erm/content/$action"(controller:'subscribedContent')

    "/erm/codex-instances" ( controller:'subscribedContent', action:'codexSearch')
    "/erm/codex-instances/$id" ( controller:'subscribedContent', action:'codexItem')

    "/erm/knowledgebase" ( controller:'kb', action:'index')
    "/erm/knowledgebase/$action" ( controller:'kb' )

  }
}
