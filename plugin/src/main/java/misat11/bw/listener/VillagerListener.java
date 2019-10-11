package misat11.bw.listener;

import misat11.bw.Main;
import misat11.bw.api.game.GameStatus;
import misat11.bw.api.game.GameStore;
import misat11.bw.api.events.BedwarsOpenShopEvent;
import misat11.bw.api.events.BedwarsOpenShopEvent.Result;
import misat11.bw.game.Game;
import misat11.bw.game.GamePlayer;
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
