# DMA-2026 — practical works

Semyon Butuzov (yorushi), group P24-3.2 · assignments: [U5er01Task/DMA-2026](https://github.com/U5er01Task/DMA-2026)

One Gradle project holds all practical works; each has its own write-up with screenshots and an
index that links every assignment item to its code and its test.

| Work | Topic | Module | Items | Write-up |
|---|---|---|---|---|
| 2 | Java for Android | `:task2` (plain JVM) | 95 / 95 | [docs/task2](docs/task2/README.md) |
| 3 | Configuring `AndroidManifest.xml` | `:app`, `:legacy-analytics` | 85 / 85 | [docs/task3](docs/task3/README.md) |

## Building

JDK 17+ is enough for task 2; the Android modules also need the Android SDK (platform 35,
build-tools 35) with `ANDROID_HOME` set. The Gradle wrapper downloads everything else.

```bash
cd dma-tasks
./gradlew :task2:build                                   # task 2: compile, test, package
./gradlew assembleMobileDebug assembleMobileRelease assembleWearDebug   # task 3 APKs
./gradlew connectedMobileDebugAndroidTest                # task 3 tests, needs a device or emulator
docs/task3/verify.sh                                     # task 3 merged-manifest checks
```

## Layout

```
dma-tasks/
├── settings.gradle.kts, gradle/   build configuration and version catalog
├── task2/                         practical work 2: Java exercises, JUnit 5 tests
├── app/                           practical work 3: the Android app built around its manifest
├── legacy-analytics/              stub library whose manifest the app has to override (task 3)
└── docs/task2, docs/task3/        write-ups, screenshots, standalone manifests and scripts
```
