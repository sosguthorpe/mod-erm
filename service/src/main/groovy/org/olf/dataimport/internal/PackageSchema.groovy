package org.olf.dataimport.internal
import java.time.LocalDate

interface PackageSchema {
  
  PackageHeaderSchema getHeader()
  Collection<ContentItemSchema> getPackageContents()
    
  public interface PackageHeaderSchema {
    /*
     "availability": {
      "type": "general"
    },
    "packageProvider": {
      "name": "Knowledge Integration Ltd"
    },
    "packageSource": "k-int",
    "packageName": "K-Int Test Package 001",
    "startDate": "2015-01-01T00:00:00Z",
    "endDate": "2020-12-31T00:00:00Z",
    "packageSlug": "kint_test_001",
    "_intenalId": 276432871386
    */
    
    PackageProviderSchema getPackageProvider()
    String getPackageSource()
    String getPackageName()
    LocalDate getStartDate()
    LocalDate getEndDate()
    String getPackageSlug()
    String getStatus()
    String get_intenalId()
  }
  
  public interface PackageProviderSchema {
    String getName()
    String getReference()
  }
  
  
  public interface ContentItemSchema {
    /*
     {
      "title": "Clinical Cancer Drugs",
      "instanceMedium": "electronic",
      "instanceMedia": "journal",
      "instanceIdentifiers": [{
        "namespace": "eissn",
        "value": "2212-6988"
      }],
      "siblingInstanceIdentifiers": [{
        "namespace": "issn",
        "value": "2212-697X"
      }],
      "coverage": [
        {
        "startVolume": "1",
        "startIssue": "1",
        "startDate": "2014-01-01",
        "endVolume": "1",
        "endIssue": "4",
        "endDate": "2014-12-31"
        },
        {
        "startVolume": "2",
        "startIssue": "1",
        "startDate": "2015-01-01",
        "endVolume": null,
        "endIssue": null,
        "endDate": null
        }
      ],
      "embargo": null,
      "coverageDepth": "fulltext",
      "coverageNote": "New for 2014. This record is a test case for split coverage",
      "platformUrl": "http://benthamscience.com/journal/index.php?journalID=ccand",
      "platformName": "Bentham Science",
      "_platformId": 627
    }
    */
    
    String getTitle()
    String getInstanceMedium()
    String getInstanceMedia()
    Collection<IdentifierSchema> getInstanceIdentifiers()
    Collection<IdentifierSchema> getSiblingInstanceIdentifiers()
    Collection<CoverageStatementSchema> getCoverage()
    String getEmbargo()
    String getCoverageDepth()
    String getCoverageNote()
    String getPlatformUrl()
    String getUrl()
    String getPlatformName()
    String get_platformId()
  }
  
  public interface IdentifierSchema {
    /*
     {
       "namespace": "issn",
       "value": "2212-697X"
     }
     */
    String getNamespace()
    String getValue()
  }
  
  public interface CoverageStatementSchema {
    /*
    {
      "startVolume": "1",
      "startIssue": "1",
      "startDate": "2014-01-01",
      "endVolume": "1",
      "endIssue": "4",
      "endDate": "2014-12-31"
    }
    */
    LocalDate getStartDate()
    String getStartVolume()
    String getStartIssue()
    
    LocalDate getEndDate()
    String getEndVolume()
    String getEndIssue()
  }
}
