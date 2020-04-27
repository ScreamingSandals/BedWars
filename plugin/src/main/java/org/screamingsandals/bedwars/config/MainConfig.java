package org.screamingsandals.bedwars.config;

import org.screamingsandals.lib.config.DefaultConfigBuilder;
import org.screamingsandals.lib.config.SpigotConfigAdapter;

import java.io.File;

public class MainConfig extends SpigotConfigAdapter {

    public MainConfig(File configFile) {
        super(configFile);
    }

    public void load() {
        if (!getConfigFile().exists()) {
            getConfigFile().mkdirs();
            loadDefaults();
        }

        super.load();
    }

    public void loadDefaults() {
        DefaultConfigBuilder.start(this)
                .put("debug", true) //for now, true.
                .put("bungeecord.enabled", false)
                .put("bungeecord.multi-arena-setup", false)
                .put("bungeecord.lobby-server-name", "hub")
                .put("bungeecord.restart-server-after-game-end", false)
                .end();
    }
}
