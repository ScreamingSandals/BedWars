package org.screamingsandals.bedwars.utils;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import lombok.experimental.UtilityClass;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.screamingsandals.bedwars.BedWarsPlugin;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.lib.debug.Debug;
import org.screamingsandals.lib.player.PlayerWrapper;
import org.screamingsandals.lib.tasker.Tasker;
import org.screamingsandals.lib.tasker.TaskerTime;

@UtilityClass
public class BungeeUtils {
    public void movePlayerToBungeeServer(PlayerWrapper playerWrapper, boolean serverRestart) {
        movePlayerToBungeeServer(playerWrapper.as(Player.class), serverRestart);
    }

    public void movePlayerToBungeeServer(Player player, boolean serverRestart) {
        if (serverRestart) {
            internalMove(player, true);
            return;
        }

        Tasker.build(() -> internalMove(player, false)).start();
    }

    public void sendPlayerBungeeMessage(PlayerWrapper playerWrapper, String s) {
        sendPlayerBungeeMessage(playerWrapper.as(Player.class), s);
    }

    public void sendPlayerBungeeMessage(Player player, String string) {
        Tasker.build(() -> {
            ByteArrayDataOutput out = ByteStreams.newDataOutput();

            out.writeUTF("Message");
            out.writeUTF(player.getName());
            out.writeUTF(string);

            Bukkit.getServer().sendPluginMessage(BedWarsPlugin.getInstance().getPluginDescription().as(JavaPlugin.class), "BungeeCord", out.toByteArray());
        }).delay(30, TaskerTime.TICKS).start();
    }

    private void internalMove(Player player, boolean restart) {
        String server = MainConfig.getInstance().node("bungee", "server").getString();
        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        out.writeUTF("Connect");
        out.writeUTF(server);

        player.sendPluginMessage(BedWarsPlugin.getInstance().getPluginDescription().as(JavaPlugin.class), "BungeeCord", out.toByteArray());
        Debug.info("player " + player.getName() + " has been moved to hub server ");
        if (!restart && MainConfig.getInstance().node("bungee", "kick-when-proxy-too-slow").getBoolean()) {
            Bukkit.getScheduler().runTaskLater(BedWarsPlugin.getInstance().getPluginDescription().as(JavaPlugin.class), () -> {
                if (player.isOnline()) {
                    player.kickPlayer("Bedwars can't properly transfer player through bungee network. Contact server admin.");
                }
            }, 20L);
        }
    }
}