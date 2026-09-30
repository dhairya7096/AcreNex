# AcreNex Officer Role Matrix

The prototype now exposes dedicated Java + XML workspaces for the following departmental roles:

| Role | Dedicated Activity | Main capabilities |
|---|---|---|
| Revenue Officer | RevenueOfficerDashboardActivity | RoR, ownership, disputes, service requests, AI risk review |
| Registration Officer | RegistrationOfficerDashboardActivity | Document verification, registration applications, status, ownership cross-check, audit |
| Survey / GIS Officer | SurveyGISOfficerDashboardActivity | Cadastral GIS, layers, change detection, ULPIN search, spatial quality |
| Municipal / Planning Officer | PlanningOfficerDashboardActivity | Building permissions, restrictions, planning GIS, master-plan layers, analytics |
| Property Tax Officer | TaxOfficerDashboardActivity | Property tax, tax-linked parcel search, revenue analytics, fiscal quality, audit |
| Dispute / Case Officer | DisputeOfficerDashboardActivity | Cases, ownership evidence, affected parcels, workflow, audit |
| Environment / Restriction Officer | EnvironmentOfficerDashboardActivity | Restriction zones, change detection, environmental GIS, analytics, spatial quality |
| Super Admin / Command Centre | SuperAdminDashboardActivity | State configuration, API/interoperability, data quality, audit, Land Stack hub |

## Demo navigation

`OfficerDashboardActivity` -> `Department Workspaces` -> role selector -> dedicated role dashboard.

Each role dashboard uses its own XML layout and Java Activity and opens existing AcreNex modules relevant to that department.

AI output is presented as decision support. Final administrative actions remain with the authorised officer.

## Officer Sign-up (V4.x)

The login screen now includes a separate **Officer / Department user** onboarding entry. The prototype collects officer identity details, employee ID, mobile, official email, password and department role. Firebase Authentication provides the account identity while Firestore stores the role-bearing profile under `users/{uid}`. After login, the stored role determines the dedicated officer workspace; users are not given a role picker on the normal citizen login screen.

The prototype deliberately excludes self-registration for **Super Admin / Command Centre**. Production deployments should replace demo self-assigned officer onboarding with department verification and server-side approval.
