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

package org.screamingsandals.bedwars.tab;

import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.bedwars.player.BedWarsPlayer;
import org.screamingsandals.lib.plugin.ServiceManager;
import org.screamingsandals.lib.utils.AdventureHelper;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.utils.annotations.methods.OnEnable;
import org.screamingsandals.lib.utils.annotations.methods.ShouldRunControllable;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TabManager {
    private final MainConfig mainConfig;

    private List<String> header;
    private List<String> footer;

    @ShouldRunControllable
    public static boolean isEnabled() {
        return MainConfig.getInstance().node("tab", "enabled").getBoolean();
    }

    public static TabManager getInstance() {
        if (!isEnabled()) {
            throw new UnsupportedOperationException("TabManager is not enabled!");
        }
        return ServiceManager.get(TabManager.class);
    }

    @OnEnable
    public void onEnable() {
        if (mainConfig.node("tab", "header", "enabled").getBoolean()) {
            header = mainConfig.node("tab", "header", "contents").childrenList().stream().map(ConfigurationNode::getString).collect(Collectors.toList());
        }
        if (mainConfig.node("tab", "footer", "enabled").getBoolean()) {
            footer = mainConfig.node("tab", "footer", "contents").childrenList().stream().map(ConfigurationNode::getString).collect(Collectors.toList());
        }
    }

    public void modifyForPlayer(BedWarsPlayer player) {
        if (player.isOnline() && (header != null || footer != null)) {
            if (header != null) {
                player.sendPlayerListHeader(translate(player, header));
            } else {
                player.sendPlayerListHeader(Component.empty());
            }
            if (header != null) {
                player.sendPlayerListFooter(translate(player, footer));
            } else {
                player.sendPlayerListFooter(Component.empty());
            }
        }
    }

    public void clear(BedWarsPlayer player) {
        if (player.isOnline() && (header != null || footer != null)) {
            player.sendPlayerListHeaderAndFooter(Component.empty(), Component.empty());
        }
    }

    public Component translate(BedWarsPlayer gamePlayer, List<String> origin) {
        var component = Component.text();
        var first = new AtomicBoolean(true);
        origin.forEach(a -> {
            if (!first.get()) {
                component.append(Component.text("\n"));
            } else {
                first.set(false);
            }
            var game = Objects.requireNonNull(gamePlayer.getGame());
            component.append(
                    AdventureHelper.toComponent(
                            a.replace("%players%", String.valueOf(game.countPlayers()))
                                    .replace("%alive%", String.valueOf(game.countAlive()))
                                    .replace("%spectating%", String.valueOf(game.countSpectating()))
                                    .replace("%spectators%", String.valueOf(game.countSpectators()))
                                    .replace("%respawnable%", String.valueOf(game.countRespawnable()))
                                    .replace("%max%", String.valueOf(game.getMaxPlayers()))
                                    .replace("%map%", game.getName()))
            );
        });
        return component.asComponent();
    }
}
