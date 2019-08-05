package misat11.bw.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.ebonjaeger.perworldinventory.event.InventoryLoadEvent;
import misat11.bw.Main;
import misat11.bw.game.GamePlayer;

public class PerWorldInventoryKotlinListener implements Listener {
	@EventHandler
	public void onInventoryChange(InventoryLoadEvent event) {
		System.out.println("DEBUG: " + event.getEventName() + " " + event.getPlayer() + " changing world");
		Player player = event.getPlayer();
		if (Main.isPlayerGameProfileRegistered(player)) {
			GamePlayer gPlayer = Main.getPlayerGameProfile(player);
			if (gPlayer.getGame() != null || gPlayer.isTeleportingFromGame_justForInventoryPlugins) {
				gPlayer.isTeleportingFromGame_justForInventoryPlugins = false;
				System.out.println("DEBUG: cancelled");
				event.setCancelled(true);
			}
		}
	}
}
