plugins {
    id("org.jetbrains.kotlin.jvm")
    id("java-gradle-plugin")
    id("maven-publish")
    `kotlin-dsl`
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

group = "com.devlopersquad"
version = "1.0"

gradlePlugin {
    plugins {
        register("instrumentation") {
            description = "compose"
            displayName = "compose"
            id = "com.devlopersquad.plugin"
            implementationClass = "com.devlopersquad.plugin.ComposeInstrumentationPlugin"
        }
    }
}
dependencies {
    compileOnly("org.ow2.asm:asm-util:9.6")
    compileOnly("com.android.tools.build:gradle:8.2.2")
}
/*publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "com.devlopersquad.plugin"
            artifactId = "instrumentation"
            version = "1.0"

            from(components["java"])
        }
    }
}*/
