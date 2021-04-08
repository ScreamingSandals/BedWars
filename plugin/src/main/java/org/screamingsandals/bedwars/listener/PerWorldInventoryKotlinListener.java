package org.screamingsandals.bedwars.listener;

import me.ebonjaeger.perworldinventory.event.InventoryLoadEvent;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.screamingsandals.bedwars.player.PlayerManager;

public class PerWorldInventoryKotlinListener implements Listener {
    @EventHandler
    public void onInventoryChange(InventoryLoadEvent event) {
        Player player = event.getPlayer();
        if (PlayerManager.getInstance().isPlayerInGame(player.getUniqueId())) {
            BedWarsPlayer gPlayer = PlayerManager.getInstance().getPlayer(player.getUniqueId()).orElseThrow();
            if (gPlayer.getGame() != null || gPlayer.isTeleportingFromGame_justForInventoryPlugins) {
                gPlayer.isTeleportingFromGame_justForInventoryPlugins = false;
                event.setCancelled(true);
            }
        }
    }
}
