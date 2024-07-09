# Resource Locator Api
A simple api to locate assets from mods. Used by [PolyMc](https://github.com/TheEpicBlock/PolyMc) to retrieve models/textures.
Licensed under MIT.

## How to use
Include Jitpack
```groovy
repositories {
    maven {
        url "https://maven.theepicblock.nl"
        content { includeGroup("nl.theepicblock") }
    }
    //...
}
```
Include the library as a Jar-in-jar.
```groovy
dependencies {
	modImplementation include("nl.theepicblock:resource-locator-api:<version>")
}
```
Replace `<version>` with the latest version from [here](https://maven.theepicblock.nl/nl/theepicblock/resource-locator-api/)
