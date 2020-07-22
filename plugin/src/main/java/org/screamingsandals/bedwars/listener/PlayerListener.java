package org.screamingsandals.bedwars.listener;

import net.milkbowl.vault.chat.Chat;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.type.Cake;
import org.bukkit.entity.*;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.metadata.FixedMetadataValue;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.RunningTeam;
import org.screamingsandals.bedwars.api.events.BedwarsPlayerKilledEvent;
import org.screamingsandals.bedwars.api.events.BedwarsTeamChestOpenEvent;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.commands.BaseCommand;
import org.screamingsandals.bedwars.game.*;
import org.screamingsandals.bedwars.inventories.TeamSelectorInventory;
import org.screamingsandals.bedwars.statistics.PlayerStatistic;
import org.screamingsandals.bedwars.utils.*;
import org.screamingsandals.lib.debug.Debug;
import org.screamingsandals.lib.nms.entity.PlayerUtils;
import org.screamingsandals.simpleinventories.utils.StackParser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import static misat11.lib.lang.I18n.i18n;
import static misat11.lib.lang.I18n.i18nonly;
import static org.screamingsandals.bedwars.commands.BaseCommand.ADMIN_PERMISSION;

public class PlayerListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        final Player victim = event.getEntity();

        if (Main.isPlayerInGame(victim)) {
            GamePlayer gVictim = Main.getPlayerGameProfile(victim);
            Game game = gVictim.getGame();
            CurrentTeam victimTeam = game.getPlayerTeam(gVictim);
            ChatColor victimColor = victimTeam.teamInfo.color.chatColor;
            List<ItemStack> drops = new ArrayList<>(event.getDrops());
            int respawnTime = Main.getConfigurator().config.getInt("respawn-cooldown.time", 5);

            event.setKeepInventory(game.getOriginalOrInheritedKeepInventory());
            event.setDroppedExp(0);

            if (game.getStatus() == GameStatus.RUNNING) {
                if (!game.getOriginalOrInheritedPlayerDrops()) {
                    event.getDrops().clear();
                }

                if (Main.getConfigurator().config.getBoolean("chat.send-death-messages-just-in-game")) {
                    String deathMessage = event.getDeathMessage();
                    if (Main.getConfigurator().config.getBoolean("chat.send-custom-death-messages")) {
                        if (event.getEntity().getKiller() != null) {
                            Player killer = event.getEntity().getKiller();
                            GamePlayer gKiller = Main.getPlayerGameProfile(killer);
                            CurrentTeam killerTeam = game.getPlayerTeam(gKiller);
                            ChatColor killerColor = killerTeam.teamInfo.color.chatColor;

                            deathMessage = i18n("player_killed")
                                    .replace("%victim%", victimColor + victim.getDisplayName())
                                    .replace("%killer%", killerColor + killer.getDisplayName())
                                    .replace("%victimTeam%", victimColor + victimTeam.getName())
                                    .replace("%killerTeam%", killerColor + killerTeam.getName());
                        } else {
                            deathMessage = i18n("player_self_killed")
                                    .replace("%victim%", victimColor + victim.getDisplayName())
                                    .replace("%victimTeam%", victimColor + victimTeam.getName());
                        }

                    }
                    if (deathMessage != null) {
                        event.setDeathMessage(null);
                        for (Player player : game.getConnectedPlayers()) {
                            player.sendMessage(deathMessage);
                        }
                    }
                }

                CurrentTeam team = game.getPlayerTeam(gVictim);
                SpawnEffects.spawnEffect(game, victim, "game-effects.kill");
                boolean isBed = team.isBed;
                if (isBed && game.getOriginalOrInheritedAnchorDecreasing() && "RESPAWN_ANCHOR".equals(team.teamInfo.bed.getBlock().getType().name())) {
                    isBed = Player116ListenerUtils.processAnchorDeath(game, team, isBed);
                }
                if (!isBed) {
                    gVictim.isSpectator = true;
                    team.players.remove(gVictim);
                    team.getScoreboardTeam().removeEntry(victim.getName());
                    if (Main.isPlayerStatisticsEnabled()) {
                        PlayerStatistic statistic = Main.getPlayerStatisticsManager().getStatistic(victim);
                        statistic.setCurrentLoses(statistic.getCurrentLoses() + 1);
                        statistic.setCurrentScore(statistic.getCurrentScore()
                                + Main.getConfigurator().config.getInt("statistics.scores.lose", 0));

                    }
                    game.updateScoreboard();
                }

                boolean onlyOnBedDestroy = Main.getConfigurator().config.getBoolean("statistics.bed-destroyed-kills",
                        false);

                Player killer = victim.getKiller();
                if (Main.isPlayerInGame(killer)) {
                    GamePlayer gKiller = Main.getPlayerGameProfile(killer);
                    if (gKiller.getGame() == game) {
                        if (!onlyOnBedDestroy || !isBed) {
                            game.dispatchRewardCommands("player-kill", killer,
                                    Main.getConfigurator().config.getInt("statistics.scores.kill", 10));
                        }
                        if (team.isDead()) {
                            SpawnEffects.spawnEffect(game, victim, "game-effects.teamkill");
                            Sounds.playSound(killer, killer.getLocation(),
                                    Main.getConfigurator().config.getString("sounds.on_team_kill"),
                                    Sounds.ENTITY_PLAYER_LEVELUP, 1, 1);
                        } else {
                            Sounds.playSound(killer, killer.getLocation(),
                                    Main.getConfigurator().config.getString("sounds.on_player_kill"),
                                    Sounds.ENTITY_PLAYER_BIG_FALL, 1, 1);
                            Main.depositPlayer(killer, Main.getVaultKillReward());
                        }

                    }
                }

                BedwarsPlayerKilledEvent killedEvent = new BedwarsPlayerKilledEvent(game, victim,
                        Main.isPlayerInGame(killer) ? killer : null, drops);
                Main.getInstance().getServer().getPluginManager().callEvent(killedEvent);

                if (Main.isPlayerStatisticsEnabled()) {
                    PlayerStatistic diePlayer = Main.getPlayerStatisticsManager().getStatistic(victim);
                    PlayerStatistic killerPlayer;

                    if (!onlyOnBedDestroy || !isBed) {
                        diePlayer.setCurrentDeaths(diePlayer.getCurrentDeaths() + 1);
                        diePlayer.setCurrentScore(diePlayer.getCurrentScore()
                                + Main.getConfigurator().config.getInt("statistics.scores.die", 0));
                    }

                    if (killer != null) {
                        if (!onlyOnBedDestroy || !isBed) {
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
            if (Main.getVersionNumber() < 115) {
                PlayerUtils.respawn(Main.getInstance(), victim, 3L);
            }
            if (Main.getConfigurator().config.getBoolean("respawn-cooldown.enabled")
                    && victimTeam.isAlive()
                    && !gVictim.isSpectator) {
                game.makeSpectator(gVictim, false);

                new BukkitRunnable() {
                    int livingTime = respawnTime;
                    GamePlayer gamePlayer = gVictim;
                    Player player = gamePlayer.player;

                    @Override
                    public void run() {
                        if (livingTime > 0) {
                            Title.send(player,
                                    i18nonly("respawn_cooldown_title").replace("%time%", String.valueOf(livingTime)), "");
                            Sounds.playSound(player, player.getLocation(),
                                    Main.getConfigurator().config.getString("sounds.on_respawn_cooldown_wait"),
                                    Sounds.BLOCK_STONE_BUTTON_CLICK_ON, 1, 1);
                        }

                        livingTime--;
                        if (livingTime == 0) {
                            game.makePlayerFromSpectator(gamePlayer);
                            Sounds.playSound(player, player.getLocation(),
                                    Main.getConfigurator().config.getString("sounds.on_respawn_cooldown_done"),
                                    Sounds.UI_BUTTON_CLICK, 1, 1);

                            this.cancel();
                        }
                    }
                }.runTaskTimer(Main.getInstance(), 20L, 20L);
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

        if (Main.isPlayerStatisticsEnabled()) {
            Main.getPlayerStatisticsManager().unloadStatistic(event.getPlayer());
        }

        if (Main.getConfigurator().config.getBoolean("disable-server-message.player-join")) {
            event.setQuitMessage(null);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        Game game = (Game) Main.getInstance().getFirstWaitingGame();

        if (Game.isBungeeEnabled() && Main.getConfigurator().config.getBoolean("bungee.auto-game-connect", false)) {
            new BukkitRunnable() {
                public void run() {
                    try {
                        game.joinToGame(player);
                    } catch (NullPointerException ignored) {
                        if (!BaseCommand.hasPermission(player, ADMIN_PERMISSION, false)) {
                            BungeeUtils.movePlayerToBungeeServer(player, false);
                        }
                    }
                }
            }.runTaskLater(Main.getInstance(), 1L);
        }

        if (Main.getConfigurator().config.getBoolean("disable-server-message.player-join")) {
            event.setJoinMessage(null);
        }
    }

    @SuppressWarnings("unchecked")
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (Main.isPlayerInGame(event.getPlayer())) {
            GamePlayer gPlayer = Main.getPlayerGameProfile(event.getPlayer());
            Game game = gPlayer.getGame();
            CurrentTeam team = game.getPlayerTeam(gPlayer);

            if (game.getStatus() == GameStatus.WAITING) {
                event.setRespawnLocation(gPlayer.getGame().getLobbySpawn());
                return;
            }
            if (gPlayer.isSpectator) {
                if (team == null) {
                    event.setRespawnLocation(gPlayer.getGame().makeSpectator(gPlayer, true));
                } else {
                    event.setRespawnLocation(gPlayer.getGame().makeSpectator(gPlayer, false));
                }
            } else {
                event.setRespawnLocation(gPlayer.getGame().getPlayerTeam(gPlayer).teamInfo.spawn);

                if (Main.getConfigurator().config.getBoolean("respawn.protection-enabled", true)) {
                    RespawnProtection respawnProtection = game.addProtectedPlayer(gPlayer.player);
                    respawnProtection.runProtection();
                }

                SpawnEffects.spawnEffect(gPlayer.getGame(), gPlayer.player, "game-effects.respawn");
                if (gPlayer.getGame().getOriginalOrInheritedPlayerRespawnItems()) {
                    List<ItemStack> givedGameStartItems = StackParser.parseAll((Collection<Object>) Main.getConfigurator().config
                            .getList("gived-player-respawn-items"));
                    if (givedGameStartItems != null) {
                        MiscUtils.giveItemsToPlayer(givedGameStartItems, gPlayer.player, team.getColor());
                    } else {
                        Debug.warn("You have wrongly configured gived-player-respawn-items!", true);
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

        if (Main.isHologramsEnabled()) {
            Main.getHologramInteraction().updateHolograms(event.getPlayer(), 10L);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) {
            if (Main.getConfigurator().config.getBoolean("event-hacks.place") && Main.isPlayerInGame(event.getPlayer())) {
                event.setCancelled(false);
            } else {
                return;
            }
        }

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

            if (game.getStatus() == GameStatus.RUNNING
                    && GameCreator.isInArea(event.getBlock().getLocation(), game.getPos1(), game.getPos2())) {
                Block block = event.getBlock();
                int explosionTime = Main.getConfigurator().config.getInt("tnt.explosion-time", 8) * 20;

                if (block.getType() == Material.TNT
                        && Main.getConfigurator().config.getBoolean("tnt.auto-ignite", false)) {
                    block.setType(Material.AIR);
                    Location location = block.getLocation().add(0.5, 0.5, 0.5);
                    ;

                    TNTPrimed tnt = (TNTPrimed) location.getWorld().spawnEntity(location, EntityType.PRIMED_TNT);
                    tnt.setFuseTicks(explosionTime);

                    tnt.setMetadata(event.getPlayer().getUniqueId().toString(), new FixedMetadataValue(Main.getInstance(), null));

                    Main.registerGameEntity(tnt, game);

                    new BukkitRunnable() {
                        public void run() {
                            Main.unregisterGameEntity(tnt);
                        }
                    }.runTaskLater(Main.getInstance(), explosionTime + 10);
                }
            }
        } else if (Main.getConfigurator().config.getBoolean("preventArenaFromGriefing")) {
            for (String gameN : Main.getGameNames()) {
                Game game = Main.getGame(gameN);
                if (game.getStatus() != GameStatus.DISABLED && GameCreator.isInArea(event.getBlock().getLocation(), game.getPos1(), game.getPos2())) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) {
            if (Main.getConfigurator().config.getBoolean("event-hacks.destroy") && Main.isPlayerInGame(event.getPlayer())) {
                event.setCancelled(false);
            } else {
                return;
            }
        }

        if (Main.isPlayerInGame(event.getPlayer())) {
            final Player player = event.getPlayer();
            final GamePlayer gamePlayer = Main.getPlayerGameProfile(player);
            final Game game = gamePlayer.getGame();
            final Block block = event.getBlock();

            if (game.getStatus() == GameStatus.WAITING) {
                event.setCancelled(true);
                return;
            }

            if (!game.blockBreak(Main.getPlayerGameProfile(event.getPlayer()), event.getBlock(), event)) {
                event.setCancelled(true);
            }

            //Fix for obsidian dropping
            if (game.getStatus() == GameStatus.RUNNING && gamePlayer.isInGame()) {
                if (block.getType() == Material.ENDER_CHEST) {
                    event.setDropItems(false);
                }
            }
        } else if (Main.getConfigurator().config.getBoolean("preventArenaFromGriefing")) {
            for (String gameN : Main.getGameNames()) {
                Game game = Main.getGame(gameN);
                if (game.getStatus() != GameStatus.DISABLED && GameCreator.isInArea(event.getBlock().getLocation(), game.getPos1(), game.getPos2())) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onCommandExecuted(PlayerCommandPreprocessEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final Player player = event.getPlayer();
        if (Main.isPlayerInGame(player)) {
            //Allow players with permissions to use all commands
            if (BaseCommand.hasPermission(player, ADMIN_PERMISSION, false)) {
                return;
            }

            final String message = event.getMessage();
            if (Main.isCommandLeaveShortcut(message)) {
                event.setCancelled(true);
                Main.getPlayerGameProfile(event.getPlayer()).changeGame(null);
            } else if (!Main.isCommandAllowedInGame(message.split(" ")[0])) {
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

            if (game.getStatus() == GameStatus.RUNNING && Main.getConfigurator().config.getBoolean("disable-hunger")) {
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
            } else if (!gPlayer.getGame().getOriginalOrInheritedCrafting()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageEvent event) {
        if (event.isCancelled()) {
            if (Main.getConfigurator().config.getBoolean("event-hacks.damage") && event.getEntity() instanceof Player && Main.isPlayerInGame((Player) event.getEntity())) {
                event.setCancelled(false);
            } else {
                return;
            }
        }

        if (!(event.getEntity() instanceof Player)) {
            if (event instanceof EntityDamageByEntityEvent) {
                Game game = Main.getInGameEntity(event.getEntity());
                if (game != null) {
                    if (game.isEntityShop(event.getEntity()) && game.getOriginalOrInheritedPreventKillingVillagers()) {
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
                    gPlayer.teleport(game.getSpecSpawn());
                }
                event.setCancelled(true);
            } else if (game.getStatus() == GameStatus.WAITING) {
                if (event.getCause() == DamageCause.VOID) {
                    gPlayer.teleport(game.getLobbySpawn());
                }
                event.setCancelled(true);
            } else if (game.getStatus() == GameStatus.RUNNING || game.getStatus() == GameStatus.GAME_END_CELEBRATING) {
                if (gPlayer.isSpectator) {
                    event.setCancelled(true);
                }
                if (game.isProtectionActive(player) && event.getCause() != DamageCause.VOID) {
                    event.setCancelled(true);
                    return;
                }

                if (event.getCause() == DamageCause.VOID) {
                    player.setHealth(0.5);
                } else if (event instanceof EntityDamageByEntityEvent) {
                    EntityDamageByEntityEvent edbee = (EntityDamageByEntityEvent) event;
                    if (edbee.getDamager() instanceof Player) {
                        Player damager = (Player) edbee.getDamager();
                        if (Main.isPlayerInGame(damager)) {
                            GamePlayer gDamager = Main.getPlayerGameProfile(damager);
                            if (gDamager.isSpectator || (gDamager.getGame().getPlayerTeam(gDamager) == game.getPlayerTeam(gPlayer) && !game.getOriginalOrInheritedFriendlyfire())) {
                                event.setCancelled(true);
                            }
                        }
                    } else if (edbee.getDamager() instanceof Firework && game.getStatus() == GameStatus.GAME_END_CELEBRATING) {
                        event.setCancelled(true);
                    } else if (edbee.getDamager() instanceof TNTPrimed) {
                        if (edbee.getDamager().hasMetadata(player.getUniqueId().toString())) {
                            edbee.setCancelled(Main.getConfigurator().config.getBoolean("tnt.dont-damage-placer", false));
                        }
                    } else if (edbee.getDamager() instanceof Projectile) {
                        Projectile projectile = (Projectile) edbee.getDamager();
                        if (projectile.getShooter() instanceof Player) {
                            Player damager = (Player) projectile.getShooter();
                            if (Main.isPlayerInGame(damager)) {
                                GamePlayer gDamager = Main.getPlayerGameProfile(damager);
                                if (gDamager.isSpectator || gDamager.getGame().getPlayerTeam(gDamager) == game.getPlayerTeam(gPlayer) && !game.getOriginalOrInheritedFriendlyfire()) {
                                    event.setCancelled(true);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onLaunchProjectile(ProjectileLaunchEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Projectile projectile = event.getEntity();
        if (projectile.getShooter() instanceof Player) {
            Player damager = (Player) projectile.getShooter();
            if (Main.isPlayerInGame(damager)) {
                if (Main.getPlayerGameProfile(damager).isSpectator) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (event.isCancelled()) {
            return;
        }
        Player player = event.getPlayer();
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

        Player player = event.getPlayer();
        if (Main.isPlayerInGame(player) && !Main.getPlayerGameProfile(player).isSpectator
               && (!player.hasPermission("bw.bypass.flight") && Main.getConfigurator().config.getBoolean("disable-flight"))) {
            event.setCancelled(true);
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
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
                    } else if (event.getMaterial() == Material
                            .valueOf(Main.getConfigurator().config.getString("items.startgame", "DIAMOND"))) {
                        if (game.getStatus() == GameStatus.WAITING && (player.hasPermission("bw.vip.startitem")
                                || player.hasPermission("misat11.bw.vip.startitem"))) {
                            if (game.checkMinPlayers()) {
                                game.gameStartItem = true;
                            } else {
                                player.sendMessage(i18n("vip_not_enough_players"));
                            }
                        }
                    } else if (event.getMaterial() == Material
                            .valueOf(Main.getConfigurator().config.getString("items.leavegame", "SLIME_BALL"))) {
                        game.leaveFromGame(player);
                    }
                } else if (game.getStatus() == GameStatus.RUNNING) {
                    if (event.getClickedBlock() != null) {
                        if (event.getClickedBlock().getType() == Material.ENDER_CHEST) {
                            Block chest = event.getClickedBlock();
                            CurrentTeam team = game.getTeamOfChest(chest);
                            event.setCancelled(true);

                            if (team == null) {
                                player.openInventory(game.getFakeEnderChest(gPlayer));
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
                        } else if (event.getClickedBlock().getState() instanceof InventoryHolder) {
                            InventoryHolder holder = (InventoryHolder) event.getClickedBlock().getState();
                            game.addChestForFutureClear(event.getClickedBlock().getLocation(), holder.getInventory());
                        } else if (event.getClickedBlock().getType().name().contains("CAKE")) {
                            if (game.getOriginalOrInheritedCakeTargetBlockEating()) {
                                if (game.getTeamOfPlayer(event.getPlayer()).getTargetBlock().equals(event.getClickedBlock().getLocation())) {
                                    event.setCancelled(true);
                                } else {
                                    if (Main.getConfigurator().config.getBoolean("disableCakeEating", true)) {
                                        event.setCancelled(true);
                                    }
                                    for (RunningTeam team : game.getRunningTeams()) {
                                        if (team.getTargetBlock().equals(event.getClickedBlock().getLocation())) {
                                            event.setCancelled(true);
                                            if (Main.isLegacy()) {
                                                if (event.getClickedBlock().getState().getData() instanceof org.bukkit.material.Cake) {
                                                    org.bukkit.material.Cake cake = (org.bukkit.material.Cake) event.getClickedBlock().getState().getData();
                                                    if (cake.getSlicesEaten() == 0) {
                                                        game.getRegion().putOriginalBlock(event.getClickedBlock().getLocation(), event.getClickedBlock().getState());
                                                    }
                                                    cake.setSlicesEaten(cake.getSlicesEaten() + 1);
                                                    if (cake.getSlicesEaten() >= 6) {
                                                        game.bedDestroyed(event.getClickedBlock().getLocation(), event.getPlayer(), false, false, true);
                                                        event.getClickedBlock().setType(Material.AIR);
                                                    } else {
                                                        BlockState state = event.getClickedBlock().getState();
                                                        state.setData(cake);
                                                        state.update();
                                                    }
                                                }
                                            } else {
                                                if (event.getClickedBlock().getBlockData() instanceof Cake) {
                                                    Cake cake = (Cake) event.getClickedBlock().getBlockData();
                                                    if (cake.getBites() == 0) {
                                                        game.getRegion().putOriginalBlock(event.getClickedBlock().getLocation(), event.getClickedBlock().getState());
                                                    }
                                                    cake.setBites(cake.getBites() + 1);
                                                    if (cake.getBites() >= cake.getMaximumBites()) {
                                                        game.bedDestroyed(event.getClickedBlock().getLocation(), event.getPlayer(), false, false, true);
                                                        event.getClickedBlock().setType(Material.AIR);
                                                    } else {
                                                        event.getClickedBlock().setBlockData(cake);
                                                    }
                                                }
                                            }
                                            break;
                                        }
                                    }
                                }
                            } else if (Main.getConfigurator().config.getBoolean("disableCakeEating", true)) {
                                event.setCancelled(true);
                            }
                        }
                    }
                }

                if (event.getClickedBlock() != null) {
                    if (game.getRegion().isBedBlock(event.getClickedBlock().getState()) || event.getClickedBlock().getType().name().equals("RESPAWN_ANCHOR")) {
                        // prevent Essentials to set home in arena
                        event.setCancelled(true);

                        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && game.getStatus() == GameStatus.RUNNING && !gPlayer.isSpectator) {
                            ItemStack stack = event.getItem();
                            if (stack != null && stack.getAmount() > 0) {
                                boolean anchorFilled = false;
                                if (game.getOriginalOrInheritedAnchorDecreasing()
                                        && event.getClickedBlock().getType().name().equals("RESPAWN_ANCHOR")
                                        && game.getPlayerTeam(gPlayer).teamInfo.bed.equals(event.getClickedBlock().getLocation())
                                        && event.getItem() != null && event.getItem().getType() == Material.GLOWSTONE) {
                                    anchorFilled = Player116ListenerUtils.anchorCharge(event, game, stack);
                                }

                                if (!anchorFilled && stack.getType().isBlock()) {
                                    BlockFace face = event.getBlockFace();
                                    Block block = event.getClickedBlock().getLocation().clone().add(MiscUtils.getDirection(face))
                                            .getBlock();
                                    if (block.getType() == Material.AIR) {
                                        BlockState originalState = block.getState();
                                        block.setType(stack.getType());
                                        try {
                                            // The method is no longer in API, but in legacy versions exists
                                            Block.class.getMethod("setData", byte.class).invoke(block,
                                                    (byte) stack.getDurability());
                                        } catch (Exception e) {
                                        }
                                        BlockPlaceEvent bevent = new BlockPlaceEvent(block, originalState,
                                                event.getClickedBlock(), stack, player, true);
                                        Bukkit.getPluginManager().callEvent(bevent);

                                        if (bevent.isCancelled()) {
                                            originalState.update(true, false);
                                        } else {
                                            stack.setAmount(stack.getAmount() - 1);
                                            // TODO get right block place sound
                                            Sounds.BLOCK_STONE_PLACE.playSound(player, block.getLocation(), 1, 1);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (event.getAction() == Action.LEFT_CLICK_BLOCK &&
                    game.getStatus() == GameStatus.RUNNING && !gPlayer.isSpectator
                    && event.getClickedBlock() != null && event.getClickedBlock().getType() == Material.DRAGON_EGG
                    && Main.getConfigurator().config.getBoolean("disableDragonEggTeleport", true)) {
                event.setCancelled(true);
                BlockBreakEvent blockBreakEvent = new BlockBreakEvent(event.getClickedBlock(), player);
                Bukkit.getPluginManager().callEvent(blockBreakEvent);
                if (blockBreakEvent.isCancelled()) {
                    return;
                }
                if (blockBreakEvent.isDropItems()) {
                    event.getClickedBlock().breakNaturally();
                } else {
                    event.getClickedBlock().setType(Material.AIR);
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
    public void onSleep(PlayerBedEnterEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (Main.isPlayerInGame(event.getPlayer())) {
            event.setCancelled(true);
        } else {
            for (String gameN : Main.getGameNames()) {
                Game game = Main.getGame(gameN);
                if (GameCreator.isInArea(event.getBed().getLocation(), game.getPos1(), game.getPos2())) {
                    event.setCancelled(true);
                    return;
                }
            }
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
        } else if (BaseCommand.hasPermission(player, ADMIN_PERMISSION, false)) {
            List<MetadataValue> values = player.getMetadata(GameCreator.BEDWARS_TEAM_JOIN_METADATA);
            if (values.size() == 0) {
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
            Main.getHologramInteraction().updateHolograms(player, 10L);

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
                Chat chat = Bukkit.getServer().getServicesManager().load(Chat.class);
                if (chat != null) {
                    format = format.replace("%prefix%", chat.getPlayerPrefix(player));
                    format = format.replace("%suffix%", chat.getPlayerSuffix(player));
                }
            }

            format = format.replace("%prefix%", "");
            format = format.replace("%suffix%", "");

            format = ChatColor.translateAlternateColorCodes('&', format);

            boolean teamChat = Main.getConfigurator().config.getBoolean("chat.default-team-chat-while-running", true)
                    && game.getStatus() == GameStatus.RUNNING && (team != null || spectator);

            String allChat = Main.getConfigurator().config.getString("chat.all-chat-prefix", "@a");
            String tChat = Main.getConfigurator().config.getString("chat.team-chat-prefix", "@t");

            if (message.startsWith(allChat)) {
                teamChat = false;
                message = message.substring(allChat.length()).trim();
            } else if (message.startsWith(tChat) && (team != null || spectator)) {
                teamChat = true;
                message = message.substring(tChat.length()).trim();
            }

            if (teamChat) {
                if (spectator) {
                    format = Main.getConfigurator().config.getString("chat.death-chat", "[DEATH] ") + format;
                } else {
                    format = Main.getConfigurator().config.getString("chat.team-chat", "[TEAM] ") + format;
                }
            } else {
                format = Main.getConfigurator().config.getString("chat.all-chat", "[ALL] ") + format;
            }

            event.setFormat(format + message.replaceAll("%", "%%")); // Fix using % in chat
            Iterator<Player> recipients = event.getRecipients().iterator();
            while (recipients.hasNext()) {
                Player recipient = recipients.next();
                GamePlayer recipientgPlayer = Main.getPlayerGameProfile(recipient);
                Game recipientGame = recipientgPlayer.getGame();
                if (recipientGame != game) {
                    if ((game.getStatus() == GameStatus.WAITING && Main.getConfigurator().config.getBoolean("chat.separate-chat.lobby"))
                            || (game.getStatus() != GameStatus.WAITING && Main.getConfigurator().config.getBoolean("chat.separate-chat.game"))) {
                        recipients.remove();
                    }
                } else if (game.getPlayerTeam(recipientgPlayer) != team && teamChat) {
                    recipients.remove();
                }
            }

            for (Player p : event.getRecipients()) {
                p.sendMessage(event.getFormat());
            }
            event.setCancelled(true);
        } else {
            if (Main.getConfigurator().config.getBoolean("chat.separate-chat.lobby") || Main.getConfigurator().config.getBoolean("chat.separate-chat.game")) {
                Iterator<Player> recipients = event.getRecipients().iterator();
                while (recipients.hasNext()) {
                    Player recipient = recipients.next();
                    GamePlayer recipientgPlayer = Main.getPlayerGameProfile(recipient);
                    Game recipientGame = recipientgPlayer.getGame();
                    if (recipientGame != null) {
                        if ((recipientGame.getStatus() == GameStatus.WAITING && Main.getConfigurator().config.getBoolean("chat.separate-chat.lobby"))
                                || (recipientGame.getStatus() != GameStatus.WAITING && Main.getConfigurator().config.getBoolean("chat.separate-chat.game"))) {
                            recipients.remove();
                        }
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
            if (game.getOriginalOrInheritedDamageWhenPlayerIsNotInArena() && game.getStatus() == GameStatus.RUNNING
                    && !gPlayer.isSpectator) {
                if (!GameCreator.isInArea(event.getTo(), game.getPos1(), game.getPos2())) {
                    player.damage(5);
                }
            } else if (Main.getConfigurator().config.getBoolean("preventSpectatorFlyingAway", false) && gPlayer.isSpectator && (game.getStatus() == GameStatus.RUNNING || game.getStatus() == GameStatus.GAME_END_CELEBRATING)) {
                if (!GameCreator.isInArea(event.getTo(), game.getPos1(), game.getPos2())) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onPlaceLiquid(PlayerBucketEmptyEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player player = event.getPlayer();
        if (Main.isPlayerInGame(player)) {
            GamePlayer gPlayer = Main.getPlayerGameProfile(player);
            Game game = gPlayer.getGame();
            Location loc = event.getBlockClicked().getLocation();

            loc.add(MiscUtils.getDirection(event.getBlockFace()));

            Block block = loc.getBlock();
            if (game.getStatus() == GameStatus.RUNNING) {
                if (block.getType() == Material.AIR || game.getRegion().isBlockAddedDuringGame(block.getLocation())) {
                    game.getRegion().addBuiltDuringGame(block.getLocation());
                } else {
                    event.setCancelled(true);
                }
            } else if (game.getStatus() != GameStatus.DISABLED) {
                event.setCancelled(true);
            }
        } else if (Main.getConfigurator().config.getBoolean("preventArenaFromGriefing")) {
            for (String gameN : Main.getGameNames()) {
                Game game = Main.getGame(gameN);
                if (game.getStatus() != GameStatus.DISABLED && GameCreator.isInArea(event.getBlockClicked().getLocation(), game.getPos1(), game.getPos2())) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    /* This event was replaced on 1.12 with newer (event handling is devided between Player112Listener and PlayerBefore112Listener) */
    public static void onItemPickup(Player player, Item item, Cancellable cancel) {
        if (cancel.isCancelled()) {
            return;
        }

        if (Main.isPlayerInGame(player)) {
            GamePlayer gPlayer = Main.getPlayerGameProfile(player);
            Game game = gPlayer.getGame();
            if (game.getStatus() == GameStatus.WAITING || gPlayer.isSpectator) {
                cancel.setCancelled(true);
            } else {
                for (ItemSpawner spawner : game.getSpawners()) {
                    if (spawner.getMaxSpawnedResources() > 0) {
                        spawner.remove(item);
                    }
                }
            }
        }
    }
}
