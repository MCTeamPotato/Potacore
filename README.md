# Use In Your Project
``````
repositories {
    maven {
        url "https://cursemaven.com"
        content {
            includeGroup "curse.maven"
        }
    }
}

dependencies {
    modImplementation("curse.maven:potacore-951521:${project.potacore_id}")
}
``````