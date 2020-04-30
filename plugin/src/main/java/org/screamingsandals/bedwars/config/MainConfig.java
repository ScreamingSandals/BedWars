package org.screamingsandals.bedwars.config;

import org.screamingsandals.lib.config.DefaultConfigBuilder;
import org.screamingsandals.lib.config.SpigotConfigAdapter;

import java.io.File;

public class MainConfig extends SpigotConfigAdapter {

    public MainConfig(File configFile) {
        super(configFile);
    }

    @Override
    public void load() {
        super.load();
        loadDefaults();
    }

    public void loadDefaults() {
        DefaultConfigBuilder.start(this)
                .put("debug", true) //for now, true.
                .put("prefix", "[BedWars]") //big news
                .put("language", "en")
                .put("bungeecord.enabled", false)
                .put("bungeecord.multi-arena-setup", false)
                .put("bungeecord.lobby-server-name", "hub")
                .put("bungeecord.restart-server-after-game-end", false)
                .end();
    }
}
