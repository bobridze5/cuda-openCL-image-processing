import java.util.Locale

plugins {
    id("java")
}

group = "com.alsaev"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val osName = System.getProperty("os.name").lowercase(Locale.ENGLISH)
val osArch = System.getProperty("os.arch").lowercase(Locale.ENGLISH)

val jcudaClassifier = when {
    osName.contains("win") && osArch.contains("64") -> "windows-x86_64"
    osName.contains("linux") && osArch.contains("64") -> "linux-x86_64"
    osName.contains("mac") && osArch.contains("64") -> "macosx-x86_64"
    else -> error("Unsupported OS: $osName $osArch")
}

val jcudaVersion = "12.6.0"

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation("org.jocl:jocl:2.0.6")
    implementation("org.jcuda:jcuda:$jcudaVersion") {
        isTransitive = false
    }
    runtimeOnly("org.jcuda:jcuda-natives:$jcudaVersion:$jcudaClassifier")

}

tasks.test {
    useJUnitPlatform()
}