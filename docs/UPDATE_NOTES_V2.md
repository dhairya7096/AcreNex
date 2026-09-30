# AcreNex Premium Update V2

This build is based on the previously working Gradle-fixed project and keeps the existing Java/XML architecture.

## User-requested changes
- Real AcreNex logo is declared as the Android application/launcher icon instead of the default Android icon.
- Splash screen reduced to logo, brand, one concise Land Stack tagline and a small GIS/AI/Governance line.
- DigiLocker login button and obsolete activity removed from the login flow; no external identity redirect is used.
- Login now supports Citizen or Government Officer access. Officer roles are selectable in the same login screen and route to the dedicated role dashboard.
- Local SQLite user persistence now stores role, department, last-login metadata and a PBKDF2-HMAC-SHA256 password hash with a random salt. Plaintext passwords are not stored.
- Database version 3 uses additive migration so existing local records are preserved.
- Citizen home page replaces the old completeness/record-health section with an explainable AcreNex AI Land Guardian card.
- Bottom navigation icons are larger and use dedicated icon/label stacks.
- Premium card spacing, hierarchy, Land Stack service grouping and AI presentation were refreshed.

## AI design
`LandStackAIEngine` is intentionally local and explainable. It scores only parcel-linked fields already present in the prototype database: ownership, registration, tax, encumbrance, land-use/zoning, building permission, linked documents and restriction signals. It reports reasons and a next action instead of fabricating government facts. This is suitable for a prototype/demo and is not a substitute for an authorized departmental ML/AI service.

## API policy
No fake government API key or unauthorized live government endpoint is embedded. The existing Retrofit/API registry remains ready for authorized department integrations. Real state/department APIs require official access, credentials and security approval.

## Validation performed in this environment
- XML parsing: all layout/resource XML files parsed successfully.
- Duplicate drawable name: `ic_acrenex_logo.xml` was removed so `ic_acrenex_logo.png` is the single logo resource.
- Java `R.id`, `R.layout`, and `R.color` references checked against resources.
- Manifest activity source files checked.
- Java syntax scan showed no Java syntax-error patterns; a full Android compile still depends on the user's local Android SDK/Gradle dependency cache.
