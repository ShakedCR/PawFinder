# 🐾 PawFinder - Lost & Found Pets

PawFinder is an Android application that helps users report and find lost or found pets. Users can post information about pets they've lost or found, browse posts from other users, and contact post owners directly.

---

## 📱 Features

- **User Authentication** – Register and login with Firebase Authentication
- **Auto Login** – The app remembers logged-in users between sessions
- **Create Posts** – Upload posts with up to 5 images, pet type, status (Lost/Found), description and location
- **Feed** – Browse all posts with search and filter options (Lost/Found/Dogs/Cats)
- **Pagination** – Posts load progressively as the user scrolls
- **Post Details** – View full post details including images carousel and fun pet facts
- **My Posts** – View, edit and delete your own posts
- **Profile** – View and edit your profile name and photo
- **Contact Owner** – Send an email directly to the post owner
- **Pet Facts API** – Displays a random fun fact about dogs or cats using an external REST API
- **Logout** – Sign out from the app

---

## 🏗️ Architecture

The app follows the **MVVM** (Model-View-ViewModel) architecture pattern:
```
Fragment → ViewModel → Repository → Room (local) / Firebase (remote)
```

- **Room (SQLite)** – Local cache for offline support
- **Firebase Firestore** – Remote database for posts and users
- **Firebase Auth** – User authentication
- **Cloudinary** – Image storage and upload
- **Glide** – Image loading and caching
- **Retrofit** – HTTP client for external REST API calls
- **Navigation Component + SafeArgs** – Fragment navigation

---

## 🛠️ Tech Stack

| Technology | Usage |
|---|---|
| Kotlin | Primary language |
| Android MVVM | Architecture pattern |
| Firebase Auth | User authentication |
| Firebase Firestore | Remote database |
| Room (SQLite) | Local cache |
| Cloudinary | Image storage |
| Retrofit + Gson | REST API calls |
| Glide | Image loading |
| Navigation Component | Fragment navigation |
| SafeArgs | Type-safe navigation arguments |
| Material Design 3 | UI components |
| Coroutines + LiveData | Async operations |

---

## 🌐 External APIs

- **Dog Facts API** – `https://dogapi.dog/api/v2/facts`
- **Cat Facts API** – `https://catfact.ninja/fact`

---

## 👩‍💻 Developers

- Shaked Crissy
- Ofek Nagauker

**Colman College of Management – Android Development Course**
