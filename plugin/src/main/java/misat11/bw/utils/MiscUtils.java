package misat11.bw.utils;

import misat11.bw.Main;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class MiscUtils {

    public static int randInt(int min, int max) {
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    public static void sendActionBarMessage(Player player, String message) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (Main.isSpigot()) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                            TextComponent.fromLegacyText(message));
                } else {
                    player.sendMessage(message);
                }
            }
        }.runTask(Main.getInstance());
    }
}
