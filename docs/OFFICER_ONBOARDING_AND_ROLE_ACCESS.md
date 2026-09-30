# AcreNex Officer Onboarding & Role Access

## Implemented

- Separate **Officer / Department user** onboarding entry from the normal citizen registration flow.
- Officer registration collects:
  - Full name
  - Employee / Officer ID
  - Official email
  - Mobile number
  - Department role
  - Password and confirmation
- Firebase-enabled builds create the account in Firebase Authentication and write the officer profile to `users/{uid}` in Firestore.
- The profile stores `accountType=OFFICER`, the selected role, employee ID, department and account status.
- Local/demo builds mirror the same role in the local user database.
- Login reads the role from the stored account profile and routes the user to the matching officer workspace.

## Department workspaces

- Revenue Officer → RoR, ownership, mutation, disputes and services
- Registration Officer → documents, applications, status, ownership and audit
- Survey / GIS Officer → GIS map, layers, change detection, parcel search and data quality
- Municipal / Planning Officer → building permissions, restrictions, maps, layers and analytics
- Property Tax Officer → tax records, parcel search, analytics, data quality and audit
- Dispute / Case Officer → disputes, ownership, parcel review, workflow and audit
- Environment / Restriction Officer → restrictions, change detection, GIS, analytics and data quality
- Super Admin / Command Centre → cross-department configuration, interoperability, quality, audit and Land Stack hub

## Security boundary

For this student/hackathon prototype, the officer onboarding form can create a role-bearing account so the complete flow can be demonstrated. A production government deployment should **not** allow public self-assignment of privileged roles. Instead, the server should create a `PENDING` officer request, verify employee/department identity, require authorized approval, and enforce role permissions server-side through Firebase Security Rules or a trusted backend.
