import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnLockMismatchReport
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsCompose)

    // kotlinx serialization plugin
    kotlin("plugin.serialization") version "1.9.22"

    // sqldelight
    id("app.cash.sqldelight") version "2.0.1"
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
    
    jvm("desktop")

    // IR Compiler - Kotlin -> Byte Code -> JS
    js(IR) {
        moduleName = "KotlinProjectCmp"
        browser {
            // Tool bundler for converting Kotlin code to JS code
            commonWebpackConfig() {
                outputFileName = "KotlinProjectCmp.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).copy()
            }
            binaries.executable() // it will generate executable JS files
        }
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }
    
    sourceSets {
        val desktopMain by getting
        
        androidMain.dependencies {
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.androidx.activity.compose)
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.material3)
            implementation(compose.ui)
            @OptIn(ExperimentalComposeLibrary::class)
            implementation(compose.components.resources)

            // kotlinx serialization
            implementation(libs.kotlinx.serialization.json)

            // kotlinx coroutines
            implementation(libs.kotlinx.coroutines.core)

            // ktor - HTTP client for retrieving data over the internet
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)

            // moko MVVM
            implementation(libs.mvvm.core)

            // compose image loader
            api(libs.image.loader)
            api(libs.image.loader.extension.moko.resources)
//            api(libs.image.loader.extension.blur)

            // coil 3
//            implementation(libs.coil)
//            implementation(libs.coil.network.ktor)
//            implementation(libs.ktor.client.android)

            // kamel
            implementation(libs.kamel.image)

            // voyager
            implementation(libs.voyager.navigator)
            implementation(libs.voyager.tabNavigator)
            implementation(libs.voyager.transitions)
            implementation(libs.voyager.bottomSheetNavigator)

            // koin
            implementation(libs.koin.core)
            implementation(libs.koin.compose)

        }
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)

            // sqldelight
            implementation(libs.sqlite.driver)
        }

        androidMain.dependencies {
            implementation(libs.ktor.client.android)

            // koin android
            implementation(libs.koin.android)

            // sqldelight
            implementation(libs.android.driver)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)

            // sqldelight
            implementation(libs.native.driver)
        }
        jvmMain.dependencies {
            api(libs.image.loader.extension.imageio)
        }
        jsMain.dependencies {
            implementation(libs.kotlinx.coroutines.core.js)

            // SQL.js Web Worker
            implementation(npm("@cashapp/sqldelight-sqljs-worker", "2.0.1"))
            implementation(npm("sql.js", "1.8.0"))

            // sqldelight
            implementation(libs.web.worker.driver)
            implementation(devNpm("copy-webpack-plugin", "9.1.0"))

            implementation("com.squareup.sqldelight:sqljs-driver:1.5.3")
        }
    }
}

android {
    namespace = "org.example.project"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")
    sourceSets["main"].resources.srcDirs("src/commonMain/resources")

    defaultConfig {
        applicationId = "org.example.project"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    dependencies {
        debugImplementation(libs.compose.ui.tooling)
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "org.example.project"
            packageVersion = "1.0.0"
        }
    }
}

compose.experimental {
    web.application {}
}

// web error
rootProject.plugins.withType(org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin::class.java) {
    rootProject.the<YarnRootExtension>().yarnLockMismatchReport =
        YarnLockMismatchReport.WARNING // NONE | FAIL
    rootProject.the<YarnRootExtension>().reportNewYarnLock = false // true
    rootProject.the<YarnRootExtension>().yarnLockAutoReplace = false // true
}

task("testClasses")

sqldelight {
    databases {
        create("AppDatabase") {
            packageName.set("org.example.project")
            generateAsync.set(true)
        }
    }
}
