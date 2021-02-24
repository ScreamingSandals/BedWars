package org.screamingsandals.bedwars.listener;

import net.milkbowl.vault.chat.Chat;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
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
import org.bukkit.event.vehicle.VehicleCreateEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.RunningTeam;
import org.screamingsandals.bedwars.api.config.ConfigurationContainer;
import org.screamingsandals.bedwars.api.events.BedwarsPlayerKilledEvent;
import org.screamingsandals.bedwars.api.events.BedwarsPlayerRespawnedEvent;
import org.screamingsandals.bedwars.api.events.BedwarsTeamChestOpenEvent;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.commands.BedWarsPermission;
import org.screamingsandals.bedwars.commands.admin.JoinTeamCommand;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.game.*;
import org.screamingsandals.bedwars.inventories.TeamSelectorInventory;
import org.screamingsandals.bedwars.statistics.PlayerStatistic;
import org.screamingsandals.bedwars.statistics.PlayerStatisticManager;
import org.screamingsandals.bedwars.utils.*;
import org.screamingsandals.bedwars.lib.debug.Debug;
import org.screamingsandals.bedwars.lib.nms.entity.PlayerUtils;
import org.screamingsandals.lib.material.builder.ItemFactory;
import org.screamingsandals.lib.player.PlayerMapper;

import java.util.*;
import java.util.stream.Collectors;

import static org.screamingsandals.bedwars.lib.lang.I18n.*;

public class PlayerListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDeath(PlayerDeathEvent event) {
        final Player victim = event.getEntity();

