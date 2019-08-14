package misat11.bw.utils;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import misat11.bw.Main;
import misat11.bw.game.Game;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import static misat11.lib.lang.I18n.i18n;

public class BungeeUtils {

    public static void movePlayer(Player player) {
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

    public static void sendMessage(Player player, String string) {
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