package org.screamingsandals.bedwars.config;

import lombok.Data;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author ScreamingSandals team
 */
@Data
public abstract class BaseConfig {
    private final File configFile;
    private YamlConfiguration yamlConfiguration;

    public BaseConfig(File configFile) {
        this.configFile = configFile;
    }

    public void initialize() {
        yamlConfiguration = new YamlConfiguration();

        try {
            yamlConfiguration.load(configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            yamlConfiguration.save(configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object get(String key) {
        return yamlConfiguration.get(key);
    }

    public String getString(String key) {
        return yamlConfiguration.getString(key);
    }

    public String getString(String key, String def) {
        return yamlConfiguration.getString(key, def);
    }

    public boolean getBoolean(String key) {
        return yamlConfiguration.getBoolean(key);
    }

    public boolean getBoolean(String key, boolean def) {
        return yamlConfiguration.getBoolean(key, def);
    }

    public int getInt(String key) {
        return yamlConfiguration.getInt(key);
    }

    public int getInt(String key, int def) {
        return yamlConfiguration.getInt(key, def);
    }

    public double getDouble(String key) {
        return yamlConfiguration.getDouble(key);
    }

    public double getDouble(String key, double def) {
        return yamlConfiguration.getDouble(key, def);
    }

    public List<?> getList(String key) {
        return yamlConfiguration.getList(key);
    }

    public List<String> getStringList(String key) {
        return yamlConfiguration.getStringList(key);
    }

    public List<Map<?, ?>> getMap(String key) {
        return yamlConfiguration.getMapList(key);
    }

    public boolean isSet(String key) {
        return yamlConfiguration.isSet(key);
    }

    public void set(String key, Object obj) {
        yamlConfiguration.set(key, obj);
    }

    public boolean contains(String path) {
        return yamlConfiguration.contains(path);
    }

    public ConfigurationSection getConfigurationSection(String path) {
        return yamlConfiguration.getConfigurationSection(path);
    }

    public File createConfigFile(File dataFolder, String fileName) {
        dataFolder.mkdirs();

        File configFile = new File(dataFolder, fileName);
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return configFile;
    }
}
