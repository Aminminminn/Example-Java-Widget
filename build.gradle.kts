/*
 * Kotlin
 *
 * Copyright 2024 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */

plugins {
	id("com.microej.gradle.application") version "1.0.0"
}

group = "com.microej.example.ui"
version = "8.1.0"
val userHome = System.getProperty("user.home")

microej {
	applicationEntryPoint = "com.microej.demo.widget.common.Navigation"
}

dependencies {
	implementation("ej.api:edc:1.3.5")
	implementation("ej.api:microui:3.1.0")
	implementation("ej.api:drawing:1.0.2")
	implementation("ej.library.ui:widget:5.2.0")
	implementation("ej.library.runtime:basictool:1.5.0")
	implementation("ej.library.runtime:service:1.1.1")
	implementation("ej.library.eclasspath:collections:1.4.0")
	implementation("ej.library.eclasspath:stringtokenizer:1.2.0")

	//microejVee("com.nxp.vee.mimxrt1170:evk_platform:2.2.0")
	microejVee(files("./veePort.zip"))
}

tasks.withType<Javadoc> {
	options.encoding = "UTF-8"
}

tasks.register<Exec>("fixScripts") {
	dependsOn("loadVee")

	//commandLine("find", ".", "-type", "f", "-name", "'*.sh'", "-exec", "sed", "-i", "'s/\\r$//'", "{}", "+")
	//commandLine("chmod", "+x", "/home/build/workspace/build/bsp/projects/nxpvee-ui/armgcc/build_flexspi_nor_sdram_release_evkb.sh")
}

tasks.named("buildExecutable") {
	dependsOn("fixScripts")
}

testing {
	suites {
		val test by getting(JvmTestSuite::class) {
			microej.useMicroejTestEngine(this)

			dependencies {
				implementation(project())
				implementation("ej.api:edc:1.3.5")
				implementation("ej.api:microui:3.1.0")
				implementation("ej.api:drawing:1.0.2")
				implementation("ej.library.test:junit:1.7.1")
				implementation("ej.library.runtime:basictool:1.5.0")
				implementation("org.junit.platform:junit-platform-launcher:1.8.2")
			}
		}
	}
}

repositories {
	/* Local Repository for Maven/Gradle modules */
	maven {
	name = "localRepository"
	url = uri("${userHome}/.microej/repository")
	}
	/* Local Repository for Ivy modules */
	ivy {
	name = "localRepositoryIvy"
	url = uri("${userHome}/.ivy2/repository")
	patternLayout {
		artifact("[organisation]/[module]/[revision]/[artifact]-[revision](-[classifier])(.[ext])")
		ivy("[organisation]/[module]/[revision]/ivy-[revision].xml")
		setM2compatible(true)
	}
	}
	/* MicroEJ Central repository for Maven/Gradle modules */
	maven {
		name = "microEJCentral"
		url = uri("https://repository.microej.com/modules")
	}
	/* MicroEJ Forge Central repository for Maven/Gradle modules */
	maven {
		name = "microEJForgeCentral"
		url = uri("https://forge.microej.com/artifactory/microej-central-repository-release")
	}
	/* MicroEJ Developer repository for Maven/Gradle modules */
	maven {
		name = "microEJForgeDeveloper"
		url = uri("https://forge.microej.com/artifactory/microej-developer-repository-release")
	}
	/* MicroEJ SDK 6 repository for Maven/Gradle modules */
	maven {
		name = "microEJForgeSDK6"
		url = uri("https://forge.microej.com/artifactory/microej-sdk6-repository-release/")
	}
	/* MicroEJ Central repository for Ivy modules */
	ivy {
		name = "microEJCentralIvy"
		url = uri("https://repository.microej.com/modules")
		patternLayout {
			artifact("[organisation]/[module]/[revision]/[artifact]-[revision](-[classifier])(.[ext])")
			ivy("[organisation]/[module]/[revision]/ivy-[revision].xml")
			setM2compatible(true)
		}
	}
	/* MicroEJ Forge Central repository for Ivy modules */
	ivy {
		name = "microEJForgeCentralIvy"
		url = uri("https://forge.microej.com/artifactory/microej-central-repository-release")
		patternLayout {
			artifact("[organisation]/[module]/[revision]/[artifact]-[revision](-[classifier])(.[ext])")
			ivy("[organisation]/[module]/[revision]/ivy-[revision].xml")
			setM2compatible(true)
		}
	}
	/* MicroEJ Developer repository for Ivy modules */
	ivy {
		name = "microEJForgeDeveloperIvy"
		url = uri("https://forge.microej.com/artifactory/microej-developer-repository-release")
		patternLayout {
			artifact("[organisation]/[module]/[revision]/[artifact]-[revision](-[classifier])(.[ext])")
			ivy("[organisation]/[module]/[revision]/ivy-[revision].xml")
			setM2compatible(true)
		}
	}
	/* MicroEJ SDK 6 repository for Ivy modules */
	ivy {
		name = "microEJForgeSDK6Ivy"
		url = uri("https://forge.microej.com/artifactory/microej-sdk6-repository-release/")
		patternLayout {
			artifact("[organisation]/[module]/[revision]/[artifact]-[revision](-[classifier])(.[ext])")
			ivy("[organisation]/[module]/[revision]/ivy-[revision].xml")
			setM2compatible(true)
		}
	}
}