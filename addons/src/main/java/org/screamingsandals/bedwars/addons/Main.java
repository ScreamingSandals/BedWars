package org.screamingsandals.bedwars.addons;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.screamingsandals.bedwars.api.BedwarsAPI;

public class Main extends JavaPlugin {
    private static Main main;
    private BedwarsAPI bedwarsAPI;

    @Override
    public void onEnable() {
        main = this;
        bedwarsAPI = BedwarsAPI.getInstance();

        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }

    @Override
    public void onDisable() {

    }

    public static Main getInstance() {
        return main;
    }

    public static BedwarsAPI getApi() {
        return main.bedwarsAPI;
    }
}
