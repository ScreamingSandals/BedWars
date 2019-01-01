package misat11.bw.listener;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import misat11.bw.Main;
import misat11.bw.game.GamePlayer;
import misat11.bw.game.GameStatus;

public class VillagerListener implements Listener {

	@EventHandler
	public void onVillagerInteract(PlayerInteractEntityEvent event) {
		if (Main.isPlayerInGame(event.getPlayer())) {
			GamePlayer gPlayer = Main.getPlayerGameProfile(event.getPlayer());
			if (event.getRightClicked().getType() == EntityType.VILLAGER && !gPlayer.isSpectator && gPlayer.getGame().getStatus() == GameStatus.RUNNING) {
				event.setCancelled(true);
				Main.openStore(event.getPlayer());
			}
		}
	}
}
