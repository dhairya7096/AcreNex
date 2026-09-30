# AcreNex V4.1 — ULPIN Command Model & GIS/Document Upgrade

## What changed

### 1. Officer section reduced to three categories
- **Agencies & Local Administration**
  - Municipal Corporation / ULB
  - District Administration
  - ROR / Revenue Office
- **Land Records & Transactions**
  - Registration Officer
  - Survey / GIS Officer
  - Property Tax Officer
- **Regulation, Risk & Casework**
  - Dispute / Case Officer
  - Environment / Restriction Officer
  - Super Admin / Command Centre

The existing officer activities are retained; only their presentation/navigation is reorganized.

### 2. One ULPIN command panel
`Parcel360Activity` now presents parcel identity, coordinates, ownership/RoR, registration, land use, building permission, tax, encumbrance, restrictions, utilities, valuation, disputes, service workflow and AI/change signals from the same ULPIN.

### 3. GIS upgrade
- Runtime location permission added.
- Current device location can be displayed on the OSMDroid map.
- A selected ULPIN can center the map on its stored latitude/longitude.
- Synthetic demo parcel vertices are stored in `parcel_vertices` and rendered as a polygon.
- Production note is shown in UI: authorized cadastral/GIS layers must replace synthetic geometry for authoritative boundaries.

### 4. Shared document checklist
The document centre now shows an A-to-Z **land purchase / transfer** checklist. Required vs conditional documents are distinguished. A citizen can select a PDF from the device and link it to the ULPIN; officers see the same ULPIN-linked document records for review.

Checklist includes identity proof, PAN, address proof, title/sale deed, RoR/7-12/property card, 8-A/land account record, mutation, encumbrance, property tax, cadastral map, sale agreement, stamp/registration receipt, NA/land-use permission, building permission/NOC, mortgage release, POA, succession documents, court/dispute order, applicable RERA/project details and possession/handover record.

Exact requirements remain state/service/land-type dependent.

### 5. Demo dataset
Five synthetic Gujarat-located parcel records are seeded for demonstration. The coordinates correspond to plausible city areas, but the parcel ownership, survey numbers, ULPIN-like identifiers and records are **synthetic** and are not live government records.

### 6. Database additions
- `parcel_vertices`
- `document_requirements`
- Database version incremented to 4 with upgrade creation for the new tables.

## Important ULPIN note
The Department of Land Resources describes ULPIN/Bhu-Aadhaar as a unique parcel identifier generated from geo-referenced parcel vertices. AcreNex's sample identifiers are demo identifiers only; a production AcreNex deployment should consume the authoritative ULPIN assigned by the relevant revenue administration.
