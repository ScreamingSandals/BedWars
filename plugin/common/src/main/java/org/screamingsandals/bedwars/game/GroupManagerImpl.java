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

package org.screamingsandals.bedwars.game;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.screamingsandals.bedwars.api.game.Game;
import org.screamingsandals.bedwars.api.game.GroupManager;
import org.screamingsandals.lib.plugin.ServiceManager;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.utils.annotations.methods.OnDisable;
import org.screamingsandals.lib.utils.annotations.methods.OnEnable;
import org.screamingsandals.lib.utils.annotations.parameters.ConfigFile;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.gson.GsonConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GroupManagerImpl implements GroupManager {
    public static final Pattern GROUP_PATTERN = Pattern.compile("[a-zA-Z\\d\\-_]+");

    private final Map<String, List<UUID>> groupMap = new HashMap<>();
    @ConfigFile("database/group.json")
    private final GsonConfigurationLoader groupConfig;

    public static GroupManagerImpl getInstance() {
        return ServiceManager.get(GroupManagerImpl.class);
    }

    @OnEnable
    public void enable() {
        try {
            var root = groupConfig.load();
            root.childrenMap().forEach((o, node) -> {
                var groupName = o.toString();
                try {
                    var games = node.getList(UUID.class);
                    groupMap.put(groupName, games);
                } catch (SerializationException e) {
                    e.printStackTrace();
                }
            });
        } catch (ConfigurateException e) {
            e.printStackTrace();
        }
    }

    @OnDisable
    public void disable() {
        try {
            var root = groupConfig.createNode();
            groupMap.forEach((s, uuids) -> {
                try {
                    root.node(s).setList(UUID.class, uuids);
                } catch (SerializationException e) {
                    e.printStackTrace();
                }
            });
            groupConfig.save(root);
        } catch (ConfigurateException e) {
            e.printStackTrace();
        }
        groupMap.clear();
    }

    public boolean addToGroup(@org.intellij.lang.annotations.Pattern("[a-zA-Z\\d\\-_]+") @NotNull String group, @NotNull Game game) {
        if (!GROUP_PATTERN.matcher(group).matches()) {
            return false;
        }

        var g = groupMap.computeIfAbsent(group, k -> new ArrayList<>());
        g.add(game.getUuid());
        return true;
    }

    @Override
    public boolean removeFromGroup(@org.intellij.lang.annotations.Pattern("[a-zA-Z\\d\\-_]+") @NotNull String group, @NotNull Game game) {
        var g = groupMap.get(group);
        if (g != null) {
            g.remove(game.getUuid());
            if (g.isEmpty()) {
                groupMap.remove(group);
            }
        }
        return true;
    }

    @Override
    @NotNull
    public List<@NotNull Game> getGamesInGroup(@org.intellij.lang.annotations.Pattern("[a-zA-Z\\d\\-_]+") @NotNull String group) {
        var g = groupMap.get(group);
        if (g != null) {
            return g.stream()
                    .map(GameManagerImpl.getInstance()::getGame)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    @Override
    public List<String> getExistingGroups() {
        return List.copyOf(groupMap.keySet());
    }
}
