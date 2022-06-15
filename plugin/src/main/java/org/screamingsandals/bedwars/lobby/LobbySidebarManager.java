package org.screamingsandals.bedwars.lobby;

import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.events.PlayerJoinedEventImpl;
import org.screamingsandals.bedwars.events.PlayerLeaveEventImpl;
import org.screamingsandals.bedwars.player.PlayerManagerImpl;
import org.screamingsandals.bedwars.statistics.PlayerStatisticManager;
import org.screamingsandals.lib.Server;
import org.screamingsandals.lib.event.OnEvent;
import org.screamingsandals.lib.event.player.SPlayerJoinEvent;
import org.screamingsandals.lib.event.player.SPlayerLeaveEvent;
import org.screamingsandals.lib.event.player.SPlayerWorldChangeEvent;
import org.screamingsandals.lib.lang.Message;
import org.screamingsandals.lib.sidebar.Sidebar;
import org.screamingsandals.lib.sidebar.SidebarManager;
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
    private String world;

    @ShouldRunControllable
    public static boolean isEnabled() {
        return MainConfig.getInstance().node("main-lobby", "sidebar", "enabled").getBoolean() && MainConfig.getInstance().node("main-lobby", "enabled").getBoolean();
    }

    @OnEnable
    public void onEnable(PlayerStatisticManager playerStatisticManager, MainConfig mainConfig) {
        var title = mainConfig.node("main-lobby", "sidebar", "title").getString("<yellow><bold>BED WARS");
        List<String> content;
        try {
            content = mainConfig.node("main-lobby", "sidebar", "content").getList(String.class, List.of());
        } catch (SerializationException e) {
            e.printStackTrace();
            content = List.of();
        }
        sidebar = Sidebar.of()
                .title(Message.ofRichText(title));

        content.forEach(s -> sidebar.bottomLine(LobbyUtils.setupPlaceholders(Message.ofRichText(s))));

        world = mainConfig.node("main-lobby", "world").getString("");

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
