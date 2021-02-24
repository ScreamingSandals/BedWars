package org.screamingsandals.bedwars.holograms;

import static org.screamingsandals.bedwars.lib.lang.I.i18n;

import java.util.*;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.commands.BedWarsPermission;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.utils.PreparedLocation;
import org.screamingsandals.lib.event.EventPriority;
import org.screamingsandals.lib.hologram.Hologram;
import org.screamingsandals.lib.hologram.HologramManager;
import org.screamingsandals.lib.hologram.event.HologramTouchEvent;
import org.screamingsandals.lib.player.PlayerMapper;
import org.screamingsandals.lib.player.PlayerWrapper;
import org.screamingsandals.lib.player.event.SPlayerJoinEvent;
import org.screamingsandals.lib.player.event.SPlayerLeaveEvent;
import org.screamingsandals.lib.player.event.SPlayerWorldChangeEvent;
import org.screamingsandals.lib.plugin.PluginDescription;
import org.screamingsandals.lib.plugin.ServiceManager;
import org.screamingsandals.lib.tasker.Tasker;
import org.screamingsandals.lib.tasker.TaskerTime;
import org.screamingsandals.lib.utils.AdventureHelper;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.utils.annotations.methods.OnPostEnable;
import org.screamingsandals.lib.utils.annotations.methods.OnPreDisable;
import org.screamingsandals.lib.utils.annotations.methods.ShouldRunControllable;
import org.screamingsandals.lib.world.LocationHolder;
import org.screamingsandals.lib.world.LocationMapper;
import org.screamingsandals.lib.world.WorldHolder;
import org.screamingsandals.lib.event.OnEvent;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

@Service(dependsOn = {
        PlayerMapper.class,
        LocationMapper.class,
        HologramManager.class,
        Tasker.class,
        MainConfig.class
})
@RequiredArgsConstructor
public class StatisticsHolograms {
    private final PluginDescription pluginDescription;
    private final MainConfig mainConfig;

    private ArrayList<PreparedLocation> hologramLocations = null;
    private Map<UUID, List<Hologram>> holograms = null;

    public void addHologramLocation(LocationHolder eyeLocation) {
        this.hologramLocations.add(new PreparedLocation(eyeLocation.add(0, -3, 0)));
        this.updateHologramDatabase();
    }

    @ShouldRunControllable
    public static boolean isEnabled() {
        return Main.isPlayerStatisticsEnabled() && MainConfig.getInstance().node("holograms", "enabled").getBoolean();
    }

    public static StatisticsHolograms getInstance() {
        if (!isEnabled()) {
            throw new UnsupportedOperationException("StatisticsHolograms are not enabled!");
        }
        return ServiceManager.get(StatisticsHolograms.class);
    }

    @OnPostEnable
    @SuppressWarnings("unchecked")
    public void loadHolograms() {
        if (this.holograms != null && this.hologramLocations != null) {
            // first unload all holograms
            this.unloadHolograms();
        }

        this.holograms = new HashMap<>();
        this.hologramLocations = new ArrayList<>();

        var file = pluginDescription.getDataFolder().resolve("database").resolve("holodb.yml").toFile();

        var loader = YamlConfigurationLoader.builder()
                .file(file)
                .build();
        if (file.exists()) {
            try {
                var config = loader.load();
                var locations = config.node("locations").getList(PreparedLocation.class);
                this.hologramLocations.addAll(locations);
            } catch (ConfigurateException e) {
                e.printStackTrace();
            }
        }

        if (this.hologramLocations.size() == 0) {
            return;
        }

        this.updateHolograms();
    }

    @OnPreDisable
    public void unloadHolograms() {
        holograms.values().forEach(Holograms -> Holograms.forEach(Hologram::destroy));
    }

    @OnEvent(priority = EventPriority.HIGHEST)
    public void onJoin(SPlayerJoinEvent event) {
        updateHolograms(event.getPlayer(), 10L);
    }

    @OnEvent
    public void onWorldChange(SPlayerWorldChangeEvent event) {
        updateHolograms(event.getPlayer(), 10L);
    }

    @OnEvent
    public void onLeave(SPlayerLeaveEvent event) {
        Tasker
                .build(() -> {
                    holograms.get(event.getPlayer().getUuid()).forEach(Hologram::destroy);
                    holograms.remove(event.getPlayer().getUuid());
                })
                .async()
                .start();
    }

    public void updateHolograms(PlayerWrapper player) {
        Tasker.build(() ->
                this.hologramLocations.forEach(holoLocation ->
                        holoLocation.asOptional(LocationHolder.class).ifPresent(location ->
                                this.updatePlayerHologram(player, location)
                        )
                ))
                .async()
                .start();
    }

    public void updateHolograms(PlayerWrapper player, long delay) {
        Tasker.build(() -> this.updateHolograms(player))
                .async()
                .delay(delay, TaskerTime.TICKS)
                .start();
    }

    public void updateHolograms() {
        PlayerMapper.getPlayers().forEach(player ->
                Tasker.build(() ->
                        this.hologramLocations.forEach(holoLocation ->
                                holoLocation.asOptional(LocationHolder.class).ifPresent(location ->
                                        this.updatePlayerHologram(player, location)
                                )
                        ))
                        .async()
                        .start()
        );
    }

