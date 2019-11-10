package org.olf

import java.time.LocalDate
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import grails.testing.mixin.integration.Integration
import spock.lang.*

@Integration
@Stepwise
class AgreementViews extends BaseSpec {

  Logger log = LoggerFactory.getLogger(AgreementViews)
  
  @Shared
  String pkg_id
  
  @Shared
  String agg_id
  
  def 'Ingest a test package' () {
    
    when: 'Testing package added'
      doPost('/erm/packages/import') {
        header {
          dataSchema {
            name "mod-agreements-package"
            version 1.0
          }
        }
        records ([
          {
            source "Folio Testing"
            reference "access_start_access_end_examples"
            name "access_start_access_end_tests Package"
            packageProvider {
              name "DIKU"
            }
            contentItems ([
              {
                depth "fulltext"
                accessStart "2011-01-01"
                accessEnd "2018-12-31"
                coverage ([
                  {
                    startDate "2018-04-01"
                    startVolume "1"
                    startIssue "1"
                  }
                ])
                platformTitleInstance {
                  platform "EUP Publishing"
                  platform_url "https://www.euppublishing.com"
                  url "https://www.euppublishing.com/loi/afg"
                  titleInstance {
                    name "Afghanistan"
                    identifiers ([
                      {
                        value "2399-357X"
                        namespace "issn"
                      }
                      {
                        value "2399-3588"
                        namespace "eissn"
                      }
                    ])
                    type "serial"
                  }
                }
              },
              {
                depth "fulltext"
                accessStart "2011-01-01"
                coverage ([
                  {
                    startDate "2017-01-01"
                    startVolume "1"
                    startIssue "1"
                  }
                ])
                platformTitleInstance {
                  platform "Archaeological and Environmental Forensic Science"
                  platform_url "http://www.equinoxjournals.com"
                  url "http://www.equinoxjournals.com/AEFS/"
                  titleInstance {
                    name "Archaeological and Environmental Forensic Science"
                    identifiers ([
                      {
                        value "2052-3378"
                        namespace "issn"
                      }
                      {
                        value "2052-3386"
                        namespace "eissn"
                      }
                    ])
                    type "serial"
                  }
                }
              },
              {
                depth "fulltext"
                accessEnd "2018-12-31"
                coverage ([
                  {
                    startDate "2000-02-01"
                    startVolume "27"
                    startIssue "1"
                  }
                ])
                platformTitleInstance {
                  platform "EUP Publishing"
                  platform_url "https://www.euppublishing.com"
                  url "https://www.euppublishing.com/loi/anh"
                  titleInstance {
                    name "Archives of Natural History"
                    identifiers ([
                      {
                        value "0260-9541"
                        namespace "issn"
                      }
                      {
                        value "1755-6260"
                        namespace "eissn"
                      }
                    ])
                    type "serial"
                  }
                }
              },
              {
                depth "fulltext"
                accessStart "2025-01-01"
                coverage ([
                  {
                    startDate "2015-01-01"
                    startVolume "33"
                  }
                ])
                platformTitleInstance {
                  platform "JSTOR"
                  platform_url "https://www.jstor.org"
                  url "https://www.jstor.org/journal/bethunivj"
                  titleInstance {
                    name "Bethlehem University Journal"
                    identifiers ([
                      {
                        value "2521-3695"
                        namespace "issn"
                      }
                      {
                        value "2410-5449"
                        namespace "eissn"
                      }
                    ])
                    type "serial"
                  }
                }
              }
            ])
          }
        ])
      }
    and: 'Find the package by name'
      List resp = doGet("/erm/packages", [filters: ['name==access_start_access_end_tests Package']])
      pkg_id = resp[0].id
      
    then: 'Expect package found'
      assert (pkg_id = resp.getAt(0)?.id) != null
      assert resp?.getAt(0)?.name == 'access_start_access_end_tests Package'
  }
  
  def 'Add an agreement for our package' () {
    final LocalDate today = LocalDate.now()
    final LocalDate tomorrow = today.plusDays(1)
    when: 'Agreement added for package'
      Map httpResult = doPost('/erm/sas') {
        periods ([{
          startDate today.toString()
          endDate tomorrow.toString()
        }])
        items ([{
          'resource' pkg_id
        }])
        name 'Test agreement'
      }
      
    then: 'Agreement added'
      assert (agg_id = httpResult?.id) != null
      assert (httpResult?.items?.size() ?: 0) == 1
  }
  
