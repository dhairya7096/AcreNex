# AcreNex — Land Stack Deep-Dive

## 1. One-line concept
AcreNex is a parcel-centric, GIS-first Land Stack prototype that connects fragmented land governance datasets around a common ULPIN and exposes the same parcel to citizens and authorised departments through role-based workflows, explainable AI, audit trails and interoperable API contracts.

## 2. Problem-to-architecture mapping

| Problem statement need | AcreNex response | Demo evidence |
|---|---|---|
| Fragmented departmental systems | Parcel-centric integration layer | Parcel 360 |
| Cadastral maps + ULPIN | GIS parcel identity | GIS Map + Parcel 360 |
| RoR / ownership | Ownership records | Land Records / Ownership |
| Registration | Registration layer | Registration Officer workspace |
| Master plan / zoning | Planning layer | Planning Officer + Layer Control |
| Building permission | Building workflow | Building Permission |
| Encumbrance / mortgage | Liability layer | Restrictions & Encumbrances |
| Property taxation | Fiscal layer | Tax Officer + Property Tax |
| Utilities | Network/service layer | Utilities |
| Environmental/restriction zones | Spatial overlay | Environment Officer |
| Disputes | Case layer | Dispute Officer |
| Citizen service delivery | Service requests + applications | Citizen Services |
| Interoperability | API registry + versioned contracts | Interoperability |
| Role-based access | Citizen + department roles | Officer Role Selection |
| Auditability | Immutable-style append-only demo log | Audit Log |
| AI/ML | Explainable parcel reasoning + OCR + change detection | AI Governance Review |
| Predictive analytics | Governance indicators | Predictive Insights |

## 3. Parcel-centric data model

`ULPIN` is the common reference in the prototype. A parcel record can be linked to:

- cadastral geometry / survey reference
- ownership / RoR
- registration
- tax
- land use / zoning / master plan
- building permission
- encumbrance
- restrictions
- utilities
- valuation
- environmental screening
- disputes
- documents
- service requests
- data-quality metrics
- change-detection events
- AI alerts

The prototype uses synthetic/demo records. Production deployment must replace them with authorised departmental data sources.

## 4. Three-layer GIS concept

### Foundation layer
- Georeferenced cadastral parcel boundaries
- Survey number / parcel identifier
- ULPIN
- coordinates

### Core governance layers
- RoR / ownership
- registration
- master plan / zoning
- building permission
- encumbrance / mortgage
- land use

### Extended use-case layers
- property tax
- valuation
- utilities
- restrictions
- environment
- disputes
- infrastructure/service links
- change detection

## 5. AI design

AcreNex uses a **human-in-the-loop** approach.

### AI modules
1. **Land Stack AI Engine** — scores consistency of linked parcel signals.
2. **Document OCR** — extracts text from land documents using ML Kit.
3. **Document/parcel matching** — compares extracted information with parcel metadata.
4. **Change detection** — stores a synthetic geospatial change event and review requirement.
5. **Predictive governance indicators** — highlights operational patterns for officers.
6. **AI assistant** — explains parcel-linked information in citizen-friendly language.

### AI guardrail
AI does not approve ownership, mutation, registration, building permission or dispute outcomes. It produces evidence, confidence, flags and recommended routing. Statutory decisions remain with authorised officers.

## 6. Interoperability

The API registry represents an adapter pattern:

`AcreNex Mobile → AcreNex Integration Layer → Department Adapter → Authorised State/Department API`

Each adapter contract should define:
- endpoint
- method
- API version
- authentication scheme
- request schema
- response schema
- source department
- sync timestamp
- error mapping
- data provenance

The demo uses placeholder contracts and synthetic data. No government credential is fabricated.

## 7. Role model

- Citizen
- Revenue Officer
- Registration Officer
- Survey / GIS Officer
- Municipal / Planning Officer
- Property Tax Officer
- Dispute / Case Officer
- Environment / Restriction Officer
- Super Admin / Command Centre

Each role receives a purpose-specific dashboard rather than a generic officer page.

## 8. Security model

Prototype controls:
- local session management
- role-aware navigation
- password hashing + per-user salt for demo login persistence
- authenticated API interceptor structure
- audit log
- least-privilege UI by role

Production controls should additionally include department identity federation, device/session policy, encryption at rest, key management, secrets management, certificate pinning where appropriate, central IAM, security monitoring and formal audit/compliance controls.

## 9. Data quality

The data-quality layer tracks:
- completeness
- consistency
- spatial accuracy
- timeliness
- last checked time
- quality status

This allows an officer to distinguish “no data” from “verified data” and avoids presenting missing records as clean records.

## 10. Workflow model

`Request → Identity → Department validation → Cross-layer checks → AI evidence → Officer decision → Audit → Citizen notification`

The workflow is designed to demonstrate interoperability rather than pretending that all departments share one database.

## 11. State configurability

The state configuration table stores examples of:
- state code/name
- language
- area unit
- ULPIN format profile
- record portal reference
- workflow profile

The goal is to show how one national architecture can adapt to different state terminology, units and workflows.

## 12. Demo dataset

The seeded records are synthetic and clearly intended for demonstration. They should never be represented to judges as live government records.

## 13. Recommended production evolution

1. Replace demo SQLite sources with authorised APIs/data exchange.
2. Move GIS geometry to PostGIS or an equivalent spatial platform.
3. Implement OGC-compliant services and metadata.
4. Add central IAM and department federation.
5. Add message/event bus for asynchronous departmental workflows.
6. Introduce data lineage and source-of-truth metadata.
7. Add model registry, evaluation datasets and human review feedback for AI.
8. Add observability, rate limiting, API gateway and disaster recovery.
9. Deploy state-specific adapters without changing the citizen UI contract.
10. Establish governance, privacy, retention and security policies before production use.
