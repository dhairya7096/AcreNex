# AcreNex
## One Parcel • One ULPIN • Complete Land Intelligence

A Java/XML Android prototype for a parcel-centric Land Stack concept.

### Included
- 16-screen citizen journey from splash to AI assistant.
- GIS parcel visualization using OSMDroid.
- ULPIN-centered parcel record model.
- RoR / ownership / registration / tax / land-use / master-plan / building permission / encumbrance layers.
- Utilities, valuation, environmental screening, restrictions and dispute signals.
- Citizen services, applications and application status.
- Officer dashboard, analytics, alerts and audit trail.
- Interoperability API registry and state configuration.
- Data-quality dashboard.
- AI OCR using Google ML Kit.
- Explainable AI record-risk scan.
- Local Land Guardian reasoning engine that scores parcel governance signals from the Land Stack database without inventing records.
- Document-to-parcel consistency matching.
- GIS/satellite change-detection prototype module.
- Transparent predictive governance indicators.
- Parcel-grounded AI assistant.
- Persistent role-aware local authentication with password hashes, departments, last-login metadata and audit events.

### Demo data
All records are synthetic. No real government credentials, live land records or private departmental secrets are embedded.

### Map/API policy
No Google Maps API key is hard-coded. OSMDroid is used for the prototype map. Retrofit contracts are included for authorized backend integration. Production deployments should use HTTPS, approved identity, role-based authorization and department-approved endpoints.

### Run
Open the `AcreNex` folder in Android Studio and allow Gradle to resolve the declared dependencies. If the local environment cannot access Gradle/Maven repositories, dependency resolution must be completed on a network-enabled Android Studio environment.

### Assets
See `docs/DRAWABLE_ASSETS.md` for optional premium imagery/icons.
### Technical design
See `docs/AcreNex_Technical_Design.md`.
### Demo flow
See `docs/DEMO_CHECKLIST.md`.

## Deep-dive documentation
- `docs/LANDSTACK_DEEP_DIVE.md` — architecture and problem-to-feature mapping.
- `docs/JUDGE_EXPLANATION.md` — concise demonstration and Q&A script.
- `docs/DATA_DICTIONARY.md` — parcel-centric schema reference.
- `docs/API_SCHEMA_EXAMPLES.md` — prototype interoperability contracts.


## Officer / Department Access

AcreNex includes a separate officer onboarding path from the login screen. Officers can create a prototype account with an employee/officer ID and one department role. After authentication, the stored role routes the account to its dedicated operational workspace: Revenue, Registration, Survey/GIS, Municipal/Planning, Property Tax, Dispute/Case, or Environment/Restriction. The Super Admin role is intentionally not self-registrable.

For Firebase builds, the officer account is created in Firebase Authentication and the profile is stored in `users/{uid}` with `accountType=OFFICER`. For production government deployment, replace demo self-assignment with department verification, approval and server-side authorization.
