package misat11.bw.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import me.ebonjaeger.perworldinventory.event.InventoryLoadEvent;
import misat11.bw.Main;

public class PerWorldInventoryKotlinListener implements Listener {
	@EventHandler
	public void onInventoryChange(InventoryLoadEvent event) {
		System.out.println("DEBUG: " + event.getEventName() + " " + event.getPlayer() + " changing world");
		Player player = event.getPlayer();
		if (Main.isPlayerInGame(player)) {
			System.out.println("DEBUG: cancelled");
			event.setCancelled(true);
		}
	}
}
