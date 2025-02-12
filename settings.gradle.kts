/*
 * Kotlin
 *
 * Copyright 2024 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
rootProject.name = "widget-examples"

pluginManagement {
    repositories {
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
}