package org.screamingsandals.bedwars.utils;

import org.screamingsandals.bedwars.api.events.*;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.upgrades.Upgrade;
import org.screamingsandals.bedwars.api.upgrades.UpgradeStorage;
import org.screamingsandals.bedwars.events.*;
import org.screamingsandals.lib.event.AbstractEvent;
import org.screamingsandals.lib.event.EventManager;
import org.screamingsandals.lib.plugin.PluginContainer;
import org.screamingsandals.lib.plugin.PluginDescription;
import org.screamingsandals.lib.plugin.PluginManager;
import org.screamingsandals.lib.plugin.ServiceManager;
import org.screamingsandals.lib.plugin.event.PluginDisabledEvent;
import org.screamingsandals.lib.utils.annotations.Service;

import java.util.Map;
import java.util.function.Consumer;

@Service
public class EventUtils implements org.screamingsandals.bedwars.api.utils.EventUtils {

    private static final Map<Class<?>, Class<? extends AbstractEvent>> classMap = Map.ofEntries(
            Map.entry(ApplyPropertyToBoughtItemEvent.class, ApplyPropertyToBoughtItemEventImpl.class),
            Map.entry(ApplyPropertyToDisplayedItemEvent.class, ApplyPropertyToDisplayedItemEventImpl.class),
            Map.entry(ApplyPropertyToItemEvent.class, ApplyPropertyToItemEventImpl.class),
            Map.entry(GameChangedStatusEvent.class, GameChangedStatusEventImpl.class),
            Map.entry(GameEndEvent.class, GameEndEventImpl.class),
            Map.entry(GameEndingEvent.class, GameEndingEventImpl.class),
            Map.entry(GameStartedEvent.class, GameStartedEventImpl.class),
            Map.entry(GameStartEvent.class, GameStartEventImpl.class),
            Map.entry(GameTickEvent.class, GameTickEventImpl.class),
            Map.entry(OpenShopEvent.class, OpenShopEventImpl.class),
            Map.entry(OpenTeamSelectionEvent.class, OpenTeamSelectionEventImpl.class),
            Map.entry(PlayerBreakBlockEvent.class, PlayerBreakBlockEventImpl.class),
            Map.entry(PlayerBuildBlockEvent.class, PlayerBuildBlockEventImpl.class),
            Map.entry(PlayerJoinedEvent.class, PlayerJoinedEventImpl.class),
            Map.entry(PlayerJoinedTeamEvent.class, PlayerJoinedTeamEventImpl.class),
            Map.entry(PlayerJoinEvent.class, PlayerJoinEventImpl.class),
            Map.entry(PlayerJoinTeamEvent.class, PlayerJoinTeamEventImpl.class),
            Map.entry(PlayerKilledEvent.class, PlayerKilledEventImpl.class),
            Map.entry(PlayerLastLeaveEvent.class, PlayerLastLeaveEventImpl.class),
            Map.entry(PlayerLeaveEvent.class, PlayerLeaveEventImpl.class),
            Map.entry(PlayerRespawnedEvent.class, PlayerRespawnedEventImpl.class),
            Map.entry(PostPropertyScanEvent.class, PostPropertyScanEventImpl.class),
            Map.entry(PostRebuildingEvent.class, PostRebuildingEventImpl.class),
            Map.entry(PostSpawnEffectEvent.class, PostSpawnEffectEventImpl.class),
            Map.entry(PrePropertyScanEvent.class, PrePropertyScanEventImpl.class),
            Map.entry(PreRebuildingEvent.class, PreRebuildingEventImpl.class),
            Map.entry(PreSpawnEffectEvent.class, PreSpawnEffectEventImpl.class),
            Map.entry(PurchaseFailedEvent.class, PurchaseFailedEventImpl.class),
            Map.entry(ResourceSpawnEvent.class, ResourceSpawnEventImpl.class),
            Map.entry(SavePlayerStatisticEvent.class, SavePlayerStatisticEventImpl.class),
            Map.entry(ServerRestartEvent.class, ServerRestartEventImpl.class),
            Map.entry(StoreIncludeEvent.class, StoreIncludeEventImpl.class),
            Map.entry(StorePostPurchaseEvent.class, StorePostPurchaseEventImpl.class),
            Map.entry(StorePrePurchaseEvent.class, StorePrePurchaseEventImpl.class),
            Map.entry(TargetBlockDestroyedEvent.class, TargetBlockDestroyedEventImpl.class),
            Map.entry(TeamChestOpenEvent.class, TeamChestOpenEventImpl.class),
            Map.entry(UpgradeBoughtEvent.class, UpgradeBoughtEventImpl.class),
            Map.entry(UpgradeImprovedEvent.class, UpgradeImprovedEventImpl.class),
            Map.entry(UpgradeRegisteredEvent.class, UpgradeRegisteredEventImpl.class),
            Map.entry(UpgradeUnregisteredEvent.class, UpgradeUnregisteredEventImpl.class),
            Map.entry(BedDestroyedMessageSendEvent.class, BedDestroyedMessageSendEventImpl.class),
            Map.entry(PlayerDeathMessageSendEvent.class, PlayerDeathMessageSendEventImpl.class)
    );

    public static EventUtils getInstance() {
        return ServiceManager.get(EventUtils.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> void handle(Object pluginObject, Class<T> event, Consumer<T> consumer) {
        PluginDescription plugin;
        if (pluginObject instanceof PluginContainer) {
            plugin = ((PluginContainer) pluginObject).getPluginDescription();
        } else if (pluginObject instanceof PluginDescription) {
            plugin = (PluginDescription) pluginObject;
        } else {
            plugin = PluginManager.getPluginFromPlatformObject(pluginObject).orElseThrow();
        }

        var mapped = classMap.get(event);
        if (mapped == null) {
            throw new UnsupportedOperationException("Unknown event class");
        }

        var handler = EventManager.getDefaultEventManager()
                .register((Class<AbstractEvent>) mapped, abstractEvent -> consumer.accept((T) abstractEvent));

        EventManager.getDefaultEventManager()
                .registerOneTime(PluginDisabledEvent.class, pluginDisabledEvent -> {
                    if (pluginDisabledEvent.getPlugin().equals(plugin)) {
                        EventManager.getDefaultEventManager().unregister(handler);
                        return true;
                    }
                    return false;
                });
    }

    @Override
    public UpgradeRegisteredEvent<Game> fireUpgradeRegisteredEvent(Game game, UpgradeStorage storage, Upgrade upgrade) {
        var event = new UpgradeRegisteredEventImpl((org.screamingsandals.bedwars.game.Game) game, upgrade, storage);
        EventManager.fire(event);
        return (UpgradeRegisteredEvent) event;
    }

    @Override
    public UpgradeUnregisteredEvent<Game> fireUpgradeUnregisteredEvent(Game game, UpgradeStorage storage, Upgrade upgrade) {
        var event = new UpgradeUnregisteredEventImpl((org.screamingsandals.bedwars.game.Game) game, upgrade, storage);
        EventManager.fire(event);
        return (UpgradeUnregisteredEvent) event;
    }
}
