# QuizApp Server

![Architecture Diagram](https://raw.githubusercontent.com/darshan-miskin/storage/refs/heads/master/Quiz%20App%20Architecture.png)

QuizApp Server is an Android backend service designed to provide quiz data to multiple client applications using **AIDL (Android Interface Definition Language)**. It acts as a centralized "Server" app that fetches quiz content from a remote API and manages client sessions and progress.

## 🚀 Key Features

- **Inter-Process Communication (IPC):** Uses AIDL to allow external applications to securely fetch quiz data and receive callbacks.
- **Multi-Client Support:** Independently tracks quiz progress for multiple client applications simultaneously using `ConcurrentHashMap` and Binder UID tracking.
- **Asynchronous Data Handling:** Leverages **Kotlin Coroutines** and **Flow** for non-blocking network operations and data streaming.
- **Reactive Architecture:** Fetches real-time quiz data via **Retrofit** and provides updates to clients through remote callbacks.
- **Dependency Injection:** Fully powered by **Dagger Hilt** for clean, testable, and modular code.

## 🛠 Tech Stack

- **Language:** Kotlin
- **UI Framework:** Jetpack Compose (for server main page)
- **Networking:** Retrofit & Gson
- **DI Framework:** Dagger Hilt
- **Background Tasks:** Android Bound Services & Coroutines
- **IPC:** AIDL (Android Interface Definition Language)

## 📁 Project Structure

- `aidl/`: Contains the AIDL definitions for IPC
- `data/`:
    - `QuizBindService.kt`: The core Bound Service handling client connections and AIDL implementations.
    - `QuizRepository.kt`: Manages data fetching and state logic.
    - `QuizApiService.kt`: Retrofit definitions for fetching quiz content.
- `di/`: Hilt modules for providing networking and repository dependencies.
- `presentation/`: Simple Jetpack Compose UI to monitor service status.
  
## 🔌 AIDL Interfaces

### `IQuizDataInterface`
Allows clients to:
- Register for callbacks.
- Start the quiz.
- Retrieve the next available question.
  
### `IQuizCallBackInterface`
A `oneway` interface allowing the Server to push updates to clients, such as:
- `onQuizLoaded()`: When data is ready.
- `onError()`: When a network or processing error occurs.
- `onQuizComplete()`: When the quiz is complete.
- `onRestart()`: When the quiz needs to be restarted.

## 📦 Prerequisites

To try out this application, you can install the [QuizApp Client](https://github.com/darshan-miskin/QuizApp-Client) on the same device.
