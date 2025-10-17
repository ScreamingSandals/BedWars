package org.screamingsandals.bedwars.listener;

import lombok.val;
import lombok.var;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.events.BedwarsGameStartedEvent;
import org.screamingsandals.bedwars.utils.MiscUtils;

import java.util.Objects;

public class LobbyInvisibleListener implements Listener {

    @EventHandler
    public void onGameStarted(BedwarsGameStartedEvent event) {
        val game = event.getGame();
        if (!game.getOriginalOrInheritedHideLobbyAfterGameStart()) {
            return;
        }
        if (game.getLobbyPos1() == null || game.getLobbyPos2() == null) {
            return;
        }

        MiscUtils.getLocationsBetween(game.getLobbyPos1(), game.getLobbyPos2()).forEach(loc -> {
            if (loc.getBlock().getType().isAir()) {
                return;
            }
            val block = loc.getBlock();
            val blockState = Objects.requireNonNull(block.getState());
            game.getRegion().putOriginalBlock(loc, blockState);

            val stack = new ItemStack(Material.AIR);
            block.setType(stack.getType(), false);
            if (Main.isLegacy()) {
                try {
                    // The method is no longer in API, but in legacy versions exists
                    Block.class.getMethod("setData", byte.class, boolean.class).invoke(block, (byte) stack.getDurability(), true);
                } catch (Exception e) {
                }
            }
        });
    }
}
