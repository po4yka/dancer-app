# Dancer app

![Android build](https://github.com/po4yka/dancer-app/actions/workflows/android_build.yml/badge.svg?branch=main)
![min API](https://img.shields.io/badge/API-24%2B-brightgreen.svg?style=flat)
![License](https://img.shields.io/github/license/po4yka/dancer-app.svg)
![Language](https://img.shields.io/github/languages/top/po4yka/dancer-app?color=blue&logo=kotlin)

> Advanced Android application for real-time pose detection and dance move classification using TensorFlow Lite and Clean Architecture.

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Technology Stack](#technology-stack)
- [Getting Started](#getting-started)
- [Building the Project](#building-the-project)
- [Testing](#testing)
- [Documentation](#documentation)
- [Contributing](#contributing)
- [License](#license)

---

## Overview

Dancer App is a sophisticated Android application that uses machine learning to detect and classify dance poses in real-time. Built with modern Android development practices, the app showcases clean architecture, dependency injection, and reactive programming patterns.

**Key Highlights:**
- Real-time pose detection using TensorFlow Lite
- Clean Architecture with clear layer separation (9/10 architecture score)
- Jetpack Compose UI with Material Design 3
- Dependency Injection with Hilt
- Reactive state management with Kotlin Flows
- Camera integration with CameraX

---

## Features

### Real-Time Pose Detection
- Analyze camera frames in real-time using TensorFlow Lite
- GPU acceleration support for better performance
- Adjustable detection threshold
- Mirror mode for front-facing camera

### User-Friendly Interface
- Modern UI built with Jetpack Compose
- Bottom navigation for easy screen switching
- Real-time results display with confidence scores
- Camera controls (capture, lens switch, recognition toggle)

### Configuration Management
- Persistent settings using DataStore
- Customizable detection threshold
- Mirror mode toggle
- Configuration survives app restarts

### Gallery Integration
- Custom gallery picker
- Image selection and processing
- Pose analysis on static images

---

## Architecture

The app follows **Clean Architecture** principles with three distinct layers:

```
┌─────────────────────────────────────┐
│     Presentation Layer              │
│  (UI, ViewModels, Screens)          │
└──────────────┬──────────────────────┘
               ↓ depends on
┌──────────────┴──────────────────────┐
│       Domain Layer                  │
│  (Use Cases, Models, Repositories)  │
└──────────────┬──────────────────────┘
               ↓ implements
┌──────────────┴──────────────────────┐
│        Data Layer                   │
│  (Repositories, Data Sources)       │
└─────────────────────────────────────┘
```

**Architecture Score: 9/10**

### Why Clean Architecture?

- **Testability**: Each layer can be tested independently
- **Maintainability**: Clear structure and responsibilities
- **Scalability**: Easy to add new features without breaking existing code
- **Flexibility**: Easy to swap implementations (e.g., change data sources)
- **Independence**: Domain layer is framework-agnostic

For detailed architecture information, see [ARCHITECTURE.md](ARCHITECTURE.md).

---

## Technology Stack

### Core
- **Language**: Kotlin 1.9+
- **Min SDK**: 24 (Android 7.0)
- **Target SDK**: 34 (Android 14)
- **Build Tool**: Gradle (Kotlin DSL)

### UI Framework
- **Jetpack Compose** - Declarative UI framework
- **Material Design 3** - Modern design system
- **Compose Navigation** - Type-safe navigation
- **Accompanist** - Compose utilities (permissions, system UI)

### Architecture Components
- **ViewModel** - UI state management
- **StateFlow** - Reactive state
- **Lifecycle** - Lifecycle-aware components
- **DataStore** - Persistent preferences

### Dependency Injection
- **Hilt** - Dependency injection framework
- **Dagger** - Compile-time DI

### Machine Learning
- **TensorFlow Lite** - On-device ML inference
- **TensorFlow Lite GPU** - GPU acceleration
- **TensorFlow Lite Support** - Image processing

### Camera
- **CameraX** - Modern camera API
- **Camera2** - Low-level camera access

### Asynchronous Programming
- **Kotlin Coroutines** - Async operations
- **Flow** - Reactive streams

### Other
- **Coil** - Image loading
- **Timber** - Logging
- **Ktlint** - Code linting
- **Detekt** - Static analysis

---

## Getting Started

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- JDK 11 or later
- Android SDK with API 34
- Git

### Clone the Repository

```bash
git clone https://github.com/po4yka/dancer-app.git
cd dancer-app
```

### Open in Android Studio

1. Open Android Studio
2. Select "Open an Existing Project"
3. Navigate to the cloned repository
4. Wait for Gradle sync to complete

---

## Building the Project

### Debug Build

```bash
./gradlew assembleDebug
```

The APK will be in `app/build/outputs/apk/debug/`

### Release Build

1. Create `app/signing.properties` with your signing configuration:
```properties
storeFilePath=/path/to/keystore.jks
storePassword=your_store_password
keyPassword=your_key_password
keyAlias=your_key_alias
```

2. Build release APK:
```bash
./gradlew assembleRelease
```

The APK will be in `app/build/outputs/apk/release/`

### Build Variants

- **debug**: Development build with debugging enabled
- **release**: Production build with ProGuard/R8 optimization

---

## Testing

### Unit Tests

Run all unit tests:
```bash
./gradlew test
```

Run specific test class:
```bash
./gradlew test --tests CameraViewModelTest
```

### Instrumented Tests

Run instrumented tests on connected device:
```bash
./gradlew connectedAndroidTest
```

### Code Coverage

Generate coverage report:
```bash
./gradlew testDebugUnitTest jacocoTestReport
```

View report at `app/build/reports/jacoco/jacocoTestReport/html/index.html`

### Testing Strategy

The app follows the **Testing Pyramid**:

- **70% Unit Tests**: ViewModels, Use Cases, Repositories
- **20% Integration Tests**: Repository + DataSource interactions
- **10% UI/E2E Tests**: Complete user flows

**Coverage Targets:**
- ViewModels: 90%+
- Use Cases: 95%+
- Repositories: 85%+
- Overall: 80%+

For detailed testing guide, see [TESTING_GUIDE.md](TESTING_GUIDE.md).

---

## Documentation

### Architecture Documentation

- [ARCHITECTURE.md](ARCHITECTURE.md) - Complete architecture overview
  - Clean Architecture layers
  - Package structure
  - Design patterns
  - How to add new features

### Migration Guide

- [MIGRATION_GUIDE.md](MIGRATION_GUIDE.md) - Guide to migrate from legacy to clean architecture
  - Before/After comparison
  - Breaking changes
  - Step-by-step migration
  - Common patterns
  - Troubleshooting

### Testing Guide

- [TESTING_GUIDE.md](TESTING_GUIDE.md) - Comprehensive testing guide
  - Testing each layer
  - Mocking strategies
  - Example test cases
  - CI/CD setup
  - Best practices

### Dependency Injection

- [DEPENDENCY_GRAPH.md](DEPENDENCY_GRAPH.md) - DI graph visualization
  - Module overview
  - Scope explanations
  - How to add dependencies
  - Common DI patterns

### Additional Documentation

- [REFACTORING_REPORT.md](REFACTORING_REPORT.md) - Detailed refactoring report
- [REFACTORING_COMPARISON.md](REFACTORING_COMPARISON.md) - Before/After code comparison
- [ARCHITECTURE_DIAGRAM.md](ARCHITECTURE_DIAGRAM.md) - Visual architecture diagrams

---

## Project Structure

```
dancer-app/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/po4yka/dancer/
│   │   │   │   ├── ui/              # Presentation layer
│   │   │   │   │   ├── components/  # Reusable UI components
│   │   │   │   │   ├── screens/     # Screen composables
│   │   │   │   │   ├── viewmodel/   # ViewModels
│   │   │   │   │   └── theme/       # Theming
│   │   │   │   ├── domain/          # Domain layer
│   │   │   │   │   ├── model/       # Domain models
│   │   │   │   │   ├── repository/  # Repository interfaces
│   │   │   │   │   └── usecase/     # Use cases
│   │   │   │   ├── data/            # Data layer
│   │   │   │   │   ├── datasource/  # Data sources
│   │   │   │   │   ├── repository/  # Repository implementations
│   │   │   │   │   └── models/      # Data models
│   │   │   │   ├── di/              # Dependency injection
│   │   │   │   ├── navigation/      # Navigation setup
│   │   │   │   └── utils/           # Utilities
│   │   │   └── res/                 # Resources
│   │   └── test/                    # Unit tests
│   │   └── androidTest/             # Instrumented tests
│   └── build.gradle.kts
├── gallerypicker/                   # Gallery picker module
├── buildSrc/                        # Build configuration
├── docs/                            # Documentation
└── README.md
```

---

## Dependencies

### Core Dependencies

```kotlin
// Jetpack Compose
implementation("androidx.compose.ui:ui:1.5.4")
implementation("androidx.compose.material3:material3:1.1.2")
implementation("androidx.activity:activity-compose:1.8.1")

// ViewModel & Lifecycle
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")

// Hilt Dependency Injection
implementation("com.google.dagger:hilt-android:2.48")
kapt("com.google.dagger:hilt-compiler:2.48")
implementation("androidx.hilt:hilt-navigation-compose:1.1.0")

// TensorFlow Lite
implementation("org.tensorflow:tensorflow-lite:2.14.0")
implementation("org.tensorflow:tensorflow-lite-gpu:2.14.0")
implementation("org.tensorflow:tensorflow-lite-support:0.4.4")

// CameraX
implementation("androidx.camera:camera-camera2:1.3.1")
implementation("androidx.camera:camera-lifecycle:1.3.1")
implementation("androidx.camera:camera-view:1.3.1")

// Kotlin Coroutines
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

// DataStore
implementation("androidx.datastore:datastore-preferences:1.0.0")

// Logging
implementation("com.jakewharton.timber:timber:5.0.1")
```

For complete dependency list, see [app/build.gradle.kts](app/build.gradle.kts).

---

## Code Quality

### Linting

Run ktlint:
```bash
./gradlew ktlintCheck
```

Auto-format with ktlint:
```bash
./gradlew ktlintFormat
```

### Static Analysis

Run Detekt:
```bash
./gradlew detekt
```

View report at `app/build/reports/detekt/detekt.html`

---

## Contributing

Contributions are welcome! Please follow these guidelines:

1. **Fork the repository**
2. **Create a feature branch**: `git checkout -b feature/amazing-feature`
3. **Follow coding standards**: Run ktlint and detekt
4. **Write tests**: Maintain test coverage above 80%
5. **Commit changes**: `git commit -m 'Add amazing feature'`
6. **Push to branch**: `git push origin feature/amazing-feature`
7. **Open a Pull Request**

### Code Style

- Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use ktlint for formatting
- Write descriptive commit messages
- Document public APIs with KDoc

---

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## Acknowledgments

### Resources

- [Mobile Development YouTube channel](https://www.youtube.com/playlist?list=PL_RkZ4J60MDn4y00uF4sslWUdYMHEDM_6)
- [Android CameraX: Preview, Analyze, Capture](https://proandroiddev.com/android-camerax-preview-analyze-capture-1b3f403a9395)
- [Compose a Smart CameraX on Android](https://proandroiddev.com/compose-a-smart-camerax-277f4933b54b)

### Technologies

- [TensorFlow Lite](https://www.tensorflow.org/lite)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Hilt](https://dagger.dev/hilt/)
- [CameraX](https://developer.android.com/training/camerax)

---

## Contact

**Project Link**: [https://github.com/po4yka/dancer-app](https://github.com/po4yka/dancer-app)

**Issues**: [https://github.com/po4yka/dancer-app/issues](https://github.com/po4yka/dancer-app/issues)

---

Made with Kotlin and Jetpack Compose

