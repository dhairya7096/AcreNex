# AcreNex — Core Data Dictionary

## users
`id`, `name`, `email`, `mobile`, `role`, `proof_verified`, `user_type`, `department`, `password_hash`, `password_salt`, `created_at`, `last_login`.

## parcels
`ulpin`, `survey_number`, `state`, `district`, `taluka`, `village`, `owner_name`, `area`, `area_unit`, `land_type`, `land_use`, `zoning`, `registration_status`, `encumbrance_status`, `tax_status`, `building_permission_status`, `latitude`, `longitude`, `created_at`, `updated_at`.

## ownership_records
Rights/RoR representation linked by ULPIN: owner, ownership type, share, record date, source, verification status.

## registration_records
Transaction/document linkage: registration ID, ULPIN, document number, date, transaction type, buyer/seller, status.

## tax_records
Annual tax, outstanding amount, financial year, last payment date and status.

## land_use_records
Land use, zoning, master-plan zone, effective date and status.

## building_permissions
Permission number, application/approval dates, building type and status.

## encumbrances
Mortgage/liability type, institution, amount, validity and status.

## restrictions
Restriction type, authority, description, severity, effective dates and status.

## utilities
Utility type, provider, connection status, network reference and last verification.

## valuation_records
Guideline value, market reference, unit, valuation year, source and status.

## environmental_zones
Zone type, authority, buffer, impact level and status.

## disputes
Case number, forum, dispute type, filing date, status and next hearing.

## service_requests
Citizen service code/name, applicant, department, status, timestamps and remarks.

## gis_layers
Layer code/name, category, source department, standard, version, enabled state and access scope.

## api_registry
API name, department, endpoint, HTTP method, version, authentication scheme, status and last sync.

## state_config
State-specific language, area unit, ULPIN profile, record portal and workflow profile.

## data_quality
Completeness, consistency, spatial accuracy, timeliness, check time and status.

## change_events
Baseline/comparison dates, change percentage/type, confidence, status and review-required flag.

## audit_logs
Actor, role, action, resource, timestamp and result. Production systems should make audit storage tamper-evident and centrally governed.
