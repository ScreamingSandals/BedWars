package org.screamingsandals.bedwars.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Cake;
import org.bukkit.entity.Player;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.lib.event.player.SPlayerInteractEvent;

public class Player113ListenerUtils {
    public static void yummyCake(SPlayerInteractEvent event, GameImpl game) {
        var bukkitBlock = event.getBlockClicked().as(Block.class);
        if (bukkitBlock.getBlockData() instanceof Cake) {
            Cake cake = (Cake) bukkitBlock.getBlockData();
            if (cake.getBites() == 0) {
                game.getRegion().putOriginalBlock(bukkitBlock.getLocation(),bukkitBlock.getState());
            }
            cake.setBites(cake.getBites() + 1);
            if (cake.getBites() >= cake.getMaximumBites()) {
                game.bedDestroyed(event.getBlockClicked().getLocation(), event.getPlayer(), false, false, true);
                bukkitBlock.setType(Material.AIR);
            } else {
                bukkitBlock.setBlockData(cake);
            }
        }
    }
}
