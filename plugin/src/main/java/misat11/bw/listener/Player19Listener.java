package misat11.bw.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import misat11.bw.Main;
import misat11.bw.api.GameStatus;
import misat11.bw.game.Game;
import misat11.bw.game.GameCreator;
import misat11.bw.game.GamePlayer;

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
	
	@EventHandler
	public void onItemMerge(ItemMergeEvent event) {
		// This is already in 1.8.8, but in older 1.8.x versions not (need to check 1.8.8/9 version)
		if (event.isCancelled()) {
			return;
		}

		for (String s : Main.getGameNames()) {
			Game game = Main.getGame(s);
			if (game.getStatus() == GameStatus.RUNNING) {
				if (GameCreator.isInArea(event.getEntity().getLocation(), game.getPos1(), game.getPos2()) || GameCreator.isInArea(event.getTarget().getLocation(), game.getPos1(), game.getPos2())) {
					event.setCancelled(true);
					return;
				}
			}
		}
	}
}
