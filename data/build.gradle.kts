import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.detekt)
    alias(libs.plugins.ksp)
}

val versionsProperties = Properties()
versionsProperties.load(rootProject.file("versions.properties").inputStream())

android {
    namespace = "com.example.fieldteam.data"
    compileSdk = versionsProperties.getProperty("compileSdk").toInt()

    defaultConfig {
        minSdk = versionsProperties.getProperty("minSdk").toInt()
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    implementation(project(":domain"))

    implementation(libs.androidx.core.ktx)

    implementation(libs.androidx.datastore)
    implementation(libs.kotlinx.serialization.protobuf)

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.koin.annotations)
    ksp(libs.koin.ksp.compiler)

    implementation(libs.google.play.services.location)
    implementation(libs.kotlinx.coroutines.play.services)
}
