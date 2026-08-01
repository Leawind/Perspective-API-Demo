plugins {
    id("dev.kikugie.stonecutter")

    id("dev.isxander.modstitch.base") version "0.8.5" apply false
    id("me.modmuss50.mod-publish-plugin") version "2.2.0" apply false
    id("net.fabricmc.fabric-loom") version "1.15-SNAPSHOT" apply false
}

stonecutter active "26.2-fabric"

val buildAndCollect by tasks.registering(Sync::class) {
    group = "build"
    description = "Builds and collects all distributable jars."
    into(layout.buildDirectory.dir("libs"))
}

allprojects {
    repositories {
        // Required to resolve locally published Perspective API snapshots.
        mavenLocal()
        mavenCentral()

        // Sometimes it responds with 502 Bad Gateway.
        // maven("https://maven.terraformersmc.com/") // ModMenu
        exclusiveContent {
            forRepository {
                maven("https://maven.gnomecraft.net/releases") {
                    name = "GnomeCraft (Terraformers Mirror)"
                }
            }
            filter { includeGroup("com.terraformersmc") }
        }

        exclusiveContent {
            forRepository { maven("https://maven.isxander.dev/releases") }
            filter { includeGroup("dev.isxander") }
        }
        exclusiveContent {
            forRepository { maven("https://maven.quiltmc.org/repository/release") }
            filter { includeGroup("org.quiltmc.parsers") }
        }
        maven("https://maven.neoforged.net/releases/") {
            content {
                includeGroupByRegex("net\\.neoforged(\\..*)?")
            }
        }
        maven("https://maven.minecraftforge.net/") {
            content {
                includeGroupByRegex("net\\.minecraftforge(\\..*)?")
            }
        }
        exclusiveContent {
            forRepository { maven("https://maven.nucleoid.xyz") }
            filter { includeGroupByRegex("eu\\.pb4(\\..*)?") }
        }
        exclusiveContent {
            forRepository { maven("https://thedarkcolour.github.io/KotlinForForge/") }
            filter { includeGroup("thedarkcolour") }
        }
    }
}
