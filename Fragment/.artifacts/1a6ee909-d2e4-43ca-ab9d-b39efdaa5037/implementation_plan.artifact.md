# Fix "Unresolved reference 'R'" and Missing Resources

The build error `Unresolved reference 'R'` in `MainActivity.kt` is caused by a package name mismatch between the Kotlin file and the Android namespace. Additionally, several IDs used in the code are not defined in the resource files.

## Proposed Changes

### 1. Fix Package Name Mismatch
The `namespace` in `app/build.gradle.kts` is `com.example.fragment`, but the Kotlin files use `package com.example.fragments`. This prevents the compiler from automatically resolving the generated `R` class.

#### [MODIFY] [MainActivity.kt](file:///C:/Users/tejas/OneDrive/Desktop/Fragment/app/src/main/java/com/example/fragment/MainActivity.kt)
- Change `package com.example.fragments` to `package com.example.fragment`.

#### [MODIFY] [fragments.kt](file:///C:/Users/tejas/OneDrive/Desktop/Fragment/app/src/main/java/com/example/fragment/fragments.kt)
- Change `package com.example.fragments` to `package com.example.fragment`.

### 2. Define Missing Resource IDs
`MainActivity.kt` uses `R.id.nav_back`, `R.id.nav_login`, and `R.id.nav_dashboard`, but these are not defined in any layout or menu XML files.

#### [NEW] [ids.xml](file:///C:/Users/tejas/OneDrive/Desktop/Fragment/app/src/main/res/values/ids.xml)
- Define the missing IDs to allow the programmatic menu population in `MainActivity`.

### 3. Resolve Missing Dependencies
The `fragments.kt` file uses `androidx.work` and `kotlinx.coroutines`, which are not currently listed in the `app/build.gradle.kts` dependencies.

#### [MODIFY] [libs.versions.toml](file:///C:/Users/tejas/OneDrive/Desktop/Fragment/gradle/libs.versions.toml)
- Add `androidx-work-runtime` and `kotlinx-coroutines-android` versions and library definitions.

#### [MODIFY] [app/build.gradle.kts](file:///C:/Users/tejas/OneDrive/Desktop/Fragment/app/build.gradle.kts)
- Add the new dependencies to the `dependencies` block.

## Verification Plan

### Automated Tests
- Run `./gradlew :app:compileDebugKotlin` to verify that all code references (including `R`) are resolved.
- Run `./gradlew :app:assembleDebug` to ensure a successful build.

### Manual Verification
- None required for this build fix.
