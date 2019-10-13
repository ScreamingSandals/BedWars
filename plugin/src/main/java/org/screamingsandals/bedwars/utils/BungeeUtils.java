package org.screamingsandals.bedwars.utils;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.screamingsandals.bedwars.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class BungeeUtils {
    public static void movePlayerToBungeeServer(Player player) {
        new BukkitRunnable() {
            public void run() {
                String server = Main.getConfigurator().config.getString("bungee.server");
                ByteArrayDataOutput out = ByteStreams.newDataOutput();

                out.writeUTF("Connect");
                out.writeUTF(server);

                player.sendPluginMessage(Main.getInstance(), "BungeeCord", out.toByteArray());
            }
        }.runTask(Main.getInstance());
    }

    public static void sendPlayerBungeeMessage(Player player, String string) {
        new BukkitRunnable() {
            public void run() {
                ByteArrayDataOutput out = ByteStreams.newDataOutput();

                out.writeUTF("Message");
                out.writeUTF(player.getName());
                out.writeUTF(string);

                Bukkit.getServer().sendPluginMessage(Main.getInstance(), "BungeeCord", out.toByteArray());
            }
        }.runTaskLater(Main.getInstance(), 30L);
    }
}