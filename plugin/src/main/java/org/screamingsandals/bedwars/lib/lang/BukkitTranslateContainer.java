package org.screamingsandals.bedwars.lib.lang;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.ChatColor;

public class BukkitTranslateContainer implements ITranslateContainer {
	
	private String key;
	private ITranslateContainer fallback;
	private YamlConfiguration config = new YamlConfiguration();
	
	public BukkitTranslateContainer(String key, Plugin plugin) {
		this(key, plugin, null);
	}
	
	public BukkitTranslateContainer(String key, Plugin plugin, ITranslateContainer fallback) {
		this.key = key;
		this.fallback = fallback;

        InputStream in = plugin.getResource("languages/language_" + key + ".yml");
        if (in != null) {
            try {
                config.load(new InputStreamReader(in, StandardCharsets.UTF_8));
            } catch (IOException | InvalidConfigurationException e) {
                e.printStackTrace();
            }
        }
	}
	
	public BukkitTranslateContainer(File dataFolder, String key) {
		this(dataFolder, key, null);
	}
	
	public BukkitTranslateContainer(File dataFolder, String key, ITranslateContainer fallback) {
		this(key, new File(dataFolder, "language_" + key + ".yml"), fallback);
	}
	
	public BukkitTranslateContainer(String key, File file) {
		this(key, file, null);
	}
	
	public BukkitTranslateContainer(String key, File file, ITranslateContainer fallback) {
		this.key = key;
		this.fallback = fallback;

		if (file.exists()) {
	        try {
	            config.load(file);
	        } catch (IOException | InvalidConfigurationException e) {
	            e.printStackTrace();
	        }
		}
	}
	
	public BukkitTranslateContainer(String key, InputStream stream) {
		this(key, stream, null);
	}
	
	public BukkitTranslateContainer(String key, InputStream stream, ITranslateContainer fallback) {
		this(key, new InputStreamReader(stream, StandardCharsets.UTF_8), fallback);
	}
	
	public BukkitTranslateContainer(String key, InputStreamReader reader) {
		this(key, reader, null);
	}
	
	public BukkitTranslateContainer(String key, InputStreamReader reader, ITranslateContainer fallback) {
		this.key = key;
		this.fallback = fallback;

        try {
            config.load(reader);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
	}

	@Override
	public String getLocaleCode() {
		return this.key;
	}

	@Override
	public ITranslateContainer getFallbackContainer() {
		return fallback;
	}

	@Override
	public String translate(String key, String def) {
        if (config.isSet(key)) {
            return ChatColor.translateAlternateColorCodes('&', config.getString(key));
        } else if (fallback != null) {
            return fallback.translate(key, def);
        } else if (def != null) {
        	return ChatColor.translateAlternateColorCodes('&', def);
        }

        return ChatColor.RED.toString() + key;
	}

}
