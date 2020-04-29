## 2.2.2 2020-04-29
 * ERM-858 Postgres sequences out of sync when existing agreement data is upgraded

## 2.2.1 2020-04-18
 * ERM-838 Add custom_properties_id to existing subscription_agreement entries (upgrade)

## 2.2.0 2020-03-13
 * ERM-747 Custom Properties: Unable to correctly save decimals with german browser locale
 * ERM-711 POST/PUT on erm/sas/{:id} endpoint appears to not save reasonForClosure
 * ERM-696 Agreements: Reason for closure is not displayed
 * ERM-689 Add monograph fields to KBART export
 * ERM-685 Support import and processing of KBART in Local KB Admin
   * ERM-687 Create tsv parser
 * ERM-682 Upgrading mod-agreements module from Daisy to Edelweiss fails
 * ERM-680 Show spinner in agreement lines when switching between agreements
 * ERM-676 Support Editor information in Agreements local KB
 * ERM-655 Sorting limits output in some cases
 * ERM-649 Display a better message when the ExternalDataSources name isn't unique
 * ERM-641 License custom property "note" field should not be returned in public API
 * ERM-587 Agreements : On duplicating an agreement the "General notes about this agreement's license" field should be copied
 * ERM-571 Ensure remote KB syncs are not left "In Progress" when an error occurs.
 * ERM-507 Agreement: Agreement Lines: Package item count always equals "1"
 * ERM-486 Support monograph volume in Agreements local KB
 * ERM-485 Support e/p monograph publication dates in Agreements local KB
 * ERM-482 Support Author information in Agreements local KB
 * ERM-481 Support Edition information in Agreements local KB
 * ERM-461 Local KB Admin | Log error when accessEnd date before accessStart date
 * ERM-437 Local KB admin | External data source name should be unique
   * ERM-643 Add validation clause to ensure that the names data source names are unique
 * ERM-434 "LOCAL" Remote KB/data source should not be editable or be able to be deleted
   * ERM-644 Add preUpdate and preDelete listeners
 * ERM-356 Expose license terms over API
   * ERM-361 Support API for retrieving license terms based on a resource identifier

## 2.0.1 2020-01-28
 * ERM-682 Upgrading mod-agreements module from Daisy to Edelweiss fails

## 2.0.0 2019-12-04
 * ERM-621 License terms not included in Agreement JSON export
 * ERM-614 rkb_cursor reset after harvest completed
 * ERM-583 Use JVM features to manage container memory
 * ERM-561 GOKb harvest job ends in failure
 * ERM-552 Clone endpoint clones properties that are omitted/set to false
 * ERM-546 Interpret <status> element on GOKb TIPP
 * ERM-538 Support health check endpoint (for example /admin/health provided by RMB)
 * ERM-536 mod-agreements Re-assess the container Memory allocation in default LaunchDescriptor
 * ERM-515 Agreements | Handling removed/deleted resources
 * ERM-508 Support multiple POLs on a single Agreement Line
 * ERM-505 Move test data so it's only active for the diku tenant only
 * ERM-500 reasonForClosure Field is not cleared
 * ERM-498 Modify KB Local admin to improve the retrieval and display of logs
 * ERM-477 License and agreement APIs are not protected by FOLIO permissions
   * ERM-479 Add permission definitions and api endpoint config
 * ERM-465 Close an Agreement
   * ERM-483 Migrate agreement status "Rejected" to 'Reason for closure'
 * ERM-462 Agreement | Export resources covered by this agreement gives error
 * ERM-457 Use Agreement Line active from/active to dates when calculating current/expected/previous content of agreement
 * ERM-443	Remove "existing coverage statements overlapping" messages from info log
 * ERM-440 KB Local Admin | Titles not searchable/viewable after file upload
 * ERM-419 Duplicate refdata entries in folio builds
 * ERM-418 Duplicate titles in KBART export
 * ERM-396 Viewing upcoming joiners/leavers for a package
 * ERM-395 Populate access start and access end dates
 * ERM-394 Viewing only current content of package in UI
   * ERM-407 Ensure "E-resources in package" concertina only includes resources currently part of the package
   * ERM-406 Ensure Agreement resources export only includes resources currently part of the package
 * ERM-393 Reporting change to the date a resource joined/date left package
   * ERM-401 Local KB Admin | Add info logging for number of titles where access_start updated
   * ERM-400 Local KB Admin | Add info logging for number of titles where access_end updated
 * ERM-392 Reporting change in title count in a package
 * ERM-377 entitlementOptions endpoint is too slow
 * ERM-376 Export full Agreement as JSON
 * ERM-362 Issue with calling install?purge=true option multiple times
 * ERM-349 Create New Local KB Import - backend work
 * ERM-338 Log import/sync errors on package import
 * ERM-315 Linking PO Line to an agreement line for an eHolding resource fails
 * ERM-297 File attachment over 10MB causes out of memory errors
 * ERM-285 Entitlement Options endpoint throws a 500 Error
 * ERM-182 On sync with remote KB, keep TIPPs where coverage data is incompatible 

