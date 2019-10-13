package org.screamingsandals.bedwars.listener;

import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.api.game.GameStore;
import org.screamingsandals.bedwars.api.events.BedwarsOpenShopEvent;
import org.screamingsandals.bedwars.api.events.BedwarsOpenShopEvent.Result;
import org.screamingsandals.bedwars.game.Game;
import org.screamingsandals.bedwars.game.GamePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class VillagerListener implements Listener {

    @EventHandler
    public void onVillagerInteract(PlayerInteractEntityEvent event) {
        if (Main.isPlayerInGame(event.getPlayer())) {
            GamePlayer gPlayer = Main.getPlayerGameProfile(event.getPlayer());
            Game game = gPlayer.getGame();
            if (event.getRightClicked().getType().isAlive() && !gPlayer.isSpectator
                    && gPlayer.getGame().getStatus() == GameStatus.RUNNING) {
                for (GameStore store : game.getGameStores()) {
                    if (store.getEntity().equals(event.getRightClicked())) {
                        event.setCancelled(true);

                        BedwarsOpenShopEvent openShopEvent = new BedwarsOpenShopEvent(gPlayer.getGame(),
                                event.getPlayer(), store, event.getRightClicked());
                        Main.getInstance().getServer().getPluginManager().callEvent(openShopEvent);

                        if (openShopEvent.getResult() != Result.ALLOW) {
                            return;
                        }

                        Main.openStore(event.getPlayer(), store);
                        return;
                    }
                }

            }
        }
    }
}
