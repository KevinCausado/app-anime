plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.apollo)
}

android {
    namespace = "com.kevindev.animeapp.core.network"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
        buildConfigField("String", "CONSUMET_BASE_URL", "\"${project.findProperty("CONSUMET_BASE_URL") ?: "https://api.consumet.org"}\"")
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

apollo {
    service("anilist") {
        packageName.set("com.kevindev.animeapp.core.network.graphql")
        schemaFile.set(file("src/main/graphql/schema.graphqls"))
        srcDir("src/main/graphql")

        introspection {
            endpointUrl.set("https://graphql.anilist.co")
            schemaFile.set(file("src/main/graphql/schema.graphqls"))
        }
    }
}

dependencies {
    implementation(project(":core:model"))

    implementation(libs.bundles.networking)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.apollo.runtime)
    implementation(libs.apollo.normalized.cache)
    implementation(libs.apollo.normalized.cache.sqlite)
    implementation(libs.kotlinx.coroutines.android)

    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
}
