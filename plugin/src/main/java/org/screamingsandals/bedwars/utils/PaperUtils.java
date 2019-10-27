package org.screamingsandals.bedwars.utils;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.Main;

/**
 * @author ScreamingSandals team
 */
public class PaperUtils {

    public static void teleport(Player player, Location location) {
        if (Main.isPaper()) {
            try {
                player.teleportAsync(location);
            } catch (Throwable t) {
                // Old Paper builds
                player.teleport(location);
            }
        } else {
            player.teleport(location);
        }
    }
}
