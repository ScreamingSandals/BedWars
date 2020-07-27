package org.screamingsandals.bedwars.listener;

import org.bukkit.Material;
import org.bukkit.block.data.type.Cake;
import org.bukkit.event.player.PlayerInteractEvent;
import org.screamingsandals.bedwars.game.Game;

public class Player113ListenerUtils {
    public static void yummyCake(PlayerInteractEvent event, Game game) {
        if (event.getClickedBlock().getBlockData() instanceof Cake) {
            Cake cake = (Cake) event.getClickedBlock().getBlockData();
            if (cake.getBites() == 0) {
                game.getRegion().putOriginalBlock(event.getClickedBlock().getLocation(), event.getClickedBlock().getState());
            }
            cake.setBites(cake.getBites() + 1);
            if (cake.getBites() >= cake.getMaximumBites()) {
                game.bedDestroyed(event.getClickedBlock().getLocation(), event.getPlayer(), false, false, true);
                event.getClickedBlock().setType(Material.AIR);
            } else {
                event.getClickedBlock().setBlockData(cake);
            }
        }
    }
}
