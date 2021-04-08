package org.screamingsandals.bedwars.listener;

import org.bukkit.Bukkit;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.game.GameStore;
import org.screamingsandals.bedwars.api.events.BedwarsOpenShopEvent;
import org.screamingsandals.bedwars.api.events.BedwarsOpenShopEvent.Result;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.screamingsandals.bedwars.lib.debug.Debug;
import org.screamingsandals.bedwars.player.PlayerManager;
import org.screamingsandals.bedwars.utils.CitizensUtils;

public class VillagerListener implements Listener {

    @EventHandler
    public void onVillagerInteract(PlayerInteractEntityEvent event) {
        if (PlayerManager.getInstance().isPlayerInGame(event.getPlayer().getUniqueId())) {
            BedWarsPlayer gPlayer = PlayerManager.getInstance().getPlayer(event.getPlayer().getUniqueId()).get();
            Game game = gPlayer.getGame();
            if (event.getRightClicked().getType().isAlive() && !gPlayer.isSpectator
                    && gPlayer.getGame().getStatus() == GameStatus.RUNNING) {

                if (Bukkit.getPluginManager().isPluginEnabled("Citizens")) {
                    // .equals doesn't work with Citizens
                    GameStore npcStore = CitizensUtils.getFromNPC(event.getRightClicked());
                    if (npcStore != null) {
                        event.setCancelled(true);
                        open(npcStore, event, game);
                        return;
                    }
                }

                for (GameStore store : game.getGameStoreList()) {
                    if (store.getEntity().equals(event.getRightClicked())) {
                        event.setCancelled(true);
                        open(store, event, game);
                    }
                }

            }
        }
    }

    public void open(GameStore store, PlayerInteractEntityEvent event, Game game) {
        BedwarsOpenShopEvent openShopEvent = new BedwarsOpenShopEvent(game,
                event.getPlayer(), store, event.getRightClicked());
        Bukkit.getServer().getPluginManager().callEvent(openShopEvent);

        if (openShopEvent.getResult() != Result.ALLOW) {
            return;
        }
        Debug.info(event.getPlayer().getName() + " opened villager");

        Main.openStore(event.getPlayer(), store);
    }
}
