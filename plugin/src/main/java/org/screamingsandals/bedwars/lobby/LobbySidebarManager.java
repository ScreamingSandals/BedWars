package org.screamingsandals.bedwars.lobby;

import org.screamingsandals.bedwars.VersionInfo;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.events.PlayerJoinedEventImpl;
import org.screamingsandals.bedwars.events.PlayerLeaveEventImpl;
import org.screamingsandals.bedwars.player.PlayerManagerImpl;
import org.screamingsandals.bedwars.statistics.PlayerStatisticManager;
import org.screamingsandals.bedwars.utils.MiscUtils;
import org.screamingsandals.lib.Server;
import org.screamingsandals.lib.event.OnEvent;
import org.screamingsandals.lib.event.player.SPlayerJoinEvent;
import org.screamingsandals.lib.event.player.SPlayerLeaveEvent;
import org.screamingsandals.lib.event.player.SPlayerWorldChangeEvent;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.player.PlayerWrapper;
import org.screamingsandals.lib.sidebar.Sidebar;
import org.screamingsandals.lib.sidebar.SidebarManager;
import org.screamingsandals.lib.spectator.Color;
import org.screamingsandals.lib.spectator.Component;
import org.screamingsandals.lib.tasker.Tasker;
import org.screamingsandals.lib.tasker.TaskerTime;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.utils.annotations.methods.OnEnable;
import org.screamingsandals.lib.utils.annotations.methods.ShouldRunControllable;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.List;

@Service(dependsOn = {
        SidebarManager.class,
        MainConfig.class,
        PlayerStatisticManager.class
})
public class LobbySidebarManager {
    private Sidebar sidebar;

    @ShouldRunControllable
    public static boolean isEnabled() {
        return MainConfig.getInstance().node("sidebar", "main-lobby", "enabled").getBoolean() && MainConfig.getInstance().node("mainlobby", "enabled").getBoolean();
    }

