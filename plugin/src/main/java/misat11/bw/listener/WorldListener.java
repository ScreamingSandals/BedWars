package misat11.bw.listener;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Phantom;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.world.StructureGrowEvent;

import misat11.bw.Main;
import misat11.bw.api.GameStatus;
import misat11.bw.game.Game;
import misat11.bw.game.GameCreator;

public class WorldListener implements Listener {
	@EventHandler
	public void onBurn(BlockBurnEvent event) {
		if (event.isCancelled()) {
			return;
		}

		for (String s : Main.getGameNames()) {
			Game game = Main.getGame(s);
			if (game.getStatus() == GameStatus.RUNNING) {
				if (GameCreator.isInArea(event.getBlock().getLocation(), game.getPos1(), game.getPos2())) {
					if (!game.isBlockAddedDuringGame(event.getBlock().getLocation())) {
						event.setCancelled(true);
					}
					return;
				}
			}
		}
	}

	@EventHandler
	public void onFade(BlockFadeEvent event) {
		if (event.isCancelled()) {
			return;
		}

		for (String s : Main.getGameNames()) {
			Game game = Main.getGame(s);
			if (game.getStatus() == GameStatus.RUNNING) {
				if (GameCreator.isInArea(event.getBlock().getLocation(), game.getPos1(), game.getPos2())) {
					if (!game.isBlockAddedDuringGame(event.getBlock().getLocation())) {
						event.setCancelled(true);
					}
					return;
				}
			}
		}
	}

	@EventHandler
	public void onForm(BlockFormEvent event) {
		if (event.isCancelled()) {
			return;
		}

		if (event.getNewState().getType() == Material.SNOW) {
			return;
		}

		for (String s : Main.getGameNames()) {
			Game game = Main.getGame(s);
			if (game.getStatus() == GameStatus.RUNNING) {
				if (GameCreator.isInArea(event.getBlock().getLocation(), game.getPos1(), game.getPos2())) {
					if (!game.isBlockAddedDuringGame(event.getBlock().getLocation())) {
						event.setCancelled(true);
					}
					return;
				}
			}
		}
	}

	@EventHandler
	public void onStructureGrow(StructureGrowEvent event) {
		if (event.isCancelled()) {
			return;
		}

		for (String s : Main.getGameNames()) {
			Game game = Main.getGame(s);
			if (game.getStatus() == GameStatus.RUNNING) {
				if (GameCreator.isInArea(event.getLocation(), game.getPos1(), game.getPos2())) {
					event.setCancelled(true);
					return;
				}
			}
		}
	}
	
	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent event) {
		if (event.isCancelled() || event.getSpawnReason() == SpawnReason.CUSTOM) {
			return;
		}
		
		for (String gameName : Main.getGameNames()) {
			Game game = Main.getGame(gameName);
			if (GameCreator.isInArea(event.getLocation(), game.getPos1(), game.getPos2())) {
				event.setCancelled(true);
				return;
			}
		}
	}
}
