# CNSE QR-Code Generator

Ein Service zum Erstellen, Abrufen und Herunterladen von QR-Codes mit Nutzerhistorie.  
Das Projekt besteht aus einem **Spring Boot Backend** (Java 25) und einem **Frontend** (HTML/JS/Nginx).

---

## Features

  - QR-Code erzeugen (Text → QR-Code)
  - QR-Code als PNG herunterladen
  - Firebase Authentication
  - Nutzerhistorie (Firebase Firestore)
  - Cloud Storage (Google Cloud Storage)
  - Vollständig containerisiert
  - Automatisches Deployment via GitHub Actions

---

## Voraussetzungen 

### Docker

  - **Java 25**
  - **Maven**
  - **Docker & Docker Compose**
  - **Firebase Service Account JSON** (firebase/cnse-qr-code-generator-firebase.json)
  - **Umgebungsvariablen** (.env)

### Google Cloud

  - **Google Cloud Konto**
  - **Google Cloud Projekt**
  - **Google Cloud SDK** (`gcloud`)
  - **Aktiver Login** (`gcloud auth login`)
  - **Aktivierte APIs:**
    - Artifact Registry API (artifactregistry.googleapis.com)
    - Firebase Management API (firebase.googleapis.com)
    - Cloud Firestore API (firestore.googleapis.com)
    - Cloud Run Admin API (run.googleapis.com)
    - Secret Manager API (secretmanager.googleapis.com)
    - Cloud Storage API (storage.googleapis.com)
  - **Google Cloud Secret für Firebase Service Account JSON** (firebase-credentials)

---

## Installation & Setup

### Credentials erstellen

**Firebase Service Account erstellen:**
  - Firebase Console → Projekteinstellungen → Dienstkonten
  - "Neuen privaten schlüssel generieren" → JSON herunterladen
  - Datei speichern als: `firebase/cnse-qr-code-generator-firebase.json`

**Google Cloud Services Account erstellen:**
  - Google Cloud Console → IAM und Verwaltung → Dienstkonten
  - "Dienstkonto erstellen" klicken
  - Name und Rollen zuweisen
  - Aktionen → Schlüssel verwalten
  - "Schlüssel hinzufügen → Neuen Schlüssel erstellen" klicken
  - "JavaScript Object Notation" wählen
  - "Erstellen" → JSON herunterladen
  - Datei speichern als: `cloud/cnse-qr-code-generator.json`

**Umgebungsvariablen setzen:**
  - .env Datei mit Keys aus .env.example erstellen
  - Values einfügen

---

### Docker Compose

```bash
docker compose up -d --build
```

**Zugriff:**
  - Frontend: http://localhost:8081
  - Backend: http://localhost:8080

---

### Google Cloud

Bei jedem Push auf `main` wird automatisch deployed:

**GitHub Secrets konfigurieren:**
  - `GCP_SA_KEY_B64`: Base64-encodierter Service Account Key
  - `FIREBASE_CREDENTIALS_B64`: Base64-encodierte Firebase Credentials
   
```bash
# Service Account Key encodieren
cat gcp-service-account.json | base64 -w 0 > gcp-key-b64.txt
   
# Firebase Credentials encodieren
cat firebase-credentials.json | base64 -w 0 > firebase-creds-b64.txt
```

**Firebase Secret in GCP Secret Manager erstellen:**
```bash
gcloud secrets create firebase-credentials --data-file=firebase/cnse-qr-code-generator-firebase.json
```

**Service Account Berechtigungen:**
```bash
SERVICE_ACCOUNT="YOUR-SA@cnse-qr-code-generator.iam.gserviceaccount.com"
   
# Storage für GCR/Artifact Registry
gcloud projects add-iam-policy-binding cnse-qr-code-generator \
  --member="serviceAccount:${SERVICE_ACCOUNT}" \
  --role="roles/storage.admin"
   
# Cloud Run Deployment
gcloud projects add-iam-policy-binding cnse-qr-code-generator \
  --member="serviceAccount:${SERVICE_ACCOUNT}" \
  --role="roles/run.admin"
   
# Service Account User
gcloud projects add-iam-policy-binding cnse-qr-code-generator \
  --member="serviceAccount:${SERVICE_ACCOUNT}" \
  --role="roles/iam.serviceAccountUser"
   
# Secret Manager Zugriff
gcloud projects add-iam-policy-binding cnse-qr-code-generator \
  --member="serviceAccount:${SERVICE_ACCOUNT}" \
  --role="roles/secretmanager.secretAccessor"
```

**Push zu GitHub → Automatisches Deployment**

### Manuelles Deployment

**Backend**
```bash
# Docker Image bauen & pushen
docker build -t gcr.io/cnse-qr-code-generator/backend:latest ./backend
docker push gcr.io/cnse-qr-code-generator/backend:latest

# Zu Cloud Run deployen
gcloud run deploy backend-service --image gcr.io/cnse-qr-code-generator/backend:latest --platform managed --region europe-west1 --allow-unauthenticated --set-secrets="/secrets/firebase/credentials.json=firebase-credentials:latest" --set-env-vars="SPRING_PROFILES_ACTIVE=prod,GCP_PROJECT_ID=cnse-qr-code-generator,GCS_BUCKET=cnse-qr-code-generator-qr-codes,FIREBASE_CREDENTIALS_PATH=/secrets/firebase/credentials.json,ALLOWED_ORIGINS=https://frontend-service-162846799968.europe-west1.run.app"
# --min-instances 1   optional gegen cold starts
```

**Frontend**
```bash
# Docker Image bauen & pushen
docker build -t gcr.io/cnse-qr-code-generator/frontend:latest ./frontend
docker push gcr.io/cnse-qr-code-generator/frontend:latest

# Zu Cloud Run deployen
gcloud run deploy frontend-service --image gcr.io/cnse-qr-code-generator/frontend:latest --platform managed --region europe-west1 --allow-unauthenticated --port=80 --set-env-vars="BACKEND_URL=https://backend-service-162846799968.europe-west1.run.app"
```

---

## Cleanup

### Docker Compose stoppen
```bash
docker compose down
docker compose down -v  # mit Volumes löschen
```

### Cloud Run Services löschen
```bash
gcloud run services delete backend-service --region europe-west1 --quiet
gcloud run services delete frontend-service --region europe-west1 --quiet
```

## Projektstruktur

```
.
│   .env  # Git-ignored
│   .env.example
│   .gitignore
│   docker-compose.yml
│   README.md
│
├───.github
│   └───workflows
│           CI-CD.yml
│
├───backend
│   │   .gitattributes
│   │   Dockerfile
│   │   mvnw
│   │   mvnw.cmd
│   │   pom.xml
│   │
│   ├───.mvn
│   │
│   └───src
│       ├───main
│       │   ├───java
│       │   └───resources
│       │
│       └───test
│           ├───java
│           └───resources
│
├───cloud
│       cnse-qr-code-generator.json  # Git-ignored
│
├───firebase
│       cnse-qr-code-generator-firebase.json  # Git-ignored
│
└───frontend
    │   Dockerfile
    │
    └───public
        │   favicon.ico
        │   index.html
        ├───css
        └───js
```