package org.screamingsandals.bedwars;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.config.VisualsConfig;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.bedwars.listeners.PlayerCoreListener;
import org.screamingsandals.lib.commands.Commands;
import org.screamingsandals.lib.config.ConfigAdapter;
import org.screamingsandals.lib.debug.Debug;
import org.screamingsandals.lib.gamecore.GameCore;
import org.screamingsandals.lib.gamecore.core.GameManager;
import org.screamingsandals.lib.gamecore.exceptions.GameCoreException;
import org.screamingsandals.lib.gamecore.player.PlayerManager;
import org.screamingsandals.lib.lang.Language;

import java.io.File;
import java.util.Collection;

public class Main extends JavaPlugin {
    private static Main instance;
    private MainConfig mainConfig;
    private VisualsConfig visualsConfig;
    @Getter
    private GameCore gameCore;
    @Getter
    private GameManager<Game> gameManager;
    private PlayerManager playerManager;
    @Getter
    private Language language;
    private Commands commands;

    @Override
    @SuppressWarnings("unchecked")
    public void onEnable() {
        Debug.setFallbackName("[BedWars] ");
        instance = this;
        try {
            mainConfig = new MainConfig(ConfigAdapter.createFile(getDataFolder(), "config.yml"));
            mainConfig.load();

            visualsConfig = new VisualsConfig(ConfigAdapter.createFile(getDataFolder(), "visuals.yml"));
            visualsConfig.load();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        language = new Language(this, mainConfig.getString("language"), mainConfig.getString("prefix"));

        Debug.init(getName());
        Debug.setDebug(mainConfig.getBoolean("debug"));

        commands = new Commands(this);
        commands.load();

        //Make sure that we destroy existing instance of the  core, fucking reloads
        if (gameCore != null) {
            gameCore.destroy();
            Debug.info("GameCore already exists, destroying and loading new!");
        }

        gameCore = new GameCore(this);

        try {
            gameCore.load(new File(getDataFolder(), "games"), Game.class);
        } catch (GameCoreException e) {
            Debug.info("This is some way of fuckup.. Please report that to our GitHub or Discord!", true);
            e.printStackTrace();
            return;
        }

        gameManager = (GameManager<Game>) GameCore.getGameManager();
        gameManager.loadGames();

        playerManager = new PlayerManager();

        //Beware of plugin reloading..
        final Collection<Player> onlinePlayers = (Collection<Player>) Bukkit.getOnlinePlayers();
        if (onlinePlayers.size() > 0) {
            onlinePlayers.forEach(player -> playerManager.registerPlayer(player));
        }

        registerListeners();

        Debug.info("&e------------ &aEverything is loaded! :) &e------------");
    }

    @Override
    public void onDisable() {
        //TODO: use paper's getServer().isStopping(); to see if we are reloading
        gameCore.destroy();
        commands.destroy();
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerCoreListener(), this);
    }

    public static Main getInstance() {
        return instance;
    }

    public static MainConfig getMainConfig() {
        return instance.mainConfig;
    }

    public static VisualsConfig getVisualsConfig() {
        return instance.visualsConfig;
    }
}
