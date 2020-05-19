package org.screamingsandals.bedwars;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.screamingsandals.bedwars.api.Permissions;
import org.screamingsandals.bedwars.commands.CommandsLanguage;
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
import org.screamingsandals.lib.gamecore.language.GameLanguage;
import org.screamingsandals.lib.gamecore.player.PlayerManager;

import java.io.File;
import java.util.Collection;

public class Main extends JavaPlugin {
    private static Main instance;
    private MainConfig mainConfig;
    private VisualsConfig visualsConfig;
    @Getter
    private GameCore gameCore;
    private GameManager<Game> gameManager;
    private PlayerManager playerManager;
    @Getter
    private GameLanguage language;
    private Commands commands;

    @Getter
    private File shopFile;
    @Getter
    private File upgradesFile;

    @Override
    @SuppressWarnings("unchecked")
    public void onEnable() {
        Debug.setFallbackName("[SBedWars] ");
        instance = this;
        try {
            mainConfig = new MainConfig(ConfigAdapter.createFile(getDataFolder(), "config.yml"));
            mainConfig.load();

            visualsConfig = new VisualsConfig(ConfigAdapter.createFile(getDataFolder(), "visuals.yml"));
            visualsConfig.load();

            var shopFileName = "shop.yml";
            var upgradesFileName = "upgrades.yml";

            if (mainConfig.getBoolean(MainConfig.ConfigPaths.GROOVY)) {
                shopFileName = "shop.groovy";
                upgradesFileName = "upgrades.groovy";
            }

            shopFile = checkIfExistsOrCopyDefault(getDataFolder(), shopFileName);
            upgradesFile = checkIfExistsOrCopyDefault(getDataFolder(), upgradesFileName);

        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        language = new GameLanguage(this, mainConfig.getString("language"), mainConfig.getString("prefix"));

        Debug.init(getName());
        Debug.setDebug(mainConfig.getBoolean("debug"));

        commands = new Commands(this);
        commands.load();
        commands.setCommandLanguage(new CommandsLanguage());

        //Make sure that we destroy existing instance of the  core, fucking reloads
        if (gameCore != null) {
            gameCore.destroy();
            Debug.info("GameCore already exists, destroying and loading new!");
        }

        gameCore = new GameCore(this, Permissions.ADMIN_COMMAND, true); //TODO: configurable

        try {
            gameCore.load(new File(getDataFolder(), "games"), Game.class);
        } catch (GameCoreException e) {
            Debug.info("This is some way of fuck up.. Please report that to our GitHub or Discord!", true);
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

    private File checkIfExistsOrCopyDefault(File folder, String fileName) {
        final var file = new File(folder, fileName);
        if (file.exists()) {
            return file;
        } else {
           saveResource(fileName, false);
        }

        return file;
    }

    public static Main getInstance() {
        return instance;
    }

    public static GameManager<Game> getGameManager() {
        return instance.gameManager;
    }

    public static MainConfig getMainConfig() {
        return instance.mainConfig;
    }

    public static VisualsConfig getVisualsConfig() {
        return instance.visualsConfig;
    }
}
