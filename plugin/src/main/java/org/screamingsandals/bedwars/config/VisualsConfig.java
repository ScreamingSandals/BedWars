package org.screamingsandals.bedwars.config;

import org.screamingsandals.lib.config.DefaultConfigBuilder;

import java.io.File;

public class VisualsConfig extends org.screamingsandals.lib.gamecore.config.VisualsConfig {
    public static String PATH_SCOREBOARDS_CUSTOM_ENABLED = "scoreboards.custom.enabled";

    public VisualsConfig(File configFile) {
        super(configFile);
    }

    @Override
    public void load() {
        super.load();
        loadDefaults();
    }

    public void loadDefaults() {
        //Alot of this will be moved to language file!
        DefaultConfigBuilder.start(this)
                .put(PATH_SCOREBOARDS_ENABLED, true)
                .put(PATH_SCOREBOARDS_NAME, "%prefix%")

                .put(PATH_BOSSBARS_ENABLED, true)
                .put(PATH_BOSSBARS_COLOR_LOBBY, "YELLOW")
                .put(PATH_BOSSBARS_COLOR_STARTING, "YELLOW")
                .put(PATH_BOSSBARS_COLOR_IN_GAME, "YELLOW")
                .put(PATH_BOSSBARS_COLOR_DEATHMATCH, "YELLOW")
                .put(PATH_BOSSBARS_COLOR_END_GAME, "YELLOW")

                .put(PATH_TITLES_LOBBY_FADE_IN, 0.2)
                .put(PATH_TITLES_LOBBY_FADE_OUT, 0.2)
                .put(PATH_TITLES_LOBBY_STAY, 1)
                .put(PATH_TITLES_LOBBY_TITLE, "%countdown%")
                .put(PATH_TITLES_LOBBY_SUBTITLE, "Let's roll!")

                .put(PATH_TITLES_IN_GAME_FADE_IN, 0.2)
                .put(PATH_TITLES_IN_GAME_FADE_OUT, 0.2)
                .put(PATH_TITLES_IN_GAME_STAY, 2)
                .put(PATH_TITLES_IN_GAME_TITLE, "WE STARTED!")
                .put(PATH_TITLES_IN_GAME_SUBTITLE, "Players to kill: %players%")

                .put(PATH_TITLES_DEATHMATCH_FADE_IN, 0.2)
                .put(PATH_TITLES_DEATHMATCH_FADE_OUT, 0.2)
                .put(PATH_TITLES_DEATHMATCH_STAY, 1)
                .put(PATH_TITLES_DEATHMATCH_TITLE, "LET'S SEEE!")
                .put(PATH_TITLES_DEATHMATCH_SUBTITLE, "Players to kill: %players%")

                .put(PATH_TITLES_END_GAME_FADE_IN, 0.2)
                .put(PATH_TITLES_END_GAME_FADE_OUT, 0.2)
                .put(PATH_TITLES_END_GAME_STAY, 1)
                .put(PATH_TITLES_END_GAME_TITLE, "Team %team% won!")
                .put(PATH_TITLES_END_GAME_SUBTITLE, "")
                .end();
    }
}
