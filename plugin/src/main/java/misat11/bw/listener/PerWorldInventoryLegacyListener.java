package misat11.bw.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.gnat008.perworldinventory.events.InventoryLoadEvent;
import misat11.bw.Main;
import misat11.bw.game.GamePlayer;

public class PerWorldInventoryLegacyListener implements Listener {

	@EventHandler
	public void onInventoryChange(InventoryLoadEvent event) {
		Player player = event.getPlayer();
		if (Main.isPlayerGameProfileRegistered(player)) {
			GamePlayer gPlayer = Main.getPlayerGameProfile(player);
			if (gPlayer.getGame() != null || gPlayer.isTeleportingFromGame_justForInventoryPlugins) {
				gPlayer.isTeleportingFromGame_justForInventoryPlugins = false;
				event.setCancelled(true);
			}
		}
	}
}
