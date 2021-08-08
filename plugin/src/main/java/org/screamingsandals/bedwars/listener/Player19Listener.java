package org.screamingsandals.bedwars.listener;

import org.screamingsandals.bedwars.api.config.ConfigurationContainer;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.game.GameManagerImpl;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.screamingsandals.bedwars.lib.debug.Debug;
import org.screamingsandals.bedwars.player.PlayerManagerImpl;
import org.screamingsandals.bedwars.utils.ArenaUtils;

public class Player19Listener implements Listener {
    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        if (PlayerManagerImpl.getInstance().isPlayerInGame(player.getUniqueId())) {
            BedWarsPlayer gPlayer = PlayerManagerImpl.getInstance().getPlayer(player.getUniqueId()).orElseThrow();
            if (gPlayer.getGame().getStatus() == GameStatus.WAITING) {
                event.setCancelled(true);
                Debug.info(event.getPlayer().getName() + " tried to swap his hands in lobby, cancelling");
            }
        }
    }

    @EventHandler
    public void onItemMerge(ItemMergeEvent event) {
        // This is already in 1.8.8, but in older 1.8.x versions not (need to check 1.8.8/9 version)
        if (event.isCancelled()) {
            return;
        }

        for (var game : GameManagerImpl.getInstance().getGames()) {
            if (game.getStatus() == GameStatus.RUNNING && game.getConfigurationContainer().getOrDefault(ConfigurationContainer.SPAWNER_DISABLE_MERGE, Boolean.class, false)) {
                if (ArenaUtils.isInArea(event.getEntity().getLocation(), game.getPos1(), game.getPos2()) || ArenaUtils.isInArea(event.getTarget().getLocation(), game.getPos1(), game.getPos2())) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }
}
