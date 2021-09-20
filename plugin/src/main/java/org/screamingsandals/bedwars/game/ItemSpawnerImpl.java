package org.screamingsandals.bedwars.game;

import lombok.Getter;
import lombok.Setter;
import org.screamingsandals.bedwars.api.config.ConfigurationContainer;
import org.screamingsandals.bedwars.api.game.ItemSpawner;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.events.ResourceSpawnEventImpl;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ItemSpawnerImpl implements ItemSpawner<LocationHolder, ItemSpawnerTypeImpl, TeamImpl> {
    @Getter
    private final LocationHolder location;
    @Getter
    @Setter
    private ItemSpawnerTypeImpl itemSpawnerType;
    @Getter
    @Setter
    private String customName;
    @Getter
    @Setter
    private boolean hologramEnabled;
    @Getter
    @Setter
    private double baseAmountPerSpawn;
    @Getter
    @Setter
    private TeamImpl team;
    @Getter
    @Setter
    private int maxSpawnedResources;
    @Getter
    @Setter
    private boolean floatingBlockEnabled;
    @Getter
    @Setter
    private Hologram.RotationMode rotationMode;
    @Getter
    @Setter
    private HologramType hologramType;

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
    private boolean hypixelHolo;
    private volatile boolean firstTick = true;
    @Getter
    private Pair<Long, TaskerTime> currentInterval;
    private long elapsedTime;
    private long remainingTimeToSpawn;

    public ItemSpawnerImpl(LocationHolder location, ItemSpawnerTypeImpl itemSpawnerType, String customName,
                           boolean hologramEnabled, double baseAmountPerSpawn, TeamImpl team,
                           int maxSpawnedResources, boolean floatingBlockEnabled, Hologram.RotationMode rotationMode, HologramType hologramType) {
        this.location = location;
        this.itemSpawnerType = itemSpawnerType;
        this.customName = customName;
        this.baseAmountPerSpawn = baseAmountPerSpawn;
        this.hologramEnabled = hologramEnabled;
        this.team = team;
        this.maxSpawnedResources = maxSpawnedResources;
        this.floatingBlockEnabled = floatingBlockEnabled;
        this.rotationMode = rotationMode;
        this.hologramType = hologramType;
    }

    public int nextMaxSpawn(int calculated) {
        if (amountPerSpawn <= 0) {
            if (hologram != null && (!spawnerIsFullHologram || currentLevelOnHologram != amountPerSpawn)) {
                spawnerIsFullHologram = true;
                currentLevelOnHologram = amountPerSpawn;
                hologram.replaceLine(1, Message.of(LangKeys.IN_GAME_SPAWNER_NOT_ENOUGH_LEVEL).placeholder("levels", (currentLevelOnHologram * (-1)) + 1).asTextEntry(null));
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
                if (hypixelHolo) {
                    hologram.replaceLine(2, Message.of(LangKeys.IN_GAME_SPAWNER_FULL_HYPIXEL).asTextEntry(null));
                } else {
                    hologram.replaceLine(1, Message.of(LangKeys.IN_GAME_SPAWNER_FULL).asTextEntry(null));
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
                if (hypixelHolo) {
                    hologram.replaceLine(2, Message.of(LangKeys.IN_GAME_SPAWNER_FULL_HYPIXEL).asTextEntry(null));
                } else {
                    hologram.replaceLine(1, Message.of(LangKeys.IN_GAME_SPAWNER_FULL).asTextEntry(null));
                }
            }
            return calculated;
        }

        if (hologram != null && !spawnerIsFullHologram) {
            spawnerIsFullHologram = true;
            if (hypixelHolo) {
                hologram.replaceLine(2, Message.of(LangKeys.IN_GAME_SPAWNER_FULL_HYPIXEL).asTextEntry(null));
            } else {
                hologram.replaceLine(1, Message.of(LangKeys.IN_GAME_SPAWNER_FULL).asTextEntry(null));
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

    public void setTier(int tier) {
        this.tier = tier;
        if (hypixelHolo) {
            hologram.replaceLine(0, Message.of(LangKeys.IN_GAME_SPAWNER_TIER).placeholder("tier", this.tier).asTextEntry(null));
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
            if (hypixelHolo) {
                hologram.firstLine(Message.of(LangKeys.IN_GAME_SPAWNER_TIER).placeholder("tier", this.tier).asTextEntry(null));
                hologram.bottomLine(TextEntry.of(itemSpawnerType.getItemBoldName()));
            } else {
                hologram.firstLine(TextEntry.of(itemSpawnerType.getItemBoldName()));
            }

            if (countdownHologram) {
                hologram.bottomLine((
                                itemSpawnerType.getInterval() < 2 ? Message.of(LangKeys.IN_GAME_SPAWNER_EVERY_SECOND)
                                        : Message.of(hypixelHolo ? LangKeys.IN_GAME_SPAWNER_COUNTDOWN_HYPIXEL : LangKeys.IN_GAME_SPAWNER_COUNTDOWN).placeholder("seconds",
                                        itemSpawnerType.getInterval())
                        ).asTextEntry(null)
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
    }

    public void start(GameImpl game) {
        if (started) {
            return;
        }

        this.game = game;
        this.amountPerSpawn = this.baseAmountPerSpawn;
        this.tier = 1;
        this.hypixelHolo = hologramType == HologramType.HYPIXEL || (hologramType == HologramType.DEFAULT && game.getConfigurationContainer().getOrDefault(ConfigurationContainer.HYPIXEL_HOLOGRAMS, Boolean.class, false));

        if (team != null && game.isTeamActive(team) && game.getConfigurationContainer().getOrDefault(ConfigurationContainer.STOP_TEAM_SPAWNERS_ON_DIE, Boolean.class, false)) {
            disabled = true;
        }

        if (!disabled) {
            if (hologramEnabled && game.getConfigurationContainer().getOrDefault(ConfigurationContainer.SPAWNER_HOLOGRAMS, Boolean.class, false)) {
                prepareHolograms(game.getConnectedPlayers(), game.getConfigurationContainer().getOrDefault(ConfigurationContainer.SPAWNER_COUNTDOWN_HOLOGRAM, Boolean.class, false));
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

                    if (team != null && game.isTeamActive(team) && game.getConfigurationContainer().getOrDefault(ConfigurationContainer.STOP_TEAM_SPAWNERS_ON_DIE, Boolean.class, false)) {
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

                    resource.setAmount(nextMaxSpawn(resource.getAmount()));

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


        if (hologramEnabled && game.getConfigurationContainer().getOrDefault(ConfigurationContainer.SPAWNER_HOLOGRAMS, Boolean.class, false)
                && game.getConfigurationContainer().getOrDefault(ConfigurationContainer.SPAWNER_COUNTDOWN_HOLOGRAM, Boolean.class, false) ) {
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
                            if (hypixelHolo) {
                                if (currentInterval.first() > 1) {
                                    hologram.replaceLine(2, Message.of(LangKeys.IN_GAME_SPAWNER_COUNTDOWN_HYPIXEL).placeholder("seconds",  currentInterval.second().getBukkitTime(remainingTimeToSpawn) / 20).asTextEntry(null));
                                } else if (rerenderHologram) {
                                    hologram.replaceLine(2, Message.of(LangKeys.IN_GAME_SPAWNER_EVERY_SECOND).asTextEntry(null));
                                    rerenderHologram = false;
                                }
                            } else {
                                if (currentInterval.first() > 1) {
                                    hologram.replaceLine(1, Message.of(LangKeys.IN_GAME_SPAWNER_COUNTDOWN).placeholder("seconds",  currentInterval.second().getBukkitTime(remainingTimeToSpawn) / 20).asTextEntry(null));
                                } else if (rerenderHologram) {
                                    hologram.replaceLine(1, Message.of(LangKeys.IN_GAME_SPAWNER_EVERY_SECOND).asTextEntry(null));
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
}
