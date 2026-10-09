pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "Skinnable"
include(":main")
include(":main-night")
project(":main-night").projectDir = File(settingsDir, "mainNight")
include(":librarys:skin-base")
project(":librarys:skin-base").projectDir = File(settingsDir, "librarys/SkinLibrary")
include(":librarys:skin-compat")
project(":librarys:skin-compat").projectDir = File(settingsDir, "librarys/SkinSupportCompat")
include(":librarys:skin-constraintlayout")
project(":librarys:skin-constraintlayout").projectDir = File(settingsDir, "librarys/SkinSupportConstraintLayout")
include(":librarys:skin-material-design")
project(":librarys:skin-material-design").projectDir = File(settingsDir, "librarys/SkinSupportDesign")