/*
 * Copyright (C) 2025 ScreamingSandals
 *
 * This file is part of Screaming BedWars.
 *
 * Screaming BedWars is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Screaming BedWars is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Screaming BedWars. If not, see <https://www.gnu.org/licenses/>.
 */

package org.screamingsandals.bedwars.game;

import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;
import org.screamingsandals.bedwars.BedWarsPlugin;
import org.screamingsandals.bedwars.api.ArenaTime;
import org.screamingsandals.bedwars.api.Team;
import org.screamingsandals.bedwars.api.boss.StatusBar;
import org.screamingsandals.bedwars.api.config.GameConfigurationContainer;
import org.screamingsandals.bedwars.api.events.TargetInvalidationReason;
import org.screamingsandals.bedwars.api.game.GameCycle;
import org.screamingsandals.bedwars.api.game.LocalGame;
import org.screamingsandals.bedwars.api.game.GameStatus;
import org.screamingsandals.bedwars.api.game.target.Target;
import org.screamingsandals.bedwars.api.player.BWPlayer;
import org.screamingsandals.bedwars.api.special.SpecialItem;
import org.screamingsandals.bedwars.api.upgrades.UpgradeRegistry;
import org.screamingsandals.bedwars.api.upgrades.UpgradeStorage;
import org.screamingsandals.bedwars.api.utils.DelayFactory;
import org.screamingsandals.bedwars.boss.BossBarImpl;
import org.screamingsandals.bedwars.boss.XPBarImpl;
import org.screamingsandals.bedwars.commands.AdminCommand;
import org.screamingsandals.bedwars.commands.BedWarsPermission;
import org.screamingsandals.bedwars.config.GameConfigurationContainerImpl;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.config.RecordSave;
import org.screamingsandals.bedwars.utils.EconomyUtils;
import org.screamingsandals.bedwars.entities.EntitiesManagerImpl;
import org.screamingsandals.bedwars.events.*;
import org.screamingsandals.bedwars.game.target.*;
import org.screamingsandals.bedwars.inventories.TeamSelectorInventory;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.lib.debug.Debug;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.bedwars.player.PlayerManagerImpl;
import org.screamingsandals.bedwars.region.RegionImpl;
import org.screamingsandals.bedwars.sidebar.GameSidebar;
import org.screamingsandals.bedwars.statistics.PlayerStatisticManager;
import org.screamingsandals.bedwars.tab.TabManager;
import org.screamingsandals.bedwars.utils.*;
import org.screamingsandals.bedwars.variants.VariantImpl;
import org.screamingsandals.bedwars.variants.VariantManagerImpl;
import org.screamingsandals.lib.Server;
import org.screamingsandals.lib.api.types.server.EntityHolder;
import org.screamingsandals.lib.api.types.server.LocationHolder;
import org.screamingsandals.lib.block.BlockPlacement;
import org.screamingsandals.lib.block.Block;
import org.screamingsandals.lib.block.snapshot.BlockSnapshot;
import org.screamingsandals.lib.container.Container;
import org.screamingsandals.lib.container.type.InventoryType;
import org.screamingsandals.lib.economy.EconomyManager;
import org.screamingsandals.lib.entity.Entity;
import org.screamingsandals.lib.entity.ItemEntity;
import org.screamingsandals.lib.entity.LivingEntity;
import org.screamingsandals.lib.event.EventManager;
import org.screamingsandals.lib.event.player.PlayerBlockBreakEvent;
import org.screamingsandals.lib.healthindicator.HealthIndicator;
import org.screamingsandals.lib.item.ItemStack;
import org.screamingsandals.lib.item.builder.ItemStackFactory;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.npc.NPC;
import org.screamingsandals.lib.player.Players;
import org.screamingsandals.lib.player.Player;
import org.screamingsandals.lib.player.Sender;
import org.screamingsandals.lib.player.gamemode.GameMode;
import org.screamingsandals.lib.spectator.Color;
import org.screamingsandals.lib.spectator.Component;
import org.screamingsandals.lib.spectator.bossbar.BossBarColor;
import org.screamingsandals.lib.spectator.sound.SoundSource;
import org.screamingsandals.lib.spectator.sound.SoundStart;
import org.screamingsandals.lib.spectator.title.Title;
import org.screamingsandals.lib.tasker.DefaultThreads;
import org.screamingsandals.lib.tasker.Tasker;
import org.screamingsandals.lib.tasker.TaskerTime;
import org.screamingsandals.lib.tasker.task.Task;
import org.screamingsandals.lib.utils.ResourceLocation;
import org.screamingsandals.lib.visuals.Visual;
import org.screamingsandals.lib.world.Location;
import org.screamingsandals.lib.world.World;
import org.screamingsandals.lib.world.Worlds;
import org.screamingsandals.lib.world.chunk.Chunk;
import org.screamingsandals.lib.world.weather.WeatherType;
import org.spongepowered.configurate.ConfigurationNode;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class GameImpl implements LocalGame {
    private final @NotNull UUID uuid;

    @NotNull
    private GameCycle gameCycle = new GameCycleImpl(this);
    public boolean gameStartItem;
    public boolean forceGameToStart;
    private String name;
    private Location pos1;
    private Location pos2;
    private Location lobbySpawn;
    @Nullable
    private Location lobbyPos1;
    @Nullable
    private Location lobbyPos2;
    private Location specSpawn;
    private final List<TeamImpl> teams = new ArrayList<>();
    private final List<ItemSpawnerImpl> spawners = new ArrayList<>();
    private final Map<BedWarsPlayer, RespawnProtection> respawnProtectionMap = new HashMap<>();
    private double fee;
    private int pauseCountdown;
    private int gameTime;
    private int minPlayers;
    private final List<BedWarsPlayer> players = new ArrayList<>();
    private World world;
    private final List<GameStoreImpl> gameStore = new ArrayList<>();
    private WeatherType arenaWeather = null;
    private boolean preServerRestart = false;
    @Setter(AccessLevel.PROTECTED)
    private File file;
    @Nullable
    private String displayName;
    @Getter
    @UnknownNullability("Shouldn't be null unless the GameImpl object has been constructed using other methods than loadGame or createGame")
    private VariantImpl gameVariant;

    // STATUS
    private GameStatus previousStatus = GameStatus.DISABLED;
    private GameStatus status = GameStatus.DISABLED;
    private GameStatus afterRebuild = GameStatus.WAITING;
    private int countdown = -1, previousCountdown = -1;
    private int calculatedMaxPlayers;
    private Task task;
    private final List<TeamImpl> teamsInGame = new ArrayList<>();
    private final RegionImpl region = new RegionImpl();
    private TeamSelectorInventory teamSelectorInventory;
    private StatusBar statusbar;
    private final Map<Location, ItemStack[]> usedChests = new HashMap<>();
    private final List<SpecialItem> activeSpecialItems = new ArrayList<>();
    private final List<DelayFactory> activeDelays = new ArrayList<>();
    private final Map<BedWarsPlayer, Container> fakeEnderChests = new HashMap<>();
    private int postGameWaiting = 3;
    private GameSidebar experimentalBoard = null;
    private HealthIndicator healthIndicator = null;
    private final List<Visual<?>> otherVisuals = new ArrayList<>();

    @Getter(AccessLevel.PROTECTED)
    private final @NotNull List<@NotNull Chunk> chunksWithTickets = new ArrayList<>();

    private final GameConfigurationContainerImpl configurationContainer = new GameConfigurationContainerImpl();

    @Setter(AccessLevel.PROTECTED)
    private boolean preparing = false;

    public void removeEntity(Entity e) {
        if (ArenaUtils.isInArea(e.getLocation(), pos1, pos2)) {
            final var chunk = e.getLocation().getChunk();
            if (!chunk.isLoaded()) {
                chunk.load();
            }
            e.remove();
        }
    }


    public static WeatherType loadWeather(String weather) {
        try {
            if ("default".equalsIgnoreCase(weather)) {
                return null;
            }
            return WeatherType.ofNullable(weather);
        } catch (Exception e) {
            return null;
        }
    }

    public static BossBarColor loadBossBarColor(String color) {
        try {
            return BossBarColor.valueOf(color);
        } catch (Exception e) {
            return null;
        }
    }

    public static GameImpl createGame(String name) {
        UUID uuid;
        do {
            uuid = UUID.randomUUID();
        } while (GameManagerImpl.getInstance().getGame(uuid).isPresent());
        var game = new GameImpl(uuid);
        game.name = name;
        game.pauseCountdown = 60;
        game.gameTime = 3600;
        game.minPlayers = 2;
        game.setGameVariant(VariantManagerImpl.getInstance().getDefaultVariant());

        return game;
    }

    public static boolean isBungeeEnabled() {
        return MainConfig.getInstance().node("bungee", "enabled").getBoolean();
    }

    public void setWorld(World world) {
        if (this.world == null) {
            this.world = world;
        }
    }

    public boolean checkMinPlayers() {
        return players.size() >= getMinPlayers();
    }

    public int countPlayers() {
        return this.players.size();
    }

    public int countSpectators() {
        return (int) this.players.stream().filter(t -> t.isSpectator() && getPlayerTeam(t) == null).count();
    }

    public int countSpectating() {
        return (int) this.players.stream().filter(BedWarsPlayer::isSpectator).count();
    }

    public int countRespawnable() {
        return (int) this.players.stream().filter(t -> getPlayerTeam(t) != null).count();
    }

    public int countAlive() {
        return (int) this.players.stream().filter(t -> !t.isSpectator()).count();
    }

    @Override
    public List<GameStoreImpl> getGameStores() {
        return List.copyOf(gameStore);
    }

    public List<GameStoreImpl> getGameStoreList() {
        return gameStore;
    }

    @Override
    public @Nullable TeamImpl getTeamFromName(@Nullable String name) {
        if (name == null) {
            return null;
        }
        return teams.stream().filter(team1 -> team1.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public boolean isBlockAddedDuringGame(Location loc) {
        return status == GameStatus.RUNNING && region.isLocationModifiedDuringGame(loc);
    }

    @Deprecated
    public boolean isBlockAddedDuringGame(LocationHolder loc) {
        return isBlockAddedDuringGame(loc.as(Location.class));
    }

    public boolean blockPlace(BedWarsPlayer player, BlockPlacement block, BlockSnapshot replaced, ItemStack itemInHand) {
        if (status != GameStatus.RUNNING) {
            return false; // ?
        }
        if (player.isSpectator()) {
            return false;
        }
        if (BedWarsPlugin.isFarmBlock(block.block())) {
            return true;
        }
        if (!ArenaUtils.isInArea(block.location(), pos1, pos2)) {
            return false;
        }

        var event = new PlayerBuildBlockEventImpl(this, player, getPlayerTeam(player), block, replaced, itemInHand);
        EventManager.fire(event);

        if (event.isCancelled()) {
            return false;
        }

        if (!replaced.block().isAir()) {
            if (region.isLocationModifiedDuringGame(replaced.location())) {
                return true;
            } else if (BedWarsPlugin.isBreakableBlock(replaced.block()) || region.isLiquid(replaced.block())) {
                region.putOriginalBlockIfAbsent(block.location(), replaced);
            } else {
                return false;
            }
        }
        region.addBuiltDuringGame(block.location());

        return true;
    }

    public boolean blockBreak(BedWarsPlayer player, BlockPlacement block, PlayerBlockBreakEvent event) {
        if (status != GameStatus.RUNNING) {
            return false; // ?
        }
        if (player.isSpectator()) {
            return false;
        }
        if (BedWarsPlugin.isFarmBlock(block.block())) {
            return true;
        }
        if (!ArenaUtils.isInArea(block.location(), pos1, pos2)) {
            return false;
        }

        var breakEvent = new PlayerBreakBlockEventImpl(this, player, getPlayerTeam(player), block, true);
        EventManager.fire(breakEvent);

        if (breakEvent.isCancelled()) {
            return false;
        }

        if (region.isLocationModifiedDuringGame(block.location())) {
            region.removeBlockBuiltDuringGame(block.location());

            if (block.block().isSameType("ender_chest")) {
                var team = getTeamOfChest(block.location());
                if (team != null) {
                    team.removeTeamChest(block.location());
                    var message = Message.of(LangKeys.SPECIALS_TEAM_CHEST_BROKEN).prefixOrDefault(getCustomPrefixComponent());
                    for (BedWarsPlayer gp : team.getPlayers()) {
                        gp.sendMessage(message);
                    }

                    if (breakEvent.isDrops()) {
                        event.dropItems(false);
                        var builtItem = ItemStackFactory.build("ENDER_CHEST");
                        if (builtItem != null) {
                            player.getPlayerInventory().addItem(builtItem);
                        }
                    }
                }
            }

            if (!breakEvent.isDrops()) {
                try {
                    event.dropItems(false);
                } catch (Throwable tr) {
                    block.block(Block.air());
                }
            }
            return true;
        }

        var loc = block.location();
        if (region.isBedBlock(block.blockSnapshot())) {
            if (!region.isBedHead(block.blockSnapshot())) {
                loc = region.getBedNeighbor(block).location();
            }
        } else if (region.isDoorBlock(block.blockSnapshot())) {
            if (!region.isDoorBottomBlock(block.blockSnapshot())) {
                loc = loc.subtract(0, 1, 0);
            }
        }
        var targetTeam = getTeamOfTargetBlock(loc);
        if (targetTeam != null) {
            if (configurationContainer.getOrDefault(GameConfigurationContainer.TARGET_BLOCK_CAKE_DESTROY_BY_EATING, false) && block.block().isSameType("cake")) {
                return false; // when CAKES are in eating mode, don't allow to just break it
            } else {
                var pt = getPlayerTeam(player);
                if (pt == targetTeam) {
                    return false;
                }
                var result = internalProcessInvalidation(targetTeam, targetTeam.getTarget(), player, TargetInvalidationReason.TARGET_BLOCK_DESTROYED);
                if (result) {
                    try {
                        event.dropItems(false);
                    } catch (Throwable ignored) {
                    }
                }
                return result;
            }
        }
        if (BedWarsPlugin.isBreakableBlock(block.block())) {
            region.putOriginalBlockIfAbsent(block.location(), block.blockSnapshot());
            return true;
        }
        return false;
    }

    public @Nullable TeamImpl getTeamOfTargetBlock(@NotNull Location loc) {
        for (var team : teamsInGame) {
            if (team.getTarget() instanceof TargetBlockImpl && ((TargetBlockImpl) team.getTarget()).getTargetBlock().equals(loc)) {
                return team;
            }
        }
        return null;
    }

    public TeamImpl getPlayerTeam(BedWarsPlayer player) {
        return teamsInGame.stream()
                .filter(team -> team.getPlayers().contains(player))
                .findFirst()
                .orElse(null);
    }

    public boolean internalProcessInvalidation(@NotNull TeamImpl team, @NotNull Target target, @Nullable Player destroyer, @NotNull TargetInvalidationReason reason) {
        return internalProcessInvalidation(team, target, destroyer, reason, true, false);
    }

    public boolean internalProcessInvalidation(@NotNull TeamImpl team, @NotNull Target target, @Nullable Player destroyer, @NotNull TargetInvalidationReason reason, boolean putOriginalBlocks, boolean ignoreInvalidity) {
        if (!teamsInGame.contains(team) || (!ignoreInvalidity && !target.isValid())) {
            return false;
        }

        @Nullable Block type = null;
        if (target instanceof TargetBlockImpl) {
            var loc = ((TargetBlockImpl) target).getTargetBlock();
            type = loc.getBlock().block();
        }

        var preTargetInvalidatedEvent = new PreTargetInvalidatedEventImpl(
                this,
                team,
                target,
                reason,
                type,
                destroyer != null ? PlayerManagerImpl.getInstance().getPlayer(destroyer).orElseThrow() : null
        );
        EventManager.fire(preTargetInvalidatedEvent);

        if (preTargetInvalidatedEvent.isCancelled()) {
            return false;
        }

        if (target instanceof TargetBlockImpl) {
            var loc = ((TargetBlockImpl) target).getTargetBlock();
            var block = loc.getBlock();
            if (type.is("#beds")) {
                if (!region.isBedHead(block.blockSnapshot())) {
                    loc = region.getBedNeighbor(block).location();
                }

                if (putOriginalBlocks) {
                    region.putOriginalBlockIfAbsent(loc, block.blockSnapshot());
                    if (block.location().equals(loc)) {
                        var neighbor = region.getBedNeighbor(block);
                        region.putOriginalBlockIfAbsent(neighbor.location(), neighbor.blockSnapshot());
                    } else {
                        region.putOriginalBlockIfAbsent(block.location(), region.getBedNeighbor(block).blockSnapshot());
                    }
                }

                region.getBedNeighbor(block).alterBlockWithoutPhysics(Block.air());
                block.block(Block.air());
            } else if (type.is("#doors", "#tall_flowers")) {
                var neighbour = loc.add(0, "lower".equals(type.get("half")) ? 1 : -1, 0);
                var neighbourBlock = neighbour.getBlock();
                if (neighbourBlock.block().is("#doors", "#tall_flowers")) {
                    if (putOriginalBlocks) {
                        region.putOriginalBlockIfAbsent(neighbour, neighbourBlock.blockSnapshot());
                    }
                    neighbourBlock.alterBlockWithoutPhysics(Block.air());
                }
                if (putOriginalBlocks) {
                    region.putOriginalBlockIfAbsent(loc, block.blockSnapshot());
                }
                loc.getBlock().block(Block.air());
            } else {
                if (putOriginalBlocks) {
                    region.putOriginalBlockIfAbsent(loc, block.blockSnapshot());
                }
                loc.getBlock().block(Block.air());
            }

            ((TargetBlockImpl) target).setValid(false);
        } else if (target instanceof AExpirableTarget) {
            ((AExpirableTarget) target).setRemainingTime(0);
        }

        var postTargetInvalidatedEvent = PostTargetInvalidatedEventImpl.fromPre(preTargetInvalidatedEvent);
        EventManager.fire(postTargetInvalidatedEvent);

        return true;
    }

    public void internalJoinPlayer(BedWarsPlayer gamePlayer) {
        var joinEvent = new PlayerJoinEventImpl(this, gamePlayer);
        EventManager.fire(joinEvent);

        if (joinEvent.isCancelled()) {
            Debug.info(gamePlayer.getName() + " can't join to the game: event cancelled");
            String message = joinEvent.getCancelMessage();
            if (message != null && !message.equals("")) {
                gamePlayer.sendMessage(Component.fromLegacy(message));
            }
            gamePlayer.changeGame(null);
            return;
        }
        Debug.info(gamePlayer.getName() + " joined bedwars match " + name);

        boolean isEmpty = players.isEmpty();
        if (!players.contains(gamePlayer)) {
            players.add(gamePlayer);
        }
        SignUtils.updateSigns(this);

        if (PlayerStatisticManager.isEnabled()) {
            // Load
            PlayerStatisticManager.getInstance().getStatistic(gamePlayer);
        }

        var arenaTime = configurationContainer.getOrDefault(GameConfigurationContainer.ARENA_TIME, ArenaTime.WORLD);
        if (arenaTime.time >= 0) {
            gamePlayer.setPlayerTime(arenaTime.time, false);
        }

        if (arenaWeather != null) {
            gamePlayer.setPlayerWeather(arenaWeather);
        }

        if (TabManager.isEnabled()) {
            players.forEach(TabManager.getInstance()::modifyForPlayer);
        }

        if (MainConfig.getInstance().node("tab", "enabled").getBoolean() && MainConfig.getInstance().node("tab", "hide-foreign-players").getBoolean()) {
            Server.getConnectedPlayers().stream().filter(p -> PlayerManagerImpl.getInstance().getGameOfPlayer(p).orElse(null) != this).forEach(gamePlayer::hidePlayer);
            players.forEach(p -> p.showPlayer(gamePlayer));
        }

        if (status == GameStatus.WAITING) {
            Debug.info(gamePlayer.getName() + " moving to lobby");
            Message
                    .of(LangKeys.IN_GAME_JOIN)
                    .placeholder("name", gamePlayer.getDisplayName())
                    .placeholder("players", players.size())
                    .placeholder("maxplayers", calculatedMaxPlayers)
                    .prefixOrDefault(getCustomPrefixComponent())
                    .send(getConnectedPlayers().stream().map(Players::wrapPlayer).collect(Collectors.toList()));

            gamePlayer.teleport(lobbySpawn, () -> {
                gamePlayer.invClean(); // temp fix for inventory issues?
                SpawnEffects.spawnEffect(GameImpl.this, gamePlayer, "game-effects.lobbyjoin");

                if (configurationContainer.getOrDefault(GameConfigurationContainer.JOIN_RANDOM_TEAM_ON_JOIN, false)) {
                    joinRandomTeam(gamePlayer);
                }

                if (configurationContainer.getOrDefault(GameConfigurationContainer.TEAM_JOIN_ITEM_ENABLED, false)) {
                    int compassPosition = MainConfig.getInstance().node("hotbar", "selector").getInt(0);
                    if (compassPosition >= 0 && compassPosition <= 8) {
                        var compass = MainConfig.getInstance()
                                .readDefinedItem("jointeam", "COMPASS")
                                .withDisplayName(Message.of(LangKeys.IN_GAME_LOBBY_ITEMS_COMPASS_SELECTOR_TEAM).asComponent(gamePlayer));
                        gamePlayer.getPlayerInventory().setItem(compassPosition, compass);
                    }
                }

                int leavePosition = MainConfig.getInstance().node("hotbar", "leave").getInt(8);
                if (leavePosition >= 0 && leavePosition <= 8) {
                    var leave = MainConfig.getInstance()
                                    .readDefinedItem("leavegame", "SLIME_BALL")
                                    .withDisplayName(Message.of(LangKeys.IN_GAME_LOBBY_ITEMS_LEAVE_FROM_GAME_ITEM).asComponent(gamePlayer));
                    gamePlayer.getPlayerInventory().setItem(leavePosition, leave);
                }

                if (gamePlayer.hasPermission(BedWarsPermission.START_ITEM_PERMISSION.asPermission())) {
                    int vipPosition = MainConfig.getInstance().node("hotbar", "start").getInt(1);
                    if (vipPosition >= 0 && vipPosition <= 8) {
                        var startGame = MainConfig.getInstance()
                                        .readDefinedItem("startgame", "DIAMOND")
                                        .withDisplayName(Message.of(LangKeys.IN_GAME_LOBBY_ITEMS_START_GAME_ITEM).asComponent(gamePlayer));
                        gamePlayer.getPlayerInventory().setItem(vipPosition, startGame);
                    }
                }
            });

            if (isEmpty) {
                runTask();
            } else {
                statusbar.addPlayer(gamePlayer);
            }
        }

        if (status == GameStatus.RUNNING || status == GameStatus.GAME_END_CELEBRATING) {
            if (MainConfig.getInstance().node("tab", "enabled").getBoolean() && MainConfig.getInstance().node("tab", "hide-spectators").getBoolean()) {
                players.stream().filter(p -> p.isSpectator() && !isPlayerInAnyTeam(p)).forEach(gamePlayer::hidePlayer);
            }

            makeSpectator(gamePlayer, true);

            spawners.forEach(itemSpawner -> {
                if (itemSpawner.getHologram() != null) {
                    itemSpawner.getHologram().addViewer(gamePlayer);
                }
            });
            teamsInGame.forEach(currentTeam -> {
                if (currentTeam.getHologram() != null) {
                    currentTeam.getHologram().addViewer(gamePlayer);
                }
            });

            otherVisuals.forEach(visual -> visual.addViewer(gamePlayer));

            if (healthIndicator != null) {
                healthIndicator.addViewer(gamePlayer);
            }
        }

        if (experimentalBoard != null) {
            experimentalBoard.addPlayer(gamePlayer);
        }

        EventManager.fire(new PlayerJoinedEventImpl(this, gamePlayer, getPlayerTeam(gamePlayer)));
    }

    public void internalLeavePlayer(BedWarsPlayer gamePlayer) {
        if (status == GameStatus.DISABLED) {
            return;
        }

        var playerLeaveEvent = new PlayerLeaveEventImpl(this, gamePlayer, getPlayerTeam(gamePlayer));
        EventManager.fire(playerLeaveEvent);
        Debug.info(name + ": player  " + gamePlayer.getName() + " is leaving the game");

        if (experimentalBoard != null) {
            experimentalBoard.removePlayer(gamePlayer);
        }

        if (healthIndicator != null) {
            healthIndicator.removeTrackedPlayer(gamePlayer);
            healthIndicator.removeViewer(gamePlayer);
        }

        if (!gamePlayer.isSpectator()) {
            if (!preServerRestart) {
                Message.of(LangKeys.IN_GAME_LEAVE)
                        .prefixOrDefault(getCustomPrefixComponent())
                        .placeholder("name", gamePlayer.getDisplayName())
                        .placeholder("players", players.size())
                        .placeholder("maxplayers", calculatedMaxPlayers)
                        .send(players);
            }
        } else {
            if (gamePlayer.getSpectatorTarget() != null) {
                gamePlayer.setSpectatorTarget(null);
            }
        }

        players.remove(gamePlayer);
        SignUtils.updateSigns(this);

        if (status == GameStatus.WAITING) {
            SpawnEffects.spawnEffect(this, gamePlayer, "game-effects.lobbyleave");
        }

        if (TabManager.isEnabled()) {
            TabManager.getInstance().clear(gamePlayer);
            players.forEach(TabManager.getInstance()::modifyForPlayer);
        }

        if (MainConfig.getInstance().node("tab", "enabled").getBoolean() && MainConfig.getInstance().node("tab", "hide-foreign-players").getBoolean()) {
            players.forEach(p -> p.hidePlayer(gamePlayer));
        }

        statusbar.removePlayer(gamePlayer);
        spawners.forEach(spawner -> {
            if (spawner.getHologram() != null) {
                spawner.getHologram().removeViewer(gamePlayer);
            }
        });
        teamsInGame.forEach(team -> {
            if (team.getHologram() != null && !team.getPlayers().contains(gamePlayer)) {
                team.getHologram().removeViewer(gamePlayer);
            }
            if (team.getProtectHologram() != null && team.getPlayers().contains(gamePlayer)) {
                team.getProtectHologram().removeViewer(gamePlayer);
            }
        });
        otherVisuals.forEach(visual -> visual.removeViewer(gamePlayer));
        gamePlayer.restoreDefaultScoreboard();

        if (MainConfig.getInstance().node("mainlobby", "enabled").getBoolean()
                && !MainConfig.getInstance().node("bungee", "enabled").getBoolean()) {
            try {
                Location mainLobbyLocation = MiscUtils.readLocationFromString(
                        Objects.requireNonNull(Worlds.getWorld(MainConfig.getInstance().node("mainlobby", "world").getString())),
                        Objects.requireNonNull(MainConfig.getInstance().node("mainlobby", "location").getString())
                );
                gamePlayer.teleport(mainLobbyLocation);
                gamePlayer.mainLobbyUsed = true;
            } catch (Throwable t) {
                BedWarsPlugin.getInstance().getLogger().error("You didn't setup the mainlobby properly! Do it via commands, not directly in config.yml!");
            }
        }

        if (status == GameStatus.RUNNING || status == GameStatus.WAITING) {
            var team = getPlayerTeam(gamePlayer);
            if (team != null) {
                team.getPlayers().remove(gamePlayer);
                if (status == GameStatus.WAITING) {
                    if (team.getPlayers().isEmpty()) {
                        teamsInGame.remove(team);
                    }
                }
            }

            if (status == GameStatus.RUNNING) {
                dispatchRewardCommands("player-early-leave", gamePlayer, 0, team, gamePlayer.isSpectator(), null);
            }
        }

        if (PlayerStatisticManager.isEnabled()) {
            var playerStatisticManager = PlayerStatisticManager.getInstance();
            var statistic = playerStatisticManager.getStatistic(gamePlayer);
            playerStatisticManager.storeStatistic(statistic);

            playerStatisticManager.unloadStatistic(gamePlayer);
        }

        if (players.isEmpty()) {
            if (!preServerRestart) {
                EventManager.fire(new PlayerLastLeaveEventImpl(this, gamePlayer, playerLeaveEvent.getTeam()));
            }

            if (status != GameStatus.WAITING) {
                afterRebuild = GameStatus.WAITING;
                SignUtils.updateSigns(this);
                rebuild();
            } else {
                cancelTask();
            }
            countdown = -1;
            teamsInGame.clear();

            for (GameStoreImpl store : gameStore) {
                var villager = store.kill();
                if (villager != null) {
                    EntitiesManagerImpl.getInstance().removeEntityFromGame(villager);
                }
            }
        }
    }

    public void start() {
        if (status == GameStatus.DISABLED) {
            preparing = true;
            status = GameStatus.WAITING;
            countdown = -1;
            calculatedMaxPlayers = 0;
            for (TeamImpl team : teams) {
                calculatedMaxPlayers += team.getMaxPlayers();
            }
            Tasker.run(DefaultThreads.GLOBAL_THREAD, () -> SignUtils.updateSigns(this));

            if (MainConfig.getInstance().node("bossbar", "use-xp-bar").getBoolean(false)) {
                statusbar = new XPBarImpl();
            } else {
                statusbar = new BossBarImpl();
            }
            preparing = false;
            EventManager.fire(new GameEnabledEventImpl(this));
        }
    }

    public void stop() {
        if (status == GameStatus.DISABLED) {
            return; // Game is already stopped
        }
        var clonedPlayers = new ArrayList<>(players);
        for (BedWarsPlayer p : clonedPlayers) {
            p.changeGame(null);
        }
        if (status != GameStatus.REBUILDING) {
            status = GameStatus.DISABLED;
            SignUtils.updateSigns(this);
        } else {
            afterRebuild = GameStatus.DISABLED;
        }
        EventManager.fire(new GameDisabledEventImpl(this));
    }

    @Override
    public void joinToGame(@NotNull BWPlayer p) {
        if (!(p instanceof BedWarsPlayer)) {
            throw new IllegalArgumentException("Provided instance of player is not created by BedWars plugin!");
        }
        var player = (BedWarsPlayer) p;

        if (status == GameStatus.DISABLED) {
            return;
        }

        if (preparing) {
            Tasker.runDelayed(DefaultThreads.GLOBAL_THREAD, () -> joinToGame(player), 1L, TaskerTime.TICKS);
            return;
        }

        if (status == GameStatus.REBUILDING) {
            if (isBungeeEnabled()) {
                BungeeUtils.sendPlayerBungeeMessage(player, Message
                                .of(LangKeys.IN_GAME_ERRORS_GAME_IS_REBUILDING)
                                .prefixOrDefault(getCustomPrefixComponent())
                                .placeholder("arena", this.name)
                                .asComponent(player)
                                .toLegacy()
                        );
                BungeeUtils.movePlayerToBungeeServer(player, false, player.getHubServerName());
            } else {
                Message
                        .of(LangKeys.IN_GAME_ERRORS_GAME_IS_REBUILDING)
                        .placeholder("arena", this.name)
                        .prefixOrDefault(getCustomPrefixComponent())
                        .send(player);
            }
            return;
        }

        if ((status == GameStatus.RUNNING || status == GameStatus.GAME_END_CELEBRATING)
                && !configurationContainer.getOrDefault(GameConfigurationContainer.ALLOW_SPECTATOR_JOIN, false)) {
            if (isBungeeEnabled()) {
                BungeeUtils.sendPlayerBungeeMessage(player,
                        Message
                                .of(LangKeys.IN_GAME_ERRORS_GAME_ALREADY_RUNNING)
                                .placeholder("arena", this.name)
                                .prefixOrDefault(getCustomPrefixComponent())
                                .asComponent(player)
                                .toLegacy()
                );
                BungeeUtils.movePlayerToBungeeServer(player, false, player.getHubServerName());
            } else {
                Message
                        .of(LangKeys.IN_GAME_ERRORS_GAME_ALREADY_RUNNING)
                        .placeholder("arena", this.name)
                        .prefixOrDefault(getCustomPrefixComponent())
                        .send(player);
            }
            return;
        }

        if (players.size() >= calculatedMaxPlayers && status == GameStatus.WAITING) {
            if (player.canJoinFullGame()) {
                List<BedWarsPlayer> withoutVIP = getPlayersWithoutVIP();

                if (withoutVIP.isEmpty()) {
                    if (isBungeeEnabled()) {
                        BungeeUtils.sendPlayerBungeeMessage(player,
                            Message
                                    .of(LangKeys.IN_GAME_ERRORS_VIP_GAME_IS_FULL)
                                    .prefixOrDefault(getCustomPrefixComponent())
                                    .asComponent(player)
                                    .toLegacy()
                        );
                        BungeeUtils.movePlayerToBungeeServer(player, false, player.getHubServerName());
                    } else {
                        Message
                                .of(LangKeys.IN_GAME_ERRORS_VIP_GAME_IS_FULL)
                                .prefixOrDefault(getCustomPrefixComponent())
                                .send(player);
                    }
                    return;
                }

                BedWarsPlayer kickPlayer;
                if (withoutVIP.size() == 1) {
                    kickPlayer = withoutVIP.get(0);
                } else {
                    kickPlayer = withoutVIP.get(MiscUtils.randInt(0, players.size() - 1));
                }

                if (isBungeeEnabled()) {
                    BungeeUtils.sendPlayerBungeeMessage(kickPlayer,
                            Message
                                    .of(LangKeys.IN_GAME_ERRORS_GAME_KICKED_BY_VIP)
                                    .placeholder("arena", this.name)
                                    .prefixOrDefault(getCustomPrefixComponent())
                                    .asComponent(kickPlayer)
                                    .toLegacy()
                    );
                } else {
                    Message
                            .of(LangKeys.IN_GAME_ERRORS_GAME_KICKED_BY_VIP)
                            .placeholder("arena", this.name)
                            .prefixOrDefault(getCustomPrefixComponent())
                            .send(kickPlayer);
                }
                kickPlayer.changeGame(null);
            } else {
                if (isBungeeEnabled()) {
                    BungeeUtils.sendPlayerBungeeMessage(player,
                            Message
                                    .of(LangKeys.IN_GAME_ERRORS_GAME_IS_FULL)
                                    .placeholder("arena", GameImpl.this.name)
                                    .prefixOrDefault(getCustomPrefixComponent())
                                    .asComponent(player)
                                    .toLegacy()
                    );
                    BungeeUtils.movePlayerToBungeeServer(player, false, player.getHubServerName());
                } else {
                    Message
                            .of(LangKeys.IN_GAME_ERRORS_GAME_IS_FULL)
                            .placeholder("arena", this.name)
                            .prefixOrDefault(getCustomPrefixComponent())
                            .send(player);
                }
                return;
            }
        }

        if (MainConfig.getInstance().node("economy", "enabled").getBoolean(true) && EconomyManager.isAvailable()) {
            if (fee > 0) {
                if (!EconomyManager.withdrawPlayer(player, fee).isSuccessful()) {
                    if (isBungeeEnabled()) {
                        BungeeUtils.sendPlayerBungeeMessage(player,
                            Message.of(LangKeys.IN_GAME_ECONOMY_MISSING_COINS)
                                    .placeholder("coins", fee)
                                    .placeholder("currency", EconomyManager.getCurrencyNameSingular())
                                    .send(player)
                                    .asComponent(player)
                                    .toLegacy()
                        );
                        BungeeUtils.movePlayerToBungeeServer(player, false, player.getHubServerName());
                    } else {
                        Message.of(LangKeys.IN_GAME_ECONOMY_MISSING_COINS)
                                .placeholder("coins", fee)
                                .placeholder("currency", EconomyManager.getCurrencyNameSingular())
                                .send(player);
                    }
                    return;
                }
            }
        }

        player.changeGame(this);
    }

    @Override
    public void leaveFromGame(BWPlayer p) {
        if (!(p instanceof BedWarsPlayer)) {
            throw new IllegalArgumentException("Provided instance of player is not created by BedWars plugin!");
        }
        var player = (BedWarsPlayer) p;

        if (status == GameStatus.DISABLED) {
            return;
        }
        if (player.isInGame() && player.getGame() == this) {
            if (MainConfig.getInstance().node("economy", "enabled").getBoolean(true) && EconomyManager.isAvailable()) {
                if (fee > 0 && MainConfig.getInstance().node("economy", "return-fee").getBoolean(true)) {
                    EconomyUtils.deposit(player, fee);
                }
            }
            player.changeGame(null);
        }
    }

    public TeamImpl getFirstTeamThatIsntInGame() {
        for (TeamImpl team : teams) {
            if (!teamsInGame.contains(team)) {
                return team;
            }
        }
        return null;
    }

    public List<BedWarsPlayer> getPlayersInTeam(TeamImpl team) {
        return team.getPlayers();
    }

    public boolean internalTeamJoin(BedWarsPlayer player, TeamImpl teamForJoin, boolean ignoreTeamSize) {
        var cur = getPlayerTeam(player);
        var event = new PlayerJoinTeamEventImpl(this, player, teamForJoin, cur);
        EventManager.fire(event);

        if (event.isCancelled()) {
            return false;
        }

        if (cur == teamForJoin) {
            Message
                    .of(LangKeys.IN_GAME_TEAM_SELECTION_ALREADY_SELECTED)
                    .prefixOrDefault(getCustomPrefixComponent())
                    .placeholder("team", Component.text(teamForJoin.getName(), teamForJoin.getColor().getTextColor()))
                    .placeholder("players", teamForJoin.countConnectedPlayers())
                    .placeholder("maxplayers", teamForJoin.getMaxPlayers())
                    .send(player);
            return false;
        }
        if (!ignoreTeamSize && teamForJoin.countConnectedPlayers() >= teamForJoin.getMaxPlayers()) {
            if (cur != null) {
                Message
                        .of(LangKeys.IN_GAME_TEAM_SELECTION_FULL_NO_CHANGE)
                        .prefixOrDefault(getCustomPrefixComponent())
                        .placeholder("team", Component.text(teamForJoin.getName(), teamForJoin.getColor().getTextColor()))
                        .placeholder("oldteam", Component.text(cur.getName(), cur.getColor().getTextColor()))
                        .send(player);
            } else {
                Message
                        .of(LangKeys.IN_GAME_TEAM_SELECTION_FULL)
                        .prefixOrDefault(getCustomPrefixComponent())
                        .placeholder("team", Component.text(teamForJoin.getName(), teamForJoin.getColor().getTextColor()))
                        .send(player);
            }
            return false;
        }

        if (cur != null) {
            cur.getPlayers().remove(player);

            if (cur.getPlayers().isEmpty()) {
                teamsInGame.remove(cur);
            }
            Debug.info(name + ": player " + player.getName() + " left the team " + cur.getName());
        }

        teamForJoin.getPlayers().add(player);

        Debug.info(name + ": player " + player.getName() + " joined the team " + teamForJoin.getName());

        Message
                .of(LangKeys.IN_GAME_TEAM_SELECTION_SELECTED)
                .prefixOrDefault(getCustomPrefixComponent())
                .placeholder("team", Component.text(teamForJoin.getName(), teamForJoin.getColor().getTextColor()))
                .placeholder("players", teamForJoin.getPlayers().size())
                .placeholder("maxplayers", teamForJoin.getMaxPlayers())
                .send(player);

        if (this.status == GameStatus.WAITING) {
            if (configurationContainer.getOrDefault(GameConfigurationContainer.ADD_WOOL_TO_INVENTORY_ON_JOIN, false)) {
                int colorPosition = MainConfig.getInstance().node("hotbar", "color").getInt(1);
                if (colorPosition >= 0 && colorPosition <= 8) {
                    var item = ItemStackFactory.build(teamForJoin.getColor().material1_13 + "_WOOL",
                            builder -> builder.displayName(Component.text(teamForJoin.getName(), teamForJoin.getColor().getTextColor()))
                    );
                    player.getPlayerInventory().setItem(colorPosition, item);
                }
            }

            if (configurationContainer.getOrDefault(GameConfigurationContainer.COLORED_LEATHER_BY_TEAM_IN_LOBBY, false)) {
                var chestplate = ItemStackFactory.build("leather_chestplate", builder ->
                        builder.color(teamForJoin.getColor().getLeatherColor())
                );
                player.getPlayerInventory().setChestplate(chestplate);
            }
        }

        if (!teamsInGame.contains(teamForJoin)) {
            teamsInGame.add(teamForJoin);
        }

        if (TabManager.isEnabled()) {
            players.forEach(TabManager.getInstance()::modifyForPlayer);
        }

        EventManager.fire(new PlayerJoinedTeamEventImpl(this, player, teamForJoin, cur));

        return true;
    }

    public void joinRandomTeam(BedWarsPlayer player) {
        // TODO: add api event to allow manipulation with this process
        var teamForJoin = this.chooseRandomTeamForPlayerToJoin(false, false);

        if (teamForJoin == null) {
            return;
        }

        internalTeamJoin(player, teamForJoin, false);
    }

    public @Nullable TeamImpl chooseRandomTeamForPlayerToJoin(boolean ignoreTeamSize, boolean onlyActiveTeams) {
        @Nullable TeamImpl teamForJoin = null;
        if (!onlyActiveTeams && teamsInGame.size() < 2) {
            teamForJoin = getFirstTeamThatIsntInGame();
        } else {
            @Nullable TeamImpl lowest = null;

            for (var team : teamsInGame) {
                if (!ignoreTeamSize && team.getPlayers().size() >= team.getMaxPlayers()) {
                    continue; // skip full teams
                }

                if (lowest == null) {
                    lowest = team;
                }

                if (lowest.getPlayers().size() > team.getPlayers().size()) {
                    lowest = team;
                }
            }
            if (lowest != null) {
                teamForJoin = lowest;
            } else if (!onlyActiveTeams) {
                teamForJoin = getFirstTeamThatIsntInGame();
            }
        }

        return teamForJoin;
    }

    public Location makeSpectator(BedWarsPlayer gamePlayer, boolean leaveItem) {
        Debug.info(gamePlayer.getName() + " spawning as spectator");
        gamePlayer.setSpectator(true);
        gamePlayer.teleport(specSpawn, () -> {
            if (!configurationContainer.getOrDefault(GameConfigurationContainer.KEEP_INVENTORY_ON_DEATH, false) || leaveItem) {
                gamePlayer.invClean(); // temp fix for inventory issues?
            }
            gamePlayer.setAllowFlight(true);
            gamePlayer.setFlying(true);
            gamePlayer.setGameMode(GameMode.of("spectator"));

            if (leaveItem) {
                if (MainConfig.getInstance().node("tab", "enabled").getBoolean() && MainConfig.getInstance().node("tab", "hide-spectators").getBoolean()) {
                    players.forEach(p -> p.hidePlayer(gamePlayer));
                }

                int leavePosition = MainConfig.getInstance().node("hotbar", "leave").getInt(8);
                if (leavePosition >= 0 && leavePosition <= 8) {
                    var leave = MainConfig.getInstance()
                            .readDefinedItem("leavegame", "SLIME_BALL")
                            .withDisplayName(Message.of(LangKeys.IN_GAME_LOBBY_ITEMS_LEAVE_FROM_GAME_ITEM).asComponent(gamePlayer));
                    gamePlayer.getPlayerInventory().setItem(leavePosition, leave);
                }
            }

            if (TabManager.isEnabled()) {
                players.forEach(TabManager.getInstance()::modifyForPlayer);
            }

            if (healthIndicator != null) {
                healthIndicator.removeTrackedPlayer(gamePlayer);
            }

            Tasker.runDelayed(DefaultThreads.GLOBAL_THREAD, () -> {
                        if (!gamePlayer.getGameMode().is("spectator")) { // Fix Multiverse overriding our gamemode
                            gamePlayer.setGameMode(GameMode.of("spectator"));
                        }
                    }, 2, TaskerTime.TICKS);
        }, true);

        return specSpawn;
    }

    public void makePlayerFromSpectator(BedWarsPlayer gamePlayer) {
        Debug.info(gamePlayer.getName() + " changing spectator to regular player");
        var currentTeam = getPlayerTeam(gamePlayer);

        if (gamePlayer.getGame() == this && currentTeam != null) {
            gamePlayer.setSpectator(false);
            if (gamePlayer.getSpectatorTarget() != null) {
                gamePlayer.setSpectatorTarget(null);
            }
            gamePlayer.teleport(MiscUtils.findEmptyLocation(currentTeam.getRandomSpawn()), () -> {
                gamePlayer.setAllowFlight(false);
                gamePlayer.setFlying(false);
                gamePlayer.setGameMode(GameMode.of("survival"));

                if (MainConfig.getInstance().node("tab", "enabled").getBoolean() && MainConfig.getInstance().node("tab", "hide-spectators").getBoolean()) {
                    players.forEach(p -> p.showPlayer(gamePlayer));
                }

                if (configurationContainer.getOrDefault(GameConfigurationContainer.RESPAWN_PROTECTION_ENABLED, true)) {
                    RespawnProtection respawnProtection = addProtectedPlayer(gamePlayer);
                    respawnProtection.runProtection();
                }

                if (configurationContainer.getOrDefault(GameConfigurationContainer.PLAYER_RESPAWN_ITEMS_ENABLED, false)) {
                    var playerRespawnItems = configurationContainer.getOrDefault(GameConfigurationContainerImpl.PLAYER_RESPAWN_ITEMS_ITEMS, List.of());
                    if (!playerRespawnItems.isEmpty()) {
                        MiscUtils.giveItemsToPlayer(playerRespawnItems, gamePlayer, currentTeam.getColor());
                    } else {
                        Debug.warn("You have wrongly configured player-respawn-items.items!", true);
                    }
                }
                MiscUtils.giveItemsToPlayer(gamePlayer.getPermanentItemsPurchased(), gamePlayer, currentTeam.getColor());

                if (configurationContainer.getOrDefault(GameConfigurationContainer.KEEP_ARMOR_ON_DEATH, false)) {
                    final var armorContents = gamePlayer.getArmorContents();
                    if (armorContents != null) {
                        gamePlayer.getPlayerInventory().setArmorContents(armorContents);
                    }
                }

                if (TabManager.isEnabled()) {
                    players.forEach(TabManager.getInstance()::modifyForPlayer);
                }

                if (healthIndicator != null) {
                    healthIndicator.addTrackedPlayer(gamePlayer);
                }

                EventManager.fire(new PlayerRespawnedEventImpl(this, gamePlayer));
            });
        }
    }

    public void setBossbarProgress(int count, int max) {
        double progress = (double) count / (double) max;
        statusbar.setProgress((float) progress);
        if (statusbar instanceof XPBarImpl) {
            XPBarImpl xpbar = (XPBarImpl) statusbar;
            xpbar.setSeconds(count);
        }
    }

    public void rebuild() {
        if (healthIndicator != null) {
            healthIndicator.destroy();
            healthIndicator = null;
        }
        if (experimentalBoard != null) {
            experimentalBoard.destroy();
            experimentalBoard = null;
        }
        otherVisuals.forEach(visual -> {
            if (visual.shown()) {
                visual.destroy();
            }
        });
        otherVisuals.clear();
        Debug.info(name + ": rebuilding starts");
        teams.forEach(TeamImpl::destroy); // Not destroyed teams may not be in teamsInGame
        teamsInGame.clear();
        activeSpecialItems.clear();
        activeDelays.clear();

        EventManager.fire(new PreRebuildingEventImpl(this));

        for (ItemSpawnerImpl spawner : spawners) {
            spawner.destroy();
        }
        for (GameStoreImpl store : gameStore) {
            var villager = store.kill();
            if (villager != null) {
                EntitiesManagerImpl.getInstance().removeEntityFromGame(villager);
            }
        }

        region.regen();
        // Remove items
        for (Entity e : this.world.getEntities()) {
            if (ArenaUtils.isInArea(e.getLocation(), pos1, pos2)) {
                if (e instanceof ItemEntity) {
                    removeEntity(e);
                }
            }
        }

        // Chest clearing
        for (var entry : usedChests.entrySet()) {
            var location = entry.getKey();
            var chunk = location.getChunk();
            if (!chunk.isLoaded()) {
                chunk.load();
            }
            var block = location.getBlock();
            var contents = entry.getValue();

            var state = block.blockSnapshot();
            if (state != null && state.holdsInventory()) {
                state.getInventory().setContents(contents);
            }
        }
        usedChests.clear();

        // Clear fake ender chests
        for (var inv : fakeEnderChests.values()) {
            inv.clear();
        }
        fakeEnderChests.clear();

        // Remove remaining entities registered by other plugins
        for (var entity : EntitiesManagerImpl.getInstance().getEntities(this)) {
            var chunk = entity.getEntity().getLocation().getChunk();
            if (!chunk.isLoaded()) {
                chunk.load();
            }
            entity.getEntity().remove();
            EntitiesManagerImpl.getInstance().removeEntityFromGame(entity);
        }

        UpgradeRegistry.clearAll(this);

        if (!chunksWithTickets.isEmpty()) {
            for (var chunk : chunksWithTickets) {
                chunk.removePluginChunkTicket();
            }
            chunksWithTickets.clear();
        }

        EventManager.fire(new PostRebuildingEventImpl(this));

        this.status = this.afterRebuild;
        this.countdown = -1;
        SignUtils.updateSigns(this);
        cancelTask();
        Debug.info(name + ": rebuilding ends");
    }

    public boolean processRecord(TeamImpl t, int wonTime) {
        var record = RecordSave.getInstance().getRecord(this.getName());
        if (record.map(RecordSave.Record::getTime).orElse(Integer.MAX_VALUE) > wonTime) {
            RecordSave.getInstance().saveRecord(RecordSave.Record.builder()
                    .game(this.getName())
                    .time(wonTime)
                    .team(t.getName())
                    .winners(t.getPlayers().stream().map(Sender::getName).collect(Collectors.toList()))
                    .build()
            );
            return true;
        }
        return false;
    }

    public void runTask() {
        gameCycle.startGameCycle();
    }

    private void cancelTask() {
        gameCycle.endGameCycle();
    }

    public void selectTeam(BedWarsPlayer playerGameProfile, String displayName) {
        if (status == GameStatus.WAITING) {
            displayName = MiscUtils.stripColor(displayName);
            playerGameProfile.closeInventory();
            for (TeamImpl team : teams) {
                if (displayName.equals(team.getName())) {
                    internalTeamJoin(playerGameProfile, team, false);
                    break;
                }
            }
        }
    }

    public int getTimeLeft() {
        return this.countdown;
    }

    public String getFormattedTimeLeft() {
        return getFormattedTimeLeft(this.countdown);
    }

    public static @NotNull String getFormattedTimeLeft(int countdown) {
        int min;
        int sec;
        String minStr;
        String secStr;

        min = (int) Math.floor(countdown / 60.0);
        sec = countdown % 60;

        minStr = (min < 10) ? "0" + min : String.valueOf(min);
        secStr = (sec < 10) ? "0" + sec : String.valueOf(sec);

        return minStr + ":" + secStr;
    }

    @Override
    public void selectPlayerTeam(BWPlayer player, Team team) {
        if (!(player instanceof BedWarsPlayer)) {
            throw new IllegalArgumentException("Provided instance of player is not created by BedWars plugin!");
        }
        if (!(team instanceof TeamImpl)) {
            throw new IllegalArgumentException("Provided instance of team is not created by BedWars plugin!");
        }
        var bwPlayer = (BedWarsPlayer) player;
        if (!bwPlayer.isInGame() || bwPlayer.getGame() != this) {
            return;
        }

        selectTeam(bwPlayer, team.getName());
    }

    @Override
    public World getGameWorld() {
        return world;
    }

    @Override
    public Location getSpectatorSpawn() {
        return specSpawn;
    }

    @Override
    public int countConnectedPlayers() {
        return players.size();
    }

    @Override
    public List<BedWarsPlayer> getConnectedPlayers() {
        return List.copyOf(players);
    }

    @Override
    public List<TeamImpl> getAvailableTeams() {
        return List.copyOf(teams);
    }

    @Override
    public List<TeamImpl> getActiveTeams() {
        return List.copyOf(teamsInGame);
    }

    @Override
    public TeamImpl getTeamOfPlayer(BWPlayer player) {
        if (!(player instanceof BedWarsPlayer)) {
            throw new IllegalArgumentException("Provided instance of player is not created by BedWars plugin!");
        }
        var bwPlayer = (BedWarsPlayer) player;
        if (!bwPlayer.isInGame()) {
            return null;
        }
        return getPlayerTeam(bwPlayer);
    }

    @Override
    public boolean isLocationInArena(LocationHolder location) {
        if (location == null) {
            return false;
        }
        return ArenaUtils.isInArea(location.as(Location.class), pos1, pos2);
    }

    public boolean isLocationInArena(Location location) {
        return ArenaUtils.isInArea(location, pos1, pos2);
    }

    @Override
    public World getLobbyWorld() {
        if (lobbySpawn == null) return null;
        return lobbySpawn.getWorld();
    }

    @Override
    public int getLobbyCountdown() {
        return pauseCountdown;
    }

    @Override
    public TeamImpl getTeamOfChest(LocationHolder location) {
        if (location == null) {
            return null;
        }
        return getTeamOfChest(location.as(Location.class));
    }

    public TeamImpl getTeamOfChest(Location location) {
        for (var team : teamsInGame) {
            if (team.isTeamChestRegistered(location)) {
                return team;
            }
        }
        return null;
    }

    public void addChestForFutureClear(Location loc, Container inventory) {
        if (!usedChests.containsKey(loc)) {
            var contents = inventory.getContents();
            var clone = new ItemStack[contents.length];
            for (int i = 0; i < contents.length; i++) {
                var stack = contents[i];
                if (stack != null)
                    clone[i] = stack.clone();
            }
            usedChests.put(loc, clone);
        }
    }

    @Override
    public int getMaxPlayers() {
        return calculatedMaxPlayers;
    }

    @Override
    public int countGameStores() {
        return gameStore.size();
    }

    @Override
    public int countAvailableTeams() {
        return teams.size();
    }

    @Override
    public int countActiveTeams() {
        return teamsInGame.size();
    }

    @Override
    public boolean isPlayerInAnyTeam(BWPlayer player) {
        if (!(player instanceof BedWarsPlayer)) {
            throw new IllegalArgumentException("Provided instance of player is not created by BedWars plugin!");
        }
        return getTeamOfPlayer((BedWarsPlayer) player) != null;
    }

    @Override
    public boolean isTeamActive(Team team) {
        if (!(team instanceof TeamImpl)) {
            throw new IllegalArgumentException("Provided instance of team is not created by BedWars plugin!");
        }
        return teamsInGame.contains(team);
    }

    @Override
    public boolean isPlayerInTeam(BWPlayer player, Team team) {
        if (!(player instanceof BedWarsPlayer)) {
            throw new IllegalArgumentException("Provided instance of player is not created by BedWars plugin!");
        }
        if (!(team instanceof TeamImpl)) {
            throw new IllegalArgumentException("Provided instance of team is not created by BedWars plugin!");
        }
        return getTeamOfPlayer((BedWarsPlayer) player) == team;
    }

    @Override
    public int countTeamChests() {
        int total = 0;
        for (TeamImpl team : teamsInGame) {
            total += team.countTeamChests();
        }
        return total;
    }

    @Override
    public List<SpecialItem> getActiveSpecialItems() {
        return List.copyOf(activeSpecialItems);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <I extends SpecialItem> List<I> getActiveSpecialItems(Class<I> type) {
        return activeSpecialItems.stream()
                .filter(type::isInstance)
                .map(specialItem -> (I) specialItem)
                .collect(Collectors.toList());
    }

    @Override
    public List<SpecialItem> getActiveSpecialItemsOfTeam(Team team) {
        if (!(team instanceof TeamImpl)) {
            throw new IllegalArgumentException("Provided instance of team is not created by BedWars plugin!");
        }
        return activeSpecialItems.stream()
                .filter(item -> item.getTeam().equals(team))
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <I extends SpecialItem> List<I> getActiveSpecialItemsOfTeam(Team team, Class<I> type) {
        if (!(team instanceof TeamImpl)) {
            throw new IllegalArgumentException("Provided instance of team is not created by BedWars plugin!");
        }
        return activeSpecialItems.stream()
                .filter(item -> type.isInstance(item) && item.getTeam().equals(team))
                .map(item -> (I) item)
                .collect(Collectors.toList());
    }

    @Override
    public SpecialItem getFirstActiveSpecialItemOfTeam(Team team) {
        if (!(team instanceof TeamImpl)) {
            throw new IllegalArgumentException("Provided instance of team is not created by BedWars plugin!");
        }
        return activeSpecialItems.stream()
                .filter(item -> item.getTeam().equals(team))
                .findFirst()
                .orElse(null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <I extends SpecialItem> I getFirstActiveSpecialItemOfTeam(Team team, Class<I> type) {
        if (!(team instanceof TeamImpl)) {
            throw new IllegalArgumentException("Provided instance of team is not created by BedWars plugin!");
        }
        return (I) activeSpecialItems.stream()
                .filter(item -> item.getTeam().equals(team) && type.isInstance(item))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<SpecialItem> getActiveSpecialItemsOfPlayer(BWPlayer player) {
        if (!(player instanceof BedWarsPlayer)) {
            throw new IllegalArgumentException("Provided instance of player is not created by BedWars plugin!");
        }
        return activeSpecialItems.stream()
                .filter(item -> item.getPlayer().equals(player))
                .collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <I extends SpecialItem> List<I> getActiveSpecialItemsOfPlayer(BWPlayer player, Class<I> type) {
        if (!(player instanceof BedWarsPlayer)) {
            throw new IllegalArgumentException("Provided instance of player is not created by BedWars plugin!");
        }
        return activeSpecialItems.stream()
                .filter(item -> item.getPlayer().equals(player) && type.isInstance(item))
                .map(item -> (I) item)
                .collect(Collectors.toList());
    }

    @Override
    public SpecialItem getFirstActiveSpecialItemOfPlayer(BWPlayer player) {
        if (!(player instanceof BedWarsPlayer)) {
            throw new IllegalArgumentException("Provided instance of player is not created by BedWars plugin!");
        }
        return activeSpecialItems.stream()
                .filter(item -> item.getPlayer().equals(player))
                .findFirst()
                .orElse(null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <I extends SpecialItem> I getFirstActiveSpecialItemOfPlayer(BWPlayer player, Class<I> type) {
        if (!(player instanceof BedWarsPlayer)) {
            throw new IllegalArgumentException("Provided instance of player is not created by BedWars plugin!");
        }
        return (I) activeSpecialItems.stream()
                .filter(item -> item.getPlayer().equals(player) && type.isInstance(item))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void registerSpecialItem(SpecialItem item) {
        if (!activeSpecialItems.contains(item)) {
            activeSpecialItems.add(item);
        }
    }

    @Override
    public void unregisterSpecialItem(SpecialItem item) {
        activeSpecialItems.remove(item);
    }

    @Override
    public boolean isRegisteredSpecialItem(SpecialItem item) {
        return activeSpecialItems.contains(item);
    }

    @Override
    public List<DelayFactory> getActiveDelays() {
        return List.copyOf(activeDelays);
    }

    @Override
    public List<DelayFactory> getActiveDelaysOfPlayer(BWPlayer player) {
        if (!(player instanceof BedWarsPlayer)) {
            throw new IllegalArgumentException("Provided instance of player is not created by BedWars plugin!");
        }
        return activeDelays.stream()
                .filter(delayFactory -> delayFactory.getPlayer().equals(player))
                .collect(Collectors.toList());
    }

    @Override
    public DelayFactory getActiveDelay(BWPlayer player, Class<? extends SpecialItem> specialItem) {
        if (!(player instanceof BedWarsPlayer)) {
            throw new IllegalArgumentException("Provided instance of player is not created by BedWars plugin!");
        }
        return activeDelays.stream()
                .filter(delayFactory -> delayFactory.getPlayer().equals(player) && specialItem.isInstance(delayFactory.getSpecialItem()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void registerDelay(DelayFactory delayFactory) {
        if (!activeDelays.contains(delayFactory)) {
            activeDelays.add(delayFactory);
        }
    }

    @Override
    public void unregisterDelay(DelayFactory delayFactory) {
        activeDelays.remove(delayFactory);
    }

    @Override
    public boolean isDelayActive(BWPlayer player, Class<? extends SpecialItem> specialItem) {
        if (!(player instanceof BedWarsPlayer)) {
            throw new IllegalArgumentException("Provided instance of player is not created by BedWars plugin!");
        }
        return activeDelays.stream()
                .filter(delayFactory -> delayFactory.getPlayer().equals(player) && specialItem.isInstance(delayFactory.getSpecialItem()))
                .findFirst()
                .map(DelayFactory::isDelayActive)
                .orElse(false);
    }

    @Override
    @Nullable
    public WeatherType getArenaWeather() {
        return arenaWeather;
    }

    @Override
    public List<ItemSpawnerImpl> getItemSpawners() {
        return List.copyOf(spawners);
    }

    public void dispatchRewardCommands(@NotNull String type, @Nullable Player player, int score) {
        dispatchRewardCommands(type, player, score, null, null, null);
    }

    public void dispatchRewardCommands(@NotNull String type, @Nullable Player player, int score, @Nullable TeamImpl team, @Nullable Boolean deathStatus, TeamImpl.@Nullable Member member) {
        if (!MainConfig.getInstance().node("rewards", "enabled").getBoolean()) {
            return;
        }

        MainConfig.getInstance().node("rewards", type).childrenList()
                .stream()
                .map(ConfigurationNode::getString)
                .filter(Objects::nonNull)
                .map(command -> {
                    if (command.startsWith("/example ")) {
                        return null; // Skip example commands
                    }

                    if (player != null) {
                        command = command.replace("{player}", player.getName());
                        command = command.replace("{playerUuid}", player.getUniqueId().toString());
                    }
                    if (member != null) {
                        command = command.replace("{player}", member.getName());
                        command = command.replace("{playerUuid}", member.getUuid().toString());
                    }
                    command = command.replace("{game}", name);
                    command = command.replace("{score}", Integer.toString(score));
                    if (team != null) {
                        command = command.replace("{team}", team.getName());
                    }
                    if (deathStatus != null) {
                        command = command.replace("{death}", deathStatus ? "true" : "false");
                    }

                    return command;
                })
                .filter(Objects::nonNull)
                .map(s -> s.startsWith("/") ? s.substring(1) : s)
                .forEach(Server.getConsoleSender()::tryToDispatchCommand);
    }

    @Override
    public void selectPlayerRandomTeam(BWPlayer player) {
        if (!(player instanceof BedWarsPlayer)) {
            throw new IllegalArgumentException("Provided instance of player is not created by BedWars plugin!");
        }
        joinRandomTeam((BedWarsPlayer) player);
    }

    @Override
    public StatusBar getStatusBar() {
        return statusbar;
    }

    public void kickAllPlayers() {
        for (var player : getConnectedPlayers()) {
            leaveFromGame(player);
        }
    }

    @Override
    public boolean getBungeeEnabled() {
        return MainConfig.getInstance().node("bungee", "enabled").getBoolean();
    }

    @Override
    public boolean isEntityShop(EntityHolder entity) {
        if (entity == null) {
            return false;
        }
        var entityObj = entity.as(Entity.class);

        for (var store : gameStore) {
            if (entityObj.equals(store.getEntity())) {
                return true;
            }
        }
        return false;
    }

    public RespawnProtection addProtectedPlayer(BedWarsPlayer player) {
        int time = configurationContainer.getOrDefault(GameConfigurationContainer.RESPAWN_PROTECTION_TIME, 10);

        var respawnProtection = new RespawnProtection(this, player, time);
        respawnProtectionMap.put(player, respawnProtection);

        return respawnProtection;
    }

    public void removeProtectedPlayer(BedWarsPlayer player) {
        var respawnProtection = respawnProtectionMap.get(player);
        if (respawnProtection == null) {
            return;
        }

        try {
            respawnProtection.getTask().cancel();
        } catch (Exception ignored) {
        }

        respawnProtectionMap.remove(player);
    }

    @Override
    public boolean isProtectionActive(BWPlayer player) {
        if (!(player instanceof BedWarsPlayer)) {
            throw new IllegalArgumentException("Provided instance of player is not created by BedWars plugin!");
        }
        return (respawnProtectionMap.containsKey(player));
    }

    public List<BedWarsPlayer> getPlayersWithoutVIP() {
        List<BedWarsPlayer> gamePlayerList = new ArrayList<>(this.players);
        gamePlayerList.removeIf(BedWarsPlayer::canJoinFullGame);

        return gamePlayerList;
    }

    public Container getFakeEnderChest(BedWarsPlayer player) {
        if (!fakeEnderChests.containsKey(player)) {
            fakeEnderChests.put(player, Objects.requireNonNull(InventoryType.of("ender_chest").createContainer()));
        }
        return fakeEnderChests.get(player);
    }

    @Override
    public int getPostGameWaiting() {
        return this.postGameWaiting;
    }

    @Override
    public Component getCustomPrefixComponent() {
        return Component.fromMiniMessage(configurationContainer.getOrDefault(GameConfigurationContainer.PREFIX, "[BW]"));
    }

    @Override
    public @NotNull Component getDisplayNameComponent() {
        if (this.displayName != null && !this.displayName.isBlank()) {
            return Component.fromMiniMessage(this.displayName);
        } else {
            return Component.text(this.name);
        }
    }

    @Override
    public boolean invalidateTarget(Team team) {
        if (!(team instanceof TeamImpl)) {
            throw new IllegalArgumentException("Provided instance of team is not created by BedWars plugin!");
        }
        return internalProcessInvalidation((TeamImpl) team, team.getTarget(), null, TargetInvalidationReason.PLUGIN);
    }

    @Override
    public boolean isInEditMode() {
        return AdminCommand.gc.containsKey(name);
    }

    public void showOtherVisual(Visual<?> visual) {
        players.forEach(visual::addViewer);
        otherVisuals.add(visual);
    }

    public void removeOtherVisual(Visual<?> visual) {
        visual.destroy();
        otherVisuals.remove(visual);
    }

    public void setGameVariant(@NotNull VariantImpl gameVariant) {
        this.gameVariant = gameVariant;
        this.configurationContainer.setParentContainer(this.gameVariant.getConfigurationContainer());
    }

    public void ensurePlayersAreTeamed() {
        if (players.size() >= getMinPlayers()) {
            makePlayersJoinRandomTeams();
        }
    }

    public void forceTeamExistence() {
        for (var team : teams) {
            if (!teamsInGame.contains(team)) {
                team.setForced(true);
                teamsInGame.add(team);
                break;
            }
        }
    }

    public void makePlayersJoinRandomTeams() {
        for (BedWarsPlayer player : players) {
            if (getPlayerTeam(player) == null) {
                joinRandomTeam(player);
            }
        }
    }

    public boolean isAllowedToStart() {
        return players.size() >= getMinPlayers()
                && (
                teamsInGame.size() > 1
                        || (getConfigurationContainer().getOrDefault(GameConfigurationContainer.JOIN_RANDOM_TEAM_AFTER_LOBBY, false) && countRespawnable() < players.size())
        );
    }

    public void configureChunkTickets() {
        int minX = Math.min(pos1.getBlockX(), pos2.getBlockX()) >> 4;
        int maxX = Math.max(pos1.getBlockX(), pos2.getBlockX()) >> 4;

        int minZ = Math.min(pos1.getBlockZ(), pos2.getBlockZ()) >> 4;
        int maxZ = Math.max(pos1.getBlockZ(), pos2.getBlockZ()) >> 4;

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                var chunk = world.getChunkAt(x, z);
                if (chunk != null && chunk.addPluginChunkTicket()) {
                    chunksWithTickets.add(chunk);
                }
            }
        }
    }

    public List<TeamImpl> getTeamsAlive() {
        return teamsInGame.stream().filter(Team::isAlive).collect(Collectors.toList());
    }

    public boolean hasGameStatusChanged() {
        return previousStatus != status;
    }

    public void useGameStartItem() {
        if (players.size() > 1) {
            ensurePlayersAreTeamed();
            countdown = 0;
            gameStartItem = false;
        }
    }

    public void forceGameStart() {
        makePlayersJoinRandomTeams();

        if (teamsInGame.size() == 1) {
            forceTeamExistence();
        }

        forceGameToStart = false;
    }

    public void showPlayerCountdown() {
        showPlayerCountdown(countdown);
    }

    public void showPlayerCountdown(int countdown) {
        for (BedWarsPlayer player : players) {
            player.showTitle(Title.title(Component.text(Integer.toString(countdown), Color.YELLOW), Component.empty(), TitleUtils.defaultTimes()));
            player.playSound(
                    SoundStart.sound(
                            ResourceLocation.of(MainConfig.getInstance().node("sounds", "countdown", "sound").getString("ui.button.click")),
                            SoundSource.AMBIENT,
                            (float) MainConfig.getInstance().node("sounds", "countdown", "volume").getDouble(1),
                            (float) MainConfig.getInstance().node("sounds", "countdown", "pitch").getDouble(1)
                    )
            );
        }
    }

    protected void dispatchPlayerWinReward(@NotNull Player player, @NotNull TeamImpl winningTeam) {
        if (PlayerStatisticManager.isEnabled()) {
            var statistic = PlayerStatisticManager.getInstance().getStatistic(player);
            dispatchRewardCommands("player-win-run-immediately", player, statistic.getScore(), winningTeam, null, null);
        } else {
            dispatchRewardCommands("player-win-run-immediately", player, 0, winningTeam, null, null);
        }
        Tasker.runDelayed(DefaultThreads.GLOBAL_THREAD, () -> {
            if (PlayerStatisticManager.isEnabled()) {
                var statistic = PlayerStatisticManager.getInstance().getStatistic(player);
                dispatchRewardCommands("player-win", player, statistic.getScore(), winningTeam, null, null);
            } else {
                dispatchRewardCommands("player-win", player, 0, winningTeam, null, null);
            }
        }, (2 + postGameWaiting) * 20L, TaskerTime.TICKS);
    }

    protected void dispatchEndGameReward(Player player) {
        Tasker.runDelayed(DefaultThreads.GLOBAL_THREAD, () -> {
            if (PlayerStatisticManager.isEnabled()) {
                var statistic = PlayerStatisticManager.getInstance()
                        .getStatistic(player);
                dispatchRewardCommands("player-end-game", player, statistic.getScore());
            } else {
                dispatchRewardCommands("player-end-game", player, 0);
            }
        }, 40, TaskerTime.TICKS);
    }

    protected void handleBungeePostGame() {
        GameManagerImpl.getInstance().reselectGame();
        preServerRestart = false;

        if (!players.isEmpty()) {
            kickAllPlayers();
        }

        Tasker.runDelayed(DefaultThreads.GLOBAL_THREAD, () -> {
            if (MainConfig.getInstance().node("bungee", "serverRestart").getBoolean()) {
                EventManager.fire(new ServerRestartEventImpl());

                Server.getConsoleSender().tryToDispatchCommand("restart");
            } else if (MainConfig.getInstance().node("bungee", "serverStop").getBoolean()) {
                Server.shutdown();
            } else {
                preServerRestart = false;
            }
        }, 30, TaskerTime.TICKS);
    }

    public void spawnGameStores() {
        for (GameStoreImpl store : gameStore) {
            if (store.getEntity() != null) {
                continue;
            }

            var villager = store.spawn();
            if (villager instanceof LivingEntity) {
                EntitiesManagerImpl.getInstance().addEntityToGame((LivingEntity) villager, this);
                ((LivingEntity) villager).setAI(false);
                ((LivingEntity) villager).getLocation().getNearbyEntities(1).forEach(entity -> {
                    if (entity.getEntityType().equals(((LivingEntity) villager).getEntityType()) && entity.getLocation().getBlock().equals(((LivingEntity) villager).getLocation().getBlock()) && !villager.equals(entity)) {
                        entity.remove();
                    }
                });
            } else if (villager instanceof NPC) {
                otherVisuals.add((NPC) villager);
                players.forEach(((NPC) villager)::addViewer);
            }
        }
    }

    public void startGameSpawners() {
        for (ItemSpawnerImpl spawner : getSpawners()) {
            if (spawner.isStarted()) {
                continue;
            }

            spawner.start(this);

            UpgradeStorage storage = UpgradeRegistry.getUpgrade("spawner");
            if (storage != null) {
                storage.addUpgrade(this, spawner);
            }
        }
    }

    protected void removeUnusedTargetBlocks() {
        for (TeamImpl team : teams) {
            if (!teamsInGame.contains(team) && team.getTarget() instanceof TargetBlockImpl) {
                Location loc = ((TargetBlockImpl) team.getTarget()).getTargetBlock();
                BlockPlacement block = loc.getBlock();
                if (region.isBedBlock(block.blockSnapshot())) {
                    region.putOriginalBlock(block.location(), block.blockSnapshot());
                    var neighbor = region.getBedNeighbor(block);
                    region.putOriginalBlock(neighbor.location(), neighbor.blockSnapshot());
                    neighbor.alterBlockWithoutPhysics(Block.air());
                } else {
                    region.putOriginalBlock(loc, block.blockSnapshot());
                }
                block.block(Block.air());
            }
        }
    }

    protected void spawnTeamPlayerOnGameStart(BedWarsPlayer player, TeamImpl team) {
        player.teleport(team.getRandomSpawn(), () -> {
            player.setGameMode(GameMode.of("survival"));
            if (configurationContainer.getOrDefault(GameConfigurationContainer.GAME_START_ITEMS_ENABLED, false)) {
                var givenGameStartItems = configurationContainer.getOrDefault(GameConfigurationContainerImpl.GAME_START_ITEMS_ITEMS, List.of());
                if (!givenGameStartItems.isEmpty()) {
                    MiscUtils.giveItemsToPlayer(givenGameStartItems, player, team.getColor());
                } else {
                    Debug.warn("You have wrongly configured game-start-items.items!", true);
                }
            }
            SpawnEffects.spawnEffect(this, player, "game-effects.start");
            player.playSound(
                    SoundStart.sound(
                            ResourceLocation.of(MainConfig.getInstance().node("sounds", "game_start", "sound").getString("entity.player.levelup")),
                            SoundSource.AMBIENT,
                            (float) MainConfig.getInstance().node("sounds", "game_start", "volume").getDouble(1),
                            (float) MainConfig.getInstance().node("sounds", "game_start", "pitch").getDouble(1)
                    )
            );
        });
        team.getTeamMembers().add(new TeamImpl.Member(player.getUniqueId(), player.getName()));
    }

    protected void spawnSpectatorOnGameStart(BedWarsPlayer player) {
        var loc = makeSpectator(player, true);
        player.playSound(
                SoundStart.sound(
                        ResourceLocation.of(MainConfig.getInstance().node("sounds", "game_start", "sound").getString("entity.player.levelup")),
                        SoundSource.AMBIENT,
                        (float) MainConfig.getInstance().node("sounds", "game_start", "volume").getDouble(1),
                        (float) MainConfig.getInstance().node("sounds", "game_start", "pitch").getDouble(1)
                ),
                loc.getX(),
                loc.getY(),
                loc.getZ()
        );
    }

    protected void startHealthIndicator() {
        if (healthIndicator != null) {
            var healthIndicator = HealthIndicator.of()
                    .symbol(Component.text("\u2665", Color.RED))
                    .show()
                    .startUpdateTask(4, TaskerTime.TICKS);
            players.forEach(healthIndicator::addViewer);
            players.stream().filter(bedWarsPlayer -> !bedWarsPlayer.isSpectator()).forEach(healthIndicator::addTrackedPlayer);
            setHealthIndicator(healthIndicator);
        }
    }
}
