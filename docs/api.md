# Using BedWars API

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

Javadoc: [https://jd.screamingsandals.org/sbw-0-2-x/BedWars-API/](https://jd.screamingsandals.org/sbw-0-2-x/BedWars-API/)