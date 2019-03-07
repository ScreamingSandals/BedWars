package misat11.bw.listener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import misat11.bw.Main;
import misat11.bw.api.Game;
import misat11.bw.api.GameStatus;
import misat11.bw.api.events.BedwarsApplyPropertyToBoughtItem;
import misat11.bw.api.events.BedwarsGameEndEvent;
import misat11.bw.api.events.BedwarsGameStartedEvent;
import misat11.bw.game.GamePlayer;

public class WarpPowderListener implements Listener {
	
	private final HashMap<Game, List<ItemStack>> stacks = new HashMap<Game, List<ItemStack>>();
	
	@EventHandler
	public void onPowderItemRegister(BedwarsApplyPropertyToBoughtItem event) {
		if (event.getPropertyName().equalsIgnoreCase("warppowder")) {
			stacks.get(event.getGame()).add(event.getStack());
		}
	}
	
	@EventHandler
	public void onPlayerUseItem(PlayerInteractEvent event) {
		if (event.isCancelled() && event.getAction() != Action.RIGHT_CLICK_AIR) {
			return;
		}
		
		if (!Main.isPlayerInGame(event.getPlayer())) {
			return;
		}

		GamePlayer gPlayer = Main.getPlayerGameProfile(event.getPlayer());
		Game game = gPlayer.getGame();
		if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
			if (game.getStatus() == GameStatus.RUNNING && !gPlayer.isSpectator) {
				if (stacks.get(game).contains(event.getItem())) {
					// SOME STUFF
				}
			}
		}
	}
	
	public void onGameStarts(BedwarsGameStartedEvent event) {
		stacks.put(event.getGame(), new ArrayList<ItemStack>());
	}
	
	public void onGameEnds(BedwarsGameEndEvent event) {
		stacks.remove(event.getGame());
	}
}
