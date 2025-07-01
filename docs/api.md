# Using BedWars API

Use the BedWars API to interact with and extend the functionality of the BedWars plugin.

!!! warning

        The API is still evolving. Breaking changes will be introduced in future versions, starting from `0.3.0`.

## Installation

=== "Maven"
    ```xml
    <repositories>
      <repository>
        <id>screaming-repo</id>
        <url>https://repo.screamingsandals.org/public/</url>
      </repository>
    </repositories>

    <dependencies>
      <dependency>
        <groupId>org.screamingsandals.bedwars</groupId>
        <artifactId>BedWars-API</artifactId>
        <version>LATEST_VERSION_HERE</version>
        <scope>provided</scope>
      </dependency>
    </dependencies>
    ```

=== "Gradle (Groovy DSL)"
    ```groovy
    repositories {
        maven { url 'https://repo.screamingsandals.org/public/' }
    }

    dependencies {
        compileOnly 'org.screamingsandals.bedwars:BedWars-API:LATEST_VERSION_HERE'
    }
    ```

=== "Gradle (Kotlin DSL)"
    ```kotlin
    repositories {
        maven(url = "https://repo.screamingsandals.org/public/")
    }

    dependencies {
        compileOnly("org.screamingsandals.bedwars:BedWars-API:LATEST_VERSION_HERE")
    }
    ```

If you prefer or need access to the entire plugin (including internals not exposed via the API), you can also depend on the main `BedWars` plugin instead of `BedWars-API`. However, **this approach is discouraged** because internal classes and methods **may change at any time and without notice**.

## Hooking into the API

Once included in your build, you can access the API using:

```java
import org.screamingsandals.bedwars.api.BedwarsAPI;

...
BedwarsAPI api = BedwarsAPI.getInstance();
...

```

Javadoc: [https://docs.screamingsandals.org/BedWars/javadoc/LATEST_VERSION_HERE/](https://docs.screamingsandals.org/BedWars/javadoc/LATEST_VERSION_HERE/)