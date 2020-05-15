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
                .put(ConfigPaths.DEBUG, true) //for now, true.
                .put(ConfigPaths.PREFIX, "&7[&bS&c&lBed&f&lWars&7]&r") //big news, whoosh
                .put(ConfigPaths.LANG, "en")
                .put(ConfigPaths.BUNGEE_ENABLED, false)
                .put(ConfigPaths.BUNGEE_LOBBY_SERVER, "your_hub_server_name_here")
                .put(ConfigPaths.BUNGEE_RESTART_ON_GAME_END, false)
                .put(ConfigPaths.GAME_DEFAULT_LOBBY_TIME, 60)
                .put(ConfigPaths.GAME_DEFAULT_START_TIME, 10)
                .put(ConfigPaths.GAME_DEFAULT_GAME_TIME, 3600)
                .put(ConfigPaths.GAME_DEFAULT_DEATHMATCH_TIME, 600)
                .put(ConfigPaths.GAME_DEFAULT_END_TIME, 5)
                .end();
    }

    public interface ConfigPaths {
        String DEBUG = "debug";
        String PREFIX = "prefix";
        String LANG = "language";

        String BUNGEE_ENABLED = "bungeecord.enabled";
        String BUNGEE_LOBBY_SERVER = "bungeecord.lobby-server-name";
        String BUNGEE_RESTART_ON_GAME_END = "bungeecord.restart-server-after-game-end";

        String GAME_DEFAULT_LOBBY_TIME = "game.times.lobby-time";
        String GAME_DEFAULT_START_TIME = "game.times.starting-time";
        String GAME_DEFAULT_GAME_TIME = "game.times.game-time";
        String GAME_DEFAULT_DEATHMATCH_TIME = "game.times.deathmatch-time";
        String GAME_DEFAULT_END_TIME = "game.times.ending-time";
    }
}
