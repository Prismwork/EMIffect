plugins {
    id("dev.isxander.modstitch.base") // version "0.5.14-unstable"
    id("me.modmuss50.mod-publish-plugin") version "0.8.4"
}

fun prop(name: String, consumer: (prop: String) -> Unit) {
    (findProperty(name) as? String?)?.let(consumer)
}

val minecraft = property("deps.minecraft") as String
val moddingPlatform = when (modstitch.platform) {
    dev.isxander.modstitch.util.Platform.Loom -> "fabric"
    dev.isxander.modstitch.util.Platform.MDG -> "neoforge"
    dev.isxander.modstitch.util.Platform.MDGLegacy -> "forge"
}
val displayPlatform = when (moddingPlatform) {
    "fabric" -> "Fabric"
    "neoforge" -> "NeoForge"
    "forge" -> "Forge"
    else -> throw IllegalArgumentException("Invalid platform: $moddingPlatform")
}
val baseVersion = "2.1.5"

modstitch {
    minecraftVersion = minecraft

    // Alternatively use stonecutter.eval if you have a lot of versions to target.
    // https://stonecutter.kikugie.dev/stonecutter/guide/setup#checking-versions
    javaTarget =
            when (minecraft) {
                "1.20.1", "1.20.4" -> 17
                "1.20.6", "1.21.1" -> 21
                else ->
                        throw IllegalArgumentException(
                                "Please store the java version for ${property("deps.minecraft")} in build.gradle.kts!"
                        )
            }

    // If parchment doesnt exist for a version yet you can safely
    // omit the "deps.parchment" property from your versioned gradle.properties
    parchment { prop("deps.parchment") { mappingsVersion = it } }

    // This metadata is used to fill out the information inside
    // the metadata files found in the templates folder.
    metadata {
        modId = "emiffect"
        modName = "EMIffect"
        modVersion = "$baseVersion+mc$minecraft"
        modGroup = "moe.prwk"
        modAuthor = "Flamarine"
        modDescription = """
            EMI addon that appends status effects in EMI and provides information about each status effect.
        """.trimIndent()
        modLicense = "MIT"

        fun <K, V> MapProperty<K, V>.populate(block: MapProperty<K, V>.() -> Unit) {
            block()
        }

        replacementProperties.populate {
            // You can put any other replacement properties/metadata here that
            // modstitch doesn't initially support. Some examples below.
            put("mod_issue_tracker", "https://github.com/Prismwork/EMITrades/issues")
            put(
                    "pack_format",
                    when (property("deps.minecraft")) {
                        "1.20.1" -> 15
                        "1.20.4" -> 26
                        "1.20.6" -> 41
                        "1.21.1" -> 46
                        else ->
                                throw IllegalArgumentException(
                                        "Please store the resource pack version for ${property("deps.minecraft")} in build.gradle.kts! https://minecraft.wiki/w/Pack_format"
                                )
                    }.toString()
            )
            put("emi_version", property("deps.emi") as String)
            put("mc_version", minecraft)
        }
    }

    // Fabric Loom (Fabric)
    loom {
        // It's not recommended to store the Fabric Loader version in properties.
        // Make sure its up to date.
        fabricLoaderVersion = "0.16.14"

        // Configure loom like normal in this block.
        configureLoom {
            // accessWidenerPath = rootProject.file("./src/main/resources/emitrades-aw/$minecraft.accesswidener")
        }
    }

    // ModDevGradle (NeoForge, Forge, Forgelike)
    moddevgradle {
        enable {
            prop("deps.forge") { forgeVersion = it }
            prop("deps.neoform") { neoFormVersion = it }
            prop("deps.neoforge") { neoForgeVersion = it }
            prop("deps.mcp") { mcpVersion = it }
        }

        // Configures client and server runs for MDG, it is not done by default
        defaultRuns()

        // This block configures the `neoforge` extension that MDG exposes by default,
        // you can configure MDG like normal from here
        configureNeoforge {
            validateAccessTransformers = false

            runs.all { disableIdeRun() }
        }

        tasks.named("createMinecraftArtifacts") { dependsOn("stonecutterGenerate") }
    }

    mixin {
        // You do not need to specify mixins in any mods.json/toml file if this is set to
        // true, it will automatically be generated.
        addMixinsToModManifest = true

        configs.register("emiffect")

        // Most of the time you wont ever need loader specific mixins.
        // If you do, simply make the mixin file and add it like so for the respective loader:
        // if (isLoom) configs.register("examplemod-fabric")
        // if (isModDevGradleRegular) configs.register("examplemod-neoforge")
        // if (isModDevGradleLegacy) configs.register("examplemod-forge")
    }

    finalJarTask {
        archiveBaseName = "${modstitch.metadata.modId.get()}-$moddingPlatform"
    }
}

val buildAndCollect by tasks.registering(Copy::class) {
    group = "build"

    dependsOn(modstitch.finalJarTask)
    from(modstitch.finalJarTask.get().archiveFile)

    into(rootProject.layout.buildDirectory.dir("finalJars"))
}

modstitch.finalJarTask.get().finalizedBy(buildAndCollect)

// Stonecutter constants for mod loaders.
// See https://stonecutter.kikugie.dev/stonecutter/guide/comments#condition-constants
var constraint: String = name.split("-")[1]

stonecutter {
    consts(
            "fabric" to constraint.equals("fabric"),
            "neoforge" to constraint.equals("neoforge"),
            "forge" to constraint.equals("forge"),
            "vanilla" to constraint.equals("vanilla")
    )
}

// All dependencies should be specified through modstitch's proxy configuration.
// Wondering where the "repositories" block is? Go to "stonecutter.gradle.kts"
// If you want to create proxy configurations for more source sets, such as client source sets,
// use the modstitch.createProxyConfigurations(sourceSets["client"]) function.
dependencies {
    modstitch.loom {
        modstitchModImplementation(
                "net.fabricmc.fabric-api:fabric-api:${property("deps.fabricApi")}"
        )
        modstitchModImplementation("dev.emi:emi-fabric:${property("deps.emi")}")
    }

    modstitch.moddevgradle {
        implementation("org.jetbrains:annotations:25.0.0")
        if (modstitch.isModDevGradleLegacy) {
            modstitchModImplementation("dev.emi:emi-forge:${property("deps.emi")}")
        } else {
            modstitchModImplementation("dev.emi:emi-neoforge:${property("deps.emi")}")
        }
    }

    // Anything else in the dependencies block will be used for all platforms.
}

tasks.named("generateModMetadata") { dependsOn("stonecutterGenerate") }

tasks.withType<JavaCompile> { dependsOn("stonecutterGenerate") }

tasks.processResources {
    filesMatching("assets/emiffect/emiffect/extra_stacks/bad_omen.json") {
        if (stonecutter.eval(minecraft, "<1.21")) exclude()
    }
}

publishMods {
    displayName.set("$baseVersion for $displayPlatform $minecraft")

    file = modstitch.finalJarTask.get().archiveFile
    version = "${modstitch.metadata.modVersion}+$moddingPlatform"
    changelog = rootProject.file("./CHANGELOG.md").readText()
    type = STABLE
    modLoaders.add(moddingPlatform)
    if (moddingPlatform == "fabric") modLoaders.add("quilt")

    modrinth {
        accessToken = providers.environmentVariable("MODRINTH_API_KEY")
        projectId = "705gWllI"
        minecraftVersions.add(minecraft)

        requires {
            slug = "emi"
        }
        if (modstitch.isLoom) {
            requires {
                slug = "fabric-api"
            }
        }
    }
}
