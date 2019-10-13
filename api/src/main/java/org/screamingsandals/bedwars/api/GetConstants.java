package org.screamingsandals.bedwars.api;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * @author Bedwars Team
 */
public class GetConstants {
    private static YamlConfiguration yaml;

    /**
     * @return
     */
    public static YamlConfiguration loadConfig() {
        if (yaml == null) {
            yaml = new YamlConfiguration();
            try {
                yaml.load(new InputStreamReader(GetConstants.class.getResourceAsStream("/org/screamingsandals/bedwars/api/constants.yml"), StandardCharsets.UTF_8));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return yaml;
    }
}
