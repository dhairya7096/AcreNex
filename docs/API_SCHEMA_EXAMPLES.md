# AcreNex — Interoperable API Contract Examples

These are prototype contracts, not live government endpoints.

## GET /v1/parcels/{ulpin}
```json
{
  "ulpin": "24-GJ-GN-0001-00001",
  "surveyNumber": "GN-101",
  "location": {"state": "Gujarat", "district": "Gandhinagar", "village": "Demo Village"},
  "area": {"value": 2.5, "unit": "acre"},
  "landUse": "Residential",
  "zoning": "R1",
  "source": {"department": "Revenue Department", "asOf": "2026-09-01"}
}
```

## GET /v1/ror/{ulpin}
Returns rights/ownership records and verification metadata.

## GET /v1/registration/{ulpin}
Returns registered transaction references and document status.

## GET /v1/gis/layers
Returns available GIS layers, versions, standards and access scopes.

## GET /v1/tax/{ulpin}
Returns fiscal/property-tax linkage and financial year.

## Security contract
Production adapters should use an approved identity and access mechanism, TLS, scoped tokens, request correlation IDs, rate limits, source provenance and auditable access logs.

## Versioning
The prototype uses `/v1/`. Backward-compatible fields may be added within a version; breaking changes should create a new version.

## Error contract
```json
{
  "code": "PARCEL_NOT_FOUND",
  "message": "No authoritative record was returned for the requested ULPIN.",
  "correlationId": "demo-2026-0001"
}
```
