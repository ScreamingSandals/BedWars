package org.screamingsandals.bedwars.addons;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.screamingsandals.bedwars.api.BedwarsAPI;

import java.util.logging.Level;

public class Main extends JavaPlugin {
    private static Main main;
    private BedwarsAPI bedwarsAPI;

    @Override
    public void onEnable() {
        main = this;
        bedwarsAPI = BedwarsAPI.getInstance();

        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        getServer().getPluginManager().registerEvents(new PlayerListener(), this);

        getServer().getLogger().log(Level.INFO, "Enabled!");
    }

    @Override
    public void onDisable() {
        getServer().getServicesManager().unregisterAll(this);
        getServer().getLogger().log(Level.INFO, "Disabled!");
    }

    public static Main getInstance() {
        return main;
    }

    public static BedwarsAPI getApi() {
        return main.bedwarsAPI;
    }
}
