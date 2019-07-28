package misat11.bw.listener;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.world.StructureGrowEvent;

import misat11.bw.Main;
import misat11.bw.api.GameStatus;
import misat11.bw.game.Game;
import misat11.bw.game.GameCreator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
	public void onExplode(EntityExplodeEvent event) {
		if (event.isCancelled()) {
			return;
		}

		for (String s : Main.getGameNames()) {
			Game game = Main.getGame(s);
			if (game.getStatus() == GameStatus.RUNNING) {
				if (GameCreator.isInArea(event.getLocation(), game.getPos1(), game.getPos2())) {
					if (Main.getConfigurator().config.getBoolean("destroy-placed-blocks-by-explosion", true)) {
						event.blockList().removeIf(block -> !game.isBlockAddedDuringGame(block.getLocation()));

					} else {
						event.blockList().clear();
					}
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
			if (game.getStatus() == GameStatus.RUNNING && game.getOriginalOrInheritedPreventSpawningMobs()) {
				if (GameCreator.isInArea(event.getLocation(), game.getPos1(), game.getPos2())) {
					event.setCancelled(true);
					return;
				}
			} else if (game.getStatus() == GameStatus.WAITING) {
				if (game.getLobbyWorld() == event.getLocation().getWorld()) {
					if (event.getLocation().distanceSquared(game.getLobbySpawn()) <= Math
							.pow(Main.getConfigurator().config.getInt("prevent-lobby-spawn-mobs-in-radius"), 2)) {
						event.setCancelled(true);
						return;
					}
				}
			}
		}
	}

	@EventHandler
	public void onLiquidFlow(BlockFromToEvent event) {
		if (event.isCancelled()) {
			return;
		}

		for (String gameName : Main.getGameNames()) {
			Game game = Main.getGame(gameName);
			if (GameCreator.isInArea(event.getBlock().getLocation(), game.getPos1(), game.getPos2())) {
				if (game.getStatus() == GameStatus.RUNNING) {
					Block block = event.getToBlock();
					if (block.getType() == Material.AIR
							|| game.getRegion().isBlockAddedDuringGame(block.getLocation())) {
						game.getRegion().addBuildedDuringGame(block.getLocation());
					} else {
						event.setCancelled(true);
					}
				} else if (game.getStatus() != GameStatus.DISABLED) {
					event.setCancelled(true);
				}
			}
		}

	}

	@EventHandler
	public void onEntityChangeBlock(EntityChangeBlockEvent event) {
		if (event.isCancelled()) {
			return;
		}

		for (String gameName : Main.getGameNames()) {
			Game game = Main.getGame(gameName);
			if (GameCreator.isInArea(event.getBlock().getLocation(), game.getPos1(), game.getPos2())) {
				if (game.getStatus() == GameStatus.RUNNING) {
					if (event.getEntityType() == EntityType.FALLING_BLOCK
							&& game.getOriginalOrInheritedAllowBlockFalling()) {
						if (event.getBlock().getType() != event.getTo()) {
							if (!game.getRegion().isBlockAddedDuringGame(event.getBlock().getLocation())) {
								if (event.getBlock().getType() != Material.AIR) {
									game.getRegion().putOriginalBlock(event.getBlock().getLocation(),
											event.getBlock().getState());
								}
								game.getRegion().addBuildedDuringGame(event.getBlock().getLocation());
							}
						}
						return; // allow block fall
					}
				}

				if (game.getStatus() != GameStatus.DISABLED) {
					event.setCancelled(true);
				}
			}
		}
	}
}
