package org.screamingsandals.bedwars.listener;

import org.bukkit.Bukkit;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.events.OpenShopEvent;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.events.OpenShopEventImpl;
import org.screamingsandals.bedwars.game.GameStore;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.screamingsandals.bedwars.lib.debug.Debug;
import org.screamingsandals.bedwars.player.PlayerManager;
import org.screamingsandals.bedwars.utils.CitizensUtils;
import org.screamingsandals.lib.entity.EntityMapper;
import org.screamingsandals.lib.event.EventManager;

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
        var openShopEvent = new OpenShopEventImpl(game, EntityMapper.wrapEntity(event.getRightClicked()).orElseThrow(), PlayerManager.getInstance().getPlayer(event.getPlayer().getUniqueId()).orElseThrow(), store);
        EventManager.fire(openShopEvent);

        if (openShopEvent.getResult() != OpenShopEvent.Result.ALLOW) {
            return;
        }
        Debug.info(openShopEvent.getPlayer().getName() + " opened villager");

        Main.openStore(openShopEvent.getPlayer(), store);
    }
}
