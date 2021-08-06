package org.screamingsandals.bedwars.listener;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Entity;
import org.screamingsandals.bedwars.api.events.OpenShopEvent;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.events.OpenShopEventImpl;
import org.screamingsandals.bedwars.game.GameStore;
import org.screamingsandals.bedwars.game.Game;
import org.bukkit.event.Listener;
import org.screamingsandals.bedwars.inventories.ShopInventory;
import org.screamingsandals.bedwars.lib.debug.Debug;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.bedwars.player.PlayerManager;
import org.screamingsandals.lib.entity.EntityBasic;
import org.screamingsandals.lib.event.EventManager;
import org.screamingsandals.lib.event.OnEvent;
import org.screamingsandals.lib.event.player.SPlayerInteractEntityEvent;
import org.screamingsandals.lib.npc.event.NPCInteractEvent;
import org.screamingsandals.lib.utils.annotations.Service;

@Service(dependsOn = {
        PlayerManager.class,
        ShopInventory.class
})
@RequiredArgsConstructor
public class VillagerListener implements Listener {
    private final PlayerManager playerManager;
    private final ShopInventory shopInventory;

    @OnEvent
    public void onVillagerInteract(SPlayerInteractEntityEvent event) {
        if (playerManager.isPlayerInGame(event.getPlayer())) {
            var gPlayer = playerManager.getPlayer(event.getPlayer()).orElseThrow();
            var game = gPlayer.getGame();
            if (event.getClickedEntity().getEntityType().isAlive() && !gPlayer.isSpectator
                    && gPlayer.getGame().getStatus() == GameStatus.RUNNING) {
                for (var store : game.getGameStoreList()) {
                    if (store.getEntity().equals(event.getClickedEntity().as(Entity.class))) {
                        event.setCancelled(true);
                        open(store, gPlayer, event.getClickedEntity(), game);
                        return;
                    }
                }
            }
        }
    }

    @OnEvent
    public void onNPCInteract(NPCInteractEvent event) {
        if (playerManager.isPlayerInGame(event.getPlayer())) {
            var gPlayer = playerManager.getPlayer(event.getPlayer()).orElseThrow();
            var game = gPlayer.getGame();
            if (!gPlayer.isSpectator && gPlayer.getGame().getStatus() == GameStatus.RUNNING) {
                for (var store : game.getGameStoreList()) {
                    if (store.getNpc().equals(event.getNpc())) {
                        open(store, gPlayer, null, game);
                        return;
                    }
                }
            }
        }
    }

    public void open(GameStore store, BedWarsPlayer player, EntityBasic clickedEntity, Game game) {
        var openShopEvent = new OpenShopEventImpl(game, clickedEntity, player, store);
        EventManager.fire(openShopEvent);

        if (openShopEvent.getResult() != OpenShopEvent.Result.ALLOW) {
            return;
        }
        Debug.info(openShopEvent.getPlayer().getName() + " opened villager");

        shopInventory.show(openShopEvent.getPlayer(), store);
    }
}
