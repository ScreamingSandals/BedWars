package org.screamingsandals.bedwars.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

public class Player112Listener implements Listener {
    @EventHandler
    public void onPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        PlayerListener.onItemPickup((Player) event.getEntity(), event.getItem(), event);
    }
}
