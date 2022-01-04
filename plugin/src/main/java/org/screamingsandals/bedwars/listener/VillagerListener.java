/*
 * Copyright (C) 2022 ScreamingSandals
 *
 * This file is part of Screaming BedWars.
 *
 * Screaming BedWars is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Screaming BedWars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Screaming BedWars. If not, see <https://www.gnu.org/licenses/>.
 */

package org.screamingsandals.bedwars.listener;

import lombok.RequiredArgsConstructor;
import org.screamingsandals.bedwars.api.events.OpenShopEvent;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.events.OpenShopEventImpl;
import org.screamingsandals.bedwars.game.GameStoreImpl;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.bedwars.inventories.ShopInventory;
import org.screamingsandals.bedwars.lib.debug.Debug;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.bedwars.player.PlayerManagerImpl;
import org.screamingsandals.lib.Server;
import org.screamingsandals.lib.entity.EntityBasic;
import org.screamingsandals.lib.event.EventManager;
import org.screamingsandals.lib.event.OnEvent;
import org.screamingsandals.lib.event.player.SPlayerInteractEntityEvent;
import org.screamingsandals.lib.npc.event.NPCInteractEvent;
import org.screamingsandals.lib.tasker.Tasker;
import org.screamingsandals.lib.utils.annotations.Service;

@Service
@RequiredArgsConstructor
public class VillagerListener {
    private final PlayerManagerImpl playerManager;
    private final ShopInventory shopInventory;

    @OnEvent
    public void onVillagerInteract(SPlayerInteractEntityEvent event) {
        if (playerManager.isPlayerInGame(event.getPlayer())) {
            var gPlayer = playerManager.getPlayer(event.getPlayer()).orElseThrow();
            var game = gPlayer.getGame();
            if (event.getClickedEntity().getEntityType().isAlive() && !gPlayer.isSpectator()
                    && gPlayer.getGame().getStatus() == GameStatus.RUNNING) {
                for (var store : game.getGameStoreList()) {
                    if (event.getClickedEntity().equals(store.getEntity())) {
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
            if (!gPlayer.isSpectator() && gPlayer.getGame().getStatus() == GameStatus.RUNNING) {
                for (var store : game.getGameStoreList()) {
                    if (event.getVisual().equals(store.getNpc())) {
                        Server.runSynchronously(() -> open(store, gPlayer, null, game));
                        return;
                    }
                }
            }
        }
    }

    public void open(GameStoreImpl store, BedWarsPlayer player, EntityBasic clickedEntity, GameImpl game) {
        var openShopEvent = new OpenShopEventImpl(game, clickedEntity, player, store);
        EventManager.fire(openShopEvent);

        if (openShopEvent.getResult() != OpenShopEvent.Result.ALLOW) {
            return;
        }
        Debug.info(openShopEvent.getPlayer().getName() + " opened villager");

        shopInventory.show(openShopEvent.getPlayer(), store);
    }
}
