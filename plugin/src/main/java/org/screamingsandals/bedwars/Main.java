package org.screamingsandals.bedwars;

import org.bukkit.plugin.java.JavaPlugin;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.config.VisualsConfig;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.lib.debug.Debug;
import org.screamingsandals.lib.gamecore.GameCore;
import org.screamingsandals.lib.gamecore.core.GameManager;

import java.io.File;

public class Main extends JavaPlugin {
    private static Main instance;
    private MainConfig mainConfig;
    private VisualsConfig visualsConfig;
    private GameCore gameCore;
    private GameManager<Game> gameManager;

    @Override
    @SuppressWarnings("unchecked")
    public void onEnable() {
        instance = this;
        mainConfig = new MainConfig(new File(getDataFolder(), "config.yml"));
        mainConfig.load();

        visualsConfig = new VisualsConfig(new File(getDataFolder(), "visuals.yml"));
        visualsConfig.load();

        Debug.init(getName());
        Debug.setDebug(mainConfig.getBoolean("debug"));


        gameCore = new GameCore(this);
        gameCore.load(new File(getDataFolder(), "games"), Game.class);

        gameManager = (GameManager<Game>) GameCore.getGameManager();
        gameManager.loadGames();

    }

    @Override
    public void onDisable() {

    }

    public static MainConfig getMainConfig() {
        return instance.mainConfig;
    }

    public static VisualsConfig getVisualsConfig() {
        return instance.visualsConfig;
    }
}
