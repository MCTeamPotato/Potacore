# Use In Your Project
``````
repositories {
    maven {
        name = "Modrinth"
        url = "https://api.modrinth.com/maven"
        content {
            includeGroup "maven.modrinth"
        }
    }
}

dependencies {
    modImplementation("maven.modrinth:potacore:{potacore_version}")
    modImplementation("maven.modrinth:potacore:{potacore_version}:universal-sources")
    modImplementation("maven.modrinth:potacore:{potacore_version}:universal-javadoc")
}
``````