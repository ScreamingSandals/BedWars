/*
 * Copyright (C) 2022 ScreamingSandals
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
import org.screamingsandals.bedwars.api.Team;
import org.screamingsandals.bedwars.api.config.GameConfigurationContainer;
import org.screamingsandals.bedwars.api.game.ItemSpawner;
import org.screamingsandals.bedwars.api.game.ItemSpawnerType;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.events.ResourceSpawnEventImpl;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.lib.entity.EntityBasic;
import org.screamingsandals.lib.entity.EntityItem;
import org.screamingsandals.lib.entity.EntityMapper;
import org.screamingsandals.lib.event.EventManager;
import org.screamingsandals.lib.hologram.Hologram;
import org.screamingsandals.lib.hologram.HologramManager;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.item.builder.ItemFactory;
import org.screamingsandals.lib.tasker.Tasker;
import org.screamingsandals.lib.tasker.TaskerTime;
import org.screamingsandals.lib.tasker.task.TaskerTask;
import org.screamingsandals.lib.utils.Pair;
import org.screamingsandals.lib.utils.visual.TextEntry;
import org.screamingsandals.lib.world.LocationHolder;
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
    private final LocationHolder location;
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
    private double amountPerSpawn;
    @Getter
    private Hologram hologram;
    @Getter
    private int tier;
    private final List<EntityItem> spawnedItems = new ArrayList<>();
    private boolean spawnerIsFullHologram = false;
    private boolean rerenderHologram = false;
    private double currentLevelOnHologram = -1;
    private GameImpl game;
    private TaskerTask spawningTask;
    private TaskerTask hologramTask;
    private boolean started;
    private boolean disabled;
    private boolean certainPopularServerHolo;
    private volatile boolean firstTick = true;
    @Getter
    private Pair<Long, TaskerTime> currentInterval;
    private long elapsedTime;
    private long remainingTimeToSpawn;

    public ItemSpawnerImpl(LocationHolder location, ItemSpawnerTypeImpl itemSpawnerType) {
        this.location = location;
        this.itemSpawnerType = itemSpawnerType;
    }

    public int nextMaxSpawn(int calculated) {
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
        spawnedItems.removeIf(EntityBasic::isDead);

        int spawned = spawnedItems.size();

        if (spawned >= maxSpawnedResources) {
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
            } else if (hologram != null && (calculated + spawned) == maxSpawnedResources) {
                spawnerIsFullHologram = true;
                if (certainPopularServerHolo) {
                    hologram.replaceLine(2, Message.of(LangKeys.IN_GAME_SPAWNER_FULL_CERTAIN_POPULAR_SERVER));
                } else {
                    hologram.replaceLine(1, Message.of(LangKeys.IN_GAME_SPAWNER_FULL));
                }
            }
            return calculated;
        }

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

    public void add(EntityItem item) {
        if (maxSpawnedResources > 0 && !spawnedItems.contains(item)) {
            spawnedItems.add(item);
        }
    }

    public void remove(EntityItem item) {
        if (maxSpawnedResources > 0 && spawnedItems.contains(item)) {
            spawnedItems.remove(item);
            if (spawnerIsFullHologram && maxSpawnedResources > spawnedItems.size()) {
                spawnerIsFullHologram = false;
                rerenderHologram = true;
            }
        }
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

    public void setTier(int tier) {
        this.tier = tier;
        if (certainPopularServerHolo) {
            hologram.replaceLine(0, Message.of(LangKeys.IN_GAME_SPAWNER_TIER).placeholder("tier", this.tier));
        }
    }

    @Override
    public long getIntervalTicks() {
        return currentInterval != null ? currentInterval.second().getBukkitTime(remainingTimeToSpawn) : 0;
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
            LocationHolder loc;
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
                hologram.bottomLine((
                                itemSpawnerType.getInterval() < 2 ? Message.of(LangKeys.IN_GAME_SPAWNER_EVERY_SECOND)
                                        : Message.of(certainPopularServerHolo ? LangKeys.IN_GAME_SPAWNER_COUNTDOWN_CERTAIN_POPULAR_SERVER : LangKeys.IN_GAME_SPAWNER_COUNTDOWN).placeholder("seconds",
                                        itemSpawnerType.getInterval())
                        )
                );
            }

            if (floatingBlockEnabled &&
                    MainConfig.getInstance().node("floating-generator", "enabled").getBoolean(true)) {
                var materialName = itemSpawnerType.getItemType().platformName();
                var indexOfUnderscore = itemSpawnerType.getItemType().platformName().indexOf("_");
                hologram
                        .item(
                                ItemFactory
                                        .build(materialName.substring(0, (indexOfUnderscore != -1 ? indexOfUnderscore : materialName.length())) + "_BLOCK")
                                        .or(() -> ItemFactory.build(itemSpawnerType.getItemType()))
                                        .orElseThrow()
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
        if (hologramTask != null) {
            hologramTask.cancel();
            hologramTask = null;
        }

        if (spawningTask != null) {
            spawningTask.cancel();
            spawningTask = null;
        }

        if (hologram != null) {
            hologram.destroy();
            hologram = null;
        }

        game = null;
        amountPerSpawn = baseAmountPerSpawn;
        spawnedItems.clear();
        firstTick = true;
        started = false;
        disabled = false;
    }

    public void start(GameImpl game) {
        if (started) {
            return;
        }

        this.game = game;
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

        changeInterval(Pair.of((long) itemSpawnerType.getInterval(), TaskerTime.SECONDS));
    }

    public void changeInterval(Pair<Long, TaskerTime> time) {
        if (!started || disabled) {
            return;
        }

        if (hologramTask != null) {
            hologramTask.cancel();
            hologramTask = null;
        }

        if (spawningTask != null) {
            spawningTask.cancel();
            spawningTask = null;
        }

        this.currentInterval = time;

        spawningTask = Tasker
                .build(() -> {
                    if (firstTick) {
                        firstTick = false;
                        return;
                    }

                    if (team != null && !game.isTeamActive(team) && game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.STOP_TEAM_SPAWNERS_ON_DIE, false)) {
                        return;
                    }

                    var calculatedStack = (int) amountPerSpawn;

                    /* Allow half level */
                    if ((amountPerSpawn % 1) != 0) {
                        int a = (int) Math.round(Math.random());
                        if ((a % 2) == 0) {
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
                        var item = EntityMapper.dropItem(resource, loc).orElseThrow();
                        var spread = itemSpawnerType.getSpread();
                        if (spread != 1.0) {
                            item.setVelocity(item.getVelocity().multiply(spread));
                        }
                        item.setPickupDelay(0, TimeUnit.SECONDS);
                        add(item);
                    }
                })
                .repeat(currentInterval.first(), currentInterval.second())
                .start();


        if (hologramEnabled && game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.SPAWNER_HOLOGRAMS, false)
                && game.getConfigurationContainer().getOrDefault(GameConfigurationContainer.SPAWNER_COUNTDOWN_HOLOGRAM, false) ) {
            hologramTask = Tasker.build(() -> {
                        if (disabled || firstTick) {
                            return;
                        }

                        remainingTimeToSpawn = currentInterval.first() - elapsedTime;

                        if (remainingTimeToSpawn == 0) {
                            elapsedTime = 0;
                            remainingTimeToSpawn = currentInterval.first();
                        }

                        elapsedTime++;

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
                    })
                    .async()
                    .delay(5, TaskerTime.TICKS)
                    .repeat(20, TaskerTime.TICKS)
                    .start();
        }
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

            return Optional.of(spawner);
        }
    }
}
