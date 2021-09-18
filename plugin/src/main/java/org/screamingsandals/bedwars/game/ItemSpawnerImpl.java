package org.screamingsandals.bedwars.game;

import lombok.Getter;
import lombok.Setter;
import org.screamingsandals.bedwars.api.config.ConfigurationContainer;
import org.screamingsandals.bedwars.api.game.ItemSpawner;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.events.ResourceSpawnEventImpl;
import org.screamingsandals.bedwars.lang.LangKeys;
import org.screamingsandals.lib.entity.EntityBasic;
import org.screamingsandals.lib.entity.EntityItem;
import org.screamingsandals.lib.entity.EntityMapper;
import org.screamingsandals.lib.event.EventManager;
import org.screamingsandals.lib.hologram.Hologram;
import org.screamingsandals.lib.hologram.HologramManager;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.item.builder.ItemFactory;
import org.screamingsandals.lib.player.PlayerWrapper;
import org.screamingsandals.lib.tasker.Tasker;
import org.screamingsandals.lib.tasker.TaskerTime;
import org.screamingsandals.lib.tasker.task.TaskerTask;
import org.screamingsandals.lib.utils.Pair;
import org.screamingsandals.lib.utils.visual.TextEntry;
import org.screamingsandals.lib.world.LocationHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ItemSpawnerImpl implements ItemSpawner<LocationHolder, ItemSpawnerTypeImpl, Team> {
    @Getter
    private final LocationHolder location;
    @Getter
    private final ItemSpawnerTypeImpl itemSpawnerType;
    @Getter
    private final String customName;
    @Getter
    private final boolean hologramEnabled;
    @Getter
    private final double startLevel;
    @Getter
    @Setter
    private Team team;
    @Getter
    private final int maxSpawnedResources;
    @Getter
    private final boolean floatingBlockEnabled;
    @Getter
    private final Hologram.RotationMode rotationMode;

    @Getter
    @Setter
    private double currentLevel;
    @Getter
    private Hologram hologram;
    private final List<EntityItem> spawnedItems = new ArrayList<>();
    private boolean spawnerIsFullHologram = false;
    private boolean rerenderHologram = false;
    private double currentLevelOnHologram = -1;
    private GameImpl game;
    private TaskerTask spawningTask;
    private TaskerTask hologramTask;
    private boolean started;
    private boolean disabled;
    private volatile boolean firstTick = true;
    @Getter
    private Pair<Integer, TaskerTime> currentInterval;
    private int elapsedTime;
    private int remainingTimeToSpawn;

    public ItemSpawnerImpl(LocationHolder location, ItemSpawnerTypeImpl itemSpawnerType, String customName,
                           boolean hologramEnabled, double startLevel, Team team,
                           int maxSpawnedResources, boolean floatingBlockEnabled, Hologram.RotationMode rotationMode) {
        this.location = location;
        this.itemSpawnerType = itemSpawnerType;
        this.customName = customName;
        this.startLevel = startLevel;
        this.hologramEnabled = hologramEnabled;
        this.team = team;
        this.maxSpawnedResources = maxSpawnedResources;
        this.floatingBlockEnabled = floatingBlockEnabled;
        this.rotationMode = rotationMode;
    }

    public int nextMaxSpawn(int calculated) {
        if (currentLevel <= 0) {
            if (hologram != null && (!spawnerIsFullHologram || currentLevelOnHologram != currentLevel)) {
                spawnerIsFullHologram = true;
                currentLevelOnHologram = currentLevel;
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
                hologram.replaceLine(1, Message.of(LangKeys.IN_GAME_SPAWNER_FULL).asTextEntry(null));
            }
            return 0;
        }

        if ((maxSpawnedResources - spawned) >= calculated) {
            if (spawnerIsFullHologram && !rerenderHologram) {
                rerenderHologram = true;
                spawnerIsFullHologram = false;
            } else if (hologram != null && (calculated + spawned) == maxSpawnedResources) {
                spawnerIsFullHologram = true;
                hologram.replaceLine(1, Message.of(LangKeys.IN_GAME_SPAWNER_FULL).asTextEntry(null));
            }
            return calculated;
        }

        if (hologram != null && !spawnerIsFullHologram) {
            spawnerIsFullHologram = true;
            hologram.replaceLine(1, Message.of(LangKeys.IN_GAME_SPAWNER_FULL).asTextEntry(null));
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

    private void prepareHolograms(List<PlayerWrapper> viewers, boolean countdownHologram) {
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
                    .hologram(loc)
                    .firstLine(TextEntry.of(itemSpawnerType.getItemBoldName()));

            if (countdownHologram) {
                hologram.bottomLine((
                                itemSpawnerType.getInterval() < 2 ? Message.of(LangKeys.IN_GAME_SPAWNER_EVERY_SECOND)
                                        : Message.of(LangKeys.IN_GAME_SPAWNER_COUNTDOWN).placeholder("seconds",
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
        currentLevel = startLevel;
        spawnedItems.clear();
        firstTick = true;
    }

    public void start(GameImpl game) {
        if (started) {
            return;
        }

        this.game = game;
        this.currentLevel = this.startLevel;

        if (team != null) {
            var spawnerTeam = game.getCurrentTeamFromTeam(team);
            if (game.getConfigurationContainer().getOrDefault(ConfigurationContainer.STOP_TEAM_SPAWNERS_ON_DIE, Boolean.class, false) && spawnerTeam == null) {
                disabled = true;
            }
        }

        if (!disabled) {
            if (hologramEnabled && game.getConfigurationContainer().getOrDefault(ConfigurationContainer.SPAWNER_HOLOGRAMS, Boolean.class, false)) {
                prepareHolograms(game.getConnectedPlayers(), game.getConfigurationContainer().getOrDefault(ConfigurationContainer.SPAWNER_COUNTDOWN_HOLOGRAM, Boolean.class, false));
            }
        }

        started = true;

        changeInterval(Pair.of(itemSpawnerType.getInterval(), TaskerTime.SECONDS));
    }

    public void changeInterval(Pair<Integer, TaskerTime> time) {
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

                    if (team != null) {
                        var spawnerTeam = game.getCurrentTeamFromTeam(team);
                        if (game.getConfigurationContainer().getOrDefault(ConfigurationContainer.STOP_TEAM_SPAWNERS_ON_DIE, Boolean.class, false) && spawnerTeam == null) {
                            return;
                        }
                    }

                    var calculatedStack = (int) currentLevel;

                    /* Allow half level */
                    if ((currentLevel % 1) != 0) {
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


        if (hologramEnabled && game.getConfigurationContainer().getOrDefault(ConfigurationContainer.SPAWNER_HOLOGRAMS, Boolean.class, false)) {
            hologramTask = Tasker.build(() -> {
                        if (disabled || firstTick) {
                            return;
                        }

                        elapsedTime++;
                        remainingTimeToSpawn = currentInterval.first() - elapsedTime;

                        if (remainingTimeToSpawn == 0) {
                            elapsedTime = 0;
                            remainingTimeToSpawn = currentInterval.first();
                        }

                        if (game.getConfigurationContainer().getOrDefault(ConfigurationContainer.SPAWNER_HOLOGRAMS, Boolean.class, false)
                                && game.getConfigurationContainer().getOrDefault(ConfigurationContainer.SPAWNER_COUNTDOWN_HOLOGRAM, Boolean.class, false)
                                && !spawnerIsFullHologram) {
                            if (currentInterval.first() > 1) {
                                hologram.replaceLine(1, Message.of(LangKeys.IN_GAME_SPAWNER_COUNTDOWN).placeholder("seconds",  currentInterval.second().getBukkitTime(remainingTimeToSpawn) / 20).asTextEntry(null));
                            } else if (rerenderHologram) {
                                hologram.replaceLine(1, Message.of(LangKeys.IN_GAME_SPAWNER_EVERY_SECOND).asTextEntry(null));
                                rerenderHologram = false;
                            }
                        }
                    })
                    .async()
                    .repeat(20, TaskerTime.TICKS)
                    .start();
        }
    }
}
