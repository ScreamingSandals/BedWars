package misat11.bw.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.scheduler.BukkitRunnable;

import misat11.bw.Main;
import misat11.bw.api.GameStatus;
import misat11.bw.api.events.BedwarsPlayerKilledEvent;
import misat11.bw.api.events.BedwarsTeamChestOpenEvent;
import misat11.bw.commands.BwCommand;
import misat11.bw.game.CurrentTeam;
import misat11.bw.game.Game;
import misat11.bw.game.GameCreator;
import misat11.bw.game.GamePlayer;
import misat11.bw.game.Team;
import misat11.bw.statistics.PlayerStatistic;
import misat11.bw.utils.ArmorStandUtils;
import misat11.bw.utils.Sounds;
import misat11.bw.utils.SpawnEffects;
import misat11.bw.utils.TeamJoinMetaDataValue;
import misat11.bw.utils.TeamSelectorInventory;
import misat11.lib.nms.NMSUtils;
import net.milkbowl.vault.chat.Chat;

import static misat11.lib.lang.I18n.i18n;

import java.util.Iterator;
import java.util.List;

public class PlayerListener implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDeath(PlayerDeathEvent event) {
		final Player victim = (Player) event.getEntity();
		if (Main.isPlayerInGame(victim)) {
			GamePlayer gVictim = Main.getPlayerGameProfile(victim);
			Game game = gVictim.getGame();
			event.setKeepInventory(game.getOriginalOrInheritedKeepInventory());
			event.setDroppedExp(0);
			if (game.getStatus() == GameStatus.RUNNING) {
				if (!game.getOriginalOrInheritedPlayerDrops()) {
					event.getDrops().clear();
				}
				CurrentTeam team = game.getPlayerTeam(gVictim);
				SpawnEffects.spawnEffect(game, victim, "game-effects.kill");
				if (!team.isBed) {
					game.updateScoreboard();
					gVictim.isSpectator = true;
					team.players.remove(gVictim);
					team.getScoreboardTeam().removeEntry(victim.getName());
					if (Main.isPlayerStatisticsEnabled()) {
						PlayerStatistic statistic = Main.getPlayerStatisticsManager().getStatistic(victim);
						statistic.setCurrentLoses(statistic.getCurrentLoses() + 1);
						statistic.setCurrentScore(statistic.getCurrentScore()
								+ Main.getConfigurator().config.getInt("statistics.scores.lose", 0));

					}
				}

				boolean onlyOnBedDestroy = Main.getConfigurator().config.getBoolean("statistics.bed-destroyed-kills",
						false);

				Player killer = victim.getKiller();
				if (Main.isPlayerInGame(killer)) {
					GamePlayer gKiller = Main.getPlayerGameProfile(killer);
					if (gKiller.getGame() == game) {
						Main.depositPlayer(killer, Main.getVaultKillReward());
						if ((onlyOnBedDestroy && !team.isBed) || !onlyOnBedDestroy) {
							game.dispatchRewardCommands("player-kill", killer,
									Main.getConfigurator().config.getInt("statistics.scores.kill", 10));
						}
						if (team.isDead()) {
							SpawnEffects.spawnEffect(game, victim, "game-effects.teamkill");
							Sounds.playSound(killer, killer.getLocation(),
									Main.getConfigurator().config.getString("sounds.on_team_kill"),
									Sounds.ENTITY_PLAYER_LEVELUP, 1, 1);
						}
					}
				}
				BedwarsPlayerKilledEvent killedEvent = new BedwarsPlayerKilledEvent(game, victim,
						Main.isPlayerInGame(killer) ? killer : null);
				Main.getInstance().getServer().getPluginManager().callEvent(killedEvent);

				if (Main.isPlayerStatisticsEnabled()) {
					PlayerStatistic diePlayer = Main.getPlayerStatisticsManager().getStatistic(victim);
					PlayerStatistic killerPlayer = null;

					boolean teamIsDead = !team.isBed;

					if ((onlyOnBedDestroy && teamIsDead) || !onlyOnBedDestroy) {
						diePlayer.setCurrentDeaths(diePlayer.getCurrentDeaths() + 1);
						diePlayer.setCurrentScore(diePlayer.getCurrentScore()
								+ Main.getConfigurator().config.getInt("statistics.scores.die", 0));
					}

					if (killer != null) {
						if ((onlyOnBedDestroy && teamIsDead) || !onlyOnBedDestroy) {
							killerPlayer = Main.getPlayerStatisticsManager().getStatistic(killer);
							if (killerPlayer != null) {
								killerPlayer.setCurrentKills(killerPlayer.getCurrentKills() + 1);
								killerPlayer.setCurrentScore(killerPlayer.getCurrentScore()
										+ Main.getConfigurator().config.getInt("statistics.scores.kill", 10));
							}
						}
					}
				}
			}
			NMSUtils.respawn(Main.getInstance(), victim, 20L);
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

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		if (Main.isPlayerInGame(event.getPlayer())) {
			GamePlayer gPlayer = Main.getPlayerGameProfile(event.getPlayer());
			if (gPlayer.getGame().getStatus() == GameStatus.WAITING) {
				event.setRespawnLocation(gPlayer.getGame().getLobbySpawn());
				return;
			}
			if (gPlayer.isSpectator) {
				event.setRespawnLocation(gPlayer.getGame().makeSpectator(gPlayer));
			} else {
				event.setRespawnLocation(gPlayer.getGame().getPlayerTeam(gPlayer).teamInfo.spawn);
				SpawnEffects.spawnEffect(gPlayer.getGame(), gPlayer.player, "game-effects.respawn");
				if (gPlayer.getGame().getOriginalOrInheritedPlayerRespawnItems()) {
					List<ItemStack> givedGameStartItems = (List<ItemStack>) Main.getConfigurator().config.getList("gived-player-respawn-items");
					for (ItemStack stack : givedGameStartItems) {
						gPlayer.player.getInventory().addItem(stack);
					}
				}
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
					event.getBlockReplacedState(), event.getItemInHand())) {
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

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onCommandExecuted(PlayerCommandPreprocessEvent event) {
		if (event.isCancelled())
			return;
		if (Main.isPlayerInGame(event.getPlayer())) {
			if (!Main.isCommandAllowedInGame(event.getMessage().split(" ")[0])) {
				event.setCancelled(true);
				event.getPlayer().sendMessage(i18n("command_is_not_allowed"));
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

	@EventHandler(priority = EventPriority.HIGH)
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
			} else if (!gPlayer.getGame().getOriginalOrInheritedCrafting()) {
				event.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDamage(EntityDamageEvent event) {
		if (event.isCancelled()) {
			return;
		}

		if (!(event.getEntity() instanceof Player)) {

			if (event instanceof EntityDamageByEntityEvent) {

				Game game = Main.getInGameEntity(event.getEntity());
				if (game != null) {
					if (game.getOriginalOrInheritedPreventKillingVillagers()) {
						event.setCancelled(true);
					}
				}

				if (event.getEntity() instanceof ArmorStand) {
					Entity damager = ((EntityDamageByEntityEvent) event).getDamager();
					if (damager instanceof Player) {
						Player player = (Player) damager;
						if (Main.isPlayerInGame(player)) {
							GamePlayer gPlayer = Main.getPlayerGameProfile(player);
							if (gPlayer.getGame().getStatus() == GameStatus.WAITING || gPlayer.isSpectator) {
								event.setCancelled(true);
							}
						}
					}
				}

			}

			return;
		}

		Player player = (Player) event.getEntity();
		if (Main.isPlayerInGame(player)) {
			GamePlayer gPlayer = Main.getPlayerGameProfile(player);
			Game game = gPlayer.getGame();
			if (gPlayer.isSpectator) {
				if (event.getCause() == DamageCause.VOID) {
					player.teleport(game.getSpecSpawn());
				}
				event.setCancelled(true);
			} else if (game.getStatus() == GameStatus.WAITING) {
				if (event.getCause() == DamageCause.VOID) {
					player.teleport(game.getLobbySpawn());
				}
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
		if (event.isCancelled() && event.getAction() != Action.RIGHT_CLICK_AIR) {
			return;
		}

		Player player = event.getPlayer();
		if (Main.isPlayerInGame(player)) {
			GamePlayer gPlayer = Main.getPlayerGameProfile(player);
			Game game = gPlayer.getGame();
			if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if (game.getStatus() == GameStatus.WAITING || gPlayer.isSpectator) {
					event.setCancelled(true);
					if (event.getMaterial() == Material
							.valueOf(Main.getConfigurator().config.getString("items.jointeam", "COMPASS"))) {
						if (game.getStatus() == GameStatus.WAITING) {
							TeamSelectorInventory inv = game.getTeamSelectorInventory();
							if (inv == null) {
								return;
							}
							inv.openForPlayer(player);
						} else if (gPlayer.isSpectator) {
							// TODO
						}
					}
					else if (event.getMaterial() == Material.valueOf(Main.getConfigurator().config.getString("items.startgame", "DIAMOND"))) {
						if (game.getStatus() == GameStatus.WAITING) {
							if (game.checkMinPlayers()) {
								game.gameStartItem = true;
								game.runTask();
							} else {
								player.sendMessage(i18n("vip_not_enough_players"));
							}
						}
					}
					else if (event.getMaterial() == Material
							.valueOf(Main.getConfigurator().config.getString("items.leavegame", "SLIME_BALL"))) {
						game.leaveFromGame(player);
					}
				}

				if (game.getStatus() == GameStatus.RUNNING) {
					if (event.getClickedBlock() != null) {
						if (event.getClickedBlock().getType() == Material.ENDER_CHEST) {
							event.setCancelled(true);

							Block chest = event.getClickedBlock();
							CurrentTeam team = game.getTeamOfChest(chest);

							if (team == null) {
								return;
							}

							if (!team.players.contains(gPlayer)) {
								player.sendMessage(i18n("team_chest_is_not_your"));
								return;
							}

							BedwarsTeamChestOpenEvent teamChestOpenEvent = new BedwarsTeamChestOpenEvent(game, player,
									team);
							Main.getInstance().getServer().getPluginManager().callEvent(teamChestOpenEvent);

							if (teamChestOpenEvent.isCancelled()) {
								return;
							}

							player.openInventory(team.getTeamChestInventory());
						} else if (event.getClickedBlock().getType() == Material.CHEST) {
							game.addChestForFutureClear(event.getClickedBlock().getLocation());
						}
					}
				}

				if (event.getClickedBlock() != null) {
					if (game.getRegion().isBedBlock(event.getClickedBlock().getState())) {
						// prevent Essentials to set home in arena
						event.setCancelled(true);
					}
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
	public void onPickup(PlayerPickupItemEvent event) {
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
				if (event.getInventory().getType() == InventoryType.ENCHANTING
						|| event.getInventory().getType() == InventoryType.CRAFTING
						|| event.getInventory().getType() == InventoryType.ANVIL
						|| event.getInventory().getType() == InventoryType.BREWING
						|| event.getInventory().getType() == InventoryType.FURNACE
						|| event.getInventory().getType() == InventoryType.WORKBENCH) {
					if (!gProfile.getGame().getOriginalOrInheritedCrafting()) {
						event.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onInteractAtEntity(PlayerInteractAtEntityEvent event) {
		if (event.isCancelled()) {
			return;
		}

		if (event.getRightClicked() == null) {
			return;
		}

		Player player = event.getPlayer();
		Entity entity = event.getRightClicked();

		if (Main.isPlayerInGame(player)) {
			Game game = Main.getPlayerGameProfile(player).getGame();
			if (!(entity instanceof LivingEntity)) {
				return;
			}

			if (game.getStatus() != GameStatus.WAITING) {
				return;
			}
			LivingEntity living = (LivingEntity) entity;
			String displayName = ChatColor.stripColor(living.getCustomName());

			for (Team team : game.getTeams()) {
				if (team.name.equals(displayName)) {
					event.setCancelled(true);
					game.selectTeam(Main.getPlayerGameProfile(player), displayName);
					return;
				}
			}
		} else if (player.hasPermission(BwCommand.ADMIN_PERMISSION)) {
			List<MetadataValue> values = player.getMetadata(GameCreator.BEDWARS_TEAM_JOIN_METADATA);
			if (values == null || values.size() == 0) {
				return;
			}

			event.setCancelled(true);
			TeamJoinMetaDataValue value = (TeamJoinMetaDataValue) values.get(0);
			if (!((boolean) value.value())) {
				return;
			}

			if (!(entity instanceof LivingEntity)) {
				player.sendMessage(i18n("admin_command_jointeam_entitynotcompatible"));
				return;
			}

			LivingEntity living = (LivingEntity) entity;
			living.setRemoveWhenFarAway(false);
			living.setCanPickupItems(false);
			living.setCustomName(value.getTeam().color.chatColor + value.getTeam().name);
			living.setCustomNameVisible(Main.getConfigurator().config.getBoolean("jointeam-entity-show-name", true));

			if (living instanceof ArmorStand) {
				ArmorStandUtils.equipArmorStand((ArmorStand) living, value.getTeam());
			}

			player.removeMetadata(GameCreator.BEDWARS_TEAM_JOIN_METADATA, Main.getInstance());
			player.sendMessage(i18n("admin_command_jointeam_entity_added"));
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onJoin(PlayerJoinEvent je) {

		final Player player = je.getPlayer();

		if (Main.isHologramsEnabled()) {
			Main.getHologramInteraction().updateHolograms(player, 60L);

		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChat(AsyncPlayerChatEvent event) {
		if (event.isCancelled() || !Main.getConfigurator().config.getBoolean("chat.override")) {
			return;
		}

		Player player = event.getPlayer();
		if (Main.isPlayerInGame(player)) {
			GamePlayer gPlayer = Main.getPlayerGameProfile(player);
			Game game = gPlayer.getGame();
			CurrentTeam team = game.getPlayerTeam(gPlayer);
			String message = event.getMessage();
			boolean spectator = gPlayer.isSpectator;

			String playerName = player.getName();
			String displayName = player.getDisplayName();
			String playerListName = player.getPlayerListName();

			String format = Main.getConfigurator().config.getString("chat.format", "<%teamcolor%%name%§r> ");
			if (team != null) {
				format = format.replace("%teamcolor%", team.teamInfo.color.chatColor.toString());
				format = format.replace("%team%", team.teamInfo.name);
				format = format.replace("%coloredteam%", team.teamInfo.color.chatColor + team.teamInfo.name);
			} else if (spectator) {
				format = format.replace("%teamcolor%", ChatColor.GRAY.toString());
				format = format.replace("%team%", "SPECTATOR");
				format = format.replace("%coloredteam%", ChatColor.GRAY.toString() + "SPECTATOR");
			} else {
				format = format.replace("%teamcolor%", ChatColor.GRAY.toString());
				format = format.replace("%team%", "");
				format = format.replace("%coloredteam%", ChatColor.GRAY.toString());
			}
			format = format.replace("%name%", playerName);
			format = format.replace("%displayName%", displayName);
			format = format.replace("%playerListName%", playerListName);

			if (Main.isVault()) {
				Chat chat = ((RegisteredServiceProvider<Chat>) Bukkit.getServer().getServicesManager()
						.getRegistration(Chat.class)).getProvider();
				if (chat != null) {
					format = format.replace("%prefix%", chat.getPlayerPrefix(player));
					format = format.replace("%suffix%", chat.getPlayerSuffix(player));
				}
			}

			format = format.replace("%prefix%", "");
			format = format.replace("%suffix%", "");
			
			format = ChatColor.translateAlternateColorCodes('&', format);

			event.setFormat(format + event.getMessage());
			if (Main.getConfigurator().config.getBoolean("chat.separate-game-chat")) {
				Iterator<Player> recipients = event.getRecipients().iterator();
				while (recipients.hasNext()) {
					Player recipient = recipients.next();
					GamePlayer recipientgPlayer = Main.getPlayerGameProfile(recipient);
					Game recipientGame = recipientgPlayer.getGame();
					if (recipientGame != game) {
						recipients.remove();
					}
				}
			}
		} else {
			if (Main.getConfigurator().config.getBoolean("chat.separate-game-chat")) {
				Iterator<Player> recipients = event.getRecipients().iterator();
				while (recipients.hasNext()) {
					Player recipient = recipients.next();
					GamePlayer recipientgPlayer = Main.getPlayerGameProfile(recipient);
					Game recipientGame = recipientgPlayer.getGame();
					if (recipientGame != null) {
						recipients.remove();
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		if (event.isCancelled()) {
			return;
		}
		
		Player player = event.getPlayer();
		if (Main.isPlayerInGame(player)) {
			GamePlayer gPlayer = Main.getPlayerGameProfile(player);
			Game game = gPlayer.getGame();
			if (game.getOriginalOrInheritedDamageWhenPlayerIsNotInArena() && game.getStatus() == GameStatus.RUNNING && !gPlayer.isSpectator) {
				if (!GameCreator.isInArea(event.getTo(), game.getPos1(), game.getPos2())) {
					player.damage(5);
				}
			}
		}
	}
}
