package misat11.bw.listener;

import org.bukkit.Sound;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import misat11.bw.Main;
import misat11.bw.game.CurrentTeam;
import misat11.bw.game.Game;
import misat11.bw.game.GamePlayer;
import misat11.bw.game.GameStatus;
import misat11.bw.utils.I18n;
import misat11.bw.utils.TeamSelectorInventory;

public class PlayerListener implements Listener {

	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		final Player victim = (Player) event.getEntity();
		if (Main.isPlayerInGame(victim)) {
			GamePlayer gVictim = Main.getPlayerGameProfile(victim);
			Game game = gVictim.getGame();
			event.setKeepInventory(Main.getConfigurator().config.getBoolean("keep-inventory-on-death"));
			if (game.getStatus() == GameStatus.RUNNING) {
				CurrentTeam team = game.getPlayerTeam(gVictim);
				if (!team.isBed) {
					game.updateScoreboard();
					gVictim.isSpectator = true;
					team.players.remove(gVictim);
					team.getScoreboardTeam().removeEntry(victim.getName());
				}
				Player killer = victim.getKiller();
				if (Main.isPlayerInGame(killer)) {
					GamePlayer gKiller = Main.getPlayerGameProfile(killer);
					if (gKiller.getGame() == game) {
						Main.depositPlayer(killer, Main.getVaultKillReward());
						if (team.isDead()) {
							killer.playSound(killer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
						}
					}
				}
			}
			if (Main.isSpigot()) {
				new BukkitRunnable() {
					public void run() {
						victim.spigot().respawn();
					}
				}.runTaskLater(Main.getInstance(), 20L);
			}
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		if (Main.isPlayerGameProfileRegistered(event.getPlayer())) {
			GamePlayer gPlayer = Main.getPlayerGameProfile(event.getPlayer());
			if (gPlayer.isInGame())
				gPlayer.changeGame(null);
			Main.unloadPlayerGameProfile(event.getPlayer());
		}
	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		if (Main.isPlayerInGame(event.getPlayer())) {
			GamePlayer gPlayer = Main.getPlayerGameProfile(event.getPlayer());
			if (gPlayer.getGame().getStatus() == GameStatus.WAITING) {
				event.setRespawnLocation(gPlayer.getGame().getLobbySpawn());
				return;
			}
			if (gPlayer.isSpectator) {
				gPlayer.getGame().makeSpectator(gPlayer);
			} else {
				event.setRespawnLocation(gPlayer.getGame().getPlayerTeam(gPlayer).teamInfo.spawn);
			}
		}
	}

	@EventHandler
	public void onPlayerWorldChange(PlayerChangedWorldEvent event) {
		if (Main.isPlayerInGame(event.getPlayer())) {
			GamePlayer gPlayer = Main.getPlayerGameProfile(event.getPlayer());
			Game game = gPlayer.getGame();
			if (game.getWorld() != event.getPlayer().getWorld()
					&& game.getLobbySpawn().getWorld() != event.getPlayer().getWorld()) {
				gPlayer.changeGame(null);
			}
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (event.isCancelled())
			return;
		if (Main.isPlayerInGame(event.getPlayer())) {
			Game game = Main.getPlayerGameProfile(event.getPlayer()).getGame();
			if (game.getStatus() == GameStatus.WAITING) {
				event.setCancelled(true);
				return;
			}
			if (!game.blockPlace(Main.getPlayerGameProfile(event.getPlayer()), event.getBlock(),
					event.getBlockReplacedState())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.isCancelled())
			return;
		if (Main.isPlayerInGame(event.getPlayer())) {
			Game game = Main.getPlayerGameProfile(event.getPlayer()).getGame();
			if (game.getStatus() == GameStatus.WAITING) {
				event.setCancelled(true);
				return;
			}
			if (!game.blockBreak(Main.getPlayerGameProfile(event.getPlayer()), event.getBlock(), event)) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onCommandExecuted(PlayerCommandPreprocessEvent event) {
		if (event.isCancelled())
			return;
		if (Main.isPlayerInGame(event.getPlayer())) {
			if (!Main.isCommandAllowedInGame(event.getMessage().split(" ")[0])) {
				event.setCancelled(true);
				event.getPlayer().sendMessage(I18n._("command_is_not_allowed"));
			}
		}
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.isCancelled()) {
			return;
		}

		if (event.getClickedInventory() == null) {
			return;
		}
		
		if (event.getClickedInventory().getType() == InventoryType.PLAYER) {
			Player p = (Player) event.getWhoClicked();
			if (Main.isPlayerInGame(p)) {
				GamePlayer gPlayer = Main.getPlayerGameProfile(p);
				Game game = gPlayer.getGame();
				if (game.getStatus() == GameStatus.WAITING || gPlayer.isSpectator) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onHunger(FoodLevelChangeEvent event) {
		if (!(event.getEntity() instanceof Player) || event.isCancelled()) {
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

	@EventHandler
	public void onCraft(CraftItemEvent event) {
		if (event.isCancelled() || !(event.getWhoClicked() instanceof Player)) {
			return;
		}
		Player player = (Player) event.getWhoClicked();
		if (Main.isPlayerInGame(player)) {
			GamePlayer gPlayer = Main.getPlayerGameProfile(player);
			if (gPlayer.getGame().getStatus() != GameStatus.RUNNING) {
				event.setCancelled(true);
				return;
			} else if (!Main.getConfigurator().config.getBoolean("allow-crafting")) {
				event.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (event.isCancelled() || !(event.getEntity() instanceof Player)) {
			return;
		}

		Player player = (Player) event.getEntity();
		if (Main.isPlayerInGame(player)) {
			GamePlayer gPlayer = Main.getPlayerGameProfile(player);
			Game game = gPlayer.getGame();
			if (game.getStatus() == GameStatus.WAITING || gPlayer.isSpectator) {
				event.setCancelled(true);
			} else if (event instanceof EntityDamageByEntityEvent) {
				EntityDamageByEntityEvent edbee = (EntityDamageByEntityEvent) event;
				if (edbee.getDamager() instanceof Player) {
					Player damager = (Player) edbee.getDamager();
					if (Main.isPlayerInGame(damager)) {
						if (Main.getPlayerGameProfile(damager).isSpectator) {
							event.setCancelled(true);
						}
					}
				} else if (edbee.getDamager() instanceof Arrow) {
					Arrow arrow = (Arrow) edbee.getDamager();
					if (arrow.getShooter() instanceof Player) {
						Player damager = (Player) arrow.getShooter();
						if (Main.isPlayerInGame(damager)) {
							if (Main.getPlayerGameProfile(damager).isSpectator) {
								event.setCancelled(true);
							}
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		if (event.isCancelled()) {
			return;
		}
		Player player = (Player) event.getPlayer();
		if (Main.isPlayerInGame(player)) {
			GamePlayer gPlayer = Main.getPlayerGameProfile(player);
			if (gPlayer.getGame().getStatus() != GameStatus.RUNNING || gPlayer.isSpectator) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onFly(PlayerToggleFlightEvent event) {
		if (event.isCancelled()) {
			return;
		}
		Player player = (Player) event.getPlayer();
		if (Main.isPlayerInGame(player)) {
			if (!Main.getPlayerGameProfile(player).isSpectator) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerIteract(PlayerInteractEvent event) {
		if (event.isCancelled()) {
			return;
		}

		Player player = event.getPlayer();
		if (Main.isPlayerInGame(player)) {
			GamePlayer gPlayer = Main.getPlayerGameProfile(player);
			Game game = gPlayer.getGame();
			if (game.getStatus() == GameStatus.WAITING || gPlayer.isSpectator) {
				event.setCancelled(true);
				switch (event.getMaterial()) {
				case COMPASS:
					if (game.getStatus() == GameStatus.WAITING) {
						TeamSelectorInventory inv = game.getTeamSelectorInventory();
						if (inv == null) {
							return;
						}
						inv.openForPlayer(player);
					} else if (gPlayer.isSpectator) {
						// TODO
					}
					break;
				case SLIME_BALL:
					game.leaveFromGame(player);
					break;
				default:
					break;

				}
			}
		}
	}

	@EventHandler
	public void onEntityInteract(PlayerInteractEntityEvent event) {
		if (event.isCancelled()) {
			return;
		}

		Player player = event.getPlayer();
		if (Main.isPlayerInGame(player)) {
			GamePlayer gPlayer = Main.getPlayerGameProfile(player);
			Game game = gPlayer.getGame();
			if (game.getStatus() == GameStatus.WAITING || gPlayer.isSpectator) {
				event.setCancelled(true);
			}
		}
	}

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

	@EventHandler
	public void onSleep(PlayerBedEnterEvent event) {
		if (event.isCancelled()) {
			return;
		}

		if (Main.isPlayerInGame(event.getPlayer())) {
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent event) {
		if (event.isCancelled() || !(event.getPlayer() instanceof Player)) {
			return;
		}

		Player player = (Player) event.getPlayer();
		if (Main.isPlayerInGame(player)) {
			GamePlayer gProfile = Main.getPlayerGameProfile(player);
			if (gProfile.getGame().getStatus() == GameStatus.RUNNING) {
				if (gProfile.isSpectator) {
					// TODO spectator compass exclude
					event.setCancelled(true);
					return;
				}
				if (event.getInventory().getType() == InventoryType.ENDER_CHEST) {
					// TODO team chest
					event.setCancelled(true);
					return;
				}
				if (event.getInventory().getType() == InventoryType.ENCHANTING
						|| event.getInventory().getType() == InventoryType.CRAFTING
						|| event.getInventory().getType() == InventoryType.ANVIL
						|| event.getInventory().getType() == InventoryType.BREWING
						|| event.getInventory().getType() == InventoryType.FURNACE
						|| event.getInventory().getType() == InventoryType.WORKBENCH) {
					if (!Main.getConfigurator().config.getBoolean("allow-crafting")) {
						event.setCancelled(true);
					}
				}
			}
		}
	}
}