        if (Main.isPlayerInGame(victim)) {
            Debug.info(victim.getName() + " died in BedWars Game, Processing his dead...");
            final var gVictim = Main.getPlayerGameProfile(victim);
            final var game = gVictim.getGame();
            final var victimTeam = game.getPlayerTeam(gVictim);
            final var victimColor = victimTeam.teamInfo.color.chatColor;
            final var drops = List.copyOf(event.getDrops());
            int respawnTime = MainConfig.getInstance().node("respawn-cooldown", "time").getInt(5);

            if (game.getConfigurationContainer().getOrDefault(ConfigurationContainer.KEEP_ARMOR, Boolean.class, false)) {
               final var armorContents = victim.getInventory().getArmorContents();
               if (armorContents != null) {
                   gVictim.setGameArmorContents(armorContents);
                   Debug.info(victim.getName() + "'s armor contents: " +
                           Arrays.stream(armorContents)
                           .filter(Objects::nonNull)
                           .map(stack -> stack.getType().name())
                           .collect(Collectors.toList()));
               }
            }

            event.setKeepInventory(game.getConfigurationContainer().getOrDefault(ConfigurationContainer.KEEP_INVENTORY, Boolean.class, false));
            event.setDroppedExp(0);

            if (game.getStatus() == GameStatus.RUNNING) {
                Debug.info(victim.getName() + " died while game was running");
                if (!game.getConfigurationContainer().getOrDefault(ConfigurationContainer.PLAYER_DROPS, Boolean.class, false)) {
                    event.getDrops().clear();
                }

                if (MainConfig.getInstance().node("chat", "send-death-messages-just-in-game").getBoolean()) {
                    var deathMessage = event.getDeathMessage();
                    final var killer = event.getEntity().getKiller();
                    if (MainConfig.getInstance().node("chat", "send-custom-death-messages").getBoolean()) {
                        if (killer != null) {
                            Debug.info(victim.getName() + " died because entity " + event.getEntity().getKiller() + " killed him");
                            final var gKiller = Main.getPlayerGameProfile(killer);
                            final var killerTeam = game.getPlayerTeam(gKiller);
                            final var killerColor = killerTeam.teamInfo.color.chatColor;

                            deathMessage = i18nc("player_killed", game.getCustomPrefix())
                                    .replace("%victim%", victimColor + victim.getDisplayName())
                                    .replace("%killer%", killerColor + killer.getDisplayName())
                                    .replace("%victimTeam%", victimColor + victimTeam.getName())
                                    .replace("%killerTeam%", killerColor + killerTeam.getName());
                        } else {
                            deathMessage = i18nc("player_self_killed", game.getCustomPrefix())
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
                if (isBed && game.getConfigurationContainer().getOrDefault(ConfigurationContainer.ANCHOR_DECREASING, Boolean.class, false) && "RESPAWN_ANCHOR".equals(team.teamInfo.bed.getBlock().getType().name())) {
                    isBed = Player116ListenerUtils.processAnchorDeath(game, team, isBed);
                }
                if (!isBed) {
                    Debug.info(victim.getName() + " died without bed, he's going to spectate the game");
                    gVictim.isSpectator = true;
                    team.players.remove(gVictim);
                    team.getScoreboardTeam().removeEntry(victim.getName());
                    if (PlayerStatisticManager.isEnabled()) {
                        var statistic = PlayerStatisticManager.getInstance().getStatistic(PlayerMapper.wrapPlayer(victim));
                        statistic.addLoses(1);
                        statistic.addScore(MainConfig.getInstance().node("statistics", "scores", "lose").getInt(0));
                    }
                    game.updateScoreboard();
                }

                boolean onlyOnBedDestroy = MainConfig.getInstance().node("statistics", "bed-destroyed-kills").getBoolean();

                Player killer = victim.getKiller();
                if (Main.isPlayerInGame(killer)) {
                    GamePlayer gKiller = Main.getPlayerGameProfile(killer);
                    if (gKiller.getGame() == game) {
                        if (!onlyOnBedDestroy || !isBed) {
                            game.dispatchRewardCommands("player-kill", killer,
                                    MainConfig.getInstance().node("statistics", "scores", "kill").getInt(10));
                        }
                        if (team.isDead()) {
                            SpawnEffects.spawnEffect(game, victim, "game-effects.teamkill");
                            Sounds.playSound(killer, killer.getLocation(),
                                    MainConfig.getInstance().node("sounds", "team_kill").getString(),
                                    Sounds.ENTITY_PLAYER_LEVELUP, 1, 1);
                        } else {
                            Sounds.playSound(killer, killer.getLocation(),
                                    MainConfig.getInstance().node("sounds", "player_kill").getString(),
                                    Sounds.ENTITY_PLAYER_BIG_FALL, 1, 1);
                            Main.depositPlayer(killer, Main.getVaultKillReward());
                        }

                    }
                }

                BedwarsPlayerKilledEvent killedEvent = new BedwarsPlayerKilledEvent(game, victim,
                        Main.isPlayerInGame(killer) ? killer : null, drops);
                Bukkit.getServer().getPluginManager().callEvent(killedEvent);

                if (PlayerStatisticManager.isEnabled()) {
                    var diePlayer = PlayerStatisticManager.getInstance().getStatistic(PlayerMapper.wrapPlayer(victim));
                    PlayerStatistic killerPlayer;

                    if (!onlyOnBedDestroy || !isBed) {
                        diePlayer.addDeaths(1);
                        diePlayer.addScore(MainConfig.getInstance().node("statistics", "scores", "die").getInt(0));
                    }

                    if (killer != null) {
                        if (!onlyOnBedDestroy || !isBed) {
                            killerPlayer = PlayerStatisticManager.getInstance().getStatistic(PlayerMapper.wrapPlayer(killer));
                            if (killerPlayer != null) {
                                killerPlayer.addKills(1);
                                killerPlayer.addScore(MainConfig.getInstance().node("statistics", "scores", "kill").getInt(10));
                            }
                        }
                    }
                }
            }
            if (Main.getVersionNumber() < 115 && !MainConfig.getInstance().node("allow-fake-death").getBoolean()) {
                Debug.info(victim.getName() + " is going to be respawned via spigot api");
                PlayerUtils.respawn(Main.getInstance().getPluginDescription().as(JavaPlugin.class), victim, 3L);
            }
            if (MainConfig.getInstance().node("respawn-cooldown", "enabled").getBoolean()
                    && victimTeam.isAlive()
                    && !gVictim.isSpectator) {
                game.makeSpectator(gVictim, false);
                Debug.info(victim.getName() + " is in respawn cooldown");

                new BukkitRunnable() {
                    int livingTime = respawnTime;
                    GamePlayer gamePlayer = gVictim;
                    Player player = gamePlayer.player;

                    @Override
                    public void run() {
                        if (livingTime > 0) {
                            TitleUtils.send(PlayerMapper.wrapPlayer(player),
                                    i18nonly("respawn_cooldown_title").replace("%time%", String.valueOf(livingTime)), "");
                            Sounds.playSound(player, player.getLocation(),
                                    MainConfig.getInstance().node("sounds", "respawn_cooldown_wait").getString(),
                                    Sounds.BLOCK_STONE_BUTTON_CLICK_ON, 1, 1);
                        }

                        livingTime--;
                        if (livingTime == 0) {
                            game.makePlayerFromSpectator(gamePlayer);
                            Sounds.playSound(player, player.getLocation(),
                                    MainConfig.getInstance().node("sounds", "respawn_cooldown_done").getString(),
                                    Sounds.UI_BUTTON_CLICK, 1, 1);

                            this.cancel();
                        }
                    }
                }.runTaskTimer(Main.getInstance().getPluginDescription().as(JavaPlugin.class), 20L, 20L);
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

        if (MainConfig.getInstance().node("disable-server-message", "player-join").getBoolean()) {
            event.setQuitMessage(null);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (Game.isBungeeEnabled() && MainConfig.getInstance().node("bungee", "auto-game-connect").getBoolean()) {
            Debug.info(event.getPlayer() + " joined the server and auto-game-connect is enabled. Registering task...");
            new BukkitRunnable() {
                public void run() {
                    try {
                        Debug.info("Selecting game for " + event.getPlayer().getName());
                        var gameManager = GameManager.getInstance();
                        var game = gameManager.getFirstWaitingGame().or(gameManager::getFirstRunningGame);
                        if (game.isEmpty()) { // still nothing?
                            if (!PlayerMapper.wrapPlayer(player).hasPermission(BedWarsPermission.ADMIN_PERMISSION.asPermission())) {
                                Debug.info(event.getPlayer().getName() + " is not connecting to any game! Kicking...");
                                BungeeUtils.movePlayerToBungeeServer(player, false);
                            }
                            return;
                        }
                        Debug.info(event.getPlayer().getName() + " is connecting to " + game.get().getName());

                        game.get().joinToGame(player);
                    } catch (NullPointerException ignored) {
                        if (!PlayerMapper.wrapPlayer(player).hasPermission(BedWarsPermission.ADMIN_PERMISSION.asPermission())) {
                            Debug.info(event.getPlayer().getName() + " is not connecting to any game! Kicking...");
                            BungeeUtils.movePlayerToBungeeServer(player, false);
                        }
                    }
                }
            }.runTaskLater(Main.getInstance().getPluginDescription().as(JavaPlugin.class), 1L);
        }

        if (MainConfig.getInstance().node("disable-server-message", "player-join").getBoolean()) {
            event.setJoinMessage(null);
        }

        if (MainConfig.getInstance().node("tab", "enabled").getBoolean() && MainConfig.getInstance().node("tab", "hide-foreign-players").getBoolean()) {
            Bukkit.getOnlinePlayers().stream().filter(Main::isPlayerInGame).forEach(p -> Main.getPlayerGameProfile(p).hidePlayer(player));
        }
    }

    @SuppressWarnings("unchecked")
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (Main.isPlayerInGame(event.getPlayer())) {
            Debug.info(event.getPlayer().getName() + " is respawning in BedWars game");
            GamePlayer gPlayer = Main.getPlayerGameProfile(event.getPlayer());
            Game game = gPlayer.getGame();
            CurrentTeam team = game.getPlayerTeam(gPlayer);

            if (game.getStatus() == GameStatus.WAITING) {
                Debug.info(event.getPlayer().getName() + " is in lobby");
                event.setRespawnLocation(gPlayer.getGame().getLobbySpawn());
                return;
            }
            Debug.info(event.getPlayer().getName() + " is in game");
            // clear inventory to fix issue 148
            if (!game.getConfigurationContainer().getOrDefault(ConfigurationContainer.KEEP_INVENTORY, Boolean.class, false)) {
                event.getPlayer().getInventory().clear();
            }
            if (gPlayer.isSpectator) {
                Debug.info(event.getPlayer().getName() + " is going to be spectator");
                if (team == null) {
                    event.setRespawnLocation(gPlayer.getGame().makeSpectator(gPlayer, true));
                } else {
                    event.setRespawnLocation(gPlayer.getGame().makeSpectator(gPlayer, false));
                }
            } else {
                Debug.info(event.getPlayer().getName() + " is going to play the game");
                event.setRespawnLocation(gPlayer.getGame().getPlayerTeam(gPlayer).teamInfo.spawn);


                BedwarsPlayerRespawnedEvent respawnEvent = new BedwarsPlayerRespawnedEvent(game, event.getPlayer());
                Bukkit.getServer().getPluginManager().callEvent(respawnEvent);

                if (MainConfig.getInstance().node("respawn", "protection-enabled").getBoolean(true)) {
                    RespawnProtection respawnProtection = game.addProtectedPlayer(gPlayer.player);
                    respawnProtection.runProtection();
                }

                SpawnEffects.spawnEffect(gPlayer.getGame(), gPlayer.player, "game-effects.respawn");
                if (gPlayer.getGame().getConfigurationContainer().getOrDefault(ConfigurationContainer.ENABLE_PLAYER_RESPAWN_ITEMS, Boolean.class, false)) {
                    var playerRespawnItems = MainConfig.getInstance().node("player-respawn-items", "items")
                            .childrenList()
                            .stream()
                            .map(ItemFactory::build)
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .map(item -> item.as(ItemStack.class))
                            .collect(Collectors.toList());
                    if (!playerRespawnItems.isEmpty()) {
                        MiscUtils.giveItemsToPlayer(playerRespawnItems, gPlayer.player, team.getColor());
                    } else {
                        Debug.warn("You have wrongly configured player-respawn-items.items!", true);
                    }
                }

                if (game.getConfigurationContainer().getOrDefault(ConfigurationContainer.KEEP_ARMOR, Boolean.class, false)) {
                    final var armorContents = gPlayer.getGameArmorContents();
                    if (armorContents != null) {
                        gPlayer.player.getInventory().setArmorContents(armorContents);
                    }
                }

                MiscUtils.giveItemsToPlayer(gPlayer.getPermaItemsPurchased(), gPlayer.player, team.getColor());
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
                Debug.info(event.getPlayer().getName() + " changed world while in BedWars arena. Kicking...");
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled()) {
            if (MainConfig.getInstance().node("event-hacks", "place").getBoolean() && Main.isPlayerInGame(event.getPlayer())) {
                event.setCancelled(false);
            } else {
                return;
            }
        }

        if (Main.isPlayerInGame(event.getPlayer())) {
            Game game = Main.getPlayerGameProfile(event.getPlayer()).getGame();
            if (game.getStatus() == GameStatus.WAITING) {
                event.setCancelled(true);
                Debug.info(event.getPlayer().getName() + " attempted to place a block, canceled");
                return;
            }
            if (!game.blockPlace(Main.getPlayerGameProfile(event.getPlayer()), event.getBlock(),
                    event.getBlockReplacedState(), event.getItemInHand())) {
                event.setCancelled(true);
                Debug.info(event.getPlayer().getName() + " attempted to place a block, canceled");
            } else {
                Debug.info(event.getPlayer().getName() + " attempted to place a block, allowed");
            }
        } else if (MainConfig.getInstance().node("preventArenaFromGriefing").getBoolean()) {
            for (var game : GameManager.getInstance().getGames()) {
                if (game.getStatus() != GameStatus.DISABLED && ArenaUtils.isInArea(event.getBlock().getLocation(), game.getPos1(), game.getPos2())) {
                    event.setCancelled(true);
                    Debug.info(event.getPlayer().getName() + " attempted to place a block in protected area while not playing BedWars game, canceled");
                    return;
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled()) {
            if (MainConfig.getInstance().node("event-hacks", "destroy").getBoolean() && Main.isPlayerInGame(event.getPlayer())) {
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
                Debug.info(event.getPlayer().getName() + " attempted to break a block, canceled");
                event.setCancelled(true);
                return;
            }

            if (!game.blockBreak(Main.getPlayerGameProfile(event.getPlayer()), event.getBlock(), event)) {
                event.setCancelled(true);
                Debug.info(event.getPlayer().getName() + " attempted to break a block, canceled");
            } else {
                Debug.info(event.getPlayer().getName() + " attempted to break a block, allowed");
            }

            //Fix for obsidian dropping
            if (game.getStatus() == GameStatus.RUNNING && gamePlayer.isInGame()) {
                if (block.getType() == Material.ENDER_CHEST) {
                    event.setDropItems(false);
                }
            }
        } else if (MainConfig.getInstance().node("preventArenaFromGriefing").getBoolean()) {
            for (var game : GameManager.getInstance().getGames()) {
                if (game.getStatus() != GameStatus.DISABLED && ArenaUtils.isInArea(event.getBlock().getLocation(), game.getPos1(), game.getPos2())) {
                    event.setCancelled(true);
                    Debug.info(event.getPlayer().getName() + " attempted to break a block in protected area while not in BedWars game, canceled");
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
            if (PlayerMapper.wrapPlayer(player).hasPermission(BedWarsPermission.ADMIN_PERMISSION.asPermission())) {
                Debug.info(event.getPlayer().getName() + " attempted to execute a command, allowed");
                return;
            }

            final String message = event.getMessage();
            final GamePlayer gamePlayer = Main.getPlayerGameProfile(event.getPlayer());
            if (Main.isCommandLeaveShortcut(message)) {
                event.setCancelled(true);
                gamePlayer.changeGame(null);
            } else if (!Main.isCommandAllowedInGame(message.split(" ")[0])) {
                Debug.info(event.getPlayer().getName() + " attempted to execute a command, canceled");
                event.setCancelled(true);
                event.getPlayer().sendMessage(i18nc("command_is_not_allowed", gamePlayer.getGame().getCustomPrefix()));
                return;
            }
            Debug.info(event.getPlayer().getName() + " attempted to execute a command, allowed");
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
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
                    Debug.info(p.getName() + " used item in lobby or as spectator");
                    if (event.getClick().isLeftClick() || event.getClick().isRightClick()) {
                        ItemStack item = event.getCurrentItem();
                        if (item != null) {
                            p.closeInventory();
                            if (item.getType() == Material
                                    .valueOf(MainConfig.getInstance().node("items", "jointeam").getString("COMPASS"))) {
                                if (game.getStatus() == GameStatus.WAITING) {
                                    TeamSelectorInventory inv = game.getTeamSelectorInventory();
                                    if (inv == null) {
                                        return;
                                    }
                                    inv.openForPlayer(p);
                                } else if (gPlayer.isSpectator) {
                                    // TODO
                                }
                            } else if (item.getType() == Material
                                    .valueOf(MainConfig.getInstance().node("items", "startgame").getString("DIAMOND"))) {
                                if (game.getStatus() == GameStatus.WAITING && (p.hasPermission("bw.vip.startitem")
                                        || p.hasPermission("misat11.bw.vip.startitem"))) {
                                    if (game.checkMinPlayers()) {
                                        game.gameStartItem = true;
                                    } else {
                                        p.sendMessage(i18nc("vip_not_enough_players", game.getCustomPrefix()));
                                    }
                                }
                            } else if (item.getType() == Material
                                    .valueOf(MainConfig.getInstance().node("items", "leavegame").getString("SLIME_BALL"))) {
                                game.leaveFromGame(p);
                            }
                        }
                    }
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
                Debug.info(player.getName() + " tried to eat while eating is not allowed");
            }

            if (game.getStatus() == GameStatus.RUNNING && MainConfig.getInstance().node("disable-hunger").getBoolean()) {
                event.setCancelled(true);
                Debug.info(player.getName() + " tried to eat while eating is not allowed");
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
                Debug.info(player.getName() + " tried to craft while crafting is not allowed");
            } else if (!gPlayer.getGame().getConfigurationContainer().getOrDefault(ConfigurationContainer.CRAFTING, Boolean.class, false)) {
                event.setCancelled(true);
                Debug.info(player.getName() + " tried to craft while crafting is not allowed");
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamage(EntityDamageEvent event) {
        if (event.isCancelled()) {
            if (MainConfig.getInstance().node("event-hacks", "damage").getBoolean()
                    && event.getEntity() instanceof Player
                    && Main.isPlayerInGame((Player) event.getEntity())) {
                event.setCancelled(false);
            } else {
                return;
            }
        }

        final Entity entity = event.getEntity();

        if (!(entity instanceof Player)) {
            if (event.getCause() != DamageCause.VOID) {
                Game game = Main.getInGameEntity(entity);
                if (game != null) {
                    if (game.isEntityShop(entity) && game.getConfigurationContainer().getOrDefault(ConfigurationContainer.PROTECT_SHOP, Boolean.class, false)) {
                        Debug.info("Game entity was damaged, cancelling");
                        event.setCancelled(true);
                    }
                }
            }

            if (event instanceof EntityDamageByEntityEvent) {
                if (event.getEntity() instanceof ArmorStand) {
                    Entity damager = ((EntityDamageByEntityEvent) event).getDamager();
                    if (damager instanceof Player) {
                        Player player = (Player) damager;
                        if (Main.isPlayerInGame(player)) {
                            GamePlayer gPlayer = Main.getPlayerGameProfile(player);
                            if (gPlayer.getGame().getStatus() == GameStatus.WAITING || gPlayer.isSpectator) {
                                Debug.info(player.getName() + " damaged armor stand in lobby, cancelling");
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
            Debug.info(player.getName() + " was damaged in game");
            GamePlayer gPlayer = Main.getPlayerGameProfile(player);
            Game game = gPlayer.getGame();
            if (gPlayer.isSpectator) {
                if (event.getCause() == DamageCause.VOID) {
                    gPlayer.player.setFallDistance(0);
                    gPlayer.teleport(game.getSpecSpawn());
                }
                event.setCancelled(true);
            } else if (game.getStatus() == GameStatus.WAITING) {
                if (event.getCause() == DamageCause.VOID) {
                    gPlayer.player.setFallDistance(0);
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

                if (event.getCause() == DamageCause.VOID && player.getHealth() > 0.5) {
                    player.setHealth(0.5);
                } else if (event instanceof EntityDamageByEntityEvent) {
                    EntityDamageByEntityEvent edbee = (EntityDamageByEntityEvent) event;
                    if (edbee.getDamager() instanceof Player) {
                        Player damager = (Player) edbee.getDamager();
                        if (Main.isPlayerInGame(damager)) {
                            GamePlayer gDamager = Main.getPlayerGameProfile(damager);
                            if (gDamager.isSpectator || (gDamager.getGame().getPlayerTeam(gDamager) == game.getPlayerTeam(gPlayer) && !game.getConfigurationContainer().getOrDefault(ConfigurationContainer.FRIENDLY_FIRE, Boolean.class, false))) {
                                event.setCancelled(true);
                            }
                        }
                    } else if (edbee.getDamager() instanceof Firework && game.getStatus() == GameStatus.GAME_END_CELEBRATING) {
                        event.setCancelled(true);
                    } else if (edbee.getDamager() instanceof Projectile) {
                        Projectile projectile = (Projectile) edbee.getDamager();
                        if (projectile instanceof Fireball  && game.getStatus() == GameStatus.RUNNING) {
                            final double damage = MainConfig.getInstance().node("specials", "throwable-fireball", "damage").getDouble();
                            event.setDamage(damage);
                        } else if (projectile.getShooter() instanceof Player) {
                            Player damager = (Player) projectile.getShooter();
                            if (Main.isPlayerInGame(damager)) {
                                GamePlayer gDamager = Main.getPlayerGameProfile(damager);
                                if (gDamager.isSpectator || gDamager.getGame().getPlayerTeam(gDamager) == game.getPlayerTeam(gPlayer) && !game.getConfigurationContainer().getOrDefault(ConfigurationContainer.FRIENDLY_FIRE, Boolean.class, false)) {
                                    event.setCancelled(true);
                                }
                            }
                        }
                    }
                }

                if (MainConfig.getInstance().node("allow-fake-death").getBoolean() && !event.isCancelled() && (player.getHealth() - event.getFinalDamage()) <= 0) {
                    event.setCancelled(true);
                    Debug.info(player.getName() + " is going to be respawned via FakeDeath");
                    FakeDeath.die(gPlayer);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerArmorStandManipulateEvent (PlayerArmorStandManipulateEvent  e) {
        final ArmorStand armorStand = e.getRightClicked();

        //this will make sure no players can interact with the rotating generators.
        if (armorStand.getCustomName() != null && armorStand.getCustomName().equalsIgnoreCase(ItemSpawner.ARMOR_STAND_DISPLAY_NAME_HIDDEN)) {
            e.setCancelled(true);
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
                    Debug.info(damager.getName() + " tried to launch projectile as spectator");
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
                Debug.info(player.getName() + " tried to drop an item as spectator or in lobby");
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
               && (!player.hasPermission("bw.bypass.flight") && MainConfig.getInstance().node("disable-flight").getBoolean())) {
            event.setCancelled(true);
            Debug.info(player.getName() + " tried to fly, canceled");
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
                    Debug.info(player.getName() + " used item in lobby or as spectator");
                    event.setCancelled(true);
                    if (event.getMaterial() == Material
                            .valueOf(MainConfig.getInstance().node("items", "jointeam").getString("COMPASS"))) {
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
                            .valueOf(MainConfig.getInstance().node("items", "startgame").getString("DIAMOND"))) {
                        if (game.getStatus() == GameStatus.WAITING && (player.hasPermission("bw.vip.startitem")
                                || player.hasPermission("misat11.bw.vip.startitem"))) {
                            if (game.checkMinPlayers()) {
                                game.gameStartItem = true;
                            } else {
                                player.sendMessage(i18nc("vip_not_enough_players", game.getCustomPrefix()));
                            }
                        }
                    } else if (event.getMaterial() == Material
                            .valueOf(MainConfig.getInstance().node("items", "leavegame").getString("SLIME_BALL"))) {
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
                                Debug.info(player.getName() + " opened personal ender chest");
                                return;
                            }

                            if (!team.players.contains(gPlayer)) {
                                player.sendMessage(i18nc("team_chest_is_not_your", game.getCustomPrefix()));
                                Debug.info(player.getName() + " tried to open foreign team chest");
                                return;
                            }

                            BedwarsTeamChestOpenEvent teamChestOpenEvent = new BedwarsTeamChestOpenEvent(game, player,
                                    team);
                            Bukkit.getServer().getPluginManager().callEvent(teamChestOpenEvent);

                            if (teamChestOpenEvent.isCancelled()) {
                                return;
                            }

                            player.openInventory(team.getTeamChestInventory());
                            Debug.info(player.getName() + " opened team chest");
                        } else if (event.getClickedBlock().getState() instanceof InventoryHolder) {
                            InventoryHolder holder = (InventoryHolder) event.getClickedBlock().getState();
                            game.addChestForFutureClear(event.getClickedBlock().getLocation(), holder.getInventory());
                            Debug.info(player.getName() + " used chest in BedWars game");
                        } else if (event.getClickedBlock().getType().name().contains("CAKE")) {
                            if (game.getConfigurationContainer().getOrDefault(ConfigurationContainer.CAKE_TARGET_BLOCK_EATING, Boolean.class, false)) {
                                if (game.getTeamOfPlayer(event.getPlayer()).getTargetBlock().equals(event.getClickedBlock().getLocation())) {
                                    event.setCancelled(true);
                                } else {
                                    if (MainConfig.getInstance().node("disableCakeEating").getBoolean(true)) {
                                        event.setCancelled(true);
                                    }
                                    Debug.info(player.getName() + " is eating cake");
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
                                                Player113ListenerUtils.yummyCake(event, game);
                                            }
                                            break;
                                        }
                                    }
                                }
                            } else if (MainConfig.getInstance().node("disableCakeEating").getBoolean(true)) {
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
                                if (game.getConfigurationContainer().getOrDefault(ConfigurationContainer.ANCHOR_DECREASING, Boolean.class, false)
                                        && event.getClickedBlock().getType().name().equals("RESPAWN_ANCHOR")
                                        && game.getPlayerTeam(gPlayer).teamInfo.bed.equals(event.getClickedBlock().getLocation())
                                        && event.getItem() != null && event.getItem().getType() == Material.GLOWSTONE) {
                                    Debug.info(player.getName() + " filled respawn anchor");
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
                    && MainConfig.getInstance().node("disableDragonEggTeleport").getBoolean(true)) {
                event.setCancelled(true);
                Debug.info(player.getName() + " interacts with dragon egg");
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
                Debug.info(player.getName() + " interacts with entity in lobby or as spectator");
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
            for (var game : GameManager.getInstance().getGames()) {
                if (ArenaUtils.isInArea(event.getBed().getLocation(), game.getPos1(), game.getPos2())) {
                    event.setCancelled(true);
                    Debug.info(event.getPlayer().getName() + " tried to sleep");
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
                    event.setCancelled(event.getInventory().getType() != InventoryType.PLAYER);
                    Debug.info(player.getName() + " tried to open prohibited inventory");
                    return;
                }
                if (event.getInventory().getType() == InventoryType.ENCHANTING
                        || event.getInventory().getType() == InventoryType.CRAFTING
                        || event.getInventory().getType() == InventoryType.ANVIL
                        || event.getInventory().getType() == InventoryType.BREWING
                        || event.getInventory().getType() == InventoryType.FURNACE
                        || event.getInventory().getType() == InventoryType.WORKBENCH) {
                    if (!gProfile.getGame().getConfigurationContainer().getOrDefault(ConfigurationContainer.CRAFTING, Boolean.class, false)) {
                        event.setCancelled(true);
                        Debug.info(player.getName() + " tried to open prohibited inventory");
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
                    Debug.info(player.getName() + " selected his team with armor stand");
                    game.selectTeam(Main.getPlayerGameProfile(player), displayName);
                    return;
                }
            }
        } else if (PlayerMapper.wrapPlayer(player).hasPermission(BedWarsPermission.ADMIN_PERMISSION.asPermission())) {
            List<MetadataValue> values = player.getMetadata(JoinTeamCommand.BEDWARS_TEAM_JOIN_METADATA);
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
            living.setCustomNameVisible(MainConfig.getInstance().node("jointeam-entity-show-name").getBoolean(true));

            if (living instanceof ArmorStand) {
                ArmorStandUtils.equipArmorStand((ArmorStand) living, value.getTeam());
            }

            player.removeMetadata(JoinTeamCommand.BEDWARS_TEAM_JOIN_METADATA, Main.getInstance().getPluginDescription().as(JavaPlugin.class));
            player.sendMessage(i18n("admin_command_jointeam_entity_added"));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onChat(AsyncPlayerChatEvent event) {
        if (event.isCancelled() || !MainConfig.getInstance().node("chat", "override").getBoolean()) {
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

            String format = MainConfig.getInstance().node("chat", "format").getString("<%teamcolor%%name%§r> ");
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

            boolean teamChat = MainConfig.getInstance().node("chat", "default-team-chat-while-running").getBoolean(true)
                    && game.getStatus() == GameStatus.RUNNING && (team != null || spectator);

            String allChat = MainConfig.getInstance().node("chat", "all-chat-prefix").getString("@a");
            String tChat = MainConfig.getInstance().node("chat", "team-chat-prefix").getString("@t");

            if (message.startsWith(allChat) && (!spectator || !MainConfig.getInstance().node("chat", "disable-all-chat-for-spectators").getBoolean())) {
                teamChat = false;
                message = message.substring(allChat.length()).trim();
            } else if (message.startsWith(tChat) && (team != null || spectator)) {
                teamChat = true;
                message = message.substring(tChat.length()).trim();
            }

            if (teamChat) {
                if (spectator) {
                    format = MainConfig.getInstance().node("chat", "death-chat").getString("[DEATH] ") + format;
                } else {
                    format = MainConfig.getInstance().node("chat", "team-chat").getString("[TEAM] ") + format;
                }
            } else {
                format = MainConfig.getInstance().node("chat", "all-chat").getString("[ALL] ") + format;
            }

            event.setFormat(format + message.replaceAll("%", "%%")); // Fix using % in chat
            Iterator<Player> recipients = event.getRecipients().iterator();
            while (recipients.hasNext()) {
                Player recipient = recipients.next();
                GamePlayer recipientgPlayer = Main.getPlayerGameProfile(recipient);
                Game recipientGame = recipientgPlayer.getGame();
                if (recipientGame != game) {
                    if ((game.getStatus() == GameStatus.WAITING && MainConfig.getInstance().node("chat", "separate-chat", "lobby").getBoolean())
                            || (game.getStatus() != GameStatus.WAITING && MainConfig.getInstance().node("chat", "separate-chat", "game").getBoolean())) {
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
            if (MainConfig.getInstance().node("chat", "separate-chat", "lobby").getBoolean() || MainConfig.getInstance().node("chat", "separate-chat", "game").getBoolean()) {
                Iterator<Player> recipients = event.getRecipients().iterator();
                while (recipients.hasNext()) {
                    Player recipient = recipients.next();
                    GamePlayer recipientgPlayer = Main.getPlayerGameProfile(recipient);
                    Game recipientGame = recipientgPlayer.getGame();
                    if (recipientGame != null) {
                        if ((recipientGame.getStatus() == GameStatus.WAITING && MainConfig.getInstance().node("chat", "separate-chat", "lobby").getBoolean())
                                || (recipientGame.getStatus() != GameStatus.WAITING && MainConfig.getInstance().node("chat", "separate-chat", "game").getBoolean())) {
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
            if (game.getConfigurationContainer().getOrDefault(ConfigurationContainer.DAMAGE_WHEN_PLAYER_IS_NOT_IN_ARENA, Boolean.class, false) && game.getStatus() == GameStatus.RUNNING
                    && !gPlayer.isSpectator) {
                if (!ArenaUtils.isInArea(event.getTo(), game.getPos1(), game.getPos2())) {
                    player.damage(5);
                    Debug.info(player.getName() + " is doing prohibited move, damaging");
                }
            } else if (MainConfig.getInstance().node("preventSpectatorFlyingAway").getBoolean() && gPlayer.isSpectator && (game.getStatus() == GameStatus.RUNNING || game.getStatus() == GameStatus.GAME_END_CELEBRATING)) {
                if (!ArenaUtils.isInArea(event.getTo(), game.getPos1(), game.getPos2())) {
                    event.setCancelled(true);
                    Debug.info(player.getName() + " is doing prohibited move, cancelling");
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
                    Debug.info(player.getName() + " placed liquid");
                } else {
                    event.setCancelled(true);
                    Debug.info(player.getName() + " placed liquid, cancelling");
                }
            } else if (game.getStatus() != GameStatus.DISABLED) {
                event.setCancelled(true);
                Debug.info(player.getName() + " placed liquid, cancelling");
            }
        } else if (MainConfig.getInstance().node("preventArenaFromGriefing").getBoolean()) {
            for (var game : GameManager.getInstance().getGames()) {
                if (game.getStatus() != GameStatus.DISABLED && ArenaUtils.isInArea(event.getBlockClicked().getLocation(), game.getPos1(), game.getPos2())) {
                    event.setCancelled(true);
                    Debug.info(player.getName() + " is doing prohibited actions in protected area while not playing BedWars");
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onVehicleCreated(VehicleCreateEvent event) {
        if (event.isCancelled()) {
            return;
        }

        for (var game : GameManager.getInstance().getGames()) {
            if (game.getStatus() == GameStatus.RUNNING || game.getStatus() == GameStatus.GAME_END_CELEBRATING) {
                if (ArenaUtils.isInArea(event.getVehicle().getLocation(), game.getPos1(), game.getPos2())) {
                    Main.registerGameEntity(event.getVehicle(), game);
                    break;
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
                Debug.info(player.getName() + " tried to pick up the item in lobby or as spectator");
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
