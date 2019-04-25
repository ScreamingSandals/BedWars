package misat11.bw.listener;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import misat11.bw.Main;
import misat11.bw.api.GameStatus;
import misat11.bw.api.GameStore;
import misat11.bw.api.events.BedwarsOpenShopEvent;
import misat11.bw.game.GamePlayer;

public class VillagerListener implements Listener {

	@EventHandler
	public void onVillagerInteract(PlayerInteractEntityEvent event) {
		if (Main.isPlayerInGame(event.getPlayer())) {
			GamePlayer gPlayer = Main.getPlayerGameProfile(event.getPlayer());
			if (event.getRightClicked().getType() == EntityType.VILLAGER && !gPlayer.isSpectator && gPlayer.getGame().getStatus() == GameStatus.RUNNING) {
				event.setCancelled(true);
				GameStore store = null;
				
				for (GameStore stor : gPlayer.getGame().getGameStores()) {
					if (stor.getEntity() == event.getRightClicked()) {
						store = stor;
						break;
					}
				}
				
				BedwarsOpenShopEvent openShopEvent = new BedwarsOpenShopEvent(gPlayer.getGame(), event.getPlayer(), store, event.getRightClicked());
				Main.getInstance().getServer().getPluginManager().callEvent(openShopEvent);
				
				if (openShopEvent.isCancelled()) {
					return;
				}
				
				Main.openStore(event.getPlayer(), store);
			}
		}
	}
}
