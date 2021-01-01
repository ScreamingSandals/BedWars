package org.screamingsandals.bedwars.utils;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.screamingsandals.bedwars.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.screamingsandals.bedwars.lib.debug.Debug;

public class BungeeUtils {
    public static void movePlayerToBungeeServer(Player player, boolean serverRestart) {
        if (serverRestart) {
            internalMove(player, true);
            return;
        }

        new BukkitRunnable() {
            public void run() {
               internalMove(player, false);
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

    private static void internalMove(Player player, boolean restart) {
        String server = Main.getConfigurator().config.getString("bungee.server");
        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        out.writeUTF("Connect");
        out.writeUTF(server);

        player.sendPluginMessage(Main.getInstance(), "BungeeCord", out.toByteArray());
        Debug.info("player " + player.getName() + " has been moved to hub server ");
        if (!restart && Main.getConfigurator().config.getBoolean("bungee.kick-when-proxy-too-slow")) {
            Bukkit.getScheduler().runTaskLater(Main.getInstance(), () -> {
                if (player.isOnline()) {
                    player.kickPlayer("Bedwars can't properly transfer player through bungee network. Contact server admin.");
                }
            }, 20L);
        }
    }
}