package org.olf

class UrlMappings {

  static mappings = {

    "/"(controller: 'application', action:'index')
    "/_/tenant"(controller: 'okapi', action:'tenant')
    "500"(view: '/error')
    "404"(view: '/notFound')
  }
}
