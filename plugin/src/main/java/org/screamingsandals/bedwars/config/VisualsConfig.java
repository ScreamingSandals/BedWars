package org.screamingsandals.bedwars.config;

import org.screamingsandals.lib.config.DefaultConfigBuilder;

import java.io.File;
import java.util.List;

public class VisualsConfig extends org.screamingsandals.lib.gamecore.config.VisualsConfig {

    public VisualsConfig(File configFile) {
        super(configFile);
    }

    @Override
    public void load() {
        if (!getConfigFile().exists()) {
            getConfigFile().mkdirs();
            loadDefaults();
        }

        super.load();
    }

    public void loadDefaults() {
        DefaultConfigBuilder.start(this)
                .put(PATH_SCOREBOARDS_ENABLED, true)
                .put(PATH_SCOREBOARDS_NAME, "%prefix%")
                .put(PATH_SCOREBOARDS_CONTENT_LOBBY, List.of(" ", "&eMap: &a%game%", "&fPlayers: &2%players%&f/&2%maxplayers%", "&4Need more %playersToStart% players!"))
                //if line contains %teams%, delete it and put teams after it
                .put(PATH_SCOREBOARDS_CONTENT_GAME, List.of(" ", "&eMap: &a%game%", " ", "%teams%", "My ass is amazing!"))
                .put(PATH_SCOREBOARDS_CONTENT_DEATHMATCH, List.of(" ", "&c&lDEATHMATCH", " ", "%teams%", "My ass is amazing!"))
                .put(PATH_SCOREBOARDS_CONTENT_END_GAME, List.of(" ", "%isWinner%", " ", "some ", "content")) //replace %isWinner% with "You won" or "You lost"

                .put(PATH_BOSSBARS_ENABLED, true)
                .put(PATH_BOSSBARS_CONTENT_LOBBY, "Game is starting soon! Needed players: %players%, Team: %selectedTeam%")
                .put(PATH_BOSSBARS_CONTENT_STARTING, "Starting in: %time%")
                .put(PATH_BOSSBARS_CONTENT_IN_GAME, "Let's play! Remaining time: %time%")
                .put(PATH_BOSSBARS_CONTENT_DEATHMATCH, "NOW LET'S SEE! Remaining time: %time%")
                .put(PATH_BOSSBARS_CONTENT_END_GAME, "Whooosh! Team %team% won!")

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
                .put(PATH_TITLES_END_GAME_SUBTITLE, "");
    }
}
