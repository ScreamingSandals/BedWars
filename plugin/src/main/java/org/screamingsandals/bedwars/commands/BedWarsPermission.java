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

package org.screamingsandals.bedwars.commands;

import lombok.RequiredArgsConstructor;
import org.screamingsandals.bedwars.config.MainConfig;
import org.screamingsandals.lib.sender.permissions.OrPermission;
import org.screamingsandals.lib.sender.permissions.Permission;
import org.screamingsandals.lib.sender.permissions.SimplePermission;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public enum BedWarsPermission {
    ADMIN_PERMISSION(List.of("misat11.bw.admin", "bw.admin"), List.of()),
    OTHER_STATS_PERMISSION(List.of("misat11.bw.otherstats", "bw.otherstats"), List.of()),
    JOIN_PERMISSION(List.of("misat11.bw.cmd.join", "bw.cmd.join"), List.of("default-permissions", "join")),
    LEAVE_PERMISSION(List.of("misat11.bw.cmd.leave", "bw.cmd.leave"), List.of("default-permissions", "leave")),
    GAMES_INVENTORY_PERMISSION(List.of("misat11.bw.cmd.gamesinv", "bw.cmd.gamesinv"), List.of("default-permissions", "gamesinv")),
    AUTOJOIN_PERMISSION(List.of("misat11.bw.cmd.autojoin", "bw.cmd.autojoin"), List.of("default-permissions", "autojoin")),
    LIST_PERMISSION(List.of("misat11.bw.cmd.list", "bw.cmd.list"), List.of("default-permissions", "list")),
    REJOIN_PERMISSION(List.of("misat11.bw.cmd.rejoin", "bw.cmd.rejoin"), List.of("default-permissions", "rejoin")),
    STATS_PERMISSION(List.of("misat11.bw.cmd.stats", "bw.cmd.stats"), List.of("default-permissions", "stats")),
    LEADERBOARD_PERMISSION(List.of("misat11.bw.cmd.leaderboard", "bw.cmd.leaderboard"), List.of("default-permissions", "leaderboard")),
    PARTY_PERMISSION(List.of("misat11.bw.cmd.party", "bw.cmd.party"), List.of("default-permissions", "party")),
    ALL_JOIN_PERMISSION(List.of("misat11.bw.admin.alljoin", "bw.admin.alljoin"), List.of()),
    DISABLE_ALL_JOIN_PERMISSION(List.of("misat11.bw.disable.joinall", "bw.disable.joinall"), List.of()),
    START_ITEM_PERMISSION(List.of("misat11.bw.vip.startitem", "bw.vip.startitem"), List.of()),
    FORCE_JOIN_PERMISSION(List.of("misat11.bw.vip.forcejoin", "bw.vip.forcejoin"), List.of()),
    BYPASS_FLIGHT_PERMISSION(List.of("misat11.bw.bypass.flight", "bw.bypass.flight"), List.of());

    private final List<String> permissions;
    private final List<String> defaultAllowedConfigurationKeys;

    public Permission asPermission() {
        var defaultAllowed = false;
        if (!defaultAllowedConfigurationKeys.isEmpty()) {
            defaultAllowed = MainConfig.getInstance().node(defaultAllowedConfigurationKeys.toArray()).getBoolean();
        }

        final var finalDefaultAllowed = defaultAllowed;
        return OrPermission.of(
                permissions.stream()
                        .map(s -> SimplePermission.of(s, finalDefaultAllowed))
                        .collect(Collectors.toList())
        );
    }
}
