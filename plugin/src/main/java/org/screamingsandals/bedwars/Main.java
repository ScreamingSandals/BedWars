package org.screamingsandals.bedwars;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.config.VisualsConfig;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.lib.config.ConfigAdapter;
import org.screamingsandals.lib.debug.Debug;
import org.screamingsandals.lib.gamecore.GameCore;
import org.screamingsandals.lib.gamecore.core.GameManager;
import org.screamingsandals.lib.gamecore.exceptions.GameCoreException;
import org.screamingsandals.lib.lang.Language;

import java.io.File;
import java.io.IOException;

public class Main extends JavaPlugin {
    private static Main instance;
    private MainConfig mainConfig;
    private VisualsConfig visualsConfig;
    @Getter
    private GameCore gameCore;
    @Getter
    private GameManager<Game> gameManager;
    @Getter
    private Language language;

    @Override
    @SuppressWarnings("unchecked")
    public void onEnable() {
        instance = this;
        try {
            mainConfig = new MainConfig(ConfigAdapter.createFile(getDataFolder(), "config.yml"));
            mainConfig.load();

            visualsConfig = new VisualsConfig(ConfigAdapter.createFile(getDataFolder(), "visuals.yml"));
            visualsConfig.load();
        } catch (IOException e) {
            Debug.warn("WHOOOSH");
            e.printStackTrace();
            return;
        }

        language = new Language(this, mainConfig.getString("language"));
        language.setCustomPrefix(mainConfig.getString("prefix"));

        Debug.init(language.getCustomPrefix());
        Debug.setDebug(mainConfig.getBoolean("debug"));

        gameCore = new GameCore(this);

        try {
            gameCore.load(new File(getDataFolder(), "games"), Game.class);
        } catch (GameCoreException e) {
            e.printStackTrace();
            return;
        }

        gameManager = (GameManager<Game>) GameCore.getGameManager();
        gameManager.loadGames();

        Debug.info("Everything is loaded!");
    }

    @Override
    public void onDisable() {
        gameCore.destroy();
    }

    public static MainConfig getMainConfig() {
        return instance.mainConfig;
    }

    public static VisualsConfig getVisualsConfig() {
        return instance.visualsConfig;
    }
}
