# Using BedWars API

!!! warning

    Note that API is going to be changed in future versions (0.3.0+)

## Maven
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

## Gradle
```groovy
repositories {
    maven { url 'https://repo.screamingsandals.org/public/' }
}

dependencies {
    compileOnly 'org.screamingsandals.bedwars:BedWars-API:LATEST_VERSION_HERE'
}
```

## Hooking into the API
```java
import org.screamingsandals.bedwars.api.BedwarsAPI;

...
BedwarsAPI api = BedwarsAPI.getInstance();
...

```

Javadoc: [https://docs.screamingsandals.org/BedWars/javadoc/LATEST_VERSION_HERE/](https://docs.screamingsandals.org/BedWars/javadoc/LATEST_VERSION_HERE/)