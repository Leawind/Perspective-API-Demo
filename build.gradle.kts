import gg.meza.stonecraft.mod
import net.fabricmc.loom.task.RemapJarTask
import org.gradle.util.internal.VersionNumber

plugins {
    id("gg.meza.stonecraft")
}

val props: Map<String, Any> = project.properties.mapNotNull { (key, value) -> value?.let { key to it } }.toMap()

val archivesBaseName = mod.id
val archivesVersion = "${mod.version}+${mod.loader}-${mod.minecraftVersion}"
tasks.withType<Jar>().configureEach {
    archiveBaseName.set(archivesBaseName)
    archiveVersion.set(archivesVersion)
}
tasks.withType<RemapJarTask>().configureEach {
    archiveBaseName.set(archivesBaseName)
    archiveVersion.set(archivesVersion)
}

modSettings {
    // https://stonecraft.meza.gg/docs/configuration

    clientOptions {
        // https://minecraft.wiki/w/Options.txt
        fov = 88
        narrator = false
        musicVolume = 0.0
        guiScale = 3

        additionalLines = mapOf(
            "maxFps" to "60",
            "renderDistance" to "8",
            "simulationDistance" to "5",
            "mouseSensitivity" to "0.22",
            "key_key.togglePerspective" to "key.keyboard.v",
        )
    }

    val vars = props
        .filterKeys { it.startsWith("mod.") }
        .mapKeys { it.key.removePrefix("mod.") }
    variableReplacements.putAll(vars)
}

stonecutter {
    // ResourceLocation -> Identifier
    replacements.string(current.parsed >= "1.21.11") {
        replace(
            "net.minecraft.resources.ResourceLocation",
            "net.minecraft.resources.Identifier"
        )
        replace("ResourceLocation", "Identifier")
    }
    // Input -> ClientInput
    replacements.string(current.parsed > "1.21") {
        replace(
            "net.minecraft.client.player.Input",
            "net.minecraft.client.player.ClientInput"
        )
    }
}

val perspectiveApiDir = System.getenv("PERSPECTIVE_API_DIR") ?: System.getProperty("PERSPECTIVE_API_DIR")

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://jitpack.io")

    exclusiveContent {
        forRepository {
            maven {
                name = "Modrinth"
                url = uri("https://api.modrinth.com/maven")
            }
        }
        filter {
            includeGroup("maven.modrinth")
        }
    }

    // Mod Menu (Fabric)
    maven {
        name = "Terraformers"
        url = uri("https://maven.terraformersmc.com/")
    }
    // KotlinForForge (required by YACL on NeoForge)
    maven {
        name = "Kotlin for Forge"
        url = uri("https://thedarkcolour.github.io/KotlinForForge/")
        content { includeGroup("thedarkcolour") }
    }
    // NeoForged
    maven {
        name = "NeoForged"
        url = uri("https://maven.neoforged.net/releases")
    }
    // Text Placeholder API
    maven {
        name = "Nucleoid"
        url = uri("https://maven.nucleoid.xyz")
    }
    if (!perspectiveApiDir.isNullOrBlank()) {
        // Yet Another Config Lib
        maven("https://maven.isxander.dev/releases") {
            name = "Xander Maven"
        }
        // Modern UI
        maven("https://maven.izzel.io/releases/") {
            name = "Modern UI"
        }
        // Forge Config API Port
        maven {
            url = uri("https://raw.githubusercontent.com/Fuzss/modresources/main/maven/")
            content {
                includeGroup("fuzs.forgeconfigapiport")
            }
        }
    }
}

fun DependencyHandlerScope.modImplAlias(dependencyNotation: String) {
    if (VersionNumber.parse(mod.minecraftVersion) >= VersionNumber.parse("26.1")) {
        implementation(dependencyNotation)
    } else {
        add("modImplementation", dependencyNotation)
    }
}

dependencies {
    val perspectiveApiDir = System.getenv("PERSPECTIVE_API_DIR") ?: System.getProperty("PERSPECTIVE_API_DIR")

    if (!perspectiveApiDir.isNullOrBlank()) {
        modImplAlias("io.github.leawind.perspectiveapi:perspective_api:0.0-SNAPSHOT+${mod.loader}-${mod.minecraftVersion}")
    } else {
        // modImplAlias("maven.modrinth:perspective-api:${props["mod.perspective_api_version"]}+${mod.loader}-${mod.minecraftVersion}")
        // Somehow if I use slug like above, it does not download sources jar. So better use project id like below:
        modImplAlias("maven.modrinth:LIqveQm1:${props["mod.perspective_api_version"]}+${mod.loader}-${mod.minecraftVersion}")
    }

    if (mod.isFabric) {
        // ModMenu (Fabric only)
        // https://modrinth.com/mod/modmenu/versions
        modImplAlias("com.terraformersmc:modmenu:${project.property("mod.modmenu_version")}")
    }

    // region test
    testCompileOnly("org.jspecify:jspecify:1.0.0")

    testImplementation("org.junit.jupiter:junit-jupiter:5.11.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    testImplementation("com.google.jimfs:jimfs:1.3.0") {
        // conflict with 1.20.1-forge `guava:32.1.1-jre`
        exclude(group = "com.google.guava", module = "guava")
    }
    // endregion

    // region compile only
    compileOnly("org.jspecify:jspecify:1.0.0")
    compileOnly("org.jetbrains:annotations:24.0.1")
    compileOnly("com.google.auto.service:auto-service-annotations:1.1.1")
    annotationProcessor("com.google.auto.service:auto-service:1.1.1")
    // endregion
}

if (mod.isFabric) {
    fabricApi {
        configureTests {
            createSourceSet = true
            modId = project.property("mod.id") as String
            enableGameTests = false
            enableClientGameTests = false
            eula = true
            // must be false
            clearRunDirectory = false
            username = "Player0"
        }
    }
}

if (mod.isForge) {
    tasks.compileTestJava {
        dependsOn("generatePackMCMetaJson")
    }
}

loom {
    if (mod.isForge) {
        forge {
            mixinConfig("${mod.id}.mixins.json")
        }
    }
}

tasks.test {
    useJUnitPlatform()
}


publishMods {
    dryRun = false
    modrinth {
        // Somehow in 1.20.1-fabric, it fails if not specified
        // refer to https://github.com/Leawind/Perspective-API/actions/runs/28410673256/job/84182772569
        projectId = System.getenv("MODRINTH_ID")
        environment = CLIENT_ONLY

        requires("perspective-api")
    }
    curseforge {
        projectId = System.getenv("CURSEFORGE_ID")
        client = true
        server = false

        requires("perspective-api")
    }
}
