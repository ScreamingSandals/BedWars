package org.screamingsandals.bedwars.listener;

import me.gnat008.perworldinventory.events.InventoryLoadEvent;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.screamingsandals.bedwars.player.PlayerManagerImpl;

public class PerWorldInventoryLegacyListener implements Listener {

    @EventHandler
    public void onInventoryChange(InventoryLoadEvent event) {
        Player player = event.getPlayer();
        if (PlayerManagerImpl.getInstance().isPlayerInGame(player.getUniqueId())) {
            BedWarsPlayer gPlayer = PlayerManagerImpl.getInstance().getPlayer(player.getUniqueId()).orElseThrow();
            if (gPlayer.getGame() != null || gPlayer.isTeleportingFromGame_justForInventoryPlugins) {
                gPlayer.isTeleportingFromGame_justForInventoryPlugins = false;
                event.setCancelled(true);
            }
        }
    }
}
