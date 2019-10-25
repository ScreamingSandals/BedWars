package org.screamingsandals.bedwars.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;

@SuppressWarnings("deprecation")
public class PlayerBefore112Listener implements Listener {

	@EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        PlayerListener.onItemPickup(event.getPlayer(), event.getItem(), event);
    }
}
