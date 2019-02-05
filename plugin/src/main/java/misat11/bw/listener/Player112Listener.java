package misat11.bw.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

import misat11.bw.Main;
import misat11.bw.api.GameStatus;
import misat11.bw.game.Game;
import misat11.bw.game.GamePlayer;

public class Player112Listener implements Listener {
	@EventHandler
	public void onPickup(EntityPickupItemEvent event) {
		if (event.isCancelled() || !(event.getEntity() instanceof Player)) {
			return;
		}

		Player player = (Player) event.getEntity();
		if (Main.isPlayerInGame(player)) {
			GamePlayer gPlayer = Main.getPlayerGameProfile(player);
			Game game = gPlayer.getGame();
			if (game.getStatus() == GameStatus.WAITING || gPlayer.isSpectator) {
				event.setCancelled(true);
			}
		}

	}
}
