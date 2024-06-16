plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hiddenSecrets)
    id("dagger.hilt.android.plugin")
}

// Для генерации Secret code :
// https://giters.com/asapsonter/hidden-secrets-gradle-plugin
// ./gradlew hideSecret -Pkey=https://feeds.feedburner.com -PkeyName=AstroBeneNewsUrl -Ppackage=zaitsevnews

android {
    namespace = libs.versions.namespace.get()
    compileSdk = libs.versions.targetSDK.get().toInt()

    defaultConfig {
        applicationId = libs.versions.namespace.get()
        minSdk = libs.versions.minSDK.get().toInt()
        targetSdk = libs.versions.targetSDK.get().toInt()
        versionCode = libs.versions.versionCode.get().toInt()
        versionName = libs.versions.diplomVersion.get()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf(
                    "room.schemaLocation" to "$projectDir/schemas",
                    "room.incremental" to "true"
                )
            }
        }
        ndk {
            //noinspection ChromeOsAbiSupport
            abiFilters += listOf("armeabi-v7a", "arm64-v8a")
        }
        externalNativeBuild {
            cmake {
                //cppFlags "-std=c++11 -frtti -fexceptions"
                // OpenSSL By default, Armeabi-V7a is built.
                // ARM64-V8A 64 bit to reference:
                // https://blog.csdn.net/github_35041937/article/details/102898087
                // https://blog.csdn.net/hnlylyb/article/details/80751220
                //arguments "-DANDROID_ABI=armeabi-v7a"

                cppFlags += listOf()
                cFlags += listOf()
                abiFilters += listOf("armeabi-v7a", "arm64-v8a")
            }
        }

    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = libs.versions.jvmTarget.get()
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.kotlinCompilerExtensionVersion.get()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
        }
    }

}


dependencies {
    implementation(project(":data"))
    implementation(project(":libApp"))
    implementation(project(":domain"))
    implementation(project(":feature:rss"))
    implementation(project(":feature:rsa"))
    implementation(project(":feature:translateML"))
    implementation(project(":feature:speachTextCompose"))
    implementation(project(":feature:telegramApi"))

    implementation(libs.core.ktx)
    //ML Model translate
    implementation(libs.translate)

    //Widget
    implementation(libs.androidx.glance.appwidget)
    implementation(libs.androidx.glance.material3)
    // room db
    implementation(libs.bundles.room)
    ksp(libs.room.compiler)
    annotationProcessor(libs.room.compiler)

    //hilt
    implementation(libs.hilt.android)
    implementation(libs.androidx.hilt.navigation.compose)
    ksp(libs.hilt.compiler)

    //Paging Data
    implementation(libs.bundles.pagingData)

    //retrofit
    implementation(libs.bundles.retrofit)
    implementation(libs.logging.interceptor)

    // Для избавления html разметки
    implementation(libs.html.parse)

    // Для изображений из сети
    implementation(libs.coil.compose)

    implementation(libs.exoplayer)
    // JSON
    implementation(libs.gson)

    //Compose
    implementation(libs.compose.activity)
    implementation(libs.bundles.compose)
    implementation(libs.compose.constraintlayout)
    implementation(libs.compose.material3)
    implementation(libs.compose.material)
    implementation(libs.compose.navigation)
    implementation(libs.compose.material.iconsExtended)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.lifecycle.runtime.compose)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.compose.ui.test.junit)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)

    implementation(libs.kotlin.reflect)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}