    private Optional<Hologram> getHologramByLocation(List<Hologram> holograms, LocationHolder holoLocation) {
        return holograms.stream()
                .filter(hologram -> {
                    var loc = hologram.getLocation().orElseGet(LocationHolder::new);
                    return loc.getWorld().getUuid().equals(holoLocation.getWorld().getUuid())
                            && loc.getX() == holoLocation.getX()
                            && loc.getY() == holoLocation.getY()
                            && loc.getZ() == holoLocation.getZ();
                })
                .findFirst();
    }

    public void updatePlayerHologram(PlayerWrapper player, LocationHolder holoLocation) {
        if (!this.holograms.containsKey(player.getUuid())) {
            this.holograms.put(player.getUuid(), new ArrayList<>());
        }

        var holograms = this.holograms.get(player.getUuid());
        var holo = this.getHologramByLocation(holograms, holoLocation);
        if (holo.isEmpty() && player.getLocation().getWorld().getUuid().equals(holoLocation.getWorld().getUuid())) {
            holograms.add(this.createPlayerStatisticHologram(player, holoLocation));
        } else if (holo.isPresent()) {
            if (player.getLocation().getWorld().getUuid().equals(holo.get().getLocation().map(LocationHolder::getWorld).map(WorldHolder::getUuid).orElse(null))) {
                this.updatePlayerStatisticHologram(player, holo.get());
            } else {
                holograms.remove(holo.get());
                holo.get().destroy();
            }
        }
    }

    private Hologram createPlayerStatisticHologram(PlayerWrapper player, LocationHolder holoLocation) {
        final var holo = HologramManager
                .hologram(holoLocation)
                .firstLine(AdventureHelper.toComponent(mainConfig.node("holograms", "headline").getString("Your §eBEDWARS§f stats")))
                .setTouchable(true);
        HologramManager.addHologram(holo);

        this.updatePlayerStatisticHologram(player, holo);
        return holo;
    }

    private void updateHologramDatabase() {
        try {
            // update hologram-database file
            var file = pluginDescription.getDataFolder().resolve("database").resolve("holodb.yml").toFile();
            var loader = YamlConfigurationLoader.builder()
                    .file(file)
                    .build();

            var node = loader.createNode();

            for (var location : hologramLocations) {
                node.node("locations").appendListNode().set(location);
            }
            loader.save(node);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private PreparedLocation getHologramLocationByLocation(LocationHolder holoLocation) {
        return hologramLocations.stream()
                .filter(loc -> loc.getWorld().equals(holoLocation.getWorld().getName())
                        && loc.getX() == holoLocation.getX()
                        && loc.getY() == holoLocation.getY()
                        && loc.getZ() == holoLocation.getZ()
                )
                .findFirst()
                .orElse(null);
    }

    @OnEvent
    public void handle(HologramTouchEvent event) {
        var player = event.getPlayer();
        var holo = event.getHologram();
        if (!holograms.containsKey(player.getUuid()) || !holograms.get(player.getUuid()).contains(holo) || holo.getLocation().isEmpty()) {
            return;
        }

        if (!player.as(Player.class).hasMetadata("bw-remove-holo") || !player.hasPermission(BedWarsPermission.ADMIN_PERMISSION.asPermission())) {
            return;
        }

        var location = holo.getLocation().get();

        player.as(Player.class).removeMetadata("bw-remove-holo", pluginDescription.as(JavaPlugin.class));
        Tasker
                .build(() -> {
                    // remove all player holograms on this location
                    for (var entry : holograms.entrySet()) {
                        var iterator = entry.getValue().iterator();
                        while (iterator.hasNext()) {
                            var hologram = iterator.next();
                            hologram.getLocation().ifPresent(locationHolder -> {
                                if (locationHolder.getWorld().getUuid().equals(location.getWorld().getUuid())
                                        && locationHolder.getX() == location.getX()
                                        && locationHolder.getY() == location.getY()
                                        && locationHolder.getZ() == location.getZ()) {
                                    hologram.destroy();
                                    iterator.remove();
                                }
                            });
                        }
                    }

                    var holoLocation = getHologramLocationByLocation(location);
                    if (holoLocation != null) {
                        hologramLocations.remove(holoLocation);
                        updateHologramDatabase();
                    }
                    player.sendMessage(i18n("holo_removed"));
                })
                .async()
                .afterOneTick()
                .start();
    }

    private void updatePlayerStatisticHologram(PlayerWrapper player, final Hologram holo) {
        var statistic = Main.getPlayerStatisticsManager().getStatistic(player.as(Player.class));

        var lines = List.of(
                i18n("statistics_kills", false).replace("%kills%",
                        Integer.toString(statistic.getKills())),
                i18n("statistics_deaths", false).replace("%deaths%",
                        Integer.toString(statistic.getDeaths())),
                i18n("statistics_kd", false).replace("%kd%",
                        Double.toString(statistic.getKD())),
                i18n("statistics_wins", false).replace("%wins%",
                        Integer.toString(statistic.getWins())),
                i18n("statistics_loses", false).replace("%loses%",
                        Integer.toString(statistic.getLoses())),
                i18n("statistics_games", false).replace("%games%",
                        Integer.toString(statistic.getGames())),
                i18n("statistics_beds", false).replace("%beds%",
                        Integer.toString(statistic.getDestroyedBeds())),
                i18n("statistics_score", false).replace("%score%",
                        Integer.toString(statistic.getScore()))
        );
        var size = holo.getLines().size();
        var increment = size == 1 || size > lines.size() ? 1 : 0;

        for (int i = 0; i < lines.size(); i++) {
            holo.setLine(i + increment, AdventureHelper.toComponent(lines.get(i)));
        }
    }

}
