package misat11.bw.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.onarandombox.multiverseinventories.event.MVInventoryHandlingEvent;

import misat11.bw.Main;

public class MultiverseInventoriesListener implements Listener {
	@EventHandler
	public void onInventoryChange(MVInventoryHandlingEvent event) {
		Player player = event.getPlayer();
		if (Main.isPlayerInGame(player)) {
			event.setCancelled(true);
		}
	}
}
