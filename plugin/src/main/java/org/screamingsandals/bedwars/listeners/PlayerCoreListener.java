package org.screamingsandals.bedwars.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.screamingsandals.lib.gamecore.events.player.SPlayerPreRegisterEvent;
import org.screamingsandals.lib.gamecore.events.player.SPlayerRegisteredEvent;
import org.screamingsandals.lib.gamecore.events.player.SPlayerUnregisteredEvent;

public class PlayerCoreListener implements Listener {

    @EventHandler
    public void onPreRegister(SPlayerPreRegisterEvent event) {
        System.out.println("Registering player!");
    }

    @EventHandler
    public void onRegister(SPlayerRegisteredEvent event) {
        System.out.println("Player registered!");
        System.out.println(event.getGamePlayer().toString());
    }

    @EventHandler
    public void onUnregister(SPlayerUnregisteredEvent event) {
        System.out.println("Player unregistered!");
    }
}