## 1.8.0 2019-09-11
 * ERM-440 KB Local Admin | Titles not searchable/viewable after file upload
 * ERM-349 Create New Local KB Import - backend work
 * ERM-338 Log import/sync errors on package import
 * ERM-182 On sync with remote KB, keep TIPPs where coverage data is incompatible 

## 1.8.0 2019-08-12
 * ERM-290 Set up Job Management for Local KB Admin
   * ERM-334 Delete job not in progress
 * ERM-274 Add cleanup task for orphan file uploads
 * ERM-265 Export Agreement data as JSON
 * ERM-215 Export Agreement data as KBART
 * ERM-327 Export entitlements as JSON from single agreement
 * ERM-328 Export entitlements as KBART from single agreement

## 1.8.0 2019-07-24
 * ERM-273 Manage usage data providers on agreements
   * ERM-291 Add usage data provider to subscription agreement
 * ERM-262 Agreement start and end date display with a time offset in some environments
 * ERM-189 Custom coverage dates display with a time offset in some environments 

## 1.7.0 2019-06-11
 * ERM-259 Set supplementary information for an Agreement
 * ERM-245 Tenant bootstrap improvements
   * ERM-249 Create bootstrap data
   * ERM-247 Change descriptors to reflect new interface version

## 1.6.0 2019-05-21
 * ERM-220 Support Organizations app as source of Organizations in Agreements
 * ERM-198 Ensure that mod-agreements used in place of olf-erm
 * ERM-92  Require UUIDs that are RFC 4122 compliant
   * ERM-135 Change UUID generator from UUID to UUID-2

## 1.5.0 2019-05-07

 * ERM-166 Remove unwanted extra license section
 * ERM-133 Configure Document Categories
 * ERM-143 Add License / Supplementaty License Information Panel UI
 * ERM-181 Fix data sync issue with GOKb (Resumption Token and Broken Coverage)
 * ERM-139 Convert from SearchAndSort to SearchAndSortQuery
 * ERM-79 Set supplementary informaiton for a license
 * ERM-173 Manage Tags on Agreements
 * ERM-174 Seach Agreements by Tag
 * ERM-194 BUGFIX: Opening edit/create license with only one page does not work

## 1.4.0 2019-04-08

 * ERM-115 Provide correct data for agreement line
 * ERM-111 Build Settings Page
 * ERM-112 Build Wrapper Component for supression
 * ERM-113 Use Wrapper Component in Agreements
 * ERM-114 Write tests
 * ERM-98 Rendering Controlling Terms License
 * ERM-127 Resources with no coverage set should not display
 * ERM-110 Agreement Detail record - View attached EBSCO eResource
 * ERM-109 Support the ability to create an agreement from eHoldings
 * ERM-108 Supress agreements app functions
 * ERM-64 Show Controlling License Terms

## 1.3.0 Not issued

## 1.2.0 2019-03-22
 * ERM-130 Sort order of multiple coverage statements should be ascending by start date
 * ERM-129 Cannot edit custom coverage dates once they have been added
 * ERM-65 Manage custom entitlement coverage for titles
 * ERM-91 Indicate the coverage for a title within an Agreement
 * ERM-63 View linked agreement details in a license 

## 1.1.0 2019-03-12
 * ERM-59 Manage licenses linked to agreements
 * ERM-71 Add Model for JSON resource
 * ERM-47 Fix defaults in SubsciriptionAgreementOrg
 * ERM-46 Update note about a license for an agreement
 * ERM-41 Manage external licenses for an Agreement
 * ERM-70 Add LicenseAttachment Domain model
 * ERM-44 Remove an external license from an Agreement
 * ERM-43 Edit external license details
 * ERM-42 Add external license for an Agreement
 * ERM-7 Add an Organisation to a License 

## 1.0.3 2019-02-23

 * ERM-1 eResource Managers can manually create licenses
 * ERM-6 Set pre-defined Terms for a License
 * ERM-7 Add an Organisation to a License
 * ERM-8 Add an Organisation to an existing License
 * ERM-10 Remove an Organisation from a License
 * ERM-11 eResource Managers can edit basic license details
 * ERM-12 Filter License Search Results by License Status
 * ERM-13 Filter License Search Results by License Type
 * ERM-16 Set open-ended License Expiry
 * ERM-17 See basic License details in search results
 * ERM-35 Filter Agreement Search Results by Organisation Role