  @Unroll
  def 'Test agreement line range #agreement_line_start - #agreement_line_end' (final String agreement_line_start, final String agreement_line_end, final Map<String, List<String>> expected) {
    
    final List<String> endpoints = ['current', 'future', 'dropped'] 
    
    when: 'Agreement read'
      Map httpResult = doGet("/erm/sas/${agg_id}")
      
    and: 'Order-line dates set to #agreement_line_start - #agreement_line_end'
      httpResult.items[0].activeFrom = agreement_line_start
      httpResult.items[0].activeTo = agreement_line_end
    
    and: 'Update put'
      httpResult = doPut("/erm/sas/${agg_id}", httpResult)
      
    then: 'Agreement saved'
      assert httpResult?.id == agg_id
      assert (httpResult?.items?.size() ?: 0) == 1
      
    when: 'Agreement re-read'
      httpResult = doGet("/erm/sas/${agg_id}", ['expand': 'items'])
    
    then: 'Dates are correct'
      assert httpResult.items[0].activeFrom == agreement_line_start
      assert httpResult.items[0].activeTo == agreement_line_end
    
    when: 'Enpoints checked'
      final List<String> nevers_not_seen = expected['never']?.collect() ?: []
      final Map<String,List<String>> seen_resources = [:].withDefault { [] }
      
      // We must check all endpoints to ensure the 'never' are met.
      for ( final String endpoint : endpoints ) {
        if (endpoint != 'never') {
          log.debug "Finding ${endpoint} resources"
          List epResult = doGet("/erm/sas/${agg_id}/resources/${endpoint}")
          for ( def result : epResult ) {
            final String name = result['_object'].pti.titleInstance.name
            seen_resources[endpoint] << name
            nevers_not_seen.remove(name)
          }
        }
      }
    then: 'Dropped resources match expected dropped resources'
      assert seen_resources['dropped'].size() == (expected['dropped']?.size() ?: 0)
    
    and: 'Future resources match expected future resources'
      assert seen_resources['future'].size() == (expected['future']?.size() ?: 0)
    
    and: 'Current resources match expected current resources'
      assert seen_resources['current'].size() == (expected['current']?.size() ?: 0)
    
    and: 'Never resources were not seen in the previous matches'
     assert nevers_not_seen.size() == (expected['never']?.size() ?: 0)
    
    where:
      agreement_line_start | agreement_line_end
      '2007-01-01'         | '2009-12-31'
      '2007-01-01'         | '2012-12-31'
      '2019-01-01'         | '2020-12-31'
      '2006-01-01'         | '2020-12-31'
      '2006-01-01'         | '2030-12-31'
      '2020-01-01'         | '2030-12-31'
      null                 | '2030-12-31'
      '2007-01-01'         | null
      
      expected << [[
        never: ['Afghanistan', 'Archaeological and Environmental Forensic Science', 'Bethlehem University Journal'],
        dropped: ['Archives of Natural History']
      ],[
        never: ['Bethlehem University Journal'],
        dropped: ['Afghanistan', 'Archaeological and Environmental Forensic Science','Archives of Natural History']
      ],[
        never: ['Afghanistan', 'Archives of Natural History', 'Bethlehem University Journal'],
        current: ['Archaeological and Environmental Forensic Science']
      ],[
        never: ['Bethlehem University Journal'],
        dropped: ['Afghanistan', 'Archives of Natural History'],
        current: ['Archaeological and Environmental Forensic Science']
      ],[
        future: ['Bethlehem University Journal'],
        dropped: ['Afghanistan', 'Archives of Natural History'],
        current: ['Archaeological and Environmental Forensic Science']
      ],[
        never: ['Afghanistan', 'Archives of Natural History'],
        future: ['Archaeological and Environmental Forensic Science','Bethlehem University Journal']
      ],[
        future: ['Bethlehem University Journal'],
        dropped: ['Afghanistan', 'Archives of Natural History'],
        current: ['Archaeological and Environmental Forensic Science']
      ],[
        future: ['Bethlehem University Journal'],
        dropped: ['Afghanistan', 'Archives of Natural History'],
        current: ['Archaeological and Environmental Forensic Science']
      ]]
  }
}
