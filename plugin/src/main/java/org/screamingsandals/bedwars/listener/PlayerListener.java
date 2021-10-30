package org.screamingsandals.bedwars.listener;

import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.screamingsandals.bedwars.BedWarsPlugin;
import org.screamingsandals.bedwars.api.config.ConfigurationContainer;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.commands.BedWarsPermission;
import org.screamingsandals.bedwars.commands.admin.JoinTeamCommand;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.entities.EntitiesManagerImpl;
import org.screamingsandals.bedwars.events.PlayerDeathMessageSendEventImpl;
import org.screamingsandals.bedwars.events.PlayerKilledEventImpl;
import org.screamingsandals.bedwars.events.PlayerRespawnedEventImpl;
import org.screamingsandals.bedwars.events.TeamChestOpenEventImpl;
import org.screamingsandals.bedwars.game.*;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.bedwars.player.PlayerManagerImpl;
import org.screamingsandals.bedwars.statistics.PlayerStatisticImpl;
import org.screamingsandals.bedwars.statistics.PlayerStatisticManager;
import org.screamingsandals.bedwars.utils.*;
import org.screamingsandals.bedwars.lib.debug.Debug;
import org.screamingsandals.bedwars.lib.nms.entity.PlayerUtils;
import org.screamingsandals.lib.Server;
import org.screamingsandals.lib.SpecialSoundKey;
import org.screamingsandals.lib.attribute.AttributeHolder;
import org.screamingsandals.lib.attribute.AttributeTypeHolder;
import org.screamingsandals.lib.block.BlockTypeHolder;
import org.screamingsandals.lib.entity.EntityLiving;
import org.screamingsandals.lib.entity.EntityProjectile;
import org.screamingsandals.lib.event.EventManager;
import org.screamingsandals.lib.event.EventPriority;
import org.screamingsandals.lib.event.OnEvent;
import org.screamingsandals.lib.event.entity.*;
import org.screamingsandals.lib.event.player.*;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.item.builder.ItemFactory;
import org.screamingsandals.lib.player.PlayerWrapper;
import org.screamingsandals.lib.tasker.Tasker;
import org.screamingsandals.lib.tasker.TaskerTime;
import org.screamingsandals.lib.tasker.task.TaskerTask;
import org.screamingsandals.lib.utils.AdventureHelper;
import org.screamingsandals.lib.utils.annotations.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@SuppressWarnings("PatternValidation")
public class PlayerListener {

    private final List<PlayerWrapper> explosionAffectedPlayers = new ArrayList<>();

