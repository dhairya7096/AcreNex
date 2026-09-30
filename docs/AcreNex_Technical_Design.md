# AcreNex — Land Stack Technical Design

## 1. Purpose
AcreNex is a parcel-centric prototype for integrated land governance. The design keeps the **ULPIN** as the common reference key and links spatial and administrative records around that parcel.

## 2. Problem-to-feature mapping
| Land Stack requirement | AcreNex prototype |
|---|---|
| Georeferenced cadastral maps | OSMDroid GIS map + parcel coordinates + polygon/marker |
| ULPIN | ULPIN is used as the parcel primary key in the local schema |
| RoR / ownership | ownership_records + Land Records + Ownership screens |
| Registration | registration_records + parcel status |
| Master plan / zoning | land_use_records + GIS layer registry |
| Building permissions | building_permissions + dedicated screen |
| Encumbrance / mortgage | encumbrances + Restrictions screen |
| Land use | land_use_records + parcel intelligence |
| Property taxation | tax_records + Property Tax screen |
| Valuation | valuation_records + reference-only valuation view |
| Utilities | utilities + Utility Infrastructure screen |
| Environmental / restrictions | environmental_zones + restrictions |
| Disputes | disputes + controlled dispute screen |
| Citizen services | service_requests + applications + status |
| Interoperability | api_registry + versioned API contracts |
| Role-based access | Citizen / Officer / Admin session roles and server-ready RBAC contracts |
| Audit trail | audit_logs + AI action logging |
| AI/ML | OCR, consistency matching, risk engine, change detection, predictive indicators, assistant |
| Data quality | completeness, consistency, spatial accuracy and timeliness metrics |
| State diversity | state_config for language, units, workflow and record portal |

## 3. Spatial architecture
`ULPIN -> Parcel geometry -> GIS layers -> governance records -> citizen services`

The mobile prototype uses OSMDroid so the app can demonstrate maps without embedding a paid map API key. Production deployments can replace the map tile/provider layer with an authority-approved GIS service.

## 4. Interoperability contract
The mobile app contains Retrofit interfaces for:
- health
- authentication
- identity/proof verification
- parcel retrieval/search
- document analysis
- risk scoring
- applications
- notifications

The local `api_registry` is a demonstration registry of versioned departmental contracts. Production endpoints must be supplied by the authorized state/department platform and must use HTTPS, approved authentication, authorization, rate limiting and audit logging.

## 5. Security
- Local session token abstraction.
- Role-aware citizen/officer/admin flows.
- Audit events for AI queries, risk scans and OCR.
- No production government credential or secret is hard-coded.
- No real DigiLocker credentials are requested in the prototype.
- Server-side authorization is required for production; mobile UI checks are not a security boundary.

## 6. AI governance
### AI Document OCR
On-device ML Kit extracts text from a selected image.

### AI Record Consistency
Owner, area and ULPIN fields can be compared transparently between document-derived values and a parcel record.

### AI Record Health / Risk
A rule-based, explainable risk engine flags configured signals such as mismatch, encumbrance, dispute and tax-pending status.

### Geospatial Change Detection
A backend-ready module accepts validated change metrics and creates a review signal. The current mobile demo uses synthetic metrics and does **not** claim to perform authoritative satellite classification.

### Predictive Governance
The prototype exposes transparent workload indicators. Production ML should be trained and validated using authorized, quality-controlled, appropriately governed data.

## 7. Data-quality model
Each parcel can be assessed across:
- completeness
- consistency
- spatial accuracy
- timeliness

These indicators are intended for data stewardship and operational review, not as legal determinations.

## 8. Deployment path
### Prototype
Android + Java/XML + SQLite + OSMDroid + ML Kit + Retrofit.

### Production
Android/web citizen interfaces -> API gateway -> state land-service adapters -> departmental systems -> spatial database/GIS services -> audit/security platform.

A recommended production spatial backend is PostgreSQL/PostGIS or another authority-approved spatial data platform. This is an architecture recommendation, not an implemented external dependency in the offline prototype.

## 9. Standards alignment
The design uses parcel identifiers, GIS layers, versioned APIs, metadata, RBAC, auditability and configurable state profiles. ULPIN is treated as the common parcel reference. Production implementations should follow the standards and specifications mandated by the Department of Land Resources and the concerned State/UT.

## 10. Demo-data disclaimer
All sample names, records, valuation references, departmental endpoints and workflow values in this Android prototype are synthetic. They are included solely to demonstrate the Land Stack concept and must not be represented as live government records.
