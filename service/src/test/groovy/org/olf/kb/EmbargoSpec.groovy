package org.olf.kb

import org.olf.kb.converters.EmbargoConverter

import grails.databinding.SimpleMapDataBindingSource
import grails.persistence.Entity
import grails.plugin.json.view.test.JsonViewTest
import grails.testing.gorm.DataTest
import grails.web.databinding.DataBindingUtils
import grails.web.databinding.GrailsWebDataBinder
import spock.lang.Specification
import spock.lang.Unroll

class EmbargoSpec extends Specification implements DataTest, JsonViewTest {

  GrailsWebDataBinder binder
  
  // ToDo: Requested by steve - G4 changed the way the tests work - this now requires a tenant
  // so comment out for now and revisit.

/*
  void setupSpec() {
    mockDomains TestEntity, Embargo, EmbargoStatement
  }
  
  void setup() {
    binder = grailsApplication.mainContext.getBean(DataBindingUtils.DATA_BINDER_BEAN_NAME)
    // in a Grails app you wouldn't do this... if the editor is a bean
    // in the Spring application context it will be auto-discovered
    binder.registerConverter(new EmbargoConverter())
  }
  
  @Unroll
  void 'Test binding of embargo' (final String embargo, final String expected, final boolean valid) {
    given: 'Test entity with embargo property'
      TestEntity te = new TestEntity()

    when: 'Embargo is #embargo'
      binder.bind te, [embargo: embargo] as SimpleMapDataBindingSource

    then: 'Embargo converts to string as #expected and validity is #valid'
      assert te.embargo?.toString() == expected
      assert ((te.embargo?.validate(deepValidate:true, failOnError: true) as boolean) == valid)
    
    where:
      
      embargo       | expected      | valid
      'P100D'       | 'P100D'       | true
      'R4M'         | 'R4M'         | true
      'R1Y;P6M'     | 'R1Y;P6M'     | true
      'R1Y;6M'      | null          | false
      'R0Y'         | 'R0Y'         | false
      'R180D;P30D'  | 'R180D;P30D'  | true
      'test'        | null          | false
      'R10M;P9D'    | 'R10M;P9D'    | true
      'R4Y;R9D'     | null          | false
      'P3M;P9D'     | null          | false
  }
  
  def 'Test Gson view template' () {    
    when:"Gson is rendered"
      def result = render(template: "/embargo/embargo", model:['embargo': Embargo.parse('R10Y;P30M')])
      
    then:"The json is correct"
      result.json.movingWallStart.type == null
      result.json.movingWallStart.length == 10
      result.json.movingWallStart.unit == 'Years'
      result.json.movingWallEnd.type == null
      result.json.movingWallEnd.length == 30
      result.json.movingWallEnd.unit == 'Months'
  }
*/
  
}

@Entity
class TestEntity {
  Embargo embargo
}
