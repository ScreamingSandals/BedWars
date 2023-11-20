/*
 * Copyright (C) 2023 ScreamingSandals
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

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Tolerate;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.screamingsandals.bedwars.api.Team;
import org.screamingsandals.bedwars.api.config.GameConfigurationContainer;
import org.screamingsandals.bedwars.api.game.ItemSpawner;
import org.screamingsandals.bedwars.api.game.ItemSpawnerType;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.events.ResourceSpawnEventImpl;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.lib.entity.Entity;
import org.screamingsandals.lib.entity.ItemEntity;
import org.screamingsandals.lib.entity.Entities;
import org.screamingsandals.lib.event.EventManager;
import org.screamingsandals.lib.hologram.Hologram;
import org.screamingsandals.lib.hologram.HologramManager;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.item.builder.ItemStackFactory;
import org.screamingsandals.lib.tasker.DefaultThreads;
import org.screamingsandals.lib.tasker.Tasker;
import org.screamingsandals.lib.tasker.TaskerTime;
import org.screamingsandals.lib.tasker.task.Task;
import org.screamingsandals.lib.utils.Pair;
import org.screamingsandals.lib.utils.visual.TextEntry;
import org.screamingsandals.lib.world.Location;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class ItemSpawnerImpl implements ItemSpawner, SerializableGameComponent {
    @Getter
    private final Location location;
    @Getter
    @Setter
    private ItemSpawnerTypeImpl itemSpawnerType;
    @Getter
    @Setter
    private String customName = null;
    @Getter
    @Setter
    private boolean hologramEnabled = true;
    @Getter
    @Setter
    private double baseAmountPerSpawn = 1;
    @Getter
    @Setter
    private TeamImpl team = null;
    @Getter
    @Setter
    private int maxSpawnedResources = -1;
    @Getter
    @Setter
    private boolean floatingBlockEnabled = false;
    @Getter
    @Setter
    private Hologram.RotationMode rotationMode = Hologram.RotationMode.Y;
    @Getter
    @Setter
    private HologramType hologramType = HologramType.DEFAULT;
    @Getter
    @Setter
    private Pair<Long, TaskerTime> initialInterval = null;
    @Getter
    @Setter
    private @Nullable Double customSpread = null;

    @Getter
    @Setter
    private double amountPerSpawn;
    @Getter
    private Hologram hologram;
    @Getter
    private int tier;
    private final List<ItemEntity> spawnedItems = new ArrayList<>();
    private boolean spawnerIsFullHologram = false;
    private boolean rerenderHologram = false;
    private double currentLevelOnHologram = -1;
    private boolean started;
    private boolean disabled;
    private boolean certainPopularServerHolo;
    private volatile boolean firstTick = true;
    @Getter
    private Pair<Long, TaskerTime> currentInterval;
    private long elapsedTime;
    private long countdownDelay;
    private volatile long currentCycle;
    private boolean spawnerLockedFull;
    private Task runningTask;

    public ItemSpawnerImpl(Location location, ItemSpawnerTypeImpl itemSpawnerType) {
        this.location = location;
        this.itemSpawnerType = itemSpawnerType;
    }

    private int nextMaxSpawn(int calculated) {
        if (amountPerSpawn <= 0) {
            if (hologram != null && (!spawnerIsFullHologram || currentLevelOnHologram != amountPerSpawn)) {
                spawnerIsFullHologram = true;
                currentLevelOnHologram = amountPerSpawn;
                hologram.replaceLine(1, Message.of(LangKeys.IN_GAME_SPAWNER_NOT_ENOUGH_LEVEL).placeholder("levels", (currentLevelOnHologram * (-1)) + 1));
            }
            return 0;
        }

        if (maxSpawnedResources <= 0) {
            if (spawnerIsFullHologram && !rerenderHologram) {
                spawnerIsFullHologram = false;
                rerenderHologram = true;
            }
            return calculated;
        }

        /* Update spawned items */
        spawnedItems.removeIf(Entity::isDead);

        int spawned = getSpawnedItemsCount();

        if (spawned >= maxSpawnedResources) {
            spawnerLockedFull = true;
            if (hologram != null && !spawnerIsFullHologram) {
                spawnerIsFullHologram = true;
                if (certainPopularServerHolo) {
                    hologram.replaceLine(2, Message.of(LangKeys.IN_GAME_SPAWNER_FULL_CERTAIN_POPULAR_SERVER));
                } else {
                    hologram.replaceLine(1, Message.of(LangKeys.IN_GAME_SPAWNER_FULL));
                }
            }
            return 0;
        }

        if ((maxSpawnedResources - spawned) >= calculated) {
            if (spawnerIsFullHologram && !rerenderHologram) {
                rerenderHologram = true;
                spawnerIsFullHologram = false;
            } else if ((calculated + spawned) == maxSpawnedResources) {
                spawnerLockedFull = true;
                if (hologram != null) {
                    spawnerIsFullHologram = true;
                    if (certainPopularServerHolo) {
                        hologram.replaceLine(2, Message.of(LangKeys.IN_GAME_SPAWNER_FULL_CERTAIN_POPULAR_SERVER));
                    } else {
                        hologram.replaceLine(1, Message.of(LangKeys.IN_GAME_SPAWNER_FULL));
                    }
                }
            }
            return calculated;
        }

        spawnerLockedFull = true;
        if (hologram != null && !spawnerIsFullHologram) {
            spawnerIsFullHologram = true;
            if (certainPopularServerHolo) {
                hologram.replaceLine(2, Message.of(LangKeys.IN_GAME_SPAWNER_FULL_CERTAIN_POPULAR_SERVER));
            } else {
                hologram.replaceLine(1, Message.of(LangKeys.IN_GAME_SPAWNER_FULL));
            }
        }

        return maxSpawnedResources - spawned;
    }

    public void add(ItemEntity item) {
        if (maxSpawnedResources > 0 && !spawnedItems.contains(item)) {
            spawnedItems.add(item);
        }
    }

    public void remove(ItemEntity item) {
        if (maxSpawnedResources > 0 && spawnedItems.contains(item)) {
            spawnedItems.remove(item);
            if (spawnerIsFullHologram && maxSpawnedResources > getSpawnedItemsCount()) {
                spawnerIsFullHologram = false;
                rerenderHologram = true;
            }
        }
    }

    /**
     * Works only if maxSpawnedResources > 0
     */
    public int getSpawnedItemsCount() {
        return spawnedItems.stream().mapToInt(i -> i.getItem().getAmount()).sum();
    }

    @Override
    @Tolerate
    public void setItemSpawnerType(ItemSpawnerType spawnerType) {
        if (!(spawnerType instanceof ItemSpawnerTypeImpl)) {
            throw new IllegalArgumentException("Provided instance of item spawner type is not created by BedWars plugin!");
        }
        this.itemSpawnerType = (ItemSpawnerTypeImpl) spawnerType;
    }

    @Override
    @Tolerate
    public void setTeam(Team team) {
        if (!(team instanceof TeamImpl)) {
            throw new IllegalArgumentException("Provided instance of team is not created by BedWars plugin!");
        }
        this.team = (TeamImpl) team;
    }

    @Override
    public long getInitialIntervalTicks() {
        return initialInterval != null ? initialInterval.second().getBukkitTime(initialInterval.first()) : itemSpawnerType.getIntervalTicks();
    }

    @Override
    public void setInitialIntervalTicks(@Nullable Long ticks) {
        this.initialInterval = ticks == null ? null : Pair.of(ticks, TaskerTime.TICKS);
    }

    public void setTier(int tier) {
        this.tier = tier;
        if (certainPopularServerHolo) {
            hologram.replaceLine(0, Message.of(LangKeys.IN_GAME_SPAWNER_TIER).placeholder("tier", this.tier));
        }
    }

    @Override
    public long getIntervalTicks() {
        return currentInterval != null ? currentInterval.second().getBukkitTime(currentCycle - elapsedTime % currentCycle) : 0;
    }

    @Override
    public void setIntervalTicks(long ticks) {
        if (!started || disabled) {
            return;
        }
        changeInterval(Pair.of(ticks, TaskerTime.TICKS));
    }

    private void prepareHolograms(List<BedWarsPlayer> viewers, boolean countdownHologram) {
        try {
            Location loc;
            if (floatingBlockEnabled &&
                    MainConfig.getInstance().node("floating-generator", "enabled").getBoolean(true)) {
                loc = this.location.add(0, MainConfig.getInstance().node("floating-generator", "holo-height").getDouble(0.5), 0);
            } else {
                loc = this.location.add(0,
                        MainConfig.getInstance().node("spawner-holo-height").getDouble(0.25), 0);
            }
            hologram = HologramManager
                    .hologram(loc);
            if (certainPopularServerHolo) {
                hologram.firstLine(Message.of(LangKeys.IN_GAME_SPAWNER_TIER).placeholder("tier", this.tier));
                hologram.bottomLine(TextEntry.of(itemSpawnerType.getItemBoldName()));
            } else {
                hologram.firstLine(TextEntry.of(itemSpawnerType.getItemBoldName()));
            }

            if (countdownHologram) {
                var interval = this.getInitialIntervalTicks();
                hologram.bottomLine((
                        interval < 40 ? Message.of(LangKeys.IN_GAME_SPAWNER_EVERY_SECOND)
                                        : Message.of(certainPopularServerHolo ? LangKeys.IN_GAME_SPAWNER_COUNTDOWN_CERTAIN_POPULAR_SERVER : LangKeys.IN_GAME_SPAWNER_COUNTDOWN).placeholder("seconds",
                                interval / 20)
                        )
                );
            }

            if (floatingBlockEnabled &&
                    MainConfig.getInstance().node("floating-generator", "enabled").getBoolean(true)) {
                var materialName = itemSpawnerType.getItemType().location().asString();
                var indexOfUnderscore = materialName.indexOf("_");
                hologram
                        .item(
                                Objects.requireNonNullElseGet(ItemStackFactory
                                        .build(materialName.substring(0, (indexOfUnderscore != -1 ? indexOfUnderscore : materialName.length())) + "_BLOCK"),
                                        () -> ItemStackFactory.build(itemSpawnerType.getItemType()))
                        )
                        .itemPosition(Hologram.ItemPosition.BELOW)
                        .rotationMode(rotationMode)
                        .rotationTime(Pair.of(2, TaskerTime.TICKS));
            }

            hologram.show();

            viewers.forEach(hologram::addViewer);

        } catch (Throwable t) {
            t.printStackTrace();
            destroy();
        }
    }

    public void destroy() {
        if (runningTask != null) {
            runningTask.cancel();
            runningTask = null;
        }

        if (hologram != null) {
            hologram.destroy();
            hologram = null;
        }

        amountPerSpawn = baseAmountPerSpawn;
        spawnedItems.clear();
        started = false;
        disabled = false;
    }

    public void start(GameImpl game) {
        if (started) {
            return;
        }

        this.amountPerSpawn = this.baseAmountPerSpawn;
        this.tier = 0;
        this.certainPopularServerHolo = hologramType == HologramType.CERTAIN_POPULAR_SERVER || (hologramType == HologramType.DEFAULT && game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.USE_CERTAIN_POPULAR_SERVER_LIKE_HOLOGRAMS_FOR_SPAWNERS, false));

        if (team != null && !game.isTeamActive(team) && game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.STOP_TEAM_SPAWNERS_ON_DIE, false)) {
            disabled = true;
        }

        if (!disabled) {
            if (hologramEnabled && game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.SPAWNER_HOLOGRAMS, false)) {
                prepareHolograms(game.getConnectedPlayers(), game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.SPAWNER_COUNTDOWN_HOLOGRAM, false));
            }
        }

        started = true;
        firstTick = true;

        // Old-new synchronous and probably more optimized spawner logic

        if (disabled) {
            return;
        }

        this.currentInterval = Objects.requireNonNullElseGet(this.initialInterval, this.itemSpawnerType::getInterval);

        if (runningTask != null) {
            runningTask.cancel();
            runningTask = null;
        }

        // Precomputed options
        boolean resetFullSpawnerCountdownAfterPicking = game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.RESET_FULL_SPAWNER_COUNTDOWN_AFTER_PICKING, true);
        boolean useHolograms = hologramEnabled && game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.SPAWNER_HOLOGRAMS, false)
                && game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.SPAWNER_COUNTDOWN_HOLOGRAM, false);
        boolean stopTeamSpawnersOnDie = game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.STOP_TEAM_SPAWNERS_ON_DIE, false);
        this.currentCycle = currentInterval.second().getBukkitTime(currentInterval.getFirst());

        // Cycle
        this.elapsedTime = 0;
        this.countdownDelay = 0;
        this.spawnerLockedFull = false;
        this.runningTask = this.location.tasker().runRepeatedly(taskItself -> {
            if (this.firstTick) {
                this.firstTick = false;
                this.elapsedTime++;
                return;
            }

            if (team != null && !game.isTeamActive(team) && stopTeamSpawnersOnDie) {
                taskItself.cancel();
                return;
            }

            long elapsedTime = this.elapsedTime - this.countdownDelay;
            boolean preventSpawn = false;

            this.elapsedTime++;

            if (resetFullSpawnerCountdownAfterPicking && this.spawnerLockedFull) {
                this.spawnedItems.removeIf(Entity::isDead);
                if (this.maxSpawnedResources > getSpawnedItemsCount()) {
                    elapsedTime += this.countdownDelay;
                    this.countdownDelay = elapsedTime % currentCycle;
                    this.spawnerLockedFull = false;
                    preventSpawn = true;
                } else {
                    return;
                }
            }

            if (useHolograms && elapsedTime % 20 == 0) {
                long remainingTimeToSpawn = (currentCycle - elapsedTime % currentCycle) / 20;

                if (remainingTimeToSpawn == 0) {
                    remainingTimeToSpawn = currentCycle / 20;
                }

                if (!spawnerIsFullHologram) {
                    if (certainPopularServerHolo) {
                        if (currentInterval.first() > 1) {
                            hologram.replaceLine(2, Message.of(LangKeys.IN_GAME_SPAWNER_COUNTDOWN_CERTAIN_POPULAR_SERVER).placeholder("seconds",  currentInterval.second().getBukkitTime(remainingTimeToSpawn) / 20));
                        } else if (rerenderHologram) {
                            hologram.replaceLine(2, Message.of(LangKeys.IN_GAME_SPAWNER_EVERY_SECOND));
                            rerenderHologram = false;
                        }
                    } else {
                        if (currentInterval.first() > 1) {
                            hologram.replaceLine(1, Message.of(LangKeys.IN_GAME_SPAWNER_COUNTDOWN).placeholder("seconds",  currentInterval.second().getBukkitTime(remainingTimeToSpawn) / 20));
                        } else if (rerenderHologram) {
                            hologram.replaceLine(1, Message.of(LangKeys.IN_GAME_SPAWNER_EVERY_SECOND));
                            rerenderHologram = false;
                        }
                    }
                }
            }

            if (preventSpawn || elapsedTime % currentCycle != 0) {
                return;
            }

            var calculatedStack = (int) amountPerSpawn;

            /* fractional levels: have a weighted chance to increment */
            /* for example, 3.1 -> 10% chance for 4 and 90% chance for 3 */
            if ((amountPerSpawn % 1) != 0) {
                if (Math.random() < (amountPerSpawn % 1)) {
                    calculatedStack++;
                }
            }

            var resourceSpawnEvent = new ResourceSpawnEventImpl(game, this, itemSpawnerType, itemSpawnerType.getItem(calculatedStack));
            EventManager.fire(resourceSpawnEvent);

            if (resourceSpawnEvent.isCancelled()) {
                return;
            }

            var resource = resourceSpawnEvent.getResource();

            resource = resource.withAmount(nextMaxSpawn(resource.getAmount()));

            if (resource.getAmount() > 0) {
                var loc = this.location.add(0, 0.05, 0);
                var item = Objects.requireNonNull(Entities.dropItem(resource, loc));
                var spread = customSpread != null ? customSpread : itemSpawnerType.getSpread();
                if (spread != 1.0) {
                    item.setVelocity(item.getVelocity().multiply(spread));
                }
                item.setPickupDelay(0, TimeUnit.SECONDS);
                add(item);
            }
        }, 1, TaskerTime.TICKS);
    }

    public void changeInterval(Pair<Long, TaskerTime> time) {
        if (!started || disabled) {
            return;
        }

        this.currentInterval = time;
        this.currentCycle = time.second().getBukkitTime(time.first());
    }

    @Override
    public void saveTo(@NotNull ConfigurationNode node) throws SerializationException {
        node.node("location").set(MiscUtils.writeLocationToString(location));
        node.node("type").set(itemSpawnerType.getConfigKey());
        node.node("customName").set(customName);
        node.node("startLevel").set(baseAmountPerSpawn);
        node.node("hologramEnabled").set(hologramEnabled);
        if (team != null) {
            node.node("team").set(team.getName());
        };
        node.node("maxSpawnedResources").set(maxSpawnedResources);
        node.node("floatingEnabled").set(floatingBlockEnabled);
        node.node("rotationMode").set(rotationMode);
        node.node("hologramType").set(hologramType);
        if (initialInterval != null) {
            node.node("initialInterval", "value").set(initialInterval.first());
            node.node("initialInterval", "unit").set(initialInterval.second());
        } else {
            node.node("initialInterval").set(null);
        }
        node.node("customSpread").set(customSpread);
    }

    public static class Loader implements SerializableGameComponentLoader<ItemSpawnerImpl> {
        public static final Loader INSTANCE = new Loader();

        @Override
        @NotNull
        public Optional<ItemSpawnerImpl> load(@NotNull GameImpl game, @NotNull ConfigurationNode node) throws ConfigurateException {
            var spawnerType = node.node("type").getString();
            if (spawnerType == null) {
                throw new UnsupportedOperationException("Wrongly configured spawner type!");
            }
            var type = game.getGameVariant().getItemSpawnerType(spawnerType);
            if (type == null) {
                throw new UnsupportedOperationException("Wrongly configured spawner type!");
            }

            var spawner = new ItemSpawnerImpl(MiscUtils.readLocationFromString(game.getWorld(), Objects.requireNonNull(node.node("location").getString())), type);
            spawner.setCustomName(node.node("customName").getString());
            spawner.setHologramEnabled(node.node("hologramEnabled").getBoolean(true));
            spawner.setBaseAmountPerSpawn(node.node("startLevel").getDouble(1));
            spawner.setTeam(game.getTeamFromName(node.node("team").getString()));
            spawner.setMaxSpawnedResources(node.node("maxSpawnedResources").getInt(-1));
            spawner.setFloatingBlockEnabled(node.node("floatingEnabled").getBoolean());
            spawner.setRotationMode(Hologram.RotationMode.valueOf(node.node("rotationMode").getString("Y")));
            spawner.setHologramType(ItemSpawner.HologramType.valueOf(node.node("hologramType").getString("DEFAULT")));

            var initialIntervalValueNode = node.node("initialInterval", "value");
            var initialIntervalUnitNode = node.node("initialInterval", "unit");
            if (!initialIntervalValueNode.empty() && !initialIntervalUnitNode.empty()) {
                spawner.setInitialInterval(Pair.of(initialIntervalValueNode.getLong(1), initialIntervalUnitNode.get(TaskerTime.class)));
            }

            var customSpreadNode = node.node("customSpread");
            if (!customSpreadNode.empty()) {
                spawner.setCustomSpread(customSpreadNode.getDouble(type.getSpread()));
            }

            return Optional.of(spawner);
        }
    }
}
