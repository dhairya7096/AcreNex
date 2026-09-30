# AcreNex Firebase

This project contains Firebase Authentication + Firestore + Storage integration code. A real Firebase project cannot be created using someone else's Google account credentials from this environment, so the ZIP intentionally contains no private service-account key.

## Configure
1. Create a Firebase project.
2. Register Android package `com.acrenex.app`.
3. Enable Authentication → Email/Password.
4. Enable Firestore Database.
5. Enable Storage.
6. Copy `firebase.properties.template` to the project root as `firebase.properties` and fill the four client values from the Firebase Android configuration.
7. Sync Gradle and run.

The app automatically uses Firebase when these values are real. If they are still placeholders, the existing local prototype backend remains available instead of crashing.

## Security
Public registration can create **Citizen only**. There is no public Officer registration. Officer accounts must be provisioned by an authorized backend/Admin SDK. Never put a Firebase service-account private-key JSON inside the Android app.

## Cloud collections
`users/{uid}`, `parcels/{ulpin}`, `documents/{documentId}`, `applications/{applicationId}`, `auditLogs/{logId}`.
