package org.olf

import org.grails.web.json.JSONArray
import org.grails.web.json.JSONObject
import org.olf.erm.ComparisonPoint

import grails.gorm.multitenancy.CurrentTenant
import groovy.util.logging.Slf4j

@Slf4j
@CurrentTenant
class ComparisonController {
  ComparisonService comparisonService
  
  def compare () {
    List<ComparisonPoint> points = []
    JSONArray data = request.JSON
    for ( JSONObject entry : data ) {
      ComparisonPoint point = new ComparisonPoint()
      bindData(point, entry)
      points << point
    }
    respond comparisonService.compare( points as ComparisonPoint[] )
  }
}