    @OnEvent(priority = org.screamingsandals.lib.event.EventPriority.HIGHEST)
    public void onPlayerDeath(SPlayerDeathEvent event) {
        final var victim = event.getPlayer();

        if (PlayerManagerImpl.getInstance().isPlayerInGame(victim)) {
            Debug.info(victim.getName() + " died in BedWars Game, Processing his dead...");
            final var gVictim = victim.as(BedWarsPlayer.class);
            final var game = gVictim.getGame();
            final var victimTeam = game.getPlayerTeam(gVictim);
            final var victimColor = victimTeam.getColor().getTextColor();
            final var drops = List.copyOf(event.getDrops());
            int respawnTime = MainConfig.getInstance().node("respawn-cooldown", "time").getInt(5);

            if (game.getConfigurationContainer().getOrDefault(ConfigurationContainer.KEEP_ARMOR, Boolean.class, false)) {
                final var armorContents = victim.getPlayerInventory().getArmorContents();
                if (armorContents != null) {
                    gVictim.setArmorContents(armorContents);
                    Debug.info(victim.getName() + "'s armor contents: " +
                            Arrays.stream(armorContents)
                                    .filter(Objects::nonNull)
                                    .map(stack -> stack.getMaterial().platformName())
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
                    Message deathMessageMsg = null;
                    final var killer = event.getKiller();
                    if (MainConfig.getInstance().node("chat", "send-custom-death-messages").getBoolean()) {
                        if (killer != null && PlayerManagerImpl.getInstance().isPlayerInGame(killer)) {
                            Debug.info(victim.getName() + " died because entity " + killer.getName() + " killed him");
                            final var gKiller = killer.as(BedWarsPlayer.class);
                            final var killerTeam = game.getPlayerTeam(gKiller);
                            final var killerColor = killerTeam.getColor().getTextColor();

                            deathMessageMsg = Message.of(LangKeys.IN_GAME_PLAYER_KILLED)
                                    .prefixOrDefault(game.getCustomPrefixComponent())
                                    .placeholder("victim", victim.getDisplayName().color(victimColor))
                                    .placeholder("killer", killer.getDisplayName().color(killerColor))
                                    .placeholder("victimTeam", Component.text(victimTeam.getName()).color(victimColor))
                                    .placeholder("killerTeam", Component.text(killerTeam.getName()).color(killerColor));
                        } else {
                            deathMessageMsg = Message.of(LangKeys.IN_GAME_PLAYER_SELF_KILLED)
                                    .prefixOrDefault(game.getCustomPrefixComponent())
                                    .placeholder("victim", victim.getDisplayName().color(victimColor))
                                    .placeholder("victimTeam", Component.text(victimTeam.getName()).color(victimColor));
                        }

                    }
                    if (deathMessage != null) {
                        var bpdmsEvent = new PlayerDeathMessageSendEventImpl(game, gVictim, deathMessageMsg != null ? deathMessageMsg : Message.ofPlainText(deathMessage));
                        EventManager.fire(bpdmsEvent);
                        if (!bpdmsEvent.isCancelled()) {
                            event.setDeathMessage(null);
                            bpdmsEvent.getMessage().send(game.getConnectedPlayers());
                        }
                    }
                }

                var team = game.getPlayerTeam(gVictim);
                SpawnEffects.spawnEffect(game, gVictim, "game-effects.kill");
                boolean isBed = team.isTargetBlockIntact();
                if (isBed && game.getConfigurationContainer().getOrDefault(ConfigurationContainer.ANCHOR_DECREASING, Boolean.class, false) && team.getTargetBlock().getBlock().getType().isSameType("respawn_anchor")) {
                    var anchor = team.getTargetBlock().getBlock().getType();
                    int charges = anchor.get("charges").map(Integer::parseInt).orElse(0);
                    if (charges > 0) {
                        var c = charges - 1;
                        team.getTargetBlock().getBlock().setType(anchor.with("charges", String.valueOf(c)));
                        if (c == 0) {
                            team.getTargetBlock().getWorld().playSound(Sound.sound(
                                    SpecialSoundKey.key(MainConfig.getInstance().node("target-block", "respawn-anchor", "sound", "deplete").getString("block.respawn_anchor.deplete")),
                                    Sound.Source.BLOCK,
                                    1,
                                    1
                            ), team.getTargetBlock().getX(), team.getTargetBlock().getY(), team.getTargetBlock().getZ());
                        } else {
                            team.getTargetBlock().getWorld().playSound(Sound.sound(
                                    SpecialSoundKey.key(MainConfig.getInstance().node("target-block", "respawn-anchor", "sound", "deplete").getString("block.glass.break")),
                                    Sound.Source.BLOCK,
                                    1,
                                    1
                            ), team.getTargetBlock().getX(), team.getTargetBlock().getY(), team.getTargetBlock().getZ());
                        }
                    } else {
                        isBed = false;
                    }
                }
                if (!isBed) {
                    Debug.info(victim.getName() + " died without bed, he's going to spectate the game");
                    gVictim.setSpectator(true);
                    team.getPlayers().remove(gVictim);
                    if (PlayerStatisticManager.isEnabled()) {
                        var statistic = PlayerStatisticManager.getInstance().getStatistic(victim);
                        statistic.addLoses(1);
                        statistic.addScore(MainConfig.getInstance().node("statistics", "scores", "lose").getInt(0));
                    }
                }

                boolean onlyOnBedDestroy = MainConfig.getInstance().node("statistics", "bed-destroyed-kills").getBoolean();

                var killer = event.getKiller();
                if (killer != null && PlayerManagerImpl.getInstance().isPlayerInGame(killer)) {
                    var gKiller = killer.as(BedWarsPlayer.class);
                    if (gKiller.getGame() == game) {
                        if (!onlyOnBedDestroy || !isBed) {
                            game.dispatchRewardCommands("player-kill", killer,
                                    MainConfig.getInstance().node("statistics", "scores", "kill").getInt(10));
                        }
                        if (!isBed) {
                            game.dispatchRewardCommands("player-final-kill", killer,
                                    MainConfig.getInstance().node("statistics", "scores", "final-kill").getInt(10));
                        }
                        if (team.isDead()) {
                            SpawnEffects.spawnEffect(game, gVictim, "game-effects.teamkill");

                            killer.playSound(Sound.sound(
                                    SpecialSoundKey.key(MainConfig.getInstance().node("sounds", "team_kill", "sound").getString("entity.player.levelup")),
                                    Sound.Source.AMBIENT,
                                    (float) MainConfig.getInstance().node("sounds", "team_kill", "volume").getDouble(),
                                    (float) MainConfig.getInstance().node("sounds", "team_kill", "pitch").getDouble()
                            ));
                        } else {
                            killer.playSound(Sound.sound(
                                    SpecialSoundKey.key(MainConfig.getInstance().node("sounds", "player_kill", "sound").getString("entity.generic.big_fall")),
                                    Sound.Source.AMBIENT,
                                    (float) MainConfig.getInstance().node("sounds", "player_kill", "volume").getDouble(),
                                    (float) MainConfig.getInstance().node("sounds", "player_kill", "pitch").getDouble()
                            ));
                            if (!isBed) {
                                VaultUtils.getInstance().depositPlayer(killer, MainConfig.getInstance().node("vault", "reward", "final-kill").getInt());
                            } else {
                                VaultUtils.getInstance().depositPlayer(killer, BedWarsPlugin.getVaultKillReward());
                            }
                        }

                    }
                }

                var killedEvent = new PlayerKilledEventImpl(game, killer != null && PlayerManagerImpl.getInstance().isPlayerInGame(killer) ? killer.as(BedWarsPlayer.class) : null, gVictim, drops);
                EventManager.fire(killedEvent);

                if (PlayerStatisticManager.isEnabled()) {
                    var diePlayer = PlayerStatisticManager.getInstance().getStatistic(victim);
                    PlayerStatisticImpl killerPlayer;

                    if (!onlyOnBedDestroy || !isBed) {
                        diePlayer.addDeaths(1);
                        diePlayer.addScore(MainConfig.getInstance().node("statistics", "scores", "die").getInt(0));
                    }

                    if (killer != null) {
                        if (!onlyOnBedDestroy || !isBed) {
                            killerPlayer = PlayerStatisticManager.getInstance().getStatistic(killer);
                            if (killerPlayer != null) {
                                killerPlayer.addKills(1);
                                killerPlayer.addScore(MainConfig.getInstance().node("statistics", "scores", "kill").getInt(10));

                                if (!isBed) {
                                    killerPlayer.addScore(MainConfig.getInstance().node("statistics", "scores", "final-kill").getInt());
                                }
                            }
                        }
                    }
                }
            }
            if (!Server.isVersion(1, 15) && !MainConfig.getInstance().node("allow-fake-death").getBoolean()) {
                Debug.info(victim.getName() + " is going to be respawned via spigot api");
                PlayerUtils.respawn(victim, 3L);
            }
            if (MainConfig.getInstance().node("respawn-cooldown", "enabled").getBoolean()
                    && victimTeam.isAlive()
                    && !gVictim.isSpectator()) {
                game.makeSpectator(gVictim, false);
                Debug.info(victim.getName() + " is in respawn cooldown");

                final var livingTime = new AtomicInteger(respawnTime);
                final var task = new AtomicReference<TaskerTask>();
                task.set(
                        Tasker.build(() -> {
                                    if (livingTime.get() > 0) {
                                        Message
                                                .of(LangKeys.IN_GAME_RESPAWN_COOLDOWN_TITLE)
                                                .placeholder("time", livingTime.get())
                                                .times(TitleUtils.defaultTimes())
                                                .title(gVictim);
                                        gVictim.playSound(Sound.sound(
                                                SpecialSoundKey.key(MainConfig.getInstance().node("sounds", "respawn_cooldown_wait", "sound").getString("block.stone_button.click_on")),
                                                Sound.Source.AMBIENT,
                                                (float) MainConfig.getInstance().node("sounds", "respawn_cooldown_wait", "volume").getDouble(),
                                                (float) MainConfig.getInstance().node("sounds", "respawn_cooldown_wait", "pitch").getDouble()
                                        ));
                                    }

                                    livingTime.decrementAndGet();
                                    if (livingTime.get() == 0) {
                                        game.makePlayerFromSpectator(gVictim);
                                        gVictim.playSound(
                                                Sound.sound(
                                                        SpecialSoundKey.key(MainConfig.getInstance().node("sounds", "respawn_cooldown_done", "sound").getString("ui.button.click")),
                                                        Sound.Source.AMBIENT,
                                                        (float) MainConfig.getInstance().node("sounds", "respawn_cooldown_done", "volume").getDouble(1),
                                                        (float) MainConfig.getInstance().node("sounds", "respawn_cooldown_done", "pitch").getDouble(1)
                                                )
                                        );
                                        task.get().cancel();
                                    }
                                })
                                .delay(20, TaskerTime.TICKS)
                                .repeat(20, TaskerTime.TICKS)
                                .start()
                );
            }
        }
    }

    @OnEvent
    public void onPlayerQuit(SPlayerLeaveEvent event) {
        if (PlayerManagerImpl.getInstance().isPlayerRegistered(event.getPlayer())) {
            var gPlayer = event.getPlayer().as(BedWarsPlayer.class);
            if (gPlayer.isInGame())
                gPlayer.changeGame(null);
            PlayerManagerImpl.getInstance().dropPlayer(gPlayer);
        }

        if (MainConfig.getInstance().node("disable-server-message", "player-join").getBoolean()) {
            event.setLeaveMessage(null);
        }
    }

    @OnEvent
    public void onPlayerJoin(SPlayerJoinEvent event) {
        var player = event.getPlayer();

        if (GameImpl.isBungeeEnabled() && MainConfig.getInstance().node("bungee", "auto-game-connect").getBoolean()) {
            Debug.info(event.getPlayer().getName() + " joined the server and auto-game-connect is enabled. Registering task...");
            Tasker.build(() -> {
                        try {
                            Debug.info("Selecting game for " + event.getPlayer().getName());
                            var gameManager = GameManagerImpl.getInstance();
                            var game = gameManager.getFirstWaitingGame().or(gameManager::getFirstRunningGame);
                            if (game.isEmpty()) { // still nothing?
                                if (!player.hasPermission(BedWarsPermission.ADMIN_PERMISSION.asPermission())) {
                                    Debug.info(event.getPlayer().getName() + " is not connecting to any game! Kicking...");
                                    BungeeUtils.movePlayerToBungeeServer(player, false);
                                }
                                return;
                            }
                            Debug.info(event.getPlayer().getName() + " is connecting to " + game.get().getName());

                            game.get().joinToGame(PlayerManagerImpl.getInstance().getPlayerOrCreate(player));
                        } catch (NullPointerException ignored) {
                            if (!player.hasPermission(BedWarsPermission.ADMIN_PERMISSION.asPermission())) {
                                Debug.info(event.getPlayer().getName() + " is not connecting to any game! Kicking...");
                                BungeeUtils.movePlayerToBungeeServer(player, false);
                            }
                        }
                    })
                    .delay(1, TaskerTime.TICKS)
                    .start();
        }

        if (MainConfig.getInstance().node("disable-server-message", "player-join").getBoolean()) {
            event.setJoinMessage(null);
        }

        if (MainConfig.getInstance().node("tab", "enabled").getBoolean() && MainConfig.getInstance().node("tab", "hide-foreign-players").getBoolean()) {
            Server.getConnectedPlayers().stream().filter(PlayerManagerImpl.getInstance()::isPlayerInGame).forEach(p -> PlayerManagerImpl.getInstance().getPlayer(p).orElseThrow().hidePlayer(player));
        }
    }

    @OnEvent(priority = org.screamingsandals.lib.event.EventPriority.HIGHEST)
    public void onPlayerRespawn(SPlayerRespawnEvent event) {
        if (PlayerManagerImpl.getInstance().isPlayerInGame(event.getPlayer())) {
            Debug.info(event.getPlayer().getName() + " is respawning in BedWars game");
            var gPlayer = event.getPlayer().as(BedWarsPlayer.class);
            var game = gPlayer.getGame();
            var team = game.getPlayerTeam(gPlayer);

            if (game.getStatus() == GameStatus.WAITING) {
                Debug.info(event.getPlayer().getName() + " is in lobby");
                event.setLocation(gPlayer.getGame().getLobbySpawn());
                return;
            }
            Debug.info(event.getPlayer().getName() + " is in game");
            // clear inventory to fix issue 148
            if (!game.getConfigurationContainer().getOrDefault(ConfigurationContainer.KEEP_INVENTORY, Boolean.class, false)) {
                event.getPlayer().getPlayerInventory().clear();
            }
            if (gPlayer.isSpectator()) {
                Debug.info(event.getPlayer().getName() + " is going to be spectator");
                if (team == null) {
                    event.setLocation(MiscUtils.findEmptyLocation(gPlayer.getGame().makeSpectator(gPlayer, true)));
                } else {
                    event.setLocation(MiscUtils.findEmptyLocation(gPlayer.getGame().makeSpectator(gPlayer, false)));
                }
            } else {
                Debug.info(event.getPlayer().getName() + " is going to play the game");
                event.setLocation(gPlayer.getGame().getPlayerTeam(gPlayer).getTeamSpawn());

                var respawnEvent = new PlayerRespawnedEventImpl(game, gPlayer);
                EventManager.fire(respawnEvent);

                if (MainConfig.getInstance().node("respawn", "protection-enabled").getBoolean(true)) {
                    game.addProtectedPlayer(gPlayer).runProtection();
                }

                SpawnEffects.spawnEffect(gPlayer.getGame(), gPlayer, "game-effects.respawn");
                if (gPlayer.getGame().getConfigurationContainer().getOrDefault(ConfigurationContainer.ENABLE_PLAYER_RESPAWN_ITEMS, Boolean.class, false)) {
                    var playerRespawnItems = MainConfig.getInstance().node("player-respawn-items", "items")
                            .childrenList()
                            .stream()
                            .map(ItemFactory::build)
                            .filter(Optional::isPresent)
                            .map(Optional::get)
                            .collect(Collectors.toList());
                    if (!playerRespawnItems.isEmpty()) {
                        MiscUtils.giveItemsToPlayer(playerRespawnItems, gPlayer, team.getColor());
                    } else {
                        Debug.warn("You have wrongly configured player-respawn-items.items!", true);
                    }
                }

                if (game.getConfigurationContainer().getOrDefault(ConfigurationContainer.KEEP_ARMOR, Boolean.class, false)) {
                    final var armorContents = gPlayer.getArmorContents();
                    if (armorContents != null) {
                        gPlayer.getPlayerInventory().setArmorContents(armorContents);
                    }
                }

                MiscUtils.giveItemsToPlayer(gPlayer.getPermanentItemsPurchased(), gPlayer, team.getColor());
            }
        }
    }

    @OnEvent
    public void onPlayerWorldChange(SPlayerWorldChangeEvent event) {
        if (PlayerManagerImpl.getInstance().isPlayerInGame(event.getPlayer())) {
            var gPlayer = event.getPlayer().as(BedWarsPlayer.class);
            var game = gPlayer.getGame();
            if (!game.getWorld().equals(event.getPlayer().getLocation().getWorld())
                    && !game.getLobbySpawn().getWorld().equals(event.getPlayer().getLocation().getWorld())) {
                gPlayer.changeGame(null);
                Debug.info(event.getPlayer().getName() + " changed world while in BedWars arena. Kicking...");
            }
        }
    }

    @OnEvent(priority = org.screamingsandals.lib.event.EventPriority.HIGHEST)
    public void onBlockPlace(SPlayerBlockPlaceEvent event) {
        if (event.isCancelled()) {
            if (MainConfig.getInstance().node("event-hacks", "place").getBoolean() && PlayerManagerImpl.getInstance().isPlayerInGame(event.getPlayer())) {
                event.setCancelled(false);
            } else {
                return;
            }
        }

        if (PlayerManagerImpl.getInstance().isPlayerInGame(event.getPlayer())) {
            var gPlayer = event.getPlayer().as(BedWarsPlayer.class);
            var game = gPlayer.getGame();
            if (game.getStatus() == GameStatus.WAITING) {
                event.setCancelled(true);
                Debug.info(event.getPlayer().getName() + " attempted to place a block, canceled");
                return;
            }
            if (!game.blockPlace(gPlayer, event.getBlock(),
                    event.getReplacedBlockState(), event.getItemInHand())) {
                event.setCancelled(true);
                Debug.info(event.getPlayer().getName() + " attempted to place a block, canceled");
            } else {
                Debug.info(event.getPlayer().getName() + " attempted to place a block, allowed");
            }
        } else if (MainConfig.getInstance().node("preventArenaFromGriefing").getBoolean()) {
            for (var game : GameManagerImpl.getInstance().getGames()) {
                if (game.getStatus() != GameStatus.DISABLED && ArenaUtils.isInArea(event.getBlock().getLocation(), game.getPos1(), game.getPos2())) {
                    event.setCancelled(true);
                    Debug.info(event.getPlayer().getName() + " attempted to place a block in protected area while not playing BedWars game, canceled");
                    return;
                }
            }
        }
    }

    @OnEvent(priority = org.screamingsandals.lib.event.EventPriority.HIGHEST)
    public void onBlockBreak(SPlayerBlockBreakEvent event) {
        if (event.isCancelled()) {
            if (MainConfig.getInstance().node("event-hacks", "destroy").getBoolean() && PlayerManagerImpl.getInstance().isPlayerInGame(event.getPlayer())) {
                event.setCancelled(false);
            } else {
                return;
            }
        }

        if (PlayerManagerImpl.getInstance().isPlayerInGame(event.getPlayer())) {
            final var gamePlayer = event.getPlayer().as(BedWarsPlayer.class);
            final var game = gamePlayer.getGame();
            final var block = event.getBlock();

            if (game.getStatus() == GameStatus.WAITING) {
                Debug.info(event.getPlayer().getName() + " attempted to break a block, canceled");
                event.setCancelled(true);
                return;
            }

            if (!game.blockBreak(gamePlayer, event.getBlock(), event)) {
                event.setCancelled(true);
                Debug.info(event.getPlayer().getName() + " attempted to break a block, canceled");
            } else {
                Debug.info(event.getPlayer().getName() + " attempted to break a block, allowed");
            }

            //Fix for obsidian dropping
            if (game.getStatus() == GameStatus.RUNNING && gamePlayer.isInGame()) {
                if (block.getType().isSameType("ender_chest")) {
                    event.setDropItems(false);
                }
            }
        } else if (MainConfig.getInstance().node("preventArenaFromGriefing").getBoolean()) {
            for (var game : GameManagerImpl.getInstance().getGames()) {
                if (game.getStatus() != GameStatus.DISABLED && ArenaUtils.isInArea(event.getBlock().getLocation(), game.getPos1(), game.getPos2())) {
                    event.setCancelled(true);
                    Debug.info(event.getPlayer().getName() + " attempted to break a block in protected area while not in BedWars game, canceled");
                    return;
                }
            }
        }
    }

    @OnEvent(priority = org.screamingsandals.lib.event.EventPriority.HIGHEST)
    public void onCommandExecuted(SPlayerCommandPreprocessEvent event) {
        if (event.isCancelled()) {
            return;
        }

        final var player = event.getPlayer();
        if (PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
            //Allow players with permissions to use all commands
            if (player.hasPermission(BedWarsPermission.ADMIN_PERMISSION.asPermission())) {
                Debug.info(event.getPlayer().getName() + " attempted to execute a command, allowed");
                return;
            }

            final var message = event.getCommand();
            final var gamePlayer = event.getPlayer().as(BedWarsPlayer.class);
            if (BedWarsPlugin.isCommandLeaveShortcut(message)) {
                event.setCancelled(true);
                gamePlayer.changeGame(null);
            } else if (!BedWarsPlugin.isCommandAllowedInGame(message.split(" ")[0])) {
                Debug.info(event.getPlayer().getName() + " attempted to execute a command, canceled");
                event.setCancelled(true);
                player.sendMessage(Message.of(LangKeys.IN_GAME_ERRORS_COMMAND_IS_NOT_ALLOWED).prefixOrDefault(gamePlayer.getGame().getCustomPrefixComponent()));
                return;
            }
            Debug.info(event.getPlayer().getName() + " attempted to execute a command, allowed");
        }
    }

    @OnEvent
    public void onInventoryClick(SPlayerInventoryClickEvent event) {
        if (event.getInventory() == null) {
            return;
        }

        if (event.getInventory().getType().is("player")) {
            var p = event.getPlayer();
            if (PlayerManagerImpl.getInstance().isPlayerInGame(p)) {
                var gPlayer = p.as(BedWarsPlayer.class);
                var game = gPlayer.getGame();
                if (game.getStatus() == GameStatus.WAITING || gPlayer.isSpectator()) {
                    event.setCancelled(true);
                    Debug.info(p.getName() + " used item in lobby or as spectator");
                    if (event.getClickType().isLeftClick() || event.getClickType().isRightClick()) {
                        var item = event.getCurrentItem();
                        if (item != null) {
                            p.closeInventory();
                            if (item.getMaterial().is(MainConfig.getInstance().node("items", "jointeam").getString("COMPASS"))) {
                                if (game.getStatus() == GameStatus.WAITING) {
                                    var inv = game.getTeamSelectorInventory();
                                    if (inv == null) {
                                        return;
                                    }
                                    inv.openForPlayer(gPlayer);
                                } else if (gPlayer.isSpectator()) {
                                    // TODO
                                }
                            } else if (item.getMaterial().is(MainConfig.getInstance().node("items", "startgame").getString("DIAMOND"))) {
                                if (game.getStatus() == GameStatus.WAITING && p.hasPermission(BedWarsPermission.START_ITEM_PERMISSION.asPermission())) {
                                    if (game.checkMinPlayers()) {
                                        game.gameStartItem = true;
                                    } else {
                                        p.sendMessage(Message.of(LangKeys.IN_GAME_ERRORS_VIP_NOT_ENOUGH_PLAYERS).prefixOrDefault(game.getCustomPrefixComponent()));
                                    }
                                }
                            } else if (item.getMaterial().is(MainConfig.getInstance().node("items", "leavegame").getString("SLIME_BALL"))) {
                                game.leaveFromGame(gPlayer);
                            }
                        }
                    }
                }
            }
        }
    }

    @OnEvent(priority = org.screamingsandals.lib.event.EventPriority.HIGH)
    public void onHunger(SFoodLevelChangeEvent event) {
        if (!(event.getEntity().getEntityType().is("player")) || event.isCancelled()) {
            return;
        }

        var player = (PlayerWrapper) event.getEntity();
        if (PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
            var gPlayer = player.as(BedWarsPlayer.class);
            var game = gPlayer.getGame();
            if (game.getStatus() == GameStatus.WAITING || gPlayer.isSpectator()) {
                event.setCancelled(true);
                Debug.info(player.getName() + " tried to eat while eating is not allowed");
            }

            if (game.getStatus() == GameStatus.RUNNING && MainConfig.getInstance().node("disable-hunger").getBoolean()) {
                event.setCancelled(true);
                Debug.info(player.getName() + " tried to eat while eating is not allowed");
            }
        }
    }

    @OnEvent
    public void onCraft(SPlayerCraftItemEvent event) {
        if (event.isCancelled()) {
            return;
        }

        var player = event.getPlayer();
        if (PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
            var gPlayer = player.as(BedWarsPlayer.class);
            if (gPlayer.getGame().getStatus() != GameStatus.RUNNING) {
                event.setCancelled(true);
                Debug.info(player.getName() + " tried to craft while crafting is not allowed");
            } else if (!gPlayer.getGame().getConfigurationContainer().getOrDefault(ConfigurationContainer.CRAFTING, Boolean.class, false)) {
                event.setCancelled(true);
                Debug.info(player.getName() + " tried to craft while crafting is not allowed");
            }
        }
    }

    @OnEvent(priority = org.screamingsandals.lib.event.EventPriority.HIGHEST)
    public void onDamage(SEntityDamageEvent event) {
        if (event.isCancelled()) {
            if (MainConfig.getInstance().node("event-hacks", "damage").getBoolean()
                    && event.getEntity().getEntityType().is("player")
                    && PlayerManagerImpl.getInstance().isPlayerInGame(event.getEntity().getUniqueId())) {
                event.setCancelled(false);
            } else {
                return;
            }
        }

        final var entity = event.getEntity();

        if (!entity.getEntityType().is("player")) {
            if (!event.getDamageCause().is("void")) {
                var game = EntitiesManagerImpl.getInstance().getGameOfEntity(entity);
                if (game.isPresent()) {
                    if (game.get().isEntityShop(entity) && game.get().getConfigurationContainer().getOrDefault(ConfigurationContainer.PROTECT_SHOP, Boolean.class, false)) {
                        Debug.info("Game entity was damaged, cancelling");
                        event.setCancelled(true);
                    }
                }
            }

            if (event instanceof SEntityDamageByEntityEvent) {
                if (event.getEntity().getEntityType().is("armor_stand")) {
                    var damager = ((SEntityDamageByEntityEvent) event).getDamager();
                    if (damager.getEntityType().is("player")) {
                        var player = (PlayerWrapper) event.getEntity();
                        if (PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
                            var gPlayer = player.as(BedWarsPlayer.class);
                            if (gPlayer.getGame().getStatus() == GameStatus.WAITING || gPlayer.isSpectator()) {
                                Debug.info(player.getName() + " damaged armor stand in lobby, cancelling");
                                event.setCancelled(true);
                            }
                        }
                    }
                }
            }
            return;
        }

        var player = (PlayerWrapper) event.getEntity();
        if (PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
            Debug.info(player.getName() + " was damaged in game");
            var gPlayer = player.as(BedWarsPlayer.class);
            var game = gPlayer.getGame();
            if (gPlayer.isSpectator()) {
                if (event.getDamageCause().is("void")) {
                    gPlayer.setFallDistance(0);
                    gPlayer.teleport(game.getSpecSpawn());
                }
                event.setCancelled(true);
            } else if (game.getStatus() == GameStatus.WAITING) {
                if (event.getDamageCause().is("void")) {
                    gPlayer.setFallDistance(0);
                    gPlayer.teleport(game.getLobbySpawn());
                }
                event.setCancelled(true);
            } else if (game.getStatus() == GameStatus.RUNNING || game.getStatus() == GameStatus.GAME_END_CELEBRATING) {
                if (gPlayer.isSpectator()) {
                    event.setCancelled(true);
                }
                if (game.isProtectionActive(gPlayer) && !event.getDamageCause().is("void")) {
                    event.setCancelled(true);
                    return;
                }

                if (event.getDamageCause().is("void") && gPlayer.getHealth() > 0.5) {
                    gPlayer.setHealth(0.5);
                } else if (event.getDamageCause().is("fall")) {
                    if (explosionAffectedPlayers.contains(player)) {
                        event.setDamage(MainConfig.getInstance().node("tnt-jump", "fall-damage").getDouble(0.75));
                        explosionAffectedPlayers.remove(player);
                    }
                } else if (event instanceof SEntityDamageByEntityEvent) {
                    var edbee = (SEntityDamageByEntityEvent) event;

                    if (edbee.getDamager().as(Entity.class) instanceof Explosive) {
                        if (MainConfig.getInstance().node("tnt-jump", "enabled").getBoolean()) {
                            if (edbee.getDamager().getEntityType().is("tnt")) {
                                final var tnt = edbee.getDamager();
                                final var tntSource = tnt.as(TNTPrimed.class).getSource();
                                if (tntSource instanceof Player) {
                                    final var playerSource = (Player) tntSource;
                                    if (playerSource.equals(player.as(Player.class))) {
                                        event.setDamage(MainConfig.getInstance().node("tnt-jump", "source-damage").getDouble(0.5));
                                        var tntVector = tnt.getLocation().asVector();
                                        var vector = player
                                                .getLocation()
                                                .clone()
                                                .add(0, MainConfig.getInstance().node("tnt-jump", "acceleration-y").getInt(), 0)
                                                .asVector()
                                                .add(-tntVector.getX(), -tntVector.getY(), -tntVector.getZ()).normalize();

                                        vector.setY(vector.getY() / MainConfig.getInstance().node("tnt-jump", "reduce-y").getDouble());
                                        vector.multiply(MainConfig.getInstance().node("tnt-jump", "launch-multiplier").getInt());
                                        player.setVelocity(vector);
                                        explosionAffectedPlayers.add(player);
                                    }
                                    if (!MainConfig.getInstance().node("tnt-jump", "team-damage").getBoolean(true)) {
                                        if (game.getPlayerTeam(gPlayer).equals(game.getPlayerTeam(PlayerManagerImpl.getInstance().getPlayer(playerSource.getUniqueId()).orElseThrow()))) {
                                            event.setCancelled(true);
                                        }
                                    }
                                }
                            }
                        }
                    } else if (edbee.getDamager().getEntityType().is("player")) {
                        var damager = (PlayerWrapper) event.getEntity();
                        if (PlayerManagerImpl.getInstance().isPlayerInGame(damager)) {
                            var gDamager = damager.as(BedWarsPlayer.class);
                            if (gDamager.isSpectator() || (gDamager.getGame().getPlayerTeam(gDamager) == game.getPlayerTeam(gPlayer) && !game.getConfigurationContainer().getOrDefault(ConfigurationContainer.FRIENDLY_FIRE, Boolean.class, false))) {
                                event.setCancelled(true);
                            }
                        }
                    } else if (edbee.getDamager().getEntityType().is("firework") && game.getStatus() == GameStatus.GAME_END_CELEBRATING) {
                        event.setCancelled(true);
                    } else if (edbee.getDamager() instanceof EntityProjectile) {
                        var projectile = (EntityProjectile) edbee.getDamager();
                        if (projectile.getEntityType().is("minecraft:fireball", "minecraft:small_fireball", "minecraft:dragon_fireball") && game.getStatus() == GameStatus.RUNNING) {
                            final double damage = MainConfig.getInstance().node("specials", "throwable-fireball", "damage").getDouble();
                            event.setDamage(damage);
                        } else if (projectile.getShooter() instanceof PlayerWrapper) {
                            var damager = projectile.getShooter().as(PlayerWrapper.class);
                            if (PlayerManagerImpl.getInstance().isPlayerInGame(damager)) {
                                var gDamager = damager.as(BedWarsPlayer.class);
                                if (gDamager.isSpectator() || gDamager.getGame().getPlayerTeam(gDamager) == game.getPlayerTeam(gPlayer) && !game.getConfigurationContainer().getOrDefault(ConfigurationContainer.FRIENDLY_FIRE, Boolean.class, false)) {
                                    event.setCancelled(true);
                                }
                            }
                        }
                    }
                }

                // TODO: check this, there was final damage before
                if (MainConfig.getInstance().node("allow-fake-death").getBoolean() && !event.isCancelled() && (player.getHealth() - event.getDamage() <= 0)) {
                    event.setCancelled(true);
                    Debug.info(player.getName() + " is going to be respawned via FakeDeath");
                    FakeDeath.die(gPlayer);
                }
            }
        }
    }

    @OnEvent
    public void onLaunchProjectile(SProjectileLaunchEvent event) {
        if (event.isCancelled()) {
            return;
        }

        var projectile = event.getEntity();
        if (projectile.getShooter() instanceof PlayerWrapper) {
            var damager = projectile.getShooter().as(PlayerWrapper.class);
            if (PlayerManagerImpl.getInstance().isPlayerInGame(damager)) {
                if (damager.as(BedWarsPlayer.class).isSpectator()) {
                    event.setCancelled(true);
                    Debug.info(damager.getName() + " tried to launch projectile as spectator");
                }
            }
        }
    }

    @OnEvent
    public void onDrop(SPlayerDropItemEvent event) {
        if (event.isCancelled()) {
            return;
        }
        var player = event.getPlayer();
        if (PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
            var gPlayer = player.as(BedWarsPlayer.class);
            if (gPlayer.getGame().getStatus() != GameStatus.RUNNING || gPlayer.isSpectator()) {
                event.setCancelled(true);
                Debug.info(player.getName() + " tried to drop an item as spectator or in lobby");
            }
        }
    }

    @OnEvent(priority = org.screamingsandals.lib.event.EventPriority.HIGHEST)
    public void onFly(SPlayerToggleFlightEvent event) {
        if (event.isCancelled()) {
            return;
        }

        var player = event.getPlayer();
        if (PlayerManagerImpl.getInstance().isPlayerInGame(player) && !player.as(BedWarsPlayer.class).isSpectator()
                && !player.hasPermission(BedWarsPermission.BYPASS_FLIGHT_PERMISSION.asPermission()) && MainConfig.getInstance().node("disable-flight").getBoolean()) {
            event.setCancelled(true);
            Debug.info(player.getName() + " tried to fly, canceled");
        }
    }

    @SuppressWarnings("deprecation")
    @OnEvent
    public void onPlayerInteract(SPlayerInteractEvent event) {
        if (event.isCancelled() && event.getAction() != SPlayerInteractEvent.Action.RIGHT_CLICK_AIR) {
            return;
        }

        var player = event.getPlayer();
        if (PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
            var gPlayer = player.as(BedWarsPlayer.class);
            var game = gPlayer.getGame();
            if (event.getAction() == SPlayerInteractEvent.Action.RIGHT_CLICK_AIR || event.getAction() == SPlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
                if (game.getStatus() == GameStatus.WAITING || gPlayer.isSpectator()) {
                    Debug.info(player.getName() + " used item in lobby or as spectator");
                    event.setCancelled(true);
                    if (event.getMaterial().is(MainConfig.getInstance().node("items", "jointeam").getString("COMPASS"))) {
                        if (game.getStatus() == GameStatus.WAITING) {
                            var inv = game.getTeamSelectorInventory();
                            if (inv == null) {
                                return;
                            }
                            inv.openForPlayer(gPlayer);
                        } else if (gPlayer.isSpectator()) {
                            // TODO
                        }
                    } else if (event.getMaterial().is(MainConfig.getInstance().node("items", "startgame").getString("DIAMOND"))) {
                        if (game.getStatus() == GameStatus.WAITING && (player.hasPermission(BedWarsPermission.START_ITEM_PERMISSION.asPermission()))) {
                            if (game.checkMinPlayers()) {
                                game.gameStartItem = true;
                            } else {
                                player.sendMessage(Message.of(LangKeys.IN_GAME_ERRORS_VIP_NOT_ENOUGH_PLAYERS).prefixOrDefault(game.getCustomPrefixComponent()));
                            }
                        }
                    } else if (event.getMaterial().is(MainConfig.getInstance().node("items", "leavegame").getString("SLIME_BALL"))) {
                        game.leaveFromGame(gPlayer);
                    }
                } else if (game.getStatus() == GameStatus.RUNNING) {
                    if (event.getBlockClicked() != null) {
                        if (event.getBlockClicked().getType().isSameType("ender_chest")) {
                            var chest = event.getBlockClicked();
                            var team = game.getTeamOfChest(chest.getLocation());
                            event.setCancelled(true);

                            if (team == null) {
                                player.openInventory(game.getFakeEnderChest(gPlayer));
                                Debug.info(player.getName() + " opened personal ender chest");
                                return;
                            }

                            if (!team.getPlayers().contains(gPlayer)) {
                                player.sendMessage(Message.of(LangKeys.SPECIALS_TEAM_CHEST_NOT_YOURS).prefixOrDefault(game.getCustomPrefixComponent()));
                                Debug.info(player.getName() + " tried to open foreign team chest");
                                return;
                            }

                            var teamChestOpenEvent = new TeamChestOpenEventImpl(game, gPlayer, team);
                            EventManager.fire(teamChestOpenEvent);

                            if (teamChestOpenEvent.isCancelled()) {
                                return;
                            }

                            player.openInventory(team.getTeamChestInventory());
                            Debug.info(player.getName() + " opened team chest");
                        } else if (event.getBlockClicked().getBlockState().orElseThrow().holdsInventory()) {
                            var inventory = event.getBlockClicked().getBlockState().orElseThrow().getInventory().orElseThrow();
                            game.addChestForFutureClear(event.getBlockClicked().getLocation(), inventory);
                            Debug.info(player.getName() + " used chest in BedWars game");
                        } else if (event.getBlockClicked().getType().platformName().toLowerCase().contains("cake")) {
                            if (game.getConfigurationContainer().getOrDefault(ConfigurationContainer.CAKE_TARGET_BLOCK_EATING, Boolean.class, false)) {
                                if (game.getPlayerTeam(gPlayer).getTargetBlock().equals(event.getBlockClicked().getLocation())) {
                                    event.setCancelled(true);
                                } else {
                                    if (MainConfig.getInstance().node("disableCakeEating").getBoolean(true)) {
                                        event.setCancelled(true);
                                    }
                                    Debug.info(player.getName() + " is eating cake");
                                    for (var team : game.getActiveTeams()) {
                                        if (team.getTargetBlock().equals(event.getBlockClicked().getLocation())) {
                                            event.setCancelled(true);
                                            if (BedWarsPlugin.isLegacy()) {
                                                var type = event.getBlockClicked().getType();
                                                var data = type.legacyData();
                                                if (data == 0) {
                                                    game.getRegion().putOriginalBlock(event.getBlockClicked().getLocation(), event.getBlockClicked().getBlockState().orElseThrow());
                                                }
                                                data++;
                                                if (data >= 6) {
                                                    game.bedDestroyed(event.getBlockClicked().getLocation(), gPlayer, false, false, true);
                                                    event.getBlockClicked().setType(BlockTypeHolder.air());
                                                } else {
                                                    event.getBlockClicked().setType(type.withLegacyData(data));
                                                }
                                            } else {
                                                var cake = event.getBlockClicked().getType();
                                                if (cake.get("bites").map("0"::equals).orElse(true)) {
                                                    game.getRegion().putOriginalBlock(event.getBlockClicked().getLocation(), event.getBlockClicked().getBlockState().orElseThrow());
                                                }
                                                var bites = cake.get("bites").map(Integer::parseInt).orElse(0) + 1;
                                                cake = cake.with("bites", String.valueOf(bites));

                                                if (bites >= 6) {
                                                    game.bedDestroyed(event.getBlockClicked().getLocation(), event.getPlayer(), false, false, true);
                                                    event.getBlockClicked().setType(BlockTypeHolder.air());
                                                } else {
                                                    event.getBlockClicked().setType(cake);
                                                }
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

                if (event.getBlockClicked() != null) {
                    if (game.getRegion().isBedBlock(event.getBlockClicked().getBlockState().orElseThrow()) || event.getBlockClicked().getType().isSameType("respawn_anchor")) {
                        // prevent Essentials to set home in arena
                        event.setCancelled(true);

                        if (event.getAction() == SPlayerInteractEvent.Action.RIGHT_CLICK_BLOCK && game.getStatus() == GameStatus.RUNNING && !gPlayer.isSpectator()) {
                            var stack = event.getItem();
                            if (stack != null && stack.getAmount() > 0) {
                                boolean anchorFilled = false;
                                if (game.getConfigurationContainer().getOrDefault(ConfigurationContainer.ANCHOR_DECREASING, Boolean.class, false)
                                        && event.getBlockClicked().getType().isSameType("respawn_anchor")
                                        && game.getPlayerTeam(gPlayer).getTargetBlock().equals(event.getBlockClicked().getLocation())
                                        && event.getItem() != null && event.getItem().getMaterial().is("glowstone")) {
                                    Debug.info(player.getName() + " filled respawn anchor");
                                    var anchor = event.getBlockClicked().getType();
                                    int charges = anchor.get("charges").map(Integer::parseInt).orElse(0);
                                    charges++;
                                    if (charges <= 4) {
                                        anchorFilled = true;
                                        event.getBlockClicked().setType(anchor.with("charges", String.valueOf(charges)));
                                        stack.setAmount(stack.getAmount() - 1);

                                        event.getBlockClicked().getLocation().getWorld().playSound(Sound.sound(
                                                SpecialSoundKey.key(MainConfig.getInstance().node("target-block", "respawn-anchor", "sound", "charge").getString("block.respawn_anchor.charge")),
                                                Sound.Source.BLOCK,
                                                1,
                                                1
                                        ), event.getBlockClicked().getLocation().getX(), event.getBlockClicked().getLocation().getY(), event.getBlockClicked().getLocation().getZ());
                                    }
                                }

                                if (!anchorFilled && stack.getMaterial().block().isPresent()) {
                                    var face = event.getBlockFace();
                                    var block = event.getBlockClicked().getLocation().clone().add(face.getDirection()).getBlock();
                                    if (block.getType().isAir()) {
                                        var originalState = block.getBlockState().orElseThrow();
                                        block.setType(stack.getMaterial().block().orElseThrow());
                                        var bevent = new BlockPlaceEvent(block.as(Block.class), originalState.as(BlockState.class),
                                                event.getBlockClicked().as(Block.class), stack.as(ItemStack.class), player.as(Player.class), true); // bruh why are we doing this
                                        Bukkit.getPluginManager().callEvent(bevent);

                                        if (bevent.isCancelled()) {
                                            originalState.updateBlock(true, false);
                                        } else {
                                            stack.setAmount(stack.getAmount() - 1);
                                            // TODO get right block place sound
                                            block.getLocation().getWorld().playSound(Sound.sound(
                                                    SpecialSoundKey.key("minecraft:block.stone.place"),
                                                    Sound.Source.BLOCK, 1, 1
                                            ), block.getLocation().getX(), block.getLocation().getY(), block.getLocation().getZ());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } else if (event.getAction() == SPlayerInteractEvent.Action.LEFT_CLICK_BLOCK &&
                    game.getStatus() == GameStatus.RUNNING && !gPlayer.isSpectator()
                    && event.getBlockClicked() != null && event.getBlockClicked().getType().isSameType("dragon_egg")
                    && MainConfig.getInstance().node("disableDragonEggTeleport").getBoolean(true)) {
                event.setCancelled(true);
                Debug.info(player.getName() + " interacts with dragon egg");
                var blockBreakEvent = new BlockBreakEvent(event.getBlockClicked().as(Block.class), player.as(Player.class));
                Bukkit.getPluginManager().callEvent(blockBreakEvent);
                if (blockBreakEvent.isCancelled()) {
                    return;
                }
                if (blockBreakEvent.isDropItems()) {
                    event.getBlockClicked().breakNaturally();
                } else {
                    event.getBlockClicked().setType(BlockTypeHolder.air());
                }
            }
        }
    }

    @OnEvent
    public void onEntityInteract(SPlayerInteractEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }

        var player = event.getPlayer();
        if (PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
            var gPlayer = player.as(BedWarsPlayer.class);
            var game = gPlayer.getGame();
            if ((game.getStatus() == GameStatus.WAITING && !(event instanceof SPlayerInteractAtEntityEvent)) || gPlayer.isSpectator()) {
                event.setCancelled(true);
                Debug.info(player.getName() + " interacts with entity in lobby or as spectator");
            }
        }
    }

    @OnEvent
    public void onSleep(SPlayerBedEnterEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (PlayerManagerImpl.getInstance().isPlayerInGame(event.getPlayer())) {
            event.setCancelled(true);
        } else {
            for (var game : GameManagerImpl.getInstance().getGames()) {
                if (ArenaUtils.isInArea(event.getBed().getLocation(), game.getPos1(), game.getPos2())) {
                    event.setCancelled(true);
                    Debug.info(event.getPlayer().getName() + " tried to sleep");
                    return;
                }
            }
        }
    }

    @OnEvent
    public void onInventoryOpen(SPlayerInventoryOpenEvent event) {
        if (event.isCancelled()) {
            return;
        }

        var player = event.getPlayer();
        if (PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
            var gProfile = player.as(BedWarsPlayer.class);
            if (gProfile.getGame().getStatus() == GameStatus.RUNNING) {
                if (gProfile.isSpectator()) {
                    event.setCancelled(!event.getTopInventory().getType().is("player"));
                    Debug.info(player.getName() + " tried to open prohibited inventory");
                    return;
                }
                if (event.getTopInventory().getType().is("enchanting", "crafting", "anvil", "brewing", "furnace", "workbench")) {
                    if (!gProfile.getGame().getConfigurationContainer().getOrDefault(ConfigurationContainer.CRAFTING, Boolean.class, false)) {
                        event.setCancelled(true);
                        Debug.info(player.getName() + " tried to open prohibited inventory");
                    }
                }
            }
        }
    }

    @OnEvent(priority = org.screamingsandals.lib.event.EventPriority.HIGH)
    public void onInteractAtEntity(SPlayerInteractAtEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }

        var player = event.getPlayer();
        var entity = event.getClickedEntity();

        if (PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
            var gPlayer = player.as(BedWarsPlayer.class);
            var game = gPlayer.getGame();
            if (!(entity instanceof EntityLiving)) {
                return;
            }

            if (game.getStatus() != GameStatus.WAITING) {
                return;
            }
            var displayName = PlainTextComponentSerializer.plainText().serializeOrNull(entity.getCustomName());

            for (var team : game.getTeams()) {
                if (team.getName().equals(displayName)) {
                    event.setCancelled(true);
                    Debug.info(player.getName() + " selected his team with armor stand");
                    game.selectTeam(gPlayer, displayName);
                    return;
                }
            }
        } else if (player.hasPermission(BedWarsPermission.ADMIN_PERMISSION.asPermission())) {
            var value = JoinTeamCommand.TEAMS_IN_HAND.get(player.getUuid());
            if (value == null) {
                return;
            }

            event.setCancelled(true);

            if (!(entity instanceof EntityLiving)) {
                player.sendMessage(Message.of(LangKeys.ADMIN_TEAM_JOIN_ENTITY_ENTITY_NOT_COMPATIBLE).defaultPrefix());
                return;
            }

            var living = (EntityLiving) entity;
            living.setRemoveWhenFarAway(false);
            living.setCanPickupItems(false);
            living.setCustomName(Component.text(value.getName()).color(value.getColor().getTextColor()));
            living.setCustomNameVisible(MainConfig.getInstance().node("jointeam-entity-show-name").getBoolean(true));

            if (living.getEntityType().is("armor_stand")) {
                ArmorStandUtils.equip(living, value);
            }

            JoinTeamCommand.TEAMS_IN_HAND.remove(player.getUuid());
            player.sendMessage(Message.of(LangKeys.ADMIN_TEAM_JOIN_ENTITY_ENTITY_ADDED).defaultPrefix());
        }
    }

    @OnEvent(priority = EventPriority.HIGHEST)
    public void onChat(SPlayerChatEvent event) {
        if (event.isCancelled() || !MainConfig.getInstance().node("chat", "override").getBoolean()) {
            return;
        }

        var player = event.getSender();
        if (PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
            var gPlayer = player.as(BedWarsPlayer.class);
            var game = gPlayer.getGame();
            var team = game.getPlayerTeam(gPlayer);
            var message = event.getMessage();
            var spectator = gPlayer.isSpectator();

            var playerName = player.getName();
            var displayName = player.getDisplayName();
            var playerListName = player.getPlayerListName();

            String format = MainConfig.getInstance().node("chat", "format").getString("<%teamcolor%%name%§r> ");
            if (team != null) {
                format = format.replace("%teamcolor%", AdventureHelper.toLegacyColorCode(team.getColor().getTextColor()));
                format = format.replace("%team%", team.getName());
                format = format.replace("%coloredteam%", AdventureHelper.toLegacy(Component.text(team.getName()).color(team.getColor().getTextColor())));
            } else if (spectator) {
                format = format.replace("%teamcolor%", AdventureHelper.toLegacyColorCode(NamedTextColor.GRAY));
                format = format.replace("%team%", "SPECTATOR");
                format = format.replace("%coloredteam%", ChatColor.GRAY + "SPECTATOR");
            } else {
                format = format.replace("%teamcolor%", AdventureHelper.toLegacyColorCode(NamedTextColor.GRAY));
                format = format.replace("%team%", "");
                format = format.replace("%coloredteam%", AdventureHelper.toLegacyColorCode(NamedTextColor.GRAY));
            }
            format = format.replace("%name%", playerName);
            format = format.replace("%displayName%", AdventureHelper.toLegacy(displayName));
            format = format.replace("%playerListName%", AdventureHelper.toLegacyNullable(playerListName));

            if (VaultUtils.getInstance().isVault()) {
                format = format.replace("%prefix%", VaultUtils.getInstance().getPrefix(player));
                format = format.replace("%suffix%", VaultUtils.getInstance().getSuffix(player));
            }

            format = format.replace("%prefix%", "");
            format = format.replace("%suffix%", "");

            format = AdventureHelper.translateAlternateColorCodes('&', format);

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
            var recipients = event.getRecipients().iterator();
            while (recipients.hasNext()) {
                var recipient = recipients.next();
                var recipientGame = PlayerManagerImpl.getInstance().getGameOfPlayer(recipient);
                if (recipientGame.isEmpty() || recipientGame.get() != game) {
                    if ((game.getStatus() == GameStatus.WAITING && MainConfig.getInstance().node("chat", "separate-chat", "lobby").getBoolean())
                            || (game.getStatus() != GameStatus.WAITING && MainConfig.getInstance().node("chat", "separate-chat", "game").getBoolean())) {
                        recipients.remove();
                    }
                } else if (game.getPlayerTeam(recipient.as(BedWarsPlayer.class)) != team && teamChat) {
                    recipients.remove();
                }
            }

            for (var p : event.getRecipients()) {
                p.sendMessage(event.getFormat());
            }
            event.setCancelled(true);
        } else {
            if (MainConfig.getInstance().node("chat", "separate-chat", "lobby").getBoolean() || MainConfig.getInstance().node("chat", "separate-chat", "game").getBoolean()) {
                var recipients = event.getRecipients().iterator();
                while (recipients.hasNext()) {
                    var recipient = recipients.next();
                    var recipientGame = PlayerManagerImpl.getInstance().getGameOfPlayer(recipient);
                    if (recipientGame.isPresent()) {
                        if ((recipientGame.get().getStatus() == GameStatus.WAITING && MainConfig.getInstance().node("chat", "separate-chat", "lobby").getBoolean())
                                || (recipientGame.get().getStatus() != GameStatus.WAITING && MainConfig.getInstance().node("chat", "separate-chat", "game").getBoolean())) {
                            recipients.remove();
                        }
                    }
                }
            }
        }
    }

    @OnEvent
    public void onMove(SPlayerMoveEvent event) {
        if (event.isCancelled()) {
            return;
        }

        var player = event.getPlayer();
        if (PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
            var gPlayer = player.as(BedWarsPlayer.class);
            var game = gPlayer.getGame();
            if (game.getConfigurationContainer().getOrDefault(ConfigurationContainer.DAMAGE_WHEN_PLAYER_IS_NOT_IN_ARENA, Boolean.class, false) && game.getStatus() == GameStatus.RUNNING
                    && !gPlayer.isSpectator()) {
                if (!ArenaUtils.isInArea(event.getNewLocation(), game.getPos1(), game.getPos2())) {
                    var armor = player.getAttribute(AttributeTypeHolder.of("minecraft:generic.armor"));
                    var armorToughness = AttributeTypeHolder.ofOptional("minecraft:generic.armor_toughness").flatMap(player::getAttribute);
                    if (armor.isEmpty()) {
                        player.damage(5);
                    } else {
                        // this is not 100% accurate formula - armorToughness check contains weaponDamage which is hardcoded to 5 (4*5=20) but we don't know the weapon damage yet
                        var multiplier = (1.0 - Math.min(20.0, Math.max(armor.get().getValue() / 5.0, armor.get().getValue() - 20.0 / (armorToughness.map(AttributeHolder::getValue).orElse(0.0) + 8))) / 25.0);
                        if (multiplier < 1) {
                            multiplier = 2 - multiplier;
                        }
                        double weaponDamage = multiplier * 5.0;
                        player.damage(weaponDamage);
                    }
                    Debug.info(player.getName() + " is doing prohibited move, damaging");
                }
            } else if (MainConfig.getInstance().node("preventSpectatorFlyingAway").getBoolean() && gPlayer.isSpectator() && (game.getStatus() == GameStatus.RUNNING || game.getStatus() == GameStatus.GAME_END_CELEBRATING)) {
                if (!ArenaUtils.isInArea(event.getNewLocation(), game.getPos1(), game.getPos2())) {
                    event.setCancelled(true);
                    Debug.info(player.getName() + " is doing prohibited move, cancelling");
                }
            }
        }
    }

    @OnEvent
    public void onPlaceLiquid(SPlayerBucketEvent event) {
        if (event.isCancelled() || event.getAction() != SPlayerBucketEvent.Action.EMPTY) {
            return;
        }

        var player = event.getPlayer();
        if (PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
            var gPlayer = player.as(BedWarsPlayer.class);
            var game = gPlayer.getGame();
            var loc = event.getBlockClicked().getLocation();

            loc.add(event.getBlockFace().getDirection().normalize());

            var block = loc.getBlock();
            if (game.getStatus() == GameStatus.RUNNING) {
                if (block.getType().isAir() || game.getRegion().isBlockAddedDuringGame(block.getLocation())) {
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
            for (var game : GameManagerImpl.getInstance().getGames()) {
                if (game.getStatus() != GameStatus.DISABLED && ArenaUtils.isInArea(event.getBlockClicked().getLocation(), game.getPos1(), game.getPos2())) {
                    event.setCancelled(true);
                    Debug.info(player.getName() + " is doing prohibited actions in protected area while not playing BedWars");
                    return;
                }
            }
        }
    }

    @OnEvent
    public void onVehicleCreated(SVehicleCreateEvent event) {
        if (event.isCancelled()) {
            return;
        }

        for (var game : GameManagerImpl.getInstance().getGames()) {
            if (game.getStatus() == GameStatus.RUNNING || game.getStatus() == GameStatus.GAME_END_CELEBRATING) {
                if (ArenaUtils.isInArea(event.getEntity().getLocation(), game.getPos1(), game.getPos2())) {
                    EntitiesManagerImpl.getInstance().addEntityToGame(event.getEntity(), game);
                    break;
                }
            }
        }
    }

    @OnEvent
    public void onItemPickup(SPlayerPickupItemEvent event) {
        if (event.isCancelled()) {
            return;
        }

        var player = event.getPlayer();
        if (PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
            var gPlayer = player.as(BedWarsPlayer.class);
            var game = gPlayer.getGame();
            if (game.getStatus() == GameStatus.WAITING || gPlayer.isSpectator()) {
                event.setCancelled(true);
                Debug.info(player.getName() + " tried to pick up the item in lobby or as spectator");
            } else {
                for (var spawner : game.getSpawners()) {
                    spawner.remove(event.getItem());
                }
            }
        }
    }

    @OnEvent
    public void onPlayerSwapHandItems(SPlayerSwapHandItemsEvent event) {
        var player = event.getPlayer();
        if (PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
            var gPlayer = player.as(BedWarsPlayer.class);
            if (gPlayer.getGame().getStatus() == GameStatus.WAITING) {
                event.setCancelled(true);
                Debug.info(event.getPlayer().getName() + " tried to swap his hands in lobby, cancelling");
            }
        }
    }

    @OnEvent
    public void onItemMerge(SItemMergeEvent event) {
        if (event.isCancelled()) {
            return;
        }

        for (var game : GameManagerImpl.getInstance().getGames()) {
            if (game.getStatus() == GameStatus.RUNNING && game.getConfigurationContainer().getOrDefault(ConfigurationContainer.SPAWNER_DISABLE_MERGE, Boolean.class, false)) {
                if (ArenaUtils.isInArea(event.getEntity().getLocation(), game.getPos1(), game.getPos2()) || ArenaUtils.isInArea(event.getTarget().getLocation(), game.getPos1(), game.getPos2())) {
                    event.setCancelled(true);
                    return;
                }
            }
        }
    }
}
