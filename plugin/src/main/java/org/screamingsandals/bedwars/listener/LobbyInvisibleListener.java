package org.screamingsandals.bedwars.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.screamingsandals.bedwars.api.events.BedwarsGameStartedEvent;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.utils.MiscUtils;

import java.util.Objects;

public class LobbyInvisibleListener implements Listener {

    @EventHandler
    public void onGameStarted(BedwarsGameStartedEvent event) {
        Game game = event.getGame();
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
            Block block = loc.getBlock();
            BlockState blockState = Objects.requireNonNull(block.getState());
            game.getRegion().putOriginalBlock(loc, blockState);
            block.setType(Material.AIR, false);
        });
    }
}
