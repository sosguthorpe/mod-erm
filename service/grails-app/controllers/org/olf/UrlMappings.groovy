package org.olf

import org.olf.general.FileUpload

class UrlMappings {

  static mappings = {

    "/"(controller: 'application', action:'index')
    "500"(view: '/error')
    "404"(view: '/notFound')

    // Map /kbs to RemoteKBController
    '/erm/kbs'(resources: 'remoteKB')

    // Map /sas to SubscriptionAgreementController
    '/erm/sas'(resources: 'subscriptionAgreement') {
      "/resources"            (action: 'resources', method: 'GET')
      "/resources/current"    (action: 'currentResources', method: 'GET')
      "/resources/future"     (action: 'futureResources', method: 'GET')
      "/resources/dropped"    (action: 'droppedResources', method: 'GET')

      "/export/$format?"          (controller: 'export', method: 'GET')
      "/export/current/$format?"  (controller: 'export', action: 'current', method: 'GET')
//      "/export/future/$format?"   (controller: 'export', action: 'future', method: 'GET')
//      "/export/dropped/$format?"  (controller: 'export', action: 'dropped', method: 'GET')
	  
      '/linkedLicenses' {
        controller = 'remoteLicenseLink'
        method = 'GET'
        filters = { "owner==${params.subscriptionAgreementId}" }
      }
      
      collection {
        '/linkedLicenses' {
          controller = 'remoteLicenseLink'
          method = 'GET'
        }
      }
	  
      '/usageDataProviders' {
  		  controller = 'usageDataProvider'
  		  method = 'GET'
  		  filters = { "owner==${params.subscriptionAgreementId}" }
  	  }
	  
      collection {
  		  '/usageDataProviders' {
  			  controller = 'usageDataProvider'
  			  method = 'GET'
  		  }
  	  }
    }

    '/erm/titles'(resources: 'title') {
      collection {
        "/entitled" (action: 'entitled')
      }
    }

    '/erm/packages'(resources: 'package') {
      collection {
        "/import" (controller: 'package', action: 'import', method: 'POST')
      }
        
      "/content"         (controller: 'package', action: 'content', method: 'GET')
      "/content/current" (controller: 'package', action: 'currentContent', method: 'GET')
      "/content/future"  (controller: 'package', action: 'futureContent', method: 'GET')
      "/content/dropped" (controller: 'package', action: 'droppedContent', method: 'GET')
    }

    "/erm/pci"(resources:'packageContentItem')
    "/erm/entitlements"(resources:'entitlement') {
      collection {
        "/external" ( action: 'external' )
      }
    }
    '/erm/contacts'(resources: 'internalContact')

    '/erm/refdataValues'(resources: 'refdata') {
      // The collection section allows us to create methods that impact the whole set of refdataValues rather than a specific resource.
      // WIthout this, the url here would be /erm/refdataValues/RDV_ID/lookupOrCreate which is not what we want. Having this here gives us a URL of
      // /erm/refdataValues/lookupOrCreate which is what we want
      collection {
        "/$domain/$property" (controller: 'refdata', action: 'lookup', method: 'GET')
      }
    }
    
    '/erm/org'(resources: 'org') {
      collection {
        "/find/$id"(controller:'org', action:'find')
      }
    }

    "/erm/admin/$action"(controller:'admin')

    "/erm/content"(controller:'subscribedContent', action:'index')
    "/erm/content/$action"(controller:'subscribedContent')

    "/codex-instances" ( controller:'subscribedContent', action:'codexSearch', stats:'true')
    "/codex-instances/$id" ( controller:'subscribedContent', action:'codexItem')

    "/erm/knowledgebase" ( controller:'kb', action:'index')
    "/erm/knowledgebase/$action" ( controller:'kb' )
    
    "/erm/jobs" ( resources:'persistentJob', excludes: ['update', 'patch', 'save']) {
      collection {
        "/$type" ( action: 'save', method: 'POST' )
      }
    }


     // This is the URL path used by the eresources screen.
     // See http://docs.grails.org/latest/guide/theWebLayer.html#embeddedVariables#_dynamically_resolved_variables for information on
     // how we might make this path more dynamic.
    "/erm/resource" ( resources:'resource', excludes: ['delete', 'update', 'patch', 'save']) {
      collection {
        "/electronic" ( action:'electronic' )
      }
      "/entitlementOptions" ( action:'entitlementOptions' )
      "/entitlements" ( action:'entitlements' )
    }

 
    get "/erm/files/$id/raw"(controller: "fileUpload", action: "getFileUploadRaw")
    get "/erm/files/$id"(controller: "fileUpload", action: "getFileUpload")
    get '/erm/files'(controller: "fileUpload", action: "getFileUploadList")
    post '/erm/files'(controller: "fileUpload", action: "postFileUploadRaw")
    delete "/erm/files/$id"(controller: "fileUpload", action: "deleteFileUpload")

    
    // export endpoints
    "/export/$format?"          (controller: 'export', method: 'GET')
    "/export/current/$format?"  (controller: 'export', action: 'current', method: 'GET')
//    "/export/future/$format?"   (controller: 'export', action: 'future', method: 'GET')
//    "/export/dropped/$format?"  (controller: 'export', action: 'dropped', method: 'GET')

  }
}
