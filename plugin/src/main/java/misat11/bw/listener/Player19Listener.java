package misat11.bw.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import misat11.bw.Main;
import misat11.bw.game.GamePlayer;
import misat11.bw.game.GameStatus;

public class Player19Listener implements Listener {
	@EventHandler
	public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
		Player player = event.getPlayer();
		if (Main.isPlayerInGame(player)) {
			GamePlayer gPlayer = Main.getPlayerGameProfile(player);
			if (gPlayer.getGame().getStatus() == GameStatus.WAITING) {
				event.setCancelled(true);
			}
		}
	}
}
