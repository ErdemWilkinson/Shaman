# Shaman — AI-Powered Mobile Health Pre-Assessment App

Shaman is an Android health pre-assessment application, powered by the Anthropic Claude AI
model, that lets users enter their symptoms and see possible conditions along with probability
percentages. This project was developed as a graduation project for the Computer Programming
Program at Karadeniz Technical University, Trabzon Vocational School.

> ⚠️ **Warning:** Shaman is not a substitute for a doctor's diagnosis or a clinical examination.
> It is only an informational tool that helps individuals make an informed pre-assessment before
> seeing a doctor.

The project's full academic report is available in
[`docs/Shaman-Proje-Raporu.pdf`](docs/Shaman-Proje-Raporu.pdf).

## Features

- **Symptom Flow:** A six-step dynamic questionnaire that collects information on body region,
  symptoms, complaint duration, severity, family history, and lifestyle.
- **AI Analysis:** The collected information is sent to the Anthropic Claude API, which returns a
  list of possible conditions with their commonly used Turkish names and probability percentages.
- **Condition Details:** A detail screen for the selected condition summarizing its causes,
  symptoms, treatment suggestions, and when to see a doctor.
- **User Accounts:** Email/password-based registration and login via Firebase Authentication.
- **Analysis History:** Analysis history stored per user in Cloud Firestore.

## Technologies Used

| Layer                | Technology                                    |
|---------------------|------------------------------------------------|
| Language             | Kotlin                                        |
| UI                   | Jetpack Compose, Navigation Compose           |
| Authentication       | Firebase Authentication                       |
| Database             | Cloud Firestore (NoSQL)                       |
| Networking            | Retrofit, OkHttp                              |
| AI                    | Anthropic Claude API (`claude-haiku-4-5`)     |

## Architecture

```
app/src/main/java/
├── com/erdem/shaman/          # MainActivity, theme
├── network/                   # ClaudeApi, ClaudeRepository, ClaudeRequest
└── ui/
    ├── navigation/             # AppNavigation
    └── screens/                 # Login, Register, SymptomFlow, Result, DiseaseDetail, Profile
```

- **UI layer:** Screens written with Jetpack Compose, with transitions managed by
  Navigation Compose.
- **Data layer:** Firebase Authentication (authentication) and Cloud Firestore (analysis history
  stored under the `users/{userId}/history` subcollection structure).
- **AI integration layer:** Claude API calls via Retrofit/OkHttp; the API key and required
  headers are added to requests through an OkHttp interceptor.

## Setup

For security reasons, this repository **does not include** any API keys or Firebase
configuration. To run the app in your own environment, follow the steps below.

### 1. Clone the repository

```bash
git clone https://github.com/ErdemWilkinson/Shaman.git
cd Shaman
```

### 2. Firebase configuration

1. Create a new project in the [Firebase Console](https://console.firebase.google.com/).
2. Add an Android app to the project with the package name `com.erdem.shaman`.
3. Enable the Authentication (Email/Password) and Cloud Firestore services.
4. Copy the downloaded `google-services.json` file into the `app/` folder (see
   [`app/google-services.json.example`](app/google-services.json.example) for a template).

### 3. Claude API key

1. Create an API key via the [Anthropic Console](https://console.anthropic.com/).
2. Copy the [`local.properties.example`](local.properties.example) file in the project root to
   `local.properties` and fill in the `CLAUDE_API_KEY` value with your own key:

   ```properties
   sdk.dir=/path/to/Android/Sdk
   CLAUDE_API_KEY=your-anthropic-api-key-here
   ```

   `local.properties` is not tracked by git; your key stays only on your local machine and is
   injected into the code as `BuildConfig.CLAUDE_API_KEY` at build time.

### 4. Firestore security rules

To ensure users can only access their own data, it is recommended to set your Firestore
security rules as follows:

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /users/{userId} {
      allow read, write: if request.auth != null && request.auth.uid == userId;
      match /history/{document=**} {
        allow read, write: if request.auth != null && request.auth.uid == userId;
      }
    }
  }
}
```

### 5. Run the app

You can open the project in Android Studio and run it on an emulator or physical device, or
build it from the command line:

```bash
./gradlew assembleDebug
```

## Security Notes

- This repository **contains no real API keys or Firebase credentials.**
- API keys are included in the app only via `local.properties` (ignored by git) through
  `BuildConfig`.
- For a production environment, it is recommended that API keys be managed through a backend
  proxy rather than kept directly in the mobile client.

## License

This project was developed as an academic graduation project. Please contact the repository
owner regarding terms of use.
