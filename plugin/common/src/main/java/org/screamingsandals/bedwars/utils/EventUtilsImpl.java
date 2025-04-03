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

package org.screamingsandals.bedwars.utils;

import org.screamingsandals.bedwars.api.events.*;
import org.screamingsandals.bedwars.api.game.LocalGame;
import org.screamingsandals.bedwars.api.upgrades.Upgrade;
import org.screamingsandals.bedwars.api.upgrades.UpgradeStorage;
import org.screamingsandals.bedwars.api.utils.EventUtils;
import org.screamingsandals.bedwars.events.*;
import org.screamingsandals.bedwars.game.GameImpl;
import org.screamingsandals.lib.event.EventManager;
import org.screamingsandals.lib.event.Event;
import org.screamingsandals.lib.plugin.Plugin;
import org.screamingsandals.lib.plugin.Plugins;
import org.screamingsandals.lib.plugin.ServiceManager;
import org.screamingsandals.lib.plugin.event.PluginDisabledEvent;
import org.screamingsandals.lib.utils.annotations.Service;

import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

@Service
public class EventUtilsImpl implements EventUtils {
    private static final Map<Class<?>, Class<? extends Event>> classMap = Map.ofEntries(
            Map.entry(ApplyPropertyToBoughtItemEvent.class, ApplyPropertyToBoughtItemEventImpl.class),
            Map.entry(ApplyPropertyToDisplayedItemEvent.class, ApplyPropertyToDisplayedItemEventImpl.class),
            Map.entry(ApplyPropertyToItemEvent.class, ApplyPropertyToItemEventImpl.class),
            Map.entry(GameChangedStatusEvent.class, GameChangedStatusEventImpl.class),
            Map.entry(GameDisabledEvent.class, GameDisabledEventImpl.class),
            Map.entry(GameEnabledEvent.class, GameEnabledEventImpl.class),
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
            Map.entry(PreTargetInvalidatedEvent.class, PreTargetInvalidatedEventImpl.class),
            Map.entry(PurchaseFailedEvent.class, PurchaseFailedEventImpl.class),
            Map.entry(ResourceSpawnEvent.class, ResourceSpawnEventImpl.class),
            Map.entry(SavePlayerStatisticEvent.class, SavePlayerStatisticEventImpl.class),
            Map.entry(ServerRestartEvent.class, ServerRestartEventImpl.class),
            Map.entry(StoreIncludeEvent.class, StoreIncludeEventImpl.class),
            Map.entry(StorePostPurchaseEvent.class, StorePostPurchaseEventImpl.class),
            Map.entry(StorePrePurchaseEvent.class, StorePrePurchaseEventImpl.class),
            Map.entry(PostTargetInvalidatedEvent.class, PostTargetInvalidatedEventImpl.class),
            Map.entry(TeamChestOpenEvent.class, TeamChestOpenEventImpl.class),
            Map.entry(UpgradeBoughtEvent.class, UpgradeBoughtEventImpl.class),
            Map.entry(UpgradeImprovedEvent.class, UpgradeImprovedEventImpl.class),
            Map.entry(UpgradeRegisteredEvent.class, UpgradeRegisteredEventImpl.class),
            Map.entry(UpgradeUnregisteredEvent.class, UpgradeUnregisteredEventImpl.class),
            Map.entry(BedDestroyedMessageSendEvent.class, BedDestroyedMessageSendEventImpl.class),
            Map.entry(PlayerDeathMessageSendEvent.class, PlayerDeathMessageSendEventImpl.class),
            Map.entry(PlayerOpenGamesInventoryEvent.class, PlayerOpenGamesInventoryEventImpl.class),
            Map.entry(UpgradeLevelChangeEvent.class, UpgradeLevelChangeEventImpl.class),
            Map.entry(UpgradeLevelChangedEvent.class, UpgradeLevelChangedEventImpl.class)
    );

    public static EventUtilsImpl getInstance() {
        return ServiceManager.get(EventUtilsImpl.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> void handle(Object pluginObject, Class<T> event, Consumer<T> consumer) {
        Plugin plugin;
        if (pluginObject instanceof Plugin) {
            plugin = (Plugin) pluginObject;
        } else {
            plugin = Objects.requireNonNull(Plugins.getPluginFromPlatformObject(pluginObject));
        }

        var mapped = classMap.get(event);
        if (mapped == null) {
            throw new UnsupportedOperationException("Unknown event class");
        }

        var handler = EventManager.getDefaultEventManager()
                .register((Class<Event>) mapped, abstractEvent -> consumer.accept((T) abstractEvent));

        EventManager.getDefaultEventManager()
                .registerOneTime(PluginDisabledEvent.class, pluginDisabledEvent -> {
                    if (pluginDisabledEvent.plugin().equals(plugin)) {
                        EventManager.getDefaultEventManager().unregister(handler);
                        return true;
                    }
                    return false;
                });
    }

    @Override
    public UpgradeRegisteredEvent fireUpgradeRegisteredEvent(LocalGame game, UpgradeStorage storage, Upgrade upgrade) {
        var event = new UpgradeRegisteredEventImpl((GameImpl) game, upgrade, storage);
        EventManager.fire(event);
        return event;
    }

    @Override
    public UpgradeUnregisteredEvent fireUpgradeUnregisteredEvent(LocalGame game, UpgradeStorage storage, Upgrade upgrade) {
        var event = new UpgradeUnregisteredEventImpl((GameImpl) game, upgrade, storage);
        EventManager.fire(event);
        return event;
    }
}
