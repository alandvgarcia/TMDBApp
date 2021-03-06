plugins {
    kotlin("multiplatform")
    id("kotlinx-serialization")
    id("com.squareup.sqldelight")
    kotlin("native.cocoapods")
    id("com.android.library")
}

version = "1.0"


sqldelight {
    database("TMDBAppDatabase") {
        packageName = "com.alandvgarcia.tmdbapp.db"
        sourceFolders = listOf("sqldelight")
    }
}

kotlin {
    android()
    iosX64()
    iosArm64()
    iosSimulatorArm64() // sure all ios dependencies support this target


    cocoapods {
        summary = "Some description for the Shared Module"
        homepage = "Link to the Shared Module homepage"
        ios.deploymentTarget = "14.1"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "tmdbapp"
        }
    }
    
    sourceSets {
        val commonMain by getting{
            dependencies{
                implementation(Deps.Ktor.ktorClientCore)
                implementation(Deps.Ktor.ktorClientSerialization)
                implementation(Deps.Coroutines.coroutinesShared){
                    version{
                        strictly(Version.coroutinesShared)
                    }
                }
                implementation(Deps.SqlDelight.sqlDelightRuntime)
                implementation(Deps.SqlDelight.sqlDelightCoroutineExtensions)
                implementation(Deps.Kotlinx.serialization)
                api(Deps.Log.kermit)
                implementation(Deps.Ktor.ktorContentNegotiation)
                implementation(Deps.Ktor.ktorJson)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val androidMain by getting {
            dependencies{
                implementation(Deps.Ktor.ktorClientAndroid)
                implementation(Deps.SqlDelight.sqlDelightAndroid)
            }
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependencies {
                implementation(Deps.Ktor.ktorClientIos)
                implementation(Deps.SqlDelight.sqlDelightIos)
            }
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
    }
}

kotlin.targets.withType(org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget::class.java) {
    binaries.all {
        binaryOptions["memoryModel"] = "experimental"
        binaryOptions["freezing"] = "disabled"
    }
}

android {
    compileSdk = 31
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 21
        targetSdk = 31
    }
}