    @OnEnable
    public void onEnable(PlayerStatisticManager playerStatisticManager, MainConfig mainConfig) {
        var title = mainConfig.node("sidebar", "main-lobby", "title").getString("<yellow><bold>BED WARS");
        List<String> content;
        try {
            content = mainConfig.node("sidebar", "main-lobby", "content").getList(String.class, List.of());
        } catch (SerializationException e) {
            e.printStackTrace();
            content = List.of();
        }
        sidebar = Sidebar.of()
                .title(Message.ofRichText(title));

        content.forEach(s -> {
            var msg = Message.ofRichText(s)
                    .placeholder("version", VersionInfo.VERSION)
                    .placeholder("date", MiscUtils.getFormattedDate(mainConfig.node("sidebar", "date-format").getString("MM/dd/yy")));

            if (PlayerStatisticManager.isEnabled()) {
                msg.placeholder("goal", sender -> {
                    if (sender instanceof PlayerWrapper) {
                        return Component.text(MiscUtils.roundForMainLobbySidebar(playerStatisticManager.getStatistic((PlayerWrapper) sender).getNeededScoreToNextLevel()));
                    }
                    return Component.text(0); // how
                }).placeholder("current-progress", sender -> {
                    if (sender instanceof PlayerWrapper) {
                        return Component.text(MiscUtils.roundForMainLobbySidebar(playerStatisticManager.getStatistic((PlayerWrapper) sender).getScoreSincePreviousLevel()));
                    }
                    return Component.text(0); // how
                }).placeholder("level", sender -> {
                    if (sender instanceof PlayerWrapper) {
                        return Component.text(playerStatisticManager.getStatistic((PlayerWrapper) sender).getLevel() + "\u272B", Color.GRAY); // TODO: use prestiges when they are implemented
                    }
                    return Component.text(0); // how
                }).placeholder("progress-bar", sender -> {
                    if (sender instanceof PlayerWrapper) {
                        var statistic = playerStatisticManager.getStatistic((PlayerWrapper) sender);
                        var progress = statistic.getScoreSincePreviousLevel();
                        var needed = statistic.getNeededScoreToNextLevel();
                        if (needed <= 0) { // invalid
                            return Component.text().content("[").append(Component.text("\u25A0".repeat(10) + "]", Color.GRAY)).color(Color.DARK_GRAY).append("]").build();
                        }
                        int numberOfBoxesFilled = (int) (((double) progress / needed) * 10);
                        return Component.text()
                                .content("[")
                                .color(Color.DARK_GRAY)
                                .append(Component.text("\u25A0".repeat(numberOfBoxesFilled), Color.AQUA))
                                .append(Component.text("\u25A0".repeat(10 - numberOfBoxesFilled), Color.GRAY))
                                .append("]")
                                .build();
                    }
                    return Component.text(0); // how
                }).placeholder("kills", sender -> {
                    if (sender instanceof PlayerWrapper) {
                        return Component.text(playerStatisticManager.getStatistic((PlayerWrapper) sender).getKills());
                    }
                    return Component.text(0); // how
                }).placeholder("deaths", sender -> {
                    if (sender instanceof PlayerWrapper) {
                        return Component.text(playerStatisticManager.getStatistic((PlayerWrapper) sender).getDeaths());
                    }
                    return Component.text(0); // how
                }).placeholder("target-blocks-destroyed", sender -> {
                    if (sender instanceof PlayerWrapper) {
                        return Component.text(playerStatisticManager.getStatistic((PlayerWrapper) sender).getDestroyedBeds());
                    }
                    return Component.text(0); // how
                }).placeholder("loses", sender -> {
                    if (sender instanceof PlayerWrapper) {
                        return Component.text(playerStatisticManager.getStatistic((PlayerWrapper) sender).getLoses());
                    }
                    return Component.text(0); // how
                }).placeholder("wins", sender -> {
                    if (sender instanceof PlayerWrapper) {
                        return Component.text(playerStatisticManager.getStatistic((PlayerWrapper) sender).getWins());
                    }
                    return Component.text(0); // how
                }).placeholder("score", sender -> {
                    if (sender instanceof PlayerWrapper) {
                        return Component.text(playerStatisticManager.getStatistic((PlayerWrapper) sender).getScore());
                    }
                    return Component.text(0); // how
                }).placeholder("kd-ratio", sender -> {
                    if (sender instanceof PlayerWrapper) {
                        return Component.text(playerStatisticManager.getStatistic((PlayerWrapper) sender).getKD());
                    }
                    return Component.text(0); // how
                }).placeholder("played-games", sender -> {
                    if (sender instanceof PlayerWrapper) {
                        return Component.text(playerStatisticManager.getStatistic((PlayerWrapper) sender).getGames());
                    }
                    return Component.text(0); // how
                });
            }

            sidebar.bottomLine(msg);
        });

        var world = mainConfig.node("mainlobby", "world").getString("");

        if (world.isEmpty()) {
            return; // :(
        }

        Server.getConnectedPlayers().forEach(player -> {
            if (player.getLocation().getWorld().getName().equals(world) && !PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
                sidebar.addViewer(player);
            }
        });

        sidebar.show();

        Tasker.build(() -> sidebar.update()).async().repeat(20, TaskerTime.TICKS).start();
    }

    @OnEvent
    public void onJoin(SPlayerJoinEvent event) {
        var player = event.player();
        var world = MainConfig.getInstance().node("mainlobby", "world").getString("");

        if (world.isEmpty()) {
            return; // :(
        }

        if (player.getLocation().getWorld().getName().equals(world) && !PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
            sidebar.addViewer(player);
        }
    }

    @OnEvent
    public void onLeave(SPlayerLeaveEvent event) {
        sidebar.removeViewer(event.player());
    }

    @OnEvent
    public void onWorldChange(SPlayerWorldChangeEvent event) {
        var player = event.player();
        var world = MainConfig.getInstance().node("mainlobby", "world").getString("");

        if (world.isEmpty()) {
            return; // :(
        }

        if (player.getLocation().getWorld().getName().equals(world)) {
            sidebar.addViewer(player);
        } else {
            sidebar.removeViewer(player);
        }
    }

    @OnEvent
    public void onBedWarsJoin(PlayerJoinedEventImpl event) {
        sidebar.removeViewer(event.getPlayer());
    }

    @OnEvent
    public void onBedWarsLeave(PlayerLeaveEventImpl event) {
        var player = event.getPlayer();
        var world = MainConfig.getInstance().node("mainlobby", "world").getString("");

        if (world.isEmpty()) {
            return; // :(
        }

        Tasker.build(() -> {
            if (player.isOnline() && player.getLocation().getWorld().getName().equals(world)) {
                sidebar.addViewer(player);
            }
        }).delay(20, TaskerTime.TICKS).start();
    }